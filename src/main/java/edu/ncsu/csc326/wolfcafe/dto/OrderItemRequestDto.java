package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to transfer associated order item data between the client and server.
 * This class will serve as the request in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequestDto {

    /** Item id */
    private Long    itemId;

    /** Quantity ordered */
    private Integer quantity;
}
