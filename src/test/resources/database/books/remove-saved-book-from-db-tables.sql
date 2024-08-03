DELETE FROM book_category WHERE book_id IN (SELECT id FROM books WHERE title = 'Sample Book 4');
DELETE FROM books WHERE title = 'Sample Book 4';
