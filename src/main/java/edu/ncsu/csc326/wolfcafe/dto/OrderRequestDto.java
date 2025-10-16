package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to transfer order and associated recipe/item data between the client and
 * server. This class will serve as the request in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    /** Id of the User (customer) associated with order */
    private Long                        userId;

    /** The tip in dollars */
    private Double                      tip;

    /** The amount paid */
    private Double                      amountPaid;

    /** Associated order recipes */
    private List<OrderRecipeRequestDto> orderRecipeRequestList;

    /** Associated order items */
    private List<OrderItemRequestDto>   orderItemRequestList;

}
