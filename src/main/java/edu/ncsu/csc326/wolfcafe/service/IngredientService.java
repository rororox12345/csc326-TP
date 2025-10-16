package edu.ncsu.csc326.wolfcafe.service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientResponseDto;

/**
 * Interface defining the ingredient behaviors.
 */
public interface IngredientService {

    /**
     * Creates an ingredient with the given information.
     *
     * @param ingredientRequestDto
     *            ingredient to create
     * @return created ingredient
     */
    IngredientResponseDto createIngredient ( IngredientRequestDto ingredientRequestDto );

    /**
     * Returns true if the ingredient already exists in the database.
     *
     * @param name
     *            ingredient's name to check
     * @return true if already in the database
     */
    boolean isDuplicateName ( String name );

}
