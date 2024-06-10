-- liquibase formatted sql

-- table author creation
CREATE TABLE author 
(
	author_id BIGINT AUTO_INCREMENT NOT NULL, 
	first_name VARCHAR(32) NOT NULL, 
	last_name VARCHAR(32) NOT NULL, 
	middle_name VARCHAR(32), 
	pen_name VARCHAR(64), 
	PRIMARY KEY (author_id))
)
-- table book creation
CREATE TABLE book 
(
	book_id BIGINT AUTO_INCREMENT NOT NULL, 
	description TEXT, 
	original_language ENUM('ENGLISH', 'SPANISH', 'RUSSIAN'), 
	original_title VARCHAR(64) NOT NULL, 
	PRIMARY KEY (book_id))
)
-- table book_author creation
CREATE TABLE book_author 
(
	book_id BIGINT NOT NULL, 
	author_id BIGINT NOT NULL, 
	PRIMARY KEY (book_id, author_id))
	FOREIGN KEY (`author_id`) REFERENCES `author` (`author_id`),
	FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`);
	)

-- table copies creation

CREATE TABLE copies 
	(
	copy_id BIGINT AUTO_INCREMENT NOT NULL, 
	language ENUM('ENGLISH', 'SPANISH', 'RUSSIAN') NOT NULL, 
	price DECIMAL(10, 2) NOT NULL, 
	publishing_house VARCHAR(128), 
	publishing_year YEAR(4), 
	title VARCHAR(64) NOT NULL, 
	translator VARCHAR(64), 
	book_id BIGINT NOT NULL, 
	PRIMARY KEY (copy_id)),
	FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`);
	)
