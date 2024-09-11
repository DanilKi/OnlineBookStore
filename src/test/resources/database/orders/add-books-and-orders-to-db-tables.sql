INSERT INTO books (id, title, author, isbn, price)
VALUES
(1, 'Sample Book 1', 'Author A', '9781234567897', 19.99),
(2, 'Sample Book 2', 'Author B', '9789876543210', 24.99),
(3, 'Sample Book 3', 'Author C', '9781122334455', 29.99);

INSERT INTO orders (id, user_id, status, total, order_date, shipping_address)
VALUES
(1, 2, 'SENT', 144.94, '2024-07-03 13:02:20', 'Kyiv, Main str, 10'),
(2, 2, 'PENDING', 29.99, '2024-07-04 12:03:05', 'Kyiv, Main str, 10');

INSERT INTO order_items (id, order_id, book_id, quantity, price)
VALUES
(1, 1, 1, 1, 19.99),
(2, 1, 2, 5, 124.95),
(3, 2, 3, 1, 29.99);
