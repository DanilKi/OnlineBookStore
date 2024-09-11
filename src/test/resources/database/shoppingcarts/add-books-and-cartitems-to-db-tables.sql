INSERT INTO books (id, title, author, isbn, price)
VALUES
(1, 'Sample Book 1', 'Author A', '9781234567897', 19.99),
(2, 'Sample Book 2', 'Author B', '9789876543210', 24.99),
(3, 'Sample Book 3', 'Author C', '9781122334455', 29.99);

INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity)
VALUES
(1, 2, 1, 1),
(2, 2, 2, 2),
(3, 1, 3, 1);
