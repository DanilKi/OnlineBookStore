package com.onlinebookstore.controller;

import com.onlinebookstore.dto.order.CreateOrderRequestDto;
import com.onlinebookstore.dto.order.OrderDto;
import com.onlinebookstore.dto.order.OrderItemDto;
import com.onlinebookstore.dto.order.UpdateOrderRequestDto;
import com.onlinebookstore.service.OrderService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place a new order",
            description = "Create a new order using items from the shopping cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "An order was placed successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class)) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Items were not found in the shopping cart", content = @Content)
    })
    @PostMapping
    public OrderDto createOrder(@RequestBody @Valid CreateOrderRequestDto requestDto,
                                @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.save(requestDto, userDetails.getUsername());
    }

    @Operation(summary = "Get the user's orders",
            description = "Get the list of orders of the authenticated user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders were found successfully",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation =
                                    OrderDto.class))) }),
            @ApiResponse(responseCode = "400",
                    description = "Invalid parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "The user was not found",
                    content = @Content)
    })
    @GetMapping
    public List<OrderDto> getAllOrdersByUser(@ParameterObject Pageable pageable,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findAllByUser(pageable, userDetails.getUsername());
    }

    @Operation(summary = "Get all items the order contains",
            description = "Get the list of items containing in the specific order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items were found successfully",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation =
                                    OrderItemDto.class))) }),
            @ApiResponse(responseCode = "404", description = "The order was not found",
                    content = @Content)
    })
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getAllItemsInOrder(@Parameter(description = "order identifier in DB",
                                                    example = "1") @PathVariable Long orderId,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findOrderItemsByOrderId(orderId, userDetails.getUsername());
    }

    @Operation(summary = "Get order item of a certain order",
            description = "Get the particular item of a specific order "
                    + "with identifiers from path variables")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The order item was found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderItemDto.class)) }),
            @ApiResponse(responseCode = "404", description = "The order item was not found")
    })
    @GetMapping("/{orderId}/items/{id}")
    public OrderItemDto getItemInOrderById(@Parameter(description = "order identifier in DB",
                                            example = "1") @PathVariable Long orderId,
                                           @Parameter(description = "order item identifier in DB",
                                            example = "1") @PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.findOrderItemById(orderId, id, userDetails.getUsername());
    }

    @Operation(summary = "Update order by id",
            description = "Update status of order with identifier from path variable")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The order status was updated in DB",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid parameter or request body"),
            @ApiResponse(responseCode = "404", description = "The order was not found in DB")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public OrderDto updateOrder(@RequestBody @Valid UpdateOrderRequestDto requestDto,
                                @Parameter(description = "order identifier in DB", example = "1")
                                @PathVariable Long id) {
        return orderService.update(id, requestDto);
    }
}
