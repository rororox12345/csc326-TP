package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to transfer order and associated recipe/item data between the client and
 * server. This class will serve as the response in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    /** Order id */
    private Long                         orderId;

    /** Id of the User (customer) associated with order */
    private Long                         userId;

    /** username of the User associated with order */
    private String                       username;

    /** The tip in dollars */
    private Double                       tip;

    /** The total with tip and tax in dollars */
    private Double                       total;

    /** order status */
    private OrderStatus                  orderStatus;

    /** Associated order recipes */
    private List<OrderRecipeResponseDto> orderRecipeResponseList;

    /** Associated order items */
    private List<OrderItemResponseDto>   orderItemResponseList;
}
