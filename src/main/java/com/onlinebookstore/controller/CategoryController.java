package com.onlinebookstore.controller;

import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.onlinebookstore.service.BookService;
import com.onlinebookstore.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

@Tag(name = "Categories management", description = "Endpoints for managing categories")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @Operation(summary = "Create a new category",
            description = "Create a new category from request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The category was saved to DB",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request body", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.save(requestDto);
    }

    @Operation(summary = "Get all categories",
            description = "Get a list of all available categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful",
            content = { @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation =
                            CategoryDto.class))) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameters", content = @Content)
    })
    @GetMapping
    public List<CategoryDto> getAll(@ParameterObject Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @Operation(summary = "Get the category by id",
            description = "Get the certain category with identifier from path variable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The category was found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class)) }),
            @ApiResponse(responseCode = "404", description = "The category was not found")
    })
    @GetMapping("/{id}")
    public CategoryDto getCategoryById(@Parameter(description = "category identifier in DB",
            example = "1") @PathVariable Long id) {
        return categoryService.getById(id);
    }

    @Operation(summary = "Update the category by id",
            description = "Update existing category with id from path variable and request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The category was updated in DB",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "The category was not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.update(id, requestDto);
    }

    @Operation(summary = "Delete the category by id",
            description = "Delete existing category with id from path variable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The category was deleted"),
            @ApiResponse(responseCode = "404", description = "The category was not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @Operation(summary = "Get all books by category id",
            description = "Get a list of all available books of the certain category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operation successful",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation =
                                    BookDtoWithoutCategoryIds.class))) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameters", content = @Content)
    })
    @GetMapping("/{id}/books")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@ParameterObject Pageable pageable,
                                                                @PathVariable Long id) {
        return bookService.getAllByCategoriesId(pageable, id);
    }
}
