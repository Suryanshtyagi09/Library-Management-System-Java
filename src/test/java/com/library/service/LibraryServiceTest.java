package com.library.service;

import com.library.dao.impl.IssueDAOImpl;
import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {
    private static BookService bookService;
    private static StudentService studentService;
    private static IssueService issueService;

    @BeforeAll
    static void setupDatabase() throws Exception {
        System.setProperty("db.url", "jdbc:h2:mem:library_test;DB_CLOSE_DELAY=-1");
        System.setProperty("db.username", "sa");
        System.setProperty("db.password", "");
        Class.forName("org.h2.Driver");
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:library_test;DB_CLOSE_DELAY=-1", "sa", "");
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE books (book_id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), author VARCHAR(255), category VARCHAR(100), isbn VARCHAR(20), quantity INT, available_quantity INT, borrow_count INT)");
            statement.execute("CREATE TABLE students (student_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(150), email VARCHAR(150), phone VARCHAR(20))");
            statement.execute("CREATE TABLE issued_books (issue_id INT AUTO_INCREMENT PRIMARY KEY, book_id INT, student_id INT, issue_date DATE, due_date DATE, return_date DATE, fine_amount DOUBLE)");
            statement.execute("CREATE TABLE audit_logs (audit_id INT AUTO_INCREMENT PRIMARY KEY, event_type VARCHAR(100), description VARCHAR(255), event_time TIMESTAMP)");
        }
        bookService = new BookService();
        studentService = new StudentService();
        issueService = new IssueService();
    }

    @Test
    void testAddBook() {
        Book book = new Book("Test Driven Development", "Kent Beck", "Programming", "9780321146533", 3);
        Book created = bookService.addBook(book);
        assertTrue(created.getBookId() > 0);
        assertEquals("Test Driven Development", created.getTitle());
    }

    @Test
    void testSearchBook() {
        Book book = new Book("Clean Architecture", "Robert C. Martin", "Programming", "9780134494166", 2);
        bookService.addBook(book);
        assertFalse(bookService.searchBooksByTitle("Clean").isEmpty());
    }

    @Test
    void testIssueBook() {
        Book book = new Book("Refactoring", "Martin Fowler", "Programming", "9780201485677", 1);
        bookService.addBook(book);
        Student student = new Student("Test Student", "test.student@example.com", "555-0000");
        studentService.addStudent(student);
        IssuedBook issuedBook = issueService.issueBook(book.getBookId(), student.getStudentId());
        assertNotNull(issuedBook);
        assertTrue(issuedBook.getIssueId() > 0);
        assertEquals(1, issueService.getActiveIssues().stream().filter(issue -> issue.getIssueId() == issuedBook.getIssueId()).count());
    }

    @Test
    void testReturnBookAndFineCalculation() {
        Book book = new Book("Domain-Driven Design", "Eric Evans", "Architecture", "9780321125217", 1);
        bookService.addBook(book);
        Student student = new Student("Return Tester", "return.tester@example.com", "555-1111");
        studentService.addStudent(student);
        IssuedBook issuedBook = issueService.issueBook(book.getBookId(), student.getStudentId());
        assertTrue(issueService.returnBook(issuedBook.getIssueId()));
        IssueDAOImpl issueDAO = new IssueDAOImpl();
        IssuedBook returned = issueDAO.findById(issuedBook.getIssueId());
        assertNotNull(returned.getReturnDate());
        assertEquals(0.0, returned.getFineAmount());
    }

    @Test
    void testFineCalculation() {
        IssuedBook issuedBook = new IssuedBook(1, 1, LocalDate.now().minusDays(21), LocalDate.now().minusDays(7));
        double fine = issueService.calculateFine(issuedBook, LocalDate.now());
        assertEquals(14.0, fine);
    }
}
