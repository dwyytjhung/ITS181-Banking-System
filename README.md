CREATE DATABASE IF NOT EXISTS `bankmsdb`;
USE `bankmsdb`;

-- 1. Create User Data Table
DROP TABLE IF EXISTS `user_data`;
CREATE TABLE `user_data` (
  `id` varchar(255) NOT NULL,
  `username` varchar(255) UNIQUE DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Seed Users (Match your standalone app credentials)
INSERT INTO `user_data` VALUES 
('U1','admin','admin123','ADMIN','System Administrator','admin@prospera.com','123-456-7890'),
('U2','customer1','pass123','CUSTOMER','John Doe','john.doe@email.com','555-0101'),
('U3','customer2','pass123','CUSTOMER','Jane Smith','jane.smith@email.com','555-0202');

-- 2. Create Account Data Table
DROP TABLE IF EXISTS `account_data`;
CREATE TABLE `account_data` (
  `accountNumber` varchar(255) NOT NULL,
  `customerId` varchar(255) DEFAULT NULL,
  `balance` double NOT NULL,
  PRIMARY KEY (`accountNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Seed Accounts
INSERT INTO `account_data` VALUES 
('100010001','U2',5000.0),
('100010002','U3',3000.0);

-- 3. Create Transaction Data Table
DROP TABLE IF EXISTS `transaction_data`;
CREATE TABLE `transaction_data` (
  `id` varchar(255) NOT NULL,
  `accountId` varchar(255) DEFAULT NULL,
  `amount` double NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Seed Initial Deposits
INSERT INTO `transaction_data` VALUES 
('T1','100010001',5000.0,'DEPOSIT','Initial Deposit',NOW()),
('T2','100010002',3000.0,'DEPOSIT','Initial Deposit',NOW());

-- 4. Create Card Request Data Table
DROP TABLE IF EXISTS `card_request_data`;
CREATE TABLE `card_request_data` (
  `id` varchar(255) NOT NULL,
  `customerId` varchar(255) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `requestDate` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Create Notification Data Table
DROP TABLE IF EXISTS `notification_data`;
CREATE TABLE `notification_data` (
  `id` varchar(255) NOT NULL,
  `recipientId` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `message` text,
  `type` varchar(50) DEFAULT NULL,
  `is_read` boolean DEFAULT FALSE,
  `timestamp` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

USE `bankmsdb`;

-- 1. Create Savings Goals Table
DROP TABLE IF EXISTS `savings_goal_data`;
CREATE TABLE `savings_goal_data` (
  `id` varchar(255) NOT NULL,
  `customerId` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `targetAmount` double NOT NULL,
  `currentAmount` double NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `targetDate` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `achievementUnlocked` boolean DEFAULT FALSE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Create Savings Transactions History Table
DROP TABLE IF EXISTS `savings_transaction_data`;
CREATE TABLE `savings_transaction_data` (
  `id` varchar(255) NOT NULL,
  `goalId` varchar(255) DEFAULT NULL,
  `amount` double NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `updatedBalance` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Seed Initial Dummy Goals for Customer 1 (customer1 / pass123)
INSERT INTO `savings_goal_data` VALUES 
('G1','U2','Emergency Fund',10000.0,8200.0,'Rainy day cash reserves','2026-12-31','ACTIVE',FALSE),
('G2','U2','New Laptop',2000.0,2000.0,'New workstation for coding','2026-06-30','COMPLETED',TRUE);

-- 4. Seed Initial Goal Transactions
INSERT INTO `savings_transaction_data` VALUES 
('SG_T1','G1',5000.0,'DEPOSIT',NOW(),5000.0),
('SG_T2','G1',3200.0,'DEPOSIT',NOW(),8200.0),
('SG_T3','G2',2000.0,'DEPOSIT',NOW(),2000.0);
