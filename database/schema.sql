CREATE TABLE books (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    published_year INTEGER CHECK (published_year IS NULL OR published_year BETWEEN 0 AND 3000),
    genre TEXT NOT NULL DEFAULT '',
    is_read INTEGER NOT NULL DEFAULT 0 CHECK (is_read IN (0, 1))
);

CREATE INDEX idx_books_title_author
ON books(title COLLATE NOCASE, author COLLATE NOCASE);
