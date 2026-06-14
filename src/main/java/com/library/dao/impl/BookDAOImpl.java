package com.library.dao.impl;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {
    private static final String INSERT_BOOK = "INSERT INTO books (title, author, category, isbn, quantity, available_quantity, borrow_count) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_BOOK = "UPDATE books SET title = ?, author = ?, category = ?, isbn = ?, quantity = ?, available_quantity = ? WHERE book_id = ?";
    private static final String DELETE_BOOK = "DELETE FROM books WHERE book_id = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM books WHERE book_id = ?";
    private static final String SEARCH_TITLE = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?)";
    private static final String SEARCH_CATEGORY = "SELECT * FROM books WHERE LOWER(category) LIKE LOWER(?)";
    private static final String SELECT_ALL = "SELECT * FROM books ORDER BY title";
    private static final String SELECT_AVAILABLE = "SELECT * FROM books WHERE available_quantity > 0 ORDER BY title";
    private static final String UPDATE_AVAILABLE = "UPDATE books SET available_quantity = available_quantity + ? WHERE book_id = ?";
    private static final String INCREMENT_BORROW = "UPDATE books SET borrow_count = borrow_count + 1 WHERE book_id = ?";
    private static final String MOST_BORROWED = "SELECT * FROM books ORDER BY borrow_count DESC LIMIT ?";

    @Override
    public int addBook(Book book) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_BOOK, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setString(4, book.getIsbn());
            statement.setInt(5, book.getQuantity());
            statement.setInt(6, book.getAvailableQuantity());
            statement.setInt(7, book.getBorrowCount());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding book", e);
        }
        return 0;
    }

    @Override
    public boolean updateBook(Book book) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BOOK)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setString(4, book.getIsbn());
            statement.setInt(5, book.getQuantity());
            statement.setInt(6, book.getAvailableQuantity());
            statement.setInt(7, book.getBookId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating book", e);
        }
    }

    @Override
    public boolean deleteBook(int bookId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BOOK)) {
            statement.setInt(1, bookId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting book", e);
        }
    }

    @Override
    public Book findById(int bookId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID)) {
            statement.setInt(1, bookId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapBook(resultSet) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding book by id", e);
        }
    }

    @Override
    public List<Book> findByTitle(String title) {
        return findByString(SEARCH_TITLE, "%" + title + "%");
    }

    @Override
    public List<Book> findByCategory(String category) {
        return findByString(SEARCH_CATEGORY, "%" + category + "%");
    }

    private List<Book> findByString(String sql, String parameter) {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, parameter);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(mapBook(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching books", e);
        }
        return books;
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                books.add(mapBook(resultSet));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Error listing all books", e);
        }
    }

    @Override
    public List<Book> findAvailableBooks() {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_AVAILABLE);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                books.add(mapBook(resultSet));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Error listing available books", e);
        }
    }

    @Override
    public boolean updateAvailableQuantity(int bookId, int delta) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_AVAILABLE)) {
            statement.setInt(1, delta);
            statement.setInt(2, bookId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating available quantity", e);
        }
    }

    @Override
    public boolean incrementBorrowCount(int bookId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INCREMENT_BORROW)) {
            statement.setInt(1, bookId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating borrow count", e);
        }
    }

    @Override
    public List<Book> findMostBorrowedBooks(int limit) {
        List<Book> books = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(MOST_BORROWED)) {
            statement.setInt(1, limit);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(mapBook(resultSet));
                }
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving most borrowed books", e);
        }
    }

    private Book mapBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setBookId(resultSet.getInt("book_id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthor(resultSet.getString("author"));
        book.setCategory(resultSet.getString("category"));
        book.setIsbn(resultSet.getString("isbn"));
        book.setQuantity(resultSet.getInt("quantity"));
        book.setAvailableQuantity(resultSet.getInt("available_quantity"));
        book.setBorrowCount(resultSet.getInt("borrow_count"));
        return book;
    }
}
