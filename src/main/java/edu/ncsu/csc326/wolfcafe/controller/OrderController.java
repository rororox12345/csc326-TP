package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.OrderRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderResponseDto;
import edu.ncsu.csc326.wolfcafe.security.JwtTokenProvider;
import edu.ncsu.csc326.wolfcafe.service.AuthService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;

/*** Controller for Order. */

@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/orders" )
public class OrderController {

    /** Connection to OrderService */
    @Autowired
    private OrderService     orderService;

    /** Connection to JwtTokenProvider to get username from token */
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /** Connection to AuthService to retrieve User from username */
    @Autowired
    private AuthService      authService;

    /**
     * REST API method to POST (place) an order.
     *
     * @param orderRequestDto
     *            The order to place
     * @param request
     *            The HttpServletRequest request
     * @return The change
     */
    @PostMapping
    @PreAuthorize ( "hasAnyRole('CUSTOMER', 'GUEST')" )
    public ResponseEntity<Double> placeOrder ( @RequestBody final OrderRequestDto orderRequestDto,
            final HttpServletRequest request ) {

        orderRequestDto.setUserId( getUserIdFromRequest( request ) );

        final Double total = orderService.getTotal( orderRequestDto );
        final Double amountPaid = orderRequestDto.getAmountPaid();

        // check sufficient payment
        if ( amountPaid < total ) {
            return new ResponseEntity<>( amountPaid, HttpStatus.PAYMENT_REQUIRED );
        }

        orderService.placeOrder( orderRequestDto );

        // return change
        return new ResponseEntity<>( amountPaid - total, HttpStatus.CREATED );
    }

    /**
     * REST API method to GET all order for self (customer).
     *
     * @param request
     *            The HttpServletRequest request
     * @return The list of order history for User
     */
    @GetMapping ( "/customer" )
    @PreAuthorize ( "hasRole('CUSTOMER')" )
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersByUser ( final HttpServletRequest request ) {
        return ResponseEntity.ok( orderService.getAllOrdersByUser( getUserIdFromRequest( request ) ) );
    }

    /**
     * Gets the user id of the user making the api call. Enhances security.
     *
     * @param request
     *            The HttpServletRequest request
     * @return The id corresponding to the user bearing auth token
     */
    private Long getUserIdFromRequest ( final HttpServletRequest request ) {
        String bearerToken = request.getHeader( "Authorization" );
        bearerToken = bearerToken.substring( 7, bearerToken.length() );
        final String username = jwtTokenProvider.getUsername( bearerToken );

        return authService.getUserIdFromUsername( username );
    }

    /**
     * returns all the orders
     *
     * @return ok if all orders are successfully gotten
     */
    @GetMapping ( "/staff" )
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<List<OrderResponseDto>> getAllOrders () {
        return ResponseEntity.ok( orderService.getAllOrders() );

    }

    /**
     * fulfills the order
     *
     * @param orderId
     *            id of the order
     * @return success if fulfilled
     */
    @PutMapping ( "/{id}/fulfill" )
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<Void> fulfillOrder ( @PathVariable ( "id" ) final Long orderId ) {
        if ( !orderService.fulfillOrder( orderId ) ) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * sets order to pickup order
     *
     * @param orderId
     *            id of the order
     * @return success if fulfilled
     */
    @PutMapping ( "/{id}/pickup" )
    @PreAuthorize ( "hasRole('CUSTOMER')" )
    public ResponseEntity<Void> pickupOrder ( @PathVariable ( "id" ) final Long orderId ) {
        if ( !orderService.pickupOrder( orderId ) ) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

}
