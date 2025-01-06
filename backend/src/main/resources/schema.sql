DROP TABLE IF EXISTS merchants;
CREATE TABLE merchants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    business_type VARCHAR(255),
    contact_name VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    description TEXT,
    business_license VARCHAR(255) NOT NULL,
    license_url VARCHAR(1000),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME,
    updated_at DATETIME
);

DROP TABLE IF EXISTS operation_logs;
CREATE TABLE operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    operation VARCHAR(255) NOT NULL,
    method VARCHAR(50) NOT NULL,
    params TEXT,
    result TEXT,
    ip VARCHAR(255),
    create_time DATETIME NOT NULL
);

DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(255) UNIQUE,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    created_at DATETIME,
    updated_at DATETIME,
    last_login_at DATETIME
);
