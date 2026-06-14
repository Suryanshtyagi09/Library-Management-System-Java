package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.IssueDAO;
import com.library.dao.ReservationDAO;
import com.library.dao.StudentDAO;
import com.library.dao.impl.BookDAOImpl;
import com.library.dao.impl.IssueDAOImpl;
import com.library.dao.impl.ReservationDAOImpl;
import com.library.dao.impl.StudentDAOImpl;
import com.library.exception.BookNotFoundException;
import com.library.exception.BookUnavailableException;
import com.library.exception.StudentNotFoundException;
import com.library.model.Book;
import com.library.model.IssuedBook;
import com.library.model.Reservation;
import com.library.model.Student;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Handles issuance, returns, fines, and reservations.
 */
public class IssueService {
    private final IssueDAO issueDAO;
    private final BookDAO bookDAO;
    private final StudentDAO studentDAO;
    private final ReservationDAO reservationDAO;
    private final AuditService auditService;

    public IssueService() {
        this.issueDAO = new IssueDAOImpl();
        this.bookDAO = new BookDAOImpl();
        this.studentDAO = new StudentDAOImpl();
        this.reservationDAO = new ReservationDAOImpl();
        this.auditService = new AuditService();
    }

    public IssuedBook issueBook(int bookId, int studentId) {
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with id " + bookId + " was not found.");
        }
        if (book.getAvailableQuantity() <= 0) {
            throw new BookUnavailableException("Book '" + book.getTitle() + "' is currently unavailable.");
        }
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with id " + studentId + " was not found.");
        }

        IssuedBook issuedBook = new IssuedBook(bookId, studentId, LocalDate.now(), LocalDate.now().plusWeeks(2));
        int issueId = issueDAO.issueBook(issuedBook);
        issuedBook.setIssueId(issueId);

        bookDAO.updateAvailableQuantity(bookId, -1);
        bookDAO.incrementBorrowCount(bookId);
        auditService.logEvent("BOOK_ISSUE", "Issued book " + book.getTitle() + " to " + student.getName());
        return issuedBook;
    }

    public boolean returnBook(int issueId) {
        IssuedBook issue = issueDAO.findById(issueId);
        if (issue == null) {
            throw new IllegalArgumentException("Issue record not found for id " + issueId);
        }
        if (issue.getReturnDate() != null) {
            return false;
        }

        double fineAmount = calculateFine(issue, LocalDate.now());
        boolean returned = issueDAO.returnBook(issueId, fineAmount);
        if (returned) {
            bookDAO.updateAvailableQuantity(issue.getBookId(), 1);
            Book book = bookDAO.findById(issue.getBookId());
            auditService.logEvent("BOOK_RETURN", "Returned book " + (book != null ? book.getTitle() : issue.getBookId()) + " with fine " + fineAmount);
        }
        return returned;
    }

    public double calculateFine(IssuedBook issuedBook, LocalDate returnDate) {
        if (issuedBook == null) {
            return 0.0;
        }
        LocalDate dueDate = issuedBook.getDueDate();
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
        long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return overdueDays > 0 ? overdueDays * 2.0 : 0.0;
    }

    public List<IssuedBook> getActiveIssues() {
        return issueDAO.findActiveIssues();
    }

    public List<IssuedBook> getOverdueIssues() {
        return issueDAO.findOverdueIssues();
    }

    public List<IssuedBook> getPendingFines() {
        return issueDAO.findPendingFines();
    }

    public Reservation reserveBook(int bookId, int studentId) {
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with id " + bookId + " was not found.");
        }
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with id " + studentId + " was not found.");
        }

        Reservation reservation = new Reservation(bookId, studentId, LocalDate.now(), LocalDate.now().plusDays(7));
        int reservationId = reservationDAO.addReservation(reservation);
        reservation.setReservationId(reservationId);
        auditService.logEvent("BOOK_RESERVATION", "Reservation created for book " + book.getTitle() + " by " + student.getName());
        return reservation;
    }
}
