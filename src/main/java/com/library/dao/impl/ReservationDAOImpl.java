package com.library.dao.impl;

import com.library.dao.ReservationDAO;
import com.library.model.Reservation;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOImpl implements ReservationDAO {
    private static final String INSERT_RESERVATION = "INSERT INTO reservations (book_id, student_id, reservation_date, reserved_until) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_BOOK = "SELECT * FROM reservations WHERE book_id = ? ORDER BY reservation_date";
    private static final String SELECT_BY_STUDENT = "SELECT * FROM reservations WHERE student_id = ? ORDER BY reservation_date";

    @Override
    public int addReservation(Reservation reservation) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_RESERVATION, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, reservation.getBookId());
            statement.setInt(2, reservation.getStudentId());
            statement.setDate(3, java.sql.Date.valueOf(reservation.getReservationDate()));
            statement.setDate(4, java.sql.Date.valueOf(reservation.getReservedUntil()));
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating reservation", e);
        }
        return 0;
    }

    @Override
    public List<Reservation> findByBookId(int bookId) {
        return findReservationsBy(SELECT_BY_BOOK, bookId);
    }

    @Override
    public List<Reservation> findByStudentId(int studentId) {
        return findReservationsBy(SELECT_BY_STUDENT, studentId);
    }

    private List<Reservation> findReservationsBy(String sql, int id) {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setReservationId(resultSet.getInt("reservation_id"));
                    reservation.setBookId(resultSet.getInt("book_id"));
                    reservation.setStudentId(resultSet.getInt("student_id"));
                    reservation.setReservationDate(resultSet.getDate("reservation_date").toLocalDate());
                    reservation.setReservedUntil(resultSet.getDate("reserved_until").toLocalDate());
                    reservations.add(reservation);
                }
            }
            return reservations;
        } catch (SQLException e) {
            throw new RuntimeException("Error reading reservations", e);
        }
    }
}
