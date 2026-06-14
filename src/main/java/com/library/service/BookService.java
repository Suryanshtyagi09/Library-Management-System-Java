package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.impl.BookDAOImpl;
import com.library.exception.BookNotFoundException;
import com.library.exception.InvalidDataException;
import com.library.model.Book;

import java.util.List;

/**
 * Provides book-related business logic and validation.
 */
public class BookService {
    private final BookDAO bookDAO;
    private final AuditService auditService;

    public BookService() {
        this.bookDAO = new BookDAOImpl();
        this.auditService = new AuditService();
    }

    public Book addBook(Book book) {
        validateBook(book);
        int bookId = bookDAO.addBook(book);
        book.setBookId(bookId);
        auditService.logEvent("BOOK_ADD", "Added book: " + book.getTitle());
        return book;
    }

    public Book updateBook(Book book) {
        validateBook(book);
        if (!bookDAO.updateBook(book)) {
            throw new BookNotFoundException("Book with id " + book.getBookId() + " was not found.");
        }
        auditService.logEvent("BOOK_UPDATE", "Updated book: " + book.getTitle());
        return book;
    }

    public boolean deleteBook(int bookId) {
        if (!bookDAO.deleteBook(bookId)) {
            throw new BookNotFoundException("Book with id " + bookId + " was not found.");
        }
        auditService.logEvent("BOOK_DELETE", "Deleted book id: " + bookId);
        return true;
    }

    public Book getBookById(int bookId) {
        Book book = bookDAO.findById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with id " + bookId + " was not found.");
        }
        return book;
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookDAO.findByTitle(title);
    }

    public List<Book> searchBooksByCategory(String category) {
        return bookDAO.findByCategory(category);
    }

    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookDAO.findAvailableBooks();
    }

    public List<Book> getMostBorrowedBooks(int limit) {
        return bookDAO.findMostBorrowedBooks(limit);
    }

    public void updateAvailability(int bookId, int delta) {
        if (!bookDAO.updateAvailableQuantity(bookId, delta)) {
            throw new BookNotFoundException("Book with id " + bookId + " was not found.");
        }
    }

    public void addBorrowCount(int bookId) {
        if (!bookDAO.incrementBorrowCount(bookId)) {
            throw new BookNotFoundException("Book with id " + bookId + " was not found.");
        }
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new InvalidDataException("Book details cannot be null.");
        }
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new InvalidDataException("Title is required.");
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            throw new InvalidDataException("Author is required.");
        }
        if (book.getCategory() == null || book.getCategory().trim().isEmpty()) {
            throw new InvalidDataException("Category is required.");
        }
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new InvalidDataException("ISBN is required.");
        }
        if (book.getQuantity() <= 0) {
            throw new InvalidDataException("Quantity must be greater than zero.");
        }
    }
}
