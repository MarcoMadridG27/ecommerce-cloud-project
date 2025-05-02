CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    phonenumber VARCHAR(20),
    email VARCHAR(100) UNIQUE,
    age INT
);

CREATE TABLE Addresses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    address_line VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE Notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    message TEXT,
    timestamp DATETIME,
    `read` BOOLEAN,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE SupportTickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    subject VARCHAR(255),
    description TEXT,
    status ENUM('open', 'in_progress', 'resolved') DEFAULT 'open',
    created_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);
