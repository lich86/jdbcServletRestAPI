-- liquibase formatted sql
ALTER DROP table IF EXISTS
-- changeset User:1718035475845-1
CREATE TABLE author (author_id BIGINT AUTO_INCREMENT NOT NULL, first_name VARCHAR(32) NOT NULL, last_name VARCHAR(32) NOT NULL, middle_name VARCHAR(32) NULL, pen_name VARCHAR(64) NULL, CONSTRAINT PK_AUTHOR PRIMARY KEY (author_id));

-- changeset User:1718035475845-2
CREATE TABLE book (book_id BIGINT AUTO_INCREMENT NOT NULL, `description` TEXT NULL, original_language ENUM('ENGLISH', 'SPANISH', 'RUSSIAN') NULL, original_title VARCHAR(64) NOT NULL, CONSTRAINT PK_BOOK PRIMARY KEY (book_id));

-- changeset User:1718035475845-3
CREATE TABLE book_author (book_id BIGINT NOT NULL, author_id BIGINT NOT NULL, CONSTRAINT PK_BOOK_AUTHOR PRIMARY KEY (book_id, author_id));

-- changeset User:1718035475845-4
CREATE TABLE copies (copy_id BIGINT AUTO_INCREMENT NOT NULL, language ENUM('ENGLISH', 'SPANISH', 'RUSSIAN') NOT NULL, price DECIMAL(10, 2) NOT NULL, publishing_house VARCHAR(128) NULL, publishing_year YEAR(4) NULL, title VARCHAR(64) NOT NULL, translator VARCHAR(64) NULL, book_id BIGINT NOT NULL, CONSTRAINT PK_COPIES PRIMARY KEY (copy_id));

-- changeset User:1718035475845-5
CREATE INDEX FKbjqhp85wjv8vpr0beygh6jsgo ON book_author(author_id);

-- changeset User:1718035475845-6
CREATE INDEX FKehypmeqyjco3f5bf9axaumc6k ON copies(book_id);

-- changeset User:1718035475845-7
ALTER TABLE book_author ADD CONSTRAINT FKbjqhp85wjv8vpr0beygh6jsgo FOREIGN KEY (author_id) REFERENCES author (author_id) ON UPDATE CASCADE ON DELETE RESTRICT;

-- changeset User:1718035475845-8
ALTER TABLE copies ADD CONSTRAINT FKehypmeqyjco3f5bf9axaumc6k FOREIGN KEY (book_id) REFERENCES book (book_id) ON UPDATE CASCADE ON DELETE RESTRICT;

-- changeset User:1718035475845-9
ALTER TABLE book_author ADD CONSTRAINT FKhwgu59n9o80xv75plf9ggj7xn FOREIGN KEY (book_id) REFERENCES book (book_id) ON UPDATE CASCADE ON DELETE RESTRICT;



--populate authors

INSERT INTO `author` VALUES (1, 'Уильям','Моэм','Сомерсет',NULL), (2, 'Аркадий','Стругацкий','Натанович',NULL), (3, 'Борис','Стругацкий','Натанович',NULL), (4, 'Джон','Рональд Руэл','Толкин',NULL), (5, 'Габриэль','Гарсиа','Маркес',NULL), (6, 'Кеннет','Кизи','Элтон','Кен Кизи'), (7, 'Сэмюэл','Ленгхорн ','Клеменс','Марк Твен');



--populate books

INSERT INTO `book` VALUES (1, 'Роман британского писателя У. С. Моэма (1925). Название позаимствовано из сонета Перси Биши Шелли, который начинается со слов: «Lift not the painted veil which those who live / Call Life», что в переводе на русский: «О, не приподнимай покров узорный, который люди жизнью называют».', 'ENGLISH', 'The Painted Veil'), (2, 'Роман английского писателя Сомерсета Моэма, впервые опубликованный в 1937 году.', 'ENGLISH', 'Theatre'), (3, 'Роман представляет собой биографию вымышленного персонажа Чарльза Стрикленда, английского биржевого маклера, который в сорокалетнем возрасте внезапно бросает жену и детей, чтобы стать художником. Прообразом Чарльза Стрикленда послужил Поль Гоген.', 'ENGLISH', 'The Moon and Sixpence'), (4, 'Философский роман (часто называемый антиутопией) Аркадия и Бориса Стругацких. Писался «в стол», когда братья оказались в состоянии мировоззренческого кризиса, а затем были резко ограничены в возможностях публиковаться.', 'RUSSIAN', 'Град обреченный'), (5, 'Научно-фантастическая повесть братьев Стругацких. Содержит жёсткую социальную сатиру, доходящую до гротеска[3], но главным смысловым пластом является нравственно-философский.', 'RUSSIAN', 'Улитка на склоне'), (6, 'Философская фантастическая повесть братьев Стругацких, впервые изданная в 1972 году. Повесть лидирует среди прочих произведений авторов по количеству переводов на иностранные языки и изданиям за пределами бывшего СССР.', 'RUSSIAN', 'Пикник на обочине'), (7, 'Роман в жанре эпического фэнтези за авторством английского писателя Джона Рональда Руэла Толкина, третья часть трилогии «Властелин колец».', 'ENGLISH', 'The Lord of the Rings:The Return of the King'), (8, 'Роман в жанре эпического фэнтези за авторством английского писателя Джона Рональда Руэла Толкина, первая часть трилогии «Властелин колец».', 'ENGLISH', 'The Lord of the Rings:The Fellowship of the Ring'), (9, 'Роман в жанре эпического фэнтези за авторством английского писателя Джона Рональда Руэла Толкина, вторая часть трилогии «Властелин колец».', 'ENGLISH', 'The Lord of the Rings:The Two Towers'), (10, 'Роман колумбийского писателя Габриэля Гарсиа Маркеса. Произведение представляет собой обобщённую историю о катастрофических последствиях концентрации власти в руках одного человека.', 'SPANISH', 'El otoño del patriarca'), (11, 'Роман колумбийского писателя Габриэля Гарсиа Маркеса, одно из наиболее характерных и популярных произведений в направлении магического реализма.', 'SPANISH', 'Cien años de soledad'), (12, 'Роман Габриэля Гарсиа Маркеса, впервые опубликованный на испанском языке в 1985 году.', 'SPANISH', 'Love in the Time of Cholera'), (13, 'Роман Кена Кизи (1962). Журнал Time включил этот роман в свой список 100 лучших англоязычных произведений с 1923 по 2005 год', 'ENGLISH', 'One Flew Over the Cuckoo\'s Nest'), (14, 'Вышедшая в 1876 году повесть Марка Твена о приключениях мальчика, живущего в небольшом американском городке Сент-Питерсберг (Санкт-Петербург) в штате Миссури.', 'ENGLISH', 'The Adventures of Tom Sawyer'), (15, 'Роман Марка Твена. Продолжение одной из сюжетных линий романа «Приключения Тома Сойера»', 'ENGLISH', 'The Adventures of Huckleberry Finn');



--populate many-to-many relationship table

INSERT INTO `book_author` VALUES (1, 1), (2, 1), (3, 1), (4, 2), (5, 2), (6, 2), (4, 3), (5, 3), (6, 3), (7, 4), (8, 4), (9, 4), (10, 5), (11, 5), (12, 5), (13, 6), (14, 7), (15, 7);

--populate copies



INSERT INTO `copies` VALUES (1, 'RUSSIAN', 400.00, 'АСТ', 2022, 'Узорный покров', 'Мария Лорие', 1), (2, 'RUSSIAN', 250.00, 'АСТ', 2022, 'Узорный покров', 'Мария Лорие', 1), (3, 'ENGLISH', 750.00, 'КАРО', 2009, 'The Painted Veil', NULL, 1), (4, 'RUSSIAN', 400.00, 'АСТ', 2018, 'Театр', 'Галина Островская', 2), (5, 'RUSSIAN', 300.00, 'АСТ, Харвест', 2013, 'Луна и грош', 'Наталия Ман', 3), (6, 'RUSSIAN', 330.00, 'АСТ, Neoclassic', 2022, 'Луна и шесть пенсов', 'В. Бернацкая', 3), (7, 'RUSSIAN', 260.00, 'АСТ', 2021, 'Град обреченный', NULL, 4), (8, 'RUSSIAN', 300.00, 'Говорящая книга, Вокруг света', 2006, 'Улитка на склоне', NULL, 5), (9, 'RUSSIAN', 500.00, 'Вокруг света', 2006, 'Пикник на обочине', '', 6), (10, 'ENGLISH', 800.00, 'Penguin Books', 1979, 'Roadside Picnic', 'Antonina W. Bouis', 6), (11, 'RUSSIAN', 400.00, 'АСТ, Mainstream', 2018, 'Властелин Колец. Часть 1. Братство Кольца', 'В. Грушецкий, Н. Григорьева', 8), (12, 'RUSSIAN', 300.00, 'Эксмо, Яуза', 2003, 'Хранители', 'А. Кистяковский, В. Муравьёв', 8);