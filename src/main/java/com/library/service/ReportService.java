package com.library.service;

import com.library.model.AuditLog;
import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.model.Student;
import com.library.util.CSVExporter;

import java.util.ArrayList;
import java.util.List;

/**
 * Prepares reports and exports CSV summaries.
 */
public class ReportService {
    private final BookService bookService;
    private final StudentService studentService;
    private final IssueService issueService;
    private final AuditService auditService;

    public ReportService() {
        this.bookService = new BookService();
        this.studentService = new StudentService();
        this.issueService = new IssueService();
        this.auditService = new AuditService();
    }

    public int getTotalBooks() {
        return bookService.getAllBooks().size();
    }

    public int getTotalStudents() {
        return studentService.getAllStudents().size();
    }

    public int getIssuedBooksCount() {
        return issueService.getActiveIssues().size();
    }

    public int getAvailableBooksCount() {
        return bookService.getAvailableBooks().size();
    }

    public int getOverdueBooksCount() {
        return issueService.getOverdueIssues().size();
    }

    public List<Book> getMostBorrowedBooks(int limit) {
        return bookService.getMostBorrowedBooks(limit);
    }

    public List<AuditLog> getRecentAuditLogs(int limit) {
        return auditService.getRecentLogs(limit);
    }

    public void exportBookReport(String filePath) {
        List<Book> books = bookService.getAllBooks();
        List<String[]> rows = new ArrayList<>();
        for (Book book : books) {
            rows.add(new String[]{String.valueOf(book.getBookId()), book.getTitle(), book.getAuthor(), book.getCategory(), book.getIsbn(), String.valueOf(book.getQuantity()), String.valueOf(book.getAvailableQuantity())});
        }
        CSVExporter.export(filePath, new String[]{"Book ID", "Title", "Author", "Category", "ISBN", "Total Quantity", "Available Quantity"}, rows);
    }

    public void exportStudentReport(String filePath) {
        List<Student> students = studentService.getAllStudents();
        List<String[]> rows = new ArrayList<>();
        for (Student student : students) {
            rows.add(new String[]{String.valueOf(student.getStudentId()), student.getName(), student.getEmail(), student.getPhone()});
        }
        CSVExporter.export(filePath, new String[]{"Student ID", "Name", "Email", "Phone"}, rows);
    }

    public void exportIssueReport(String filePath) {
        List<IssuedBook> issuedBooks = issueService.getActiveIssues();
        List<String[]> rows = new ArrayList<>();
        for (IssuedBook issuedBook : issuedBooks) {
            rows.add(new String[]{String.valueOf(issuedBook.getIssueId()), String.valueOf(issuedBook.getBookId()), String.valueOf(issuedBook.getStudentId()), issuedBook.getIssueDate().toString(), issuedBook.getDueDate().toString()});
        }
        CSVExporter.export(filePath, new String[]{"Issue ID", "Book ID", "Student ID", "Issue Date", "Due Date"}, rows);
    }
}
