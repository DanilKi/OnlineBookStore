INSERT INTO books (id, title, author, isbn, price)
VALUES
(1, 'Sample Book 1', 'Author A', '9781234567897', 19.99),
(2, 'Sample Book 2', 'Author B', '9789876543210', 24.99),
(3, 'Sample Book 3', 'Author C', '9781122334455', 29.99);

INSERT INTO categories(id, name)
VALUES
(1, 'Sample Category 1'),
(2, 'Sample Category 2');

INSERT INTO book_category(book_id, category_id)
VALUES
(1, 1),
(2, 1),
(3, 2);
