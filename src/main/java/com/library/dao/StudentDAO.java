package com.library.dao;

import com.library.model.Student;

import java.util.List;

public interface StudentDAO {
    int addStudent(Student student);
    boolean updateStudent(Student student);
    boolean deleteStudent(int studentId);
    Student findById(int studentId);
    List<Student> findAll();
}
