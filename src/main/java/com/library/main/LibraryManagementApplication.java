package com.library.main;

import com.library.exception.BookNotFoundException;
import com.library.exception.BookUnavailableException;
import com.library.exception.InvalidDataException;
import com.library.exception.StudentNotFoundException;
import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.model.Reservation;
import com.library.model.Student;
import com.library.service.BookService;
import com.library.service.IssueService;
import com.library.service.ReportService;
import com.library.service.StudentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;

/**
 * Console application for the library management system.
 */
public class LibraryManagementApplication {
    private static final Logger logger = LogManager.getLogger(LibraryManagementApplication.class);
    private final Scanner scanner = new Scanner(System.in);
    private final BookService bookService = new BookService();
    private final StudentService studentService = new StudentService();
    private final IssueService issueService = new IssueService();
    private final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        new LibraryManagementApplication().run();
    }

    private void run() {
        printHeader();
        while (true) {
            printMainMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> handleBookManagement();
                case "2" -> handleStudentManagement();
                case "3" -> handleIssueBook();
                case "4" -> handleReturnBook();
                case "5" -> handleReports();
                case "6" -> exitApplication();
                default -> println("Invalid selection. Please choose a valid option.");
            }
        }
    }

    private void printHeader() {
        println("=================================");
        println("LIBRARY MANAGEMENT SYSTEM");
        println("=================================");
    }

    private void printMainMenu() {
        println("1. Book Management");
        println("2. Student Management");
        println("3. Issue Book");
        println("4. Return Book");
        println("5. Reports");
        println("6. Exit");
        print("Select an option: ");
    }

    private void handleBookManagement() {
        while (true) {
            println("\n--- BOOK MANAGEMENT ---");
            println("1. Add Book");
            println("2. Update Book");
            println("3. Delete Book");
            println("4. Search Book by ID");
            println("5. Search Book by Title");
            println("6. Search Book by Category");
            println("7. View All Books");
            println("8. View Available Books");
            println("9. Most Borrowed Books");
            println("10. Reserve Book");
            println("0. Back to Main Menu");
            print("Select an option: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> addBook();
                case "2" -> updateBook();
                case "3" -> deleteBook();
                case "4" -> searchBookById();
                case "5" -> searchBookByTitle();
                case "6" -> searchBookByCategory();
                case "7" -> listBooks(bookService.getAllBooks());
                case "8" -> listBooks(bookService.getAvailableBooks());
                case "9" -> listBooks(bookService.getMostBorrowedBooks(5));
                case "10" -> reserveBook();
                case "0" -> {
                    return;
                }
                default -> println("Invalid option.");
            }
        }
    }

    private void handleStudentManagement() {
        while (true) {
            println("\n--- STUDENT MANAGEMENT ---");
            println("1. Add Student");
            println("2. Update Student");
            println("3. Delete Student");
            println("4. Search Student by ID");
            println("5. View All Students");
            println("0. Back to Main Menu");
            print("Select an option: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> addStudent();
                case "2" -> updateStudent();
                case "3" -> deleteStudent();
                case "4" -> searchStudentById();
                case "5" -> listStudents(studentService.getAllStudents());
                case "0" -> {
                    return;
                }
                default -> println("Invalid option.");
            }
        }
    }

    private void handleIssueBook() {
        try {
            print("Book ID: ");
            int bookId = Integer.parseInt(scanner.nextLine().trim());
            print("Student ID: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            IssuedBook issuedBook = issueService.issueBook(bookId, studentId);
            println("Book issued successfully. Issue ID: " + issuedBook.getIssueId());
        } catch (BookNotFoundException | StudentNotFoundException | BookUnavailableException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error issuing book", e);
            println("An unexpected error occurred while issuing the book.");
        }
    }

    private void handleReturnBook() {
        try {
            print("Issue ID: ");
            int issueId = Integer.parseInt(scanner.nextLine().trim());
            if (issueService.returnBook(issueId)) {
                println("Book returned successfully.");
            } else {
                println("This issue record has already been returned or does not exist.");
            }
        } catch (Exception e) {
            logger.error("Error returning book", e);
            println("An unexpected error occurred while returning the book.");
        }
    }

    private void reserveBook() {
        try {
            print("Book ID: ");
            int bookId = Integer.parseInt(scanner.nextLine().trim());
            print("Student ID: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            Reservation reservation = issueService.reserveBook(bookId, studentId);
            println("Reservation created successfully. Reservation ID: " + reservation.getReservationId());
        } catch (BookNotFoundException | StudentNotFoundException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error reserving book", e);
            println("An unexpected error occurred while reserving the book.");
        }
    }

    private void handleReports() {
        while (true) {
            println("\n--- REPORTS ---");
            println("1. Dashboard Summary");
            println("2. Pending Fines");
            println("3. Export Book Report");
            println("4. Export Student Report");
            println("5. Export Issued Books Report");
            println("0. Back to Main Menu");
            print("Select an option: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> showDashboard();
                case "2" -> showPendingFines();
                case "3" -> exportReport("books_report.csv", () -> reportService.exportBookReport("books_report.csv"));
                case "4" -> exportReport("students_report.csv", () -> reportService.exportStudentReport("students_report.csv"));
                case "5" -> exportReport("issued_books_report.csv", () -> reportService.exportIssueReport("issued_books_report.csv"));
                case "0" -> {
                    return;
                }
                default -> println("Invalid option.");
            }
        }
    }

    private void showDashboard() {
        println("Total Books: " + reportService.getTotalBooks());
        println("Available Books: " + reportService.getAvailableBooksCount());
        println("Total Students: " + reportService.getTotalStudents());
        println("Issued Books: " + reportService.getIssuedBooksCount());
        println("Overdue Books: " + reportService.getOverdueBooksCount());
        println("Most Borrowed Books:");
        listBooks(reportService.getMostBorrowedBooks(5));
    }

    private void showPendingFines() {
        List<IssuedBook> fines = issueService.getPendingFines();
        if (fines.isEmpty()) {
            println("No pending fines.");
            return;
        }
        println("Issue ID | Book ID | Student ID | Issue Date | Due Date");
        for (IssuedBook issued : fines) {
            println(issued.getIssueId() + " | " + issued.getBookId() + " | " + issued.getStudentId() + " | " + issued.getIssueDate() + " | " + issued.getDueDate());
        }
    }

    private void exportReport(String filePath, Runnable exporter) {
        try {
            exporter.run();
            println("Report exported successfully to " + filePath);
        } catch (Exception e) {
            logger.error("Error exporting report", e);
            println("Could not export report. Check logs for details.");
        }
    }

    private void addBook() {
        try {
            print("Title: ");
            String title = scanner.nextLine();
            print("Author: ");
            String author = scanner.nextLine();
            print("Category: ");
            String category = scanner.nextLine();
            print("ISBN: ");
            String isbn = scanner.nextLine();
            print("Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            Book book = new Book(title, author, category, isbn, quantity);
            bookService.addBook(book);
            println("Book added successfully with ID " + book.getBookId());
        } catch (InvalidDataException e) {
            println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding book", e);
            println("An unexpected error occurred while adding the book.");
        }
    }

    private void updateBook() {
        try {
            print("Book ID: ");
            int bookId = Integer.parseInt(scanner.nextLine().trim());
            Book existing = bookService.getBookById(bookId);
            print("Title [" + existing.getTitle() + "]: ");
            String title = optionalInput(existing.getTitle());
            print("Author [" + existing.getAuthor() + "]: ");
            String author = optionalInput(existing.getAuthor());
            print("Category [" + existing.getCategory() + "]: ");
            String category = optionalInput(existing.getCategory());
            print("ISBN [" + existing.getIsbn() + "]: ");
            String isbn = optionalInput(existing.getIsbn());
            print("Quantity [" + existing.getQuantity() + "]: ");
            int quantity = optionalInt(existing.getQuantity());
            existing.setTitle(title);
            existing.setAuthor(author);
            existing.setCategory(category);
            existing.setIsbn(isbn);
            existing.setQuantity(quantity);
            existing.setAvailableQuantity(Math.max(existing.getAvailableQuantity(), quantity));
            bookService.updateBook(existing);
            println("Book updated successfully.");
        } catch (BookNotFoundException | InvalidDataException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating book", e);
            println("An unexpected error occurred while updating the book.");
        }
    }

    private void deleteBook() {
        try {
            print("Book ID: ");
            int bookId = Integer.parseInt(scanner.nextLine().trim());
            bookService.deleteBook(bookId);
            println("Book deleted successfully.");
        } catch (BookNotFoundException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting book", e);
            println("An unexpected error occurred while deleting the book.");
        }
    }

    private void searchBookById() {
        try {
            print("Book ID: ");
            int bookId = Integer.parseInt(scanner.nextLine().trim());
            Book book = bookService.getBookById(bookId);
            println(book.toString());
        } catch (BookNotFoundException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error searching book", e);
            println("An unexpected error occurred while searching the book.");
        }
    }

    private void searchBookByTitle() {
        print("Title keyword: ");
        String keyword = scanner.nextLine();
        listBooks(bookService.searchBooksByTitle(keyword));
    }

    private void searchBookByCategory() {
        print("Category keyword: ");
        String keyword = scanner.nextLine();
        listBooks(bookService.searchBooksByCategory(keyword));
    }

    private void addStudent() {
        try {
            print("Name: ");
            String name = scanner.nextLine();
            print("Email: ");
            String email = scanner.nextLine();
            print("Phone: ");
            String phone = scanner.nextLine();
            Student student = new Student(name, email, phone);
            studentService.addStudent(student);
            println("Student added successfully with ID " + student.getStudentId());
        } catch (InvalidDataException e) {
            println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding student", e);
            println("An unexpected error occurred while adding the student.");
        }
    }

    private void updateStudent() {
        try {
            print("Student ID: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            Student existing = studentService.getStudentById(studentId);
            print("Name [" + existing.getName() + "]: ");
            existing.setName(optionalInput(existing.getName()));
            print("Email [" + existing.getEmail() + "]: ");
            existing.setEmail(optionalInput(existing.getEmail()));
            print("Phone [" + existing.getPhone() + "]: ");
            existing.setPhone(optionalInput(existing.getPhone()));
            studentService.updateStudent(existing);
            println("Student updated successfully.");
        } catch (StudentNotFoundException | InvalidDataException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating student", e);
            println("An unexpected error occurred while updating the student.");
        }
    }

    private void deleteStudent() {
        try {
            print("Student ID: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            studentService.deleteStudent(studentId);
            println("Student deleted successfully.");
        } catch (StudentNotFoundException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error deleting student", e);
            println("An unexpected error occurred while deleting the student.");
        }
    }

    private void searchStudentById() {
        try {
            print("Student ID: ");
            int studentId = Integer.parseInt(scanner.nextLine().trim());
            Student student = studentService.getStudentById(studentId);
            println(student.toString());
        } catch (StudentNotFoundException e) {
            println("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error searching student", e);
            println("An unexpected error occurred while searching the student.");
        }
    }

    private void listBooks(List<Book> books) {
        if (books.isEmpty()) {
            println("No books found.");
            return;
        }
        println("ID | Title | Author | Category | ISBN | Available | Total");
        books.forEach(book -> println(book.getBookId() + " | " + book.getTitle() + " | " + book.getAuthor() + " | " + book.getCategory() + " | " + book.getIsbn() + " | " + book.getAvailableQuantity() + " | " + book.getQuantity()));
    }

    private void listStudents(List<Student> students) {
        if (students.isEmpty()) {
            println("No students found.");
            return;
        }
        println("ID | Name | Email | Phone");
        students.forEach(student -> println(student.getStudentId() + " | " + student.getName() + " | " + student.getEmail() + " | " + student.getPhone()));
    }

    private String optionalInput(String existingValue) {
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? existingValue : input;
    }

    private int optionalInt(int existingValue) {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return existingValue;
        }
        return Integer.parseInt(input);
    }

    private void exitApplication() {
        println("Exiting application. Goodbye!");
        scanner.close();
        System.exit(0);
    }

    private void println(String message) {
        System.out.println(message);
    }

    private void print(String message) {
        System.out.print(message);
    }
}
