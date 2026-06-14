package com.library.dao;

import com.library.model.IssuedBook;

import java.util.List;

public interface IssueDAO {
    int issueBook(IssuedBook issuedBook);
    boolean returnBook(int issueId, double fineAmount);
    IssuedBook findById(int issueId);
    List<IssuedBook> findActiveIssues();
    List<IssuedBook> findOverdueIssues();
    List<IssuedBook> findAllIssues();
    List<IssuedBook> findPendingFines();
}
