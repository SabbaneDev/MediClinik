-- SQL Script: Creation and Initialization of the Database
-- Course: Java Programming Mini-Project
-- Instructor: Pr. Soufiane HAMIDA
-- Subject: 07. Gestion de clinique (patients, rendez-vous)

CREATE DATABASE IF NOT EXISTS clinic_management_db;
USE clinic_management_db;

-- -----------------------------------------------------
-- Table `patients`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS patients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    gender VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `appointments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS appointments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    appointment_date DATETIME NOT NULL,
    patient_id INT NOT NULL,
    doctor_name VARCHAR(100) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient 
        FOREIGN KEY (patient_id) REFERENCES patients(id) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Sample Seed Data (For Testing and Demonstrations)
-- -----------------------------------------------------
INSERT INTO patients (first_name, last_name, age, phone_number, gender) VALUES
('Jean', 'Dupont', 45, '0612345678', 'Male'),
('Marie', 'Curie', 32, '0798765432', 'Female'),
('Ahmed', 'Benali', 28, '0533445566', 'Male');

INSERT INTO appointments (appointment_date, patient_id, doctor_name, description) VALUES
(NOW() + INTERVAL 1 DAY, 1, 'Dr. Sarah Martin', 'Annual checkup and cardiovascular scan'),
(NOW() + INTERVAL 2 DAY, 2, 'Dr. Pierre Dubois', 'Follow-up consultation for blood tests'),
(NOW() + INTERVAL 3 DAY, 3, 'Dr. Sarah Martin', 'Dental emergency and cleaning');
