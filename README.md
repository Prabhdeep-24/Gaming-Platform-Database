JDBC Project - Gaming Platform Database Integration
This project provides a simple Java application designed to interact with a MySQL database, specifically for a gaming platform. It demonstrates basic JDBC (Java Database Connectivity) operations and includes setup instructions for both the database and the application itself.

Project Structure
Your project directory should be structured as follows:

JDBC PROJECT/
├── out/                        # Compiled Java classes
│   ├── Main.class
│   └── Queries.class
├── src/                        # Source code and configuration
│   ├── db.properties           # Database connection configuration
│   ├── Main.java               # Main application entry point
│   |── Queries.java            # Database query handling (if applicable)
|   ├── JDBC_Project.jar            # The compiled and executable JAR file
|   ├── manifest.txt                # Manifest file for JAR metadata (if building a runnable JAR)
|   └── mysql-connector-j-9.0.0.jar # MySQL JDBC Driver (Version 9.0.0)
└── database_setup.sql          # SQL script to set up the database schema

Getting Started
Follow these steps to quickly set up the database, configure the project, and run the application on your local machine.

1. Clone the Repository
Begin by cloning this GitHub repository to your local system:

git clone <your-repository-url>
cd <your-repository-name> # Example: cd JDBC_Project

2. Database Setup
This project requires a MySQL database. Use the database_setup.sql script to create the necessary gamingPlatform database and its tables.

Steps:

Ensure MySQL Server is Running: Confirm that your MySQL database server is actively running on localhost:3306.

Access MySQL: Open your MySQL command-line client (e.g., mysql.exe in your MySQL installation's bin folder) or a GUI tool like MySQL Workbench.

Execute the SQL File: Run the database_setup.sql script to create your database schema.

mysql -u your_mysql_username -p < database_setup.sql
# Replace 'your_mysql_username' with your actual MySQL username (e.g., 'root').
# You will be prompted to enter your MySQL password.

Alternatively, you can manually copy and paste the contents of database_setup.sql into your MySQL client and execute it.

3. Database Configuration (db.properties)
The application connects to your MySQL database using the credentials and connection string defined in src/db.properties.

File: src/db.properties content:

# Database connection URL (adjust 'localhost' and '3306' if your server is different)
db.url=jdbc:mysql://localhost:3306/gamingPlatform?useSSL=false&serverTimezone=UTC

# Database username
db.user=Enter your UserName

# Database password
db.password=Enter your Password

# JDBC driver class name
db.driver=com.mysql.cj.jdbc.Driver

4. Build and Run the Application
This project can be executed directly from your compiled classes, ensuring the necessary JDBC driver is on the classpath.

Navigate to the project root: Open your terminal or PowerShell and change your directory to the JDBC PROJECT root folder. This is where out/, src/, mysql-connector-j-9.0.0.jar, and Main.java are located.

Compile Java sources (if you've made changes):
javac -d out src/*.java

If you've modified any .java files, recompile them, using the above command.

Run the application using explicit classpath:
java -cp ".;mysql-connector-j-9.0.0.jar;out" Main

This command tells Java where to find your compiled classes (out directory) and the MySQL JDBC driver (mysql-connector-j-9.0.0.jar).

Includes the current directory in the classpath.

mysql-connector-j-9.0.0.jar: Explicitly adds the JDBC driver to the classpath.
