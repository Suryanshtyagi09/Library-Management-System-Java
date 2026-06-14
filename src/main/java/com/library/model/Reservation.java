package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a reservation request for a book.
 */
public class Reservation {
    private int reservationId;
    private int bookId;
    private int studentId;
    private LocalDate reservationDate;
    private LocalDate reservedUntil;

    public Reservation() {
    }

    public Reservation(int reservationId, int bookId, int studentId, LocalDate reservationDate, LocalDate reservedUntil) {
        this.reservationId = reservationId;
        this.bookId = bookId;
        this.studentId = studentId;
        this.reservationDate = reservationDate;
        this.reservedUntil = reservedUntil;
    }

    public Reservation(int bookId, int studentId, LocalDate reservationDate, LocalDate reservedUntil) {
        this(0, bookId, studentId, reservationDate, reservedUntil);
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
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

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDate getReservedUntil() {
        return reservedUntil;
    }

    public void setReservedUntil(LocalDate reservedUntil) {
        this.reservedUntil = reservedUntil;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return reservationId == that.reservationId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }

    @Override
    public String toString() {
        return String.format("Reservation{id=%d, bookId=%d, studentId=%d, reservedUntil=%s}", reservationId, bookId, studentId, reservedUntil);
    }
}
