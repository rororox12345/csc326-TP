package edu.ncsu.csc326.wolfcafe.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Helps create composite primary id for OrderItem.
 */
@Data
@Embeddable
@SuppressWarnings ( "serial" )
public class OrderItemId implements Serializable {

    /** Order Id */
    private Long orderId;

    /** Item Id */
    private Long itemId;
}
