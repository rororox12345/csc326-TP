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
 * RecipeIngredient for wolf cafe. Relates recipes and ingredients with
 * quantity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "recipe_ingredients" )
public class RecipeIngredient {

    /** Composite primary id */
    @EmbeddedId
    RecipeIngredientId id;

    /** Recipe id */
    @ManyToOne
    @MapsId ( "recipeId" )
    @JoinColumn ( name = "recipe_id", nullable = false )
    private Recipe     recipe;

    /** Ingredient id */
    @ManyToOne
    @MapsId ( "ingredientId" )
    @JoinColumn ( name = "ingredient_id", nullable = false )
    private Ingredient ingredient;

    /** Quantity */
    @Column ( nullable = false )
    private int        quantity;

}
