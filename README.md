# Online Quiz Application

A real-time online quiz application built using **Java**, **multithreading**, **networking**, **AWT**, and **JDBC**. This application allows multiple users to participate in quizzes, create custom quizzes, and compete in real-time quiz battles.

---

## Features
- **Real-time quiz battles**: Multiple users can participate in quizzes simultaneously.
- **Private rooms**: Users can create and join private rooms for custom quiz tournaments.
- **Database integration**: Quiz data, user information, and results are stored in a MySQL database.
- **GUI**: A user-friendly interface built using AWT/Swing.

---

## Technologies Used
- **Java**: Core programming language.
- **Multithreading**: Handle multiple clients simultaneously.
- **Networking**: Sockets for real-time communication.
- **AWT/Swing**: Graphical user interface.
- **JDBC**: Database connectivity.
- **MySQL**: Database management.

---

## Prerequisites
1. **Java Development Kit (JDK)**: Version 8 or higher.
2. **MySQL Server**: Installed and running.
3. **MySQL Connector/J**: JDBC driver for MySQL.

---

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/OnlineQuizApplication.git
cd OnlineQuizApplication
```
### 2. Set Up the Database
   mysql -u root -p
   CREATE DATABASE quiz_db;
   USE quiz_db;
   mysql -u root -p quiz_db < src/main/resources/sql/schema.sql
   INSERT INTO users (username, password) VALUES ('user1', 'password1');
   INSERT INTO users (username, password) VALUES ('user2', 'password2');

### 3. Configure the Database
   db.url=jdbc:mysql://localhost:3306/quiz_db
   db.username=root
   db.password=your_password

### 4. Add MYSQL to the Connector/J to the Project
   Download the MySQL Connector/J driver from the official website.
   Place the mysql-connector-j-x.x.x.jar file in the lib/ directory.

### 5. Running The Application
   javac -cp lib/mysql-connector-j-x.x.x.jar com/quizapp/server/*.java
   java -cp .:lib/mysql-connector-j-x.x.x.jar com.quizapp.server.QuizServer
   javac -cp lib/mysql-connector-j-x.x.x.jar com/quizapp/client/*.java
   java -cp .:lib/mysql-connector-j-x.x.x.jar com.quizapp.client.QuizClient
