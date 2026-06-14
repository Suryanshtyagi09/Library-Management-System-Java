package com.library.dao;

import com.library.model.Book;

import java.util.List;

public interface BookDAO {
    int addBook(Book book);
    boolean updateBook(Book book);
    boolean deleteBook(int bookId);
    Book findById(int bookId);
    List<Book> findByTitle(String title);
    List<Book> findByCategory(String category);
    List<Book> findAll();
    List<Book> findAvailableBooks();
    boolean updateAvailableQuantity(int bookId, int delta);
    boolean incrementBorrowCount(int bookId);
    List<Book> findMostBorrowedBooks(int limit);
}
