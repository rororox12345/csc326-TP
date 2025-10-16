package edu.ncsu.csc326.wolfcafe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderItem for wolf cafe. Relates orders and items with quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "order_items" )
public class OrderItem {

    /** Composite primary id */
    @EmbeddedId
    OrderItemId     id;

    /** Order id */
    @ManyToOne
    @MapsId ( "orderId" )
    @JoinColumn ( name = "order_id", nullable = false )
    private Order   order;

    /** Item id */
    @ManyToOne
    @MapsId ( "itemId" )
    @JoinColumn ( name = "item_id", nullable = false )
    private Item    item;

    /** Quantity */
    @Column ( nullable = false )
    private Integer quantity;

}
