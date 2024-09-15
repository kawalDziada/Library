CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    isbn VARCHAR(255),
    name VARCHAR(255),
    author VARCHAR(255),
    pageNumber INT,
    publishDate DATE,
    isAvailable BOOLEAN,
    borrowedBy UUID REFERENCES users(id)
);
