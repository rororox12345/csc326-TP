package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeResponseDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;

/**
 * Interface defining the recipe behaviors.
 */
public interface RecipeService {

    /**
     * Creates a recipe with the given information.
     *
     * @param recipeRequestDto
     *            recipe to create
     * @return created recipe
     */
    RecipeResponseDto createRecipe ( RecipeRequestDto recipeRequestDto );

    /**
     * Returns the recipe with the given id.
     *
     * @param recipeId
     *            recipe's id
     * @return the recipe with the given id
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    RecipeResponseDto getRecipeById ( Long recipeId );

    /**
     * Returns the recipe with the given name
     *
     * @param recipeName
     *            recipe's name
     * @return the recipe with the given name.
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    RecipeResponseDto getRecipeByName ( String recipeName );

    /**
     * Returns true if the recipe already exists in the database.
     *
     * @param recipeName
     *            recipe's name to check
     * @return true if already in the database
     */
    boolean isDuplicateName ( String recipeName );

    /**
     * Returns a list of all the recipes
     *
     * @return all the recipes
     */
    List<RecipeResponseDto> getAllRecipes ();

    /**
     * Updates the recipe with the given id with the recipe information
     *
     * @param recipeRequestDto
     *            values to update
     * @return updated recipe
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    RecipeResponseDto updateRecipe ( RecipeRequestDto recipeRequestDto );

    /**
     * Deletes the recipe with the given id
     *
     * @param recipeId
     *            recipe's id
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    void deleteRecipe ( Long recipeId );

}
