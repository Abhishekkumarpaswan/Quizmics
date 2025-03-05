-- Create the database
CREATE DATABASE IF NOT EXISTS quiz_db;
USE quiz_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
    );

-- Quizzes table
CREATE TABLE IF NOT EXISTS quizzes (
                                       quiz_id INT AUTO_INCREMENT PRIMARY KEY,
                                       quiz_name VARCHAR(100) NOT NULL,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    );

-- Questions table
CREATE TABLE IF NOT EXISTS questions (
                                         question_id INT AUTO_INCREMENT PRIMARY KEY,
                                         quiz_id INT,
                                         question_text VARCHAR(255) NOT NULL,
    option1 VARCHAR(100) NOT NULL,
    option2 VARCHAR(100) NOT NULL,
    option3 VARCHAR(100) NOT NULL,
    option4 VARCHAR(100) NOT NULL,
    correct_answer VARCHAR(100) NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id)
    );

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
                                     room_id INT AUTO_INCREMENT PRIMARY KEY,
                                     room_name VARCHAR(100) NOT NULL,
    quiz_id INT,
    created_by INT,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
    );

-- Results table
CREATE TABLE IF NOT EXISTS results (
                                       result_id INT AUTO_INCREMENT PRIMARY KEY,
                                       user_id INT,
                                       quiz_id INT,
                                       score INT,
                                       FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id)
    );