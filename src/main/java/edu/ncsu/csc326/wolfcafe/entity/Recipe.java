package edu.ncsu.csc326.wolfcafe.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recipe for the coffee maker. Recipe is a Data Access Object (DAO) is tied to
 * the database using Hibernate libraries. RecipeRepository provides the methods
 * for database CRUD operations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "recipes" )
public class Recipe {

    /** Recipe id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long                   id;

    /** Recipe name */
    private String                 name;

    /** Recipe price */
    private Integer                price;

    /** Associated recipe ingredients */
    @OneToMany ( mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true )
    private List<RecipeIngredient> recipeIngredientList;

}
