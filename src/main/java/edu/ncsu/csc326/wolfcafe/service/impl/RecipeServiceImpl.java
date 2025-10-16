package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeResponseDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;
import edu.ncsu.csc326.wolfcafe.entity.RecipeIngredient;
import edu.ncsu.csc326.wolfcafe.entity.RecipeIngredientId;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;

/**
 * Implementation of the RecipeService interface.
 */
@Service
public class RecipeServiceImpl implements RecipeService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private RecipeRepository     recipeRepository;

    /** Reference to ingredient repository for validation purposes */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Creates a recipe with the given information.
     *
     * @param recipeRequestDto
     *            recipe to create
     * @return created recipe
     */
    @Override
    public RecipeResponseDto createRecipe ( final RecipeRequestDto recipeRequestDto ) {

        final List<IngredientAssociationRequestDto> recipeIngredientRequestList = recipeRequestDto
                .getRecipeIngredientsRequestList();

        // start constructing Recipe
        final Recipe recipe = new Recipe();
        recipe.setName( recipeRequestDto.getName() );
        recipe.setPrice( recipeRequestDto.getPrice() );
        recipe.setRecipeIngredientList( new ArrayList<RecipeIngredient>() );

        // validate and add row by row
        for ( final IngredientAssociationRequestDto recipeIngredientRequestDto : recipeIngredientRequestList ) {

            final Long ingredientId = recipeIngredientRequestDto.getId();
            final int quantity = recipeIngredientRequestDto.getQuantity();

            // validate ingredient
            final Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                    () -> new ResourceNotFoundException( "Ingredient id" + ingredientId + " does not exist" ) );

            // validate quantity
            if ( quantity < 0 ) {
                throw new IllegalArgumentException(
                        "Ingredient id " + ingredientId + " quantity for Recipe cannot have a negative value." );
            }

            final RecipeIngredient recipeIngredient = new RecipeIngredient( new RecipeIngredientId(), recipe,
                    ingredient, quantity );

            recipe.getRecipeIngredientList().add( recipeIngredient );

        }

        final Long recipeId = recipeRepository.save( recipe ).getId();

        return recipeToResponseDto( recipeId, recipe );
    }

    /**
     * Returns the recipe with the given id.
     *
     * @param recipeId
     *            recipe's id
     * @return the recipe with the given id
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public RecipeResponseDto getRecipeById ( final Long recipeId ) {

        // find recipe
        final Recipe recipe = recipeRepository.findById( recipeId )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with id " + recipeId ) );

        return recipeToResponseDto( recipe.getId(), recipe );
    }

    /**
     * Returns the recipe with the given name
     *
     * @param recipeName
     *            recipe's name
     * @return the recipe with the given name.
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public RecipeResponseDto getRecipeByName ( final String recipeName ) {
        // find recipe
        final Recipe recipe = recipeRepository.findByName( recipeName )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with name " + recipeName ) );

        return recipeToResponseDto( recipe.getId(), recipe );
    }

    /**
     * Returns true if the recipe already exists in the database.
     *
     * @param recipeName
     *            recipe's name to check
     * @return true if already in the database
     */
    @Override
    public boolean isDuplicateName ( final String recipeName ) {
        try {
            getRecipeByName( recipeName );
            return true;
        }
        catch ( final ResourceNotFoundException e ) {
            return false;
        }
    }

    /**
     * Returns a list of all the recipes
     *
     * @return all the recipes
     */
    @Override
    public List<RecipeResponseDto> getAllRecipes () {

        final List<Recipe> recipes = recipeRepository.findAll();
        final List<RecipeResponseDto> recipeResponseList = new ArrayList<>();

        // add response for each recipe
        for ( final Recipe recipe : recipes ) {

            recipeResponseList.add( recipeToResponseDto( recipe.getId(), recipe ) );
        }

        return recipeResponseList;

    }

    /**
     * Updates the recipe with the given id with the recipe information
     *
     * @param recipeRequestDto
     *            recipe to update
     * @return updated recipe
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public RecipeResponseDto updateRecipe ( final RecipeRequestDto recipeRequestDto ) {

        final List<IngredientAssociationRequestDto> recipeIngredientRequestList = recipeRequestDto
                .getRecipeIngredientsRequestList();
        final List<RecipeIngredient> recipeIngredientList = new ArrayList<>();

        final Recipe recipe = recipeRepository.findById( recipeRequestDto.getId() ).orElseThrow(
                () -> new ResourceNotFoundException( "Recipe does not exist with id " + recipeRequestDto.getId() ) );

        // validate and add row by row
        for ( final IngredientAssociationRequestDto recipeIngredientRequestDto : recipeIngredientRequestList ) {

            final Long ingredientId = recipeIngredientRequestDto.getId();
            final int quantity = recipeIngredientRequestDto.getQuantity();

            // validate ingredient
            final Ingredient ingredient = ingredientRepository.findById( ingredientId ).orElseThrow(
                    () -> new ResourceNotFoundException( "Ingredient id" + ingredientId + " does not exist" ) );

            // validate quantity
            if ( quantity < 0 ) {
                throw new IllegalArgumentException(
                        "Ingredient id " + ingredientId + " quantity for Recipe cannot have a negative value." );
            }

            final RecipeIngredient recipeIngredient = new RecipeIngredient( new RecipeIngredientId(), recipe,
                    ingredient, quantity );

            recipeIngredientList.add( recipeIngredient );

        }

        recipe.setName( recipeRequestDto.getName() );
        recipe.setPrice( recipeRequestDto.getPrice() );
        recipe.getRecipeIngredientList().clear();
        recipeRepository.save( recipe );

        for ( final RecipeIngredient recipeIngredient : recipeIngredientList ) {
            recipe.getRecipeIngredientList().add( recipeIngredient );
        }

        final Long recipeId = recipeRepository.save( recipe ).getId();

        return recipeToResponseDto( recipeId, recipe );

    }

    /**
     * Deletes the recipe with the given id
     *
     * @param recipeId
     *            recipe's id
     * @throws ResourceNotFoundException
     *             if the recipe doesn't exist
     */
    @Override
    public void deleteRecipe ( final Long recipeId ) {
        final Recipe recipe = recipeRepository.findById( recipeId )
                .orElseThrow( () -> new ResourceNotFoundException( "Recipe does not exist with id " + recipeId ) );

        recipeRepository.delete( recipe );
    }

    /**
     * Helps map entity to Dto
     *
     * @param recipeId
     *            The id of recipe
     * @param recipe
     *            The recipe enity
     * @return Recipe Response Dto
     */
    private RecipeResponseDto recipeToResponseDto ( final Long recipeId, final Recipe recipe ) {

        // create and set main fields
        final RecipeResponseDto recipeResponseDto = new RecipeResponseDto();
        recipeResponseDto.setId( recipeId );
        recipeResponseDto.setName( recipe.getName() );
        recipeResponseDto.setPrice( recipe.getPrice() );

        // set ingredient information
        recipeResponseDto
                .setRecipeIngredientsResponseList( recipe.getRecipeIngredientList().stream().map( recipeIngredient -> {
                    final IngredientAssociationResponseDto ingredientAssociationResponseDto = new IngredientAssociationResponseDto();
                    ingredientAssociationResponseDto.setId( recipeIngredient.getIngredient().getId() );
                    ingredientAssociationResponseDto.setName( recipeIngredient.getIngredient().getName() );
                    ingredientAssociationResponseDto.setQuantity( recipeIngredient.getQuantity() );
                    return ingredientAssociationResponseDto;
                } ).collect( Collectors.toList() ) );

        return recipeResponseDto;
    }

}
