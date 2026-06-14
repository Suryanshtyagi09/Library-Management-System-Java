package com.library.service;

import com.library.dao.StudentDAO;
import com.library.dao.impl.StudentDAOImpl;
import com.library.exception.InvalidDataException;
import com.library.exception.StudentNotFoundException;
import com.library.model.Student;

import java.util.List;

/**
 * Provides student-related business logic and validation.
 */
public class StudentService {
    private final StudentDAO studentDAO;
    private final AuditService auditService;

    public StudentService() {
        this.studentDAO = new StudentDAOImpl();
        this.auditService = new AuditService();
    }

    public Student addStudent(Student student) {
        validateStudent(student);
        int studentId = studentDAO.addStudent(student);
        student.setStudentId(studentId);
        auditService.logEvent("STUDENT_ADD", "Added student: " + student.getName());
        return student;
    }

    public Student updateStudent(Student student) {
        validateStudent(student);
        if (!studentDAO.updateStudent(student)) {
            throw new StudentNotFoundException("Student with id " + student.getStudentId() + " was not found.");
        }
        auditService.logEvent("STUDENT_UPDATE", "Updated student: " + student.getName());
        return student;
    }

    public boolean deleteStudent(int studentId) {
        if (!studentDAO.deleteStudent(studentId)) {
            throw new StudentNotFoundException("Student with id " + studentId + " was not found.");
        }
        auditService.logEvent("STUDENT_DELETE", "Deleted student id: " + studentId);
        return true;
    }

    public Student getStudentById(int studentId) {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException("Student with id " + studentId + " was not found.");
        }
        return student;
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new InvalidDataException("Student details cannot be null.");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new InvalidDataException("Student name is required.");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new InvalidDataException("Student email is required.");
        }
        if (student.getPhone() == null || student.getPhone().trim().isEmpty()) {
            throw new InvalidDataException("Student phone is required.");
        }
    }
}
