package edu.ncsu.csc326.wolfcafe.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientResponseDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.InventoryIngredient;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryIngredientRepository;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;

/**
 * Implementation of the RecipeService interface.
 */
@Service
public class IngredientServiceImpl implements IngredientService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryIngredientRepository inventoryIngredientRepository;

    /**
     * Creates an ingredient with the given information.
     *
     * @param ingredientRequestDto
     *            ingredient to create
     * @return created ingredient
     */
    @Override
    public IngredientResponseDto createIngredient ( final IngredientRequestDto ingredientRequestDto ) {

        final Ingredient ingredient = ingredientRepository.save( new Ingredient( 0L, ingredientRequestDto.getName() ) );
        addInventoryIngredient( ingredient.getId(), ingredientRequestDto.getInitialQuantity() );

        return new IngredientResponseDto( ingredient.getId(), ingredient.getName() );
    }

    /**
     * Adds ingredient to inventory
     *
     * @param id
     *            the Ingredient id to add
     * @param quantity
     *            the quantity to add
     */
    private void addInventoryIngredient ( final Long id, final int quantity ) {
        inventoryIngredientRepository.save( new InventoryIngredient( id, quantity ) );
    }

    /**
     * Returns true if the ingredient already exists in the database.
     *
     * @param name
     *            ingredient's name to check
     * @return true if already in the database
     */
    @Override
    public boolean isDuplicateName ( final String name ) {

        return ingredientRepository.findByName( name ).isPresent();

    }

}
