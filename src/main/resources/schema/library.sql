CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    isbn VARCHAR(20) NOT NULL UNIQUE,
    quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    borrow_count INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS issued_books (
    issue_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    fine_amount DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    student_id INT NOT NULL,
    reservation_date DATE NOT NULL,
    reserved_until DATE NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    event_time DATETIME NOT NULL
);

INSERT INTO books (title, author, category, isbn, quantity, available_quantity)
VALUES
('Effective Java', 'Joshua Bloch', 'Programming', '9780134685991', 5, 5),
('Clean Code', 'Robert C. Martin', 'Programming', '9780132350884', 4, 4),
('Design Patterns', 'Erich Gamma et al.', 'Architecture', '9780201633610', 3, 3),
('Introduction to Algorithms', 'Thomas H. Cormen', 'Algorithms', '9780262033848', 2, 2),
('The Pragmatic Programmer', 'Andrew Hunt', 'Programming', '9780201616224', 3, 3);

INSERT INTO students (name, email, phone)
VALUES
('Alice Johnson', 'alice.johnson@example.com', '555-0123'),
('Bob Martin', 'bob.martin@example.com', '555-0456'),
('Cara Lee', 'cara.lee@example.com', '555-0789');
