package com.library.dao;

import com.library.model.Reservation;

import java.util.List;

public interface ReservationDAO {
    int addReservation(Reservation reservation);
    List<Reservation> findByBookId(int bookId);
    List<Reservation> findByStudentId(int studentId);
}
