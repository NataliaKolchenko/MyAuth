--drop table if exists app_users;

CREATE TABLE IF NOT EXISTS app_user (
    email VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    role INT NOT NULL
);