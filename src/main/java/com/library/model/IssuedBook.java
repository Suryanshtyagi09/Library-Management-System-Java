package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an issued book transaction.
 */
public class IssuedBook {
    private int issueId;
    private int bookId;
    private int studentId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;

    public IssuedBook() {
    }

    public IssuedBook(int issueId, int bookId, int studentId, LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, double fineAmount) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    public IssuedBook(int bookId, int studentId, LocalDate issueDate, LocalDate dueDate) {
        this(0, bookId, studentId, issueDate, dueDate, null, 0.0);
    }

    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IssuedBook)) return false;
        IssuedBook that = (IssuedBook) o;
        return issueId == that.issueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(issueId);
    }

    @Override
    public String toString() {
        return String.format("IssuedBook{id=%d, bookId=%d, studentId=%d, issueDate=%s, dueDate=%s, returnDate=%s, fine=%.2f}",
                issueId, bookId, studentId, issueDate, dueDate, returnDate, fineAmount);
    }
}
