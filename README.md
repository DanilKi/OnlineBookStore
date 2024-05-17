# OnlineBookStore
A simple online bookstore written in Java using the Spring framework

Project description
In this app we will have the following domain models (entities):
User: Contains information about the registered user including their authentication details and personal information.
Role: Represents the role of a user in the system, for example, admin or user.
Book: Represents a book available in the store.
Category: Represents a category that a book can belong to.
ShoppingCart: Represents a user's shopping cart.
CartItem: Represents an item in a user's shopping cart.
Order: Represents an order placed by a user.
OrderItem: Represents an item in a user's order.

People involved:
1. Shopper (User): Someone who looks at books, puts them in a basket (shopping cart), and buys them.
2. Manager (Admin): Someone who arranges the books on the shelf and watches what gets bought.

Things Shoppers Can Do:
1. Join and sign in:
   - Join the store.
   - Sign in to look at books and buy them.
2. Look at and search for books:
   - Look at all the books.
   - Look closely at one book.
   - Find a book by typing its name.
3. Look at bookshelf sections:
   - See all bookshelf sections.
   - See all books in one section.
4. Use the basket:
   - Put a book in the basket.
   - Look inside the basket.
   - Take a book out of the basket.
5. Buying books:
   - Buy all the books in the basket.
   - Look at past receipts.
6. Look at receipts:
   - See all books on one receipt.
   - Look closely at one book on a receipt.
Things Managers Can Do:
1. Arrange books:
   - Add a new book to the store.
   - Change details of a book.
   - Remove a book from the store.
2. Organize bookshelf sections:
   - Make a new bookshelf section.
   - Change details of a section.
   - Remove a section.
3. Look at and change receipts:
   - Change the status of a receipt, like "Shipped" or "Delivered".


Endpoints
Book Endpoints:

GET: /api/books (Retrieve book catalog)
Example of response body:
[
{
"id": 1,
"title": "Sample Book 1",
"author": "Author A",
"isbn": "9781234567897",
"price": 19.99,
"description": "This is a sample book description.",
"coverImage": "http://example.com/cover1.jpg"
},
{
"id": 2,
"title": "Sample Book 2",
"author": "Author B",
"isbn": "9789876543210",
"price": 24.99,
"description": "Another sample book description.",
"coverImage": "http://example.com/cover2.jpg"
}
]

GET: /api/books/{id} (Retrieve book details)
Example of response body:
{
"id": 1,
"title": "Sample Book 1",
"author": "Author A",
"isbn": "9781234567897",
"price": 19.99,
"description": "This is a sample book description.",
"coverImage": "http://example.com/cover1.jpg"
}

GET: /api/books/search (Filter book catalog)
search?params:
titles=[title1,titleN]&authors=[name2,nameN]&isbn=[%value%]&priceFrom=[decimal]$priceTo=[decimal]

POST: /api/books (Create a new book)
Example of request body:
{
"title": "Sample Book 3",
"author": "Author C",
"isbn": "9781122334455",
"price": 29.99,
"description": "Yet another sample book description.",
"coverImage": "http://example.com/cover3.jpg"
}

PUT: /api/books/{id} (Update a specific book)
Example of request body:
{
"title": "Updated Title",
"author": "Updated Author",
"isbn": "978-1234567890",
"price": 19.99,
"description": "Updated description",
"coverImage": "https://example.com/updated-cover-image.jpg"
}

DELETE: /api/books/{id} (Delete a specific book)
