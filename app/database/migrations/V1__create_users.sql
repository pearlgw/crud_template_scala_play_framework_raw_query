CREATE TABLE users
(
    id         CHAR(36) PRIMARY KEY,
    nama       VARCHAR(100) NOT NULL,
    no_telepon VARCHAR(20),
    alamat     TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);