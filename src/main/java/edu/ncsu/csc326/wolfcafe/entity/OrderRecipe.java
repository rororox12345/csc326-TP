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
 * OrderRecipe for wolf cafe. Relates orders and recipes with quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "order_recipes" )
public class OrderRecipe {

    /** Composite primary id */
    @EmbeddedId
    OrderRecipeId   id;

    /** Order id */
    @ManyToOne
    @MapsId ( "orderId" )
    @JoinColumn ( name = "order_id", nullable = false )
    private Order   order;

    /** Recipe id */
    @ManyToOne
    @MapsId ( "recipeId" )
    @JoinColumn ( name = "recipe_id", nullable = false )
    private Recipe  recipe;

    /** Quantity */
    @Column ( nullable = false )
    private Integer quantity;

}
