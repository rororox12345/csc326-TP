package edu.ncsu.csc326.wolfcafe.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Helps create composite primary id for OrderRecipe.
 */
@Data
@Embeddable
@SuppressWarnings ( "serial" )
public class OrderRecipeId implements Serializable {

    /** Order Id */
    private Long orderId;

    /** Recipe Id */
    private Long recipeId;
}
