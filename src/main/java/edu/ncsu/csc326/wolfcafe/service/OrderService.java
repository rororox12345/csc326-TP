package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.OrderRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderResponseDto;

/**
 * Interface for the customer orders
 */
public interface OrderService {

    /**
     * Place order method
     *
     * @param orderRequestDto
     *            the order the user creates
     */
    void placeOrder ( OrderRequestDto orderRequestDto );

    /**
     * returns the order history of a user by their id
     *
     * @param userId
     *            the id of the user
     * @return list of all orders in a users history
     */
    List<OrderResponseDto> getAllOrdersByUser ( Long userId );

    /**
     * returns the total of an order
     *
     * @param orderRequestDto
     *            order request dto
     * @return total of the order
     */
    Double getTotal ( OrderRequestDto orderRequestDto );

    /**
     * changes the order status to fulfilled
     *
     * @param orderId
     *            id of the order
     * @return true if the order was successfully fulfilled, false if there is
     *         insufficient inventory
     */
    public boolean fulfillOrder ( final Long orderId );

    /**
     * changes the order status to picked up
     *
     * @param orderId
     *            id of the order
     * @return true if the order is successfully set to pickup
     */
    public boolean pickupOrder ( final long orderId );

    /**
     * Returns all the orders in the system
     *
     * @return list of orders
     */
    List<OrderResponseDto> getAllOrders ();

}
