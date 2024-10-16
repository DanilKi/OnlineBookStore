# Online Book Store
A simple online bookstore written in Java using the Spring framework.  
RESTful application that uses REST API architecture for communication via HTTP requests between the server and the client to access and use data, which is transmitted in JSON format.

## Project description
### In this app we have the following domain models (entities):
* User: Contains information about the registered user including their authentication details and personal information.
* Role: Represents the role of a user in the system, for example, admin or user.
* Book: Represents a book available in the store.
* Category: Represents a category that a book can belong to.
* ShoppingCart: Represents a user's shopping cart.
* CartItem: Represents an item in a user's shopping cart.
* Order: Represents an order placed by a user.
* OrderItem: Represents an item in a user's order.

People involved:
1. Shopper (User): Someone who looks at books, puts them in a basket (shopping cart), and buys them.
2. Manager (Admin): Someone who arranges the books on the shelf and watches what gets bought.

### Things Shoppers Can Do:
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

### Things Managers Can Do:
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

## Technologies and tools
- Source code: Java 17
- Framework: Spring (Boot, Security, Data JPA, Web MVC)
- Database: MySQL, Hibernate, Liquibase
- Testing: JUnit, Mockito, TestContainers
- Utility: Lombok, MapStruct
- Project build: Apache Maven
- Documenting, Front view: Swagger
- Deployment: Docker

## Configuration and launch
To set up and run the project, please do the following:
1. Download or clone the project from GitHub repository.
2. Install and run Docker Desktop.
3. Set the necessary settings in *.env* file. You can use *.env.example* file as a hint.
4. Open the console and navigate to the project's root folder.
5. Run *docker-compose up* command in the terminal to build the containers and start the application.
6. Use Swagger to test the application's functionality. For example, open URL: *http://localhost:8088/api/swagger-ui/index.html* in a web browser. Or you may use another software (like 'Postman' or similar) for this.
7. You can run *docker-compose down* to shut it down after, when needed.  

It's the easiest way. But you can alternatively, instead of Docker, install JDK17 (or higher), Maven 3.0 (or higher), MySQL Server 8.0 (or higher), with all the necessary system environment variables set and necessary settings in *application.properties* file provided, navigate to project's main directory and run *mvn spring-boot:run* command from the console. And that's it.  

There is a predefined user with admin role for testing purposes. You can log in with the following credentials:  
> login: admin@gmail.com | pass: admin1234  

## API Endpoints
Authentication is required to access endpoints. Only user registration and login endpoints are available without authentication.  
Authentication is obtained by receiving a JWT token, which is then used for authorization when interacting with other endpoints of the web application.  
To test app's functionality using Swagger you should provide a JWT token, received as a result of login, by clicking on the green button 'Authorize' on the Swagger's main page. Using other software (e.g. Postman etc) you should also define obtained token in its appropriate properties.  
  

### Book Endpoints:

- GET: */api/books* (Retrieve book catalog)  
Example of response body:
```json
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
```

- GET: */api/books/{id}* (Retrieve book details)  
Example of response body:
```json
{
"id": 1,
"title": "Sample Book 1",
"author": "Author A",
"isbn": "9781234567897",
"price": 19.99,
"description": "This is a sample book description.",
"coverImage": "http://example.com/cover1.jpg"
}
```

- GET: */api/books/search* (Filter book catalog)  
search?params:  
```code
titles=[title1,titleN]&authors=[name1,nameN]&isbn=[%value%]&priceFrom=[decimal]&priceTo=[decimal]
```

- POST: */api/books* (Create a new book)  
Example of request body:
```json
{
"title": "Sample Book 3",
"author": "Author C",
"isbn": "9781122334455",
"price": 29.99,
"description": "Yet another sample book description.",
"coverImage": "http://example.com/cover3.jpg"
}
```

- PUT: */api/books/{id}* (Update a specific book)  
Example of request body:
```json
{
"title": "Updated Title",
"author": "Updated Author",
"isbn": "978-1234567890",
"price": 19.99,
"description": "Updated description",
"coverImage": "https://example.com/updated-cover-image.jpg"
}
```

- DELETE: */api/books/{id}* (Delete a specific book)

### User Endpoints:

- POST: */api/auth/register* (User registration)  
Example of request body:
```json
{
"email": "john.doe@example.com",
"password": "securePassword123",
"repeatPassword": "securePassword123",
"firstName": "John",
"lastName": "Doe",
"shippingAddress": "123 Main St, City, Country"
}
```

Example of response body:
```json
{
"id": 1,
"email": "john.doe@example.com",
"firstName": "John",
"lastName": "Doe",
"shippingAddress": "123 Main St, City, Country"
}
```

- POST: */api/auth/login* (User authentication)  
Example of request body:
```json
{
"email": "john.doe@example.com",
"password": "securePassword123"
}
```

Example of response body:
```json
{
"token": "eyJhbGciOiJJ9.eyJzdWIiOiIxMjM0yfQ.SflKssw5c"
}
```

### Category endpoints:

- POST: */api/categories* (Create a new category)  
Example of request body:
```json
{
"name": "Fiction",
"description": "Fiction books"
}
```

- GET: */api/categories* (Retrieve all categories)  
Example of response body:
```json
{
"id": 1,
"name": "Fiction",
"description": "Fiction books"
}
```

- GET: */api/categories/{id}* (Retrieve a specific category by its ID)

- PUT: */api/categories/{id}* (Update a specific category)  
Example of request body:
```json
{
"name": "Fiction",
"description": "Fiction books"
}
```

- DELETE: */api/categories/{id}* (Delete a specific category)

- GET: */api/categories/{id}/books* (Retrieve books by a specific category)

### Shopping Cart Endpoints:

- GET: */api/cart* (Retrieve user's shopping cart)  
Example of response body:
```json
{
"id": 123,
"userId": 456,
"cartItems": [
{
"id": 1,
"bookId": 789,
"bookTitle": "Sample Book 1",
"quantity": 2
},
{
"id": 2,
"bookId": 790,
"bookTitle": "Sample Book 2",
"quantity": 1
}
]
}
```

- POST: */api/cart* (Add book to the shopping cart)  
Example of request body:
```json
{
"bookId": 2,
"quantity": 5
}
```

- PUT: */api/cart/items/{cartItemId}* (Update quantity of a book in the shopping cart)  
Example of request body:
```json
{
"quantity": 10
}
```

- DELETE: */api/cart/items/{cartItemId}* (Remove a book from the shopping cart)

### Order Endpoints:

- POST: */api/orders* (Place an order)  
Example of request body:
```json
{
"shippingAddress": "Kyiv, Shevchenko ave, 1"
}
```

- GET: */api/orders* (Retrieve user's order history)  
Example of response body:
```json
[
{
"id": 101,
"userId": 456,
"orderItems": [
{
"id": 1,
"bookId": 789,
"quantity": 2
},
{
"id": 2,
"bookId": 790,
"quantity": 1
}
],
"orderDate": "2023-07-25T10:15:30",
"total": 29.98,
"status": "COMPLETED"
},
{
"id": 102,
"userId": 456,
"orderItems": [
{
"id": 3,
"bookId": 791,
"quantity": 1
}
],
"orderDate": "2023-07-23T15:20:45",
"total": 14.99,
"status": "PENDING"
}
]
```

- PATCH: */api/orders/{id}* (Update order status)  
Example of request body:
```json
{
"status": "DELIVERED"
}
```

### OrderItem Endpoints: 

- GET: */api/orders/{orderId}/items* (Retrieve all OrderItems for a specific order)  
Example of response body:
```json
[
{
"id": 1,
"bookId": 789,
"quantity": 2
},
{
"id": 2,
"bookId": 790,
"quantity": 1
}
]
```

- GET: */api/orders/{orderId}/items/{itemId}* (Retrieve a specific OrderItem within an order)  
Example of response body:
```json
{
"id": 2,
"bookId": 790,
"quantity": 1
}
```

- POST, PUT, and DELETE endpoints for OrderItem are not necessary, as OrderItems are typically created, updated, 
or removed when a user interacts with their shopping cart or places an order.

