DROP DATABASE IF EXISTS DEP_master;

CREATE DATABASE DEP_master;

USE DEP_master;

DROP TABLE IF EXISTS student;

CREATE TABLE student (
     id   VARCHAR(6) PRIMARY KEY ,
     name VARCHAR(50) NOT NULL,
     contact VARCHAR(50) NOT NULL
 );

INSERT INTO student VALUES ('C001', 'Dulanga', '');
INSERT INTO student VALUES ('C002', 'Nuwan', '');

# ALTER TABLE student ADD COLUMN contact VARCHAR(50);

DROP TABLE IF EXISTS contact;

CREATE TABLE contact(
        student_id VARCHAR(6) NOT NULL,
        contact VARCHAR(15) NOT NULL,
        CONSTRAINT PRIMARY KEY ( student_id, contact ),
        CONSTRAINT fk_contact FOREIGN KEY (student_id) REFERENCES student(id)
);

INSERT INTO contact VALUES ( 'C001', '0772851828');
INSERT INTO contact VALUES ( 'C001', '0912255470');
INSERT INTO contact VALUES ( 'C002', '0714463394');
