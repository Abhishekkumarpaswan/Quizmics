-- SQL scripts for database setup

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
                                     room_name VARCHAR(255) NOT NULL,
    quiz_id INT NOT NULL,
    created_by INT NOT NULL,
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

INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer)
VALUES
    (1, 'What is the output of the following program?\n#include <stdio.h>\nvoid swap (int *x, int *y) { static int *temp; temp = x; x = y; y = temp; }\nvoid printab () { static int i, a = -3, b = -6; i = 0; while (i <= 4) { if ((i++)%2 == 1) continue; a = a + i; b = b + i; } swap (&a, &b); printf("a = %d, b = %d\\n", a, b); }\nmain() { printab(); printab(); }', 'a=6,b=3 a=12,b=15', 'a=3,b=0 a=12,b=9', 'a=3,b=6 a=3,b=6', 'a=6,b=3 a=15,b=12', 'a=6,b=3 a=12,b=15'),

    (1, 'What is the output of the following program?\n#include <stdio.h>\nint main( ) { static int a[] = {10, 20, 30, 40, 50}; static int *p[] = {a, a+3, a+4, a+1, a+2}; int **ptr = p; ptr++; printf("%d %d", ptr - p, **ptr); }', '1 40', '1 30', '0 40', '0 30', '1 40'),

    (1, 'What is the output of the following program?\n#include <stdio.h>\nint main() { int a[][3] = {1, 2, 3, 4, 5, 6}; int (*ptr)[3] = a; printf("%d %d ", (*ptr)[1], (*ptr)[3]); ++ptr; printf("%d %d", (*ptr)[1], (*ptr)[2]); return 0; }', '2 4 5 6', '2 3 5 6', '2 3 4 6', '3 4 5 6', '2 4 5 6'),

    (1, 'What is the output of the following program?\nvoid count(int n) { static int d=1; printf("%d ", n); d++; if(n>1) count(n-1); printf("%d ", d); }\nvoid main() { count(3); }', '3 2 1 4 3 3', '3 3 3 4 4 4', '3 2 1 4 4 4', '3 2 1 3 3 3', '3 2 1 4 4 4'),

    (1, 'What is the output of the following program?\nstruct Ournode { char x,y,z; };\nint main() { struct Ournode p = {\'1\', \'0\', \'b\'+2}; struct Ournode *q = &p; printf ("%c, %c", *((char*)q+2), *((char*)q+1)); return 0; }', '0,d', '0, a+2', 'd,0', '‘0’,’d’', 'd,0');

INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer)
VALUES
    (1, 'What is the output of the following program?\nint counter = 0;\nint calc (int a, int b) { int c; counter++; if (b==9) return (a*a*a); else { c = calc(a, b/3); return (c*c*c); } }\nint main () { calc(3, 27); printf ("%d", counter); }', '1', '27', '3', '2', '3'),

    (1, 'What is the output of the following program?\nstruct node { int i; int *c; };\nint main() { struct node a[2], *p; int b[2] = {15,20}; p = &a; a[0].i=30; a[1].i=45; a[0].c=b+1; printf ("%d", *p++->c++); return 0; }', '15', '20', '16', '21', '20'),

    (1, 'How many times * is printed in this program?\nint count(int x,int y) { if (y != 1){ if (x != 1) { printf("*"); count(x/2, y); } else { y = y-1; count(1, y); } } return 0; }\nint main(){ count(27,64); return 0; }', '30', '4', '5', '3', '4'),

    (1, 'What will the output of the following program?\n#include <stdio.h>\nint main(void) { char c[ ] = "NITKMCA2022"; char *p=c; printf("%s",c+3[p]-6[p]-1); return 0; }', '2022', '22', 'KMCA2022', 'MCA2022', 'KMCA2022'),

    (1, 'What is the output of the following program?\nstruct node { int i; int *c; };\nint main() { struct node a[2], *p; int b[2] = {10,25}; p = &a; a[0].i=35; a[1].i=40; a[0].c=b; printf ("%d, %d", (p++)->i,++p->i); return 0; }', '36,36', '35,36', '35,35', '36,41', '35,36'),

    (1, 'What will the output of the following program?\nvoid main() { unsigned int x[4][6] = {{1, 2, 3, 13, 14, 23}, {4, 5, 6, 16, 17, 24}, {7, 8, 9, 18, 19,45}, {10, 11, 12, 20, 21,56}}; printf("%d, %d", **(x+2)+3, *(*(x+2)+3)); }', '18, 10', '6, 12', '12, 6', '10, 18', '10, 18'),

    (1, 'Find the output of program.\nint fun(char *str1) { char *str2 = str1; while(*str1++); return (str1-str2); }\nint main() { char *str = "NITKMCA"; printf("%d", fun(str)); return 0; }', '1', '7', '8', '4', '8'),

    (1, 'What is the return value of the function foo when it is called as foo(64, 12) ?\nunsigned int foo(unsigned int n, unsigned int r) { if (n > 0) return (n%r + foo (n/r, r )); else return 0; }', '14', '6', '4', '9', '6'),

    (1, 'Find the output of program.\nchar *c[] = {"GatsQuiz", "MCQ", "TEST", "QUIZ"}; char **cp[] = {c+3, c+1, c, c+2}; char ***cpp = cp;\nint main() { printf("%s ", **cpp++); printf("%s ", *cpp[-1]+3); return 0; }', 'MCQ Z', 'QUIZ Z', 'TEST Z', 'MCQ IZ', 'QUIZ Z'),

    (1, 'What is the output of the following program?\nint main ( ) { static int x[ ] = {2, 1, 4, 3, 5, 6, 8, 7}; int i;\nfor (i = 4; i<6; ++i) x [x[i]] = x [i];\nfor (i = 5; i<8; ++i) printf ("%d", x [i]); return 0; }', '587', '687', '568', '368', '687');



INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer)
VALUES
    (2, 'What will be the output of the following Java code?\n\nclass Test {\n    public static void main(String[] args) {\n        int x = 5;\n        System.out.println(x++ * ++x);\n    }\n}', '30', '36', '35', 'Compilation error', '36'),

    (2, 'What happens when a constructor is declared as private in Java?', 'It cannot be accessed outside the class.', 'The class cannot be instantiated.', 'Only the class itself can create its objects.', 'All of the above.', 'All of the above.'),

    (2, 'What is the output of the following code?\n\npublic class Test {\n    public static void main(String[] args) {\n        String s1 = "hello";\n        String s2 = new String("hello");\n        System.out.println(s1 == s2);\n    }\n}', 'true', 'false', 'Compilation error', 'Runtime error', 'false'),

    (2, 'Which of the following statements about Java’s static keyword is true?', 'A static method can access instance variables directly.', 'Static methods can be overridden.', 'Static variables belong to the class, not instances.', 'Static blocks execute after the main method.', 'Static variables belong to the class, not instances.'),

    (2, 'What will be the output of the following code?\n\nclass Test {\n    static int x = 10;\n    static { x = x * 2; }\n    public static void main(String[] args) {\n        System.out.println(x);\n    }\n}', '10', '20', 'Compilation error', '0', '20'),

    (2, 'Which data type will you use to store a 64-bit floating-point value?', 'float', 'double', 'long', 'BigDecimal', 'double'),

    (2, 'What will be the output of the following code?\n\nclass Test {\n    public static void main(String[] args) {\n        System.out.println(10 + 20 + "Hello" + 10 + 20);\n    }\n}', '30Hello1020', 'Hello1020', '30Hello30', 'Compilation error', '30Hello1020'),

    (2, 'What does the final keyword prevent in Java?', 'Inheritance', 'Method Overriding', 'Reassignment of Variables', 'All of the above', 'All of the above'),

    (2, 'What is the default value of a local variable in Java?', 'null', '0', 'Garbage value', 'No default value; must be initialized before use', 'No default value; must be initialized before use'),

    (2, 'What will be the output of the following code?\n\nclass Test {\n    public static void main(String[] args) {\n        String str = "Java";\n        str.concat(" Programming");\n        System.out.println(str);\n    }\n}', 'Java Programming', 'Java', 'Compilation error', 'Runtime error', 'Java'),

    (2, 'Which of the following is true about interfaces in Java?', 'Interfaces can have constructors.', 'Interfaces can contain instance variables.', 'An interface can extend another interface.', 'A class can extend multiple interfaces.', 'An interface can extend another interface.'),

    (2, 'What is the purpose of super in Java?', 'To call the superclass constructor', 'To access superclass methods and fields', 'To avoid ambiguity when subclass and superclass have the same field names', 'All of the above', 'All of the above'),

    (2, 'What is the output of the following code?\n\nclass Parent {\n    int a = 10;\n}\n\nclass Child extends Parent {\n    int a = 20;\n    void print() {\n        System.out.println(super.a);\n    }\n}\n\npublic class Test {\n    public static void main(String[] args) {\n        new Child().print();\n    }\n}', '20', '10', 'Compilation error', 'Runtime error', '10'),

    (2, 'What will be the output of the following code?\n\nclass Test {\n    public static void main(String[] args) {\n        StringBuffer sb1 = new StringBuffer("Hello");\n        StringBuffer sb2 = sb1.append(" World");\n        System.out.println(sb1.equals(sb2));\n    }\n}', 'true', 'false', 'Compilation error', 'Runtime error', 'false'),

    (2, 'What will be the output of the following code?\n\nclass Test {\n    public static void main(String[] args) {\n        int a = 10;\n        int b = 0;\n        try {\n            System.out.println(a / b);\n        } catch (ArithmeticException e) {\n            System.out.println("Exception: " + e.getMessage());\n        } finally {\n            System.out.println("Finally executed");\n        }\n    }\n}', 'Exception: / by zero', 'Exception: / by zero Finally executed', 'Finally executed', 'Compilation error', 'Exception: / by zero Finally executed');



INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer)
VALUES
    (3, 'What is the time complexity of inserting an element at the end of an array (assuming the array is not full)?', 'O(1)', 'O(n)', 'O(log n)', 'O(n²)', 'O(1)'),
    (3, 'Which data structure is used for implementing recursion?', 'Queue', 'Stack', 'Linked List', 'Tree', 'Stack'),
    (3, 'What is the worst-case time complexity of QuickSort?', 'O(n log n)', 'O(n²)', 'O(n)', 'O(log n)', 'O(n²)'),
    (3, 'Which of the following operations is not efficient in an array?', 'Insertion at the end', 'Deletion from the end', 'Insertion at the beginning', 'Accessing an element by index', 'Insertion at the beginning'),
    (3, 'In which data structure, elements are always inserted and deleted from opposite ends?', 'Queue', 'Deque', 'Stack', 'Array', 'Deque'),
    (3, 'What is the main advantage of a doubly linked list over a singly linked list?', 'Uses less memory', 'Simpler implementation', 'Can be traversed in both directions', 'Faster search time', 'Can be traversed in both directions'),
    (3, 'What is the time complexity of searching for an element in a balanced binary search tree (BST)?', 'O(n)', 'O(log n)', 'O(1)', 'O(n²)', 'O(log n)'),
    (3, 'Which of the following sorting algorithms has the best average-case performance?', 'Merge Sort', 'Quick Sort', 'Bubble Sort', 'Insertion Sort', 'Quick Sort'),
    (3, 'What is the purpose of a hashing function?', 'To sort data', 'To map keys to memory locations', 'To implement recursion', 'To traverse trees', 'To map keys to memory locations'),
    (3, 'What is the best-case time complexity of Bubble Sort?', 'O(n)', 'O(n log n)', 'O(n²)', 'O(1)', 'O(n)'),
    (3, 'Which data structure is used to implement LRU (Least Recently Used) cache?', 'Queue', 'Stack', 'HashMap + Doubly Linked List', 'Heap', 'HashMap + Doubly Linked List'),
    (3, 'What is the space complexity of Depth First Search (DFS) in a graph?', 'O(V + E)', 'O(V)', 'O(E)', 'O(1)', 'O(V + E)'),
    (3, 'Which of the following is NOT an application of a stack?', 'Function calls in recursion', 'Undo operations in text editors', 'Scheduling tasks in CPU', 'Balancing parentheses in an expression', 'Scheduling tasks in CPU'),
    (3, 'What is the advantage of an AVL tree over a simple Binary Search Tree (BST)?', 'Uses less memory', 'Maintains balance to ensure O(log n) search time', 'Easier to implement', 'No need for additional rotations', 'Maintains balance to ensure O(log n) search time'),
    (3, 'Which of the following is the most suitable data structure for implementing a priority queue?', 'Stack', 'Heap', 'Linked List', 'Queue', 'Heap');




INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer)
VALUES
    (4, 'Which of the following is NOT a function of an operating system?', 'Memory Management', 'Process Scheduling', 'Compiling Programs', 'File System Management',  'Compiling Programs'),
    (4, 'What is the main purpose of a process scheduler?', 'To manage memory allocation', 'To control file access', 'To schedule CPU execution of processes', 'To handle hardware interrupts', 'To schedule CPU execution of processes'),
    (4, 'Which of the following scheduling algorithms prevents starvation?', 'First-Come, First-Served (FCFS)', 'Shortest Job Next (SJN)', 'Round Robin (RR)', 'Priority Scheduling', 'Round Robin (RR)'),
    (4, 'What is the main disadvantage of a monolithic kernel?', 'Low performance', 'Difficult to maintain and modify', 'High security risks', 'Requires multiple CPU cores', 'Difficult to maintain and modify'),
    (4, 'In a paging system, what is the purpose of the page table?', 'To store file directory information', 'To map logical addresses to physical addresses', 'To keep track of CPU processes', 'To manage disk storage', 'To map logical addresses to physical addresses'),
    (4, 'Which of the following is a characteristic of a real-time operating system (RTOS)?', 'Provides high throughput', 'Provides predictable response times', 'Uses time-sharing techniques', 'Runs on single-user systems only', 'Provides predictable response times'),
    (4, 'What is the primary role of a device driver in an OS?', 'To manage CPU scheduling', 'To provide a user interface', 'To enable communication between the OS and hardware', 'To execute user programs', 'To enable communication between the OS and hardware'),
    (4, 'Which of the following page replacement algorithms is considered optimal?', 'FIFO (First-In-First-Out)', 'LRU (Least Recently Used)', 'Optimal Page Replacement', 'Clock Algorithm', 'Optimal Page Replacement'),
    (4, 'What is the primary purpose of the swap space in an operating system?', 'To store executable programs', 'To extend RAM by using disk space', 'To improve CPU speed', 'To store temporary files',  'To extend RAM by using disk space'),
    (4, 'In which scheduling algorithm does the process that arrives first get executed first?', 'Round Robin', 'Shortest Job First', 'First-Come, First-Served', 'Priority Scheduling', 'First-Come, First-Served'),
    (4, 'A deadlock can occur when which of the following conditions hold?', 'Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait', 'Starvation, Mutual Exclusion, Preemption, Circular Wait', 'Deadlock Prevention, No Preemption, Hold and Wait, FIFO', 'Paging, Segmentation, Swapping, Virtual Memory', 'Mutual Exclusion, Hold and Wait, No Preemption, Circular Wait'),
    (4, 'Which of the following is NOT a type of interprocess communication (IPC)?', 'Shared Memory', 'Message Passing', 'Paging', 'Signals', 'Paging'),
    (4, 'What is a thread in an operating system?', 'A process with multiple input streams', 'A lightweight process that shares resources with other threads', 'A process running in the background', 'A unit of memory allocated to a process',  'A lightweight process that shares resources with other threads'),
    (4, 'What does the "thrashing" condition indicate in an operating system?', 'High CPU usage due to too many processes', 'Frequent swapping of pages between RAM and disk, reducing performance', 'A system crash due to lack of resources', 'Excessive use of virtual memory', 'Frequent swapping of pages between RAM and disk, reducing performance'),
    (4, 'Which of the following is a valid method for deadlock prevention?', 'Allowing circular wait', 'Allocating all required resources before execution', 'Removing mutual exclusion', 'Avoiding process scheduling', 'Allocating all required resources before execution');


INSERT INTO questions (quiz_id, question_text, option1, option2, option3, option4, correct_answer) VALUES
                                                                                                       (5, 'What is the primary function of a database management system (DBMS)?', 'Data encryption', 'Managing and organizing data', 'Performing complex calculations', 'Controlling network traffic',  'Managing and organizing data'),
                                                                                                       (5, 'In an ER model, which of the following represents a relationship?', 'Ellipse', 'Diamond', 'Rectangle', 'Triangle', 'Diamond'),
                                                                                                       (5, 'Which normal form eliminates partial dependency?', '1NF', '2NF', '3NF', 'BCNF', '2NF'),
                                                                                                       (5, 'What is the purpose of indexing in a database?', 'To reduce redundancy', 'To improve query performance', 'To enforce referential integrity', 'To normalize the database', 'To improve query performance'),
                                                                                                       (5, 'Which SQL clause is used to filter records based on a specified condition?', 'ORDER BY', 'GROUP BY', 'HAVING', 'WHERE',  'WHERE'),
                                                                                                       (5, 'What is a foreign key in a database?', 'A key that uniquely identifies a record in a table', 'A key that is used for indexing', 'A key that references a primary key in another table', 'A key that is used for encryption', 'A key that references a primary key in another table'),
                                                                                                       (5, 'What type of JOIN returns all records when there is a match in one of the tables?', 'INNER JOIN', 'LEFT JOIN', 'RIGHT JOIN', 'FULL OUTER JOIN', 'FULL OUTER JOIN'),
                                                                                                       (5, 'What is the ACID property in DBMS?', 'Atomicity, Consistency, Isolation, Durability', 'Accuracy, Complexity, Isolation, Dependability', 'Automatic, Consistent, Indexed, Distributed', 'Association, Collection, Indexing, Durability', 'Atomicity, Consistency, Isolation, Durability'),
                                                                                                       (5, 'What is the purpose of the COMMIT command in SQL?', 'To undo all changes made in a transaction', 'To permanently save all changes made in a transaction', 'To lock a table', 'To execute a procedure', 'To permanently save all changes made in a transaction'),
                                                                                                       (5, 'In a relational database, what does a tuple represent?', 'A row in a table', 'A column in a table', 'A relationship between two tables', 'A primary key', 'A row in a table'),
                                                                                                       (5, 'Which of the following is NOT a type of database model?', 'Hierarchical', 'Network', 'Object-oriented', 'Procedural', 'Procedural'),
                                                                                                       (5, 'Which SQL command is used to remove duplicate rows from the result set?', 'UNIQUE', 'DISTINCT', 'DELETE', 'DROP', 'DISTINCT'),
                                                                                                       (5, 'Which of the following is true about stored procedures?', 'They cannot return values', 'They improve performance by reducing the number of SQL queries sent to the database', 'They do not support parameters', 'They cannot be used in relational databases',  'They improve performance by reducing the number of SQL queries sent to the database'),
                                                                                                       (5, 'In a relational database, which operation is used to extract data from multiple tables?', 'Projection', 'Selection', 'Join', 'Aggregation','Join'),
                                                                                                       (5, 'What is the main purpose of a transaction log in a database?', 'To store backup copies of the database', 'To keep track of all changes made to the database', 'To manage user authentication', 'To store temporary data for faster access', 'To keep track of all changes made to the database');
