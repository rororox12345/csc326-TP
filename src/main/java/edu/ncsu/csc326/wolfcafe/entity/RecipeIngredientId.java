package edu.ncsu.csc326.wolfcafe.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

/**
 * Helps create composite primary id for RecipeIngredient.
 */
@Data
@Embeddable
@SuppressWarnings ( "serial" )
public class RecipeIngredientId implements Serializable {

    /** Recipe Id */
    private Long recipeId;

    /** Ingredient Id */
    private Long ingredientId;
}
