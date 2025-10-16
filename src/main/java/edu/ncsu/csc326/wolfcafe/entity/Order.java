package edu.ncsu.csc326.wolfcafe.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order for the coffee maker. Order is a Data Access Object (DAO) is tied to
 * the database using Hibernate libraries. OrderRepository provides the methods
 * for database CRUD operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "orders" )
public class Order {

    /** Order id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long              id;

    /** User (customer id */
    @ManyToOne
    @JoinColumn ( name = "user_id", nullable = false )
    private User              user;

    /** Tip in dollars */
    private Double            tip;

    /** Total with tip and taxes in dollars */
    private Double            total;

    /** Order status of order */
    private OrderStatus       orderStatus;

    /** Associated order recipes */
    @OneToMany ( mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<OrderRecipe> orderRecipeList;

    /** Associated order items */
    @OneToMany ( mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<OrderItem>   orderItemList;
}
