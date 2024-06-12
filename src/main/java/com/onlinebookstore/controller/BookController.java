package com.onlinebookstore.controller;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import com.onlinebookstore.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @Operation(summary = "Get all books", description = "Get a list of all available books")
    @ApiResponse(responseCode = "200", description = "Operation successful")
    @GetMapping
    public List<BookDto> getAll(@ParameterObject Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @Operation(summary = "Get the book by id",
            description = "Get the book with identifier from path variable")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "The book was found",
                content = { @Content(mediaType = "application/json",
                        schema = @Schema(implementation = BookDto.class)) }),
        @ApiResponse(responseCode = "404", description = "The book was not found")
    })
    @GetMapping("/{id}")
    public BookDto getBookById(@Parameter(description = "book identifier in DB", example = "1")
                                   @PathVariable Long id) {
        return bookService.findById(id);
    }

    @Operation(summary = "Create a new book", description = "Create a new book from request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The book was saved to DB"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }

    @Operation(summary = "Update the book by id",
            description = "Update existing book with id from path variable and request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The book was updated in DB"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "The book was not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public BookDto updateBook(@RequestBody @Valid CreateBookRequestDto requestDto,
                              @PathVariable Long id) {
        return bookService.update(requestDto, id);
    }

    @Operation(summary = "Delete the book by id",
            description = "Delete existing book with id from path variable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The book was deleted"),
            @ApiResponse(responseCode = "404", description = "The book was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @Operation(summary = "Get certain books",
            description = "Get books corresponding to specified parameters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search successful"),
            @ApiResponse(responseCode = "400", description = "Incorrect parameters")
    })
    @GetMapping("/search")
    public List<BookDto> searchBooks(@ParameterObject
                                         @Valid BookSearchParametersDto searchParameters) {
        return bookService.search(searchParameters);
    }
}
