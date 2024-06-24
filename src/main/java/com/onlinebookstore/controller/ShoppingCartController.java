package com.onlinebookstore.controller;

import com.onlinebookstore.dto.shoppingcart.CreateCartItemRequestDto;
import com.onlinebookstore.dto.shoppingcart.ShoppingCartDto;
import com.onlinebookstore.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping cart items")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Add a new item to the shopping cart",
            description = "Create a new item in the shopping cart from request body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "An item was added to the shopping cart successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartDto.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "The book was not found in DB", content = @Content),
            @ApiResponse(responseCode = "409",
                    description = "The book is already present in the shopping cart",
                    content = @Content)
    })
    @PostMapping
    public ShoppingCartDto addItemToShoppingCart(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        return shoppingCartService.addCartItem(requestDto, userDetails.getUsername());
    }

    @Operation(summary = "Get the user's shopping cart",
            description = "Get the shopping cart of the authenticated user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The shopping cart was found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartDto.class)) }),
            @ApiResponse(responseCode = "404", description = "The shopping cart was not found",
                    content = @Content)
    })
    @GetMapping
    public ShoppingCartDto getShoppingCartByUserName(@AuthenticationPrincipal
                                                         UserDetails userDetails) {
        return shoppingCartService.getByUser(userDetails.getUsername());
    }

    @Operation(summary = "Update cart item by id",
            description = "Update cart item with id from path variable in user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The cart item was updated in DB",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShoppingCartDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter or request body"),
            @ApiResponse(responseCode = "404", description = "The cart item was not found")
    })
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartDto updateItemInShoppingCart(@Parameter(description = "cart item id in DB",
            example = "1") @PathVariable Long cartItemId,
                                        @RequestBody @Valid CreateCartItemRequestDto requestDto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return shoppingCartService.updateCartItemById(cartItemId, requestDto,
                userDetails.getUsername());
    }

    @Operation(summary = "Delete cart item by id",
            description = "Delete cart item with id from path variable from user's shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The cart item was deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid parameter"),
            @ApiResponse(responseCode = "404", description = "The cart item was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{cartItemId}")
    public void deleteItemFromShoppingCart(@Parameter(description = "cart item identifier in DB",
            example = "1") @PathVariable Long cartItemId,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        shoppingCartService.removeCartItemById(cartItemId, userDetails.getUsername());
    }
}
