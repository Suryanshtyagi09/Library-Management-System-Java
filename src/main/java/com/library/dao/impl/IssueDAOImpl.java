package com.library.dao.impl;

import com.library.dao.IssueDAO;
import com.library.model.IssuedBook;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IssueDAOImpl implements IssueDAO {
    private static final String INSERT_ISSUE = "INSERT INTO issued_books (book_id, student_id, issue_date, due_date, fine_amount) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_RETURN = "UPDATE issued_books SET return_date = CURRENT_DATE(), fine_amount = ? WHERE issue_id = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM issued_books WHERE issue_id = ?";
    private static final String SELECT_ACTIVE = "SELECT * FROM issued_books WHERE return_date IS NULL ORDER BY issue_date";
    private static final String SELECT_OVERDUE = "SELECT * FROM issued_books WHERE return_date IS NULL AND due_date < CURRENT_DATE() ORDER BY due_date";
    private static final String SELECT_ALL = "SELECT * FROM issued_books ORDER BY issue_date DESC";
    private static final String SELECT_PENDING_FINES = "SELECT * FROM issued_books WHERE return_date IS NULL AND due_date < CURRENT_DATE() ORDER BY due_date";

    @Override
    public int issueBook(IssuedBook issuedBook) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_ISSUE, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, issuedBook.getBookId());
            statement.setInt(2, issuedBook.getStudentId());
            statement.setDate(3, java.sql.Date.valueOf(issuedBook.getIssueDate()));
            statement.setDate(4, java.sql.Date.valueOf(issuedBook.getDueDate()));
            statement.setDouble(5, issuedBook.getFineAmount());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error issuing book", e);
        }
        return 0;
    }

    @Override
    public boolean returnBook(int issueId, double fineAmount) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_RETURN)) {
            statement.setDouble(1, fineAmount);
            statement.setInt(2, issueId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error returning book", e);
        }
    }

    @Override
    public IssuedBook findById(int issueId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setInt(1, issueId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapIssuedBook(resultSet) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding issue by id", e);
        }
    }

    @Override
    public List<IssuedBook> findActiveIssues() {
        return findIssues(SELECT_ACTIVE);
    }

    @Override
    public List<IssuedBook> findOverdueIssues() {
        return findIssues(SELECT_OVERDUE);
    }

    @Override
    public List<IssuedBook> findAllIssues() {
        return findIssues(SELECT_ALL);
    }

    @Override
    public List<IssuedBook> findPendingFines() {
        return findIssues(SELECT_PENDING_FINES);
    }

    private List<IssuedBook> findIssues(String sql) {
        List<IssuedBook> issues = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                issues.add(mapIssuedBook(resultSet));
            }
            return issues;
        } catch (SQLException e) {
            throw new RuntimeException("Error reading issues", e);
        }
    }

    private IssuedBook mapIssuedBook(ResultSet resultSet) throws SQLException {
        IssuedBook issuedBook = new IssuedBook();
        issuedBook.setIssueId(resultSet.getInt("issue_id"));
        issuedBook.setBookId(resultSet.getInt("book_id"));
        issuedBook.setStudentId(resultSet.getInt("student_id"));
        issuedBook.setIssueDate(resultSet.getDate("issue_date").toLocalDate());
        issuedBook.setDueDate(resultSet.getDate("due_date").toLocalDate());
        java.sql.Date returnDate = resultSet.getDate("return_date");
        if (returnDate != null) {
            issuedBook.setReturnDate(returnDate.toLocalDate());
        }
        issuedBook.setFineAmount(resultSet.getDouble("fine_amount"));
        return issuedBook;
    }
}
