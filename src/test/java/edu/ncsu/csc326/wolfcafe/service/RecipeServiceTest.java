/**
 *
 */
package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeResponseDto;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;

/**
 * Test class for recipe service.
 */
@SpringBootTest
class RecipeServiceTest {

    /** Reference to recipe service */
    @Autowired
    private RecipeService        recipeService;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /** Reference to ingredient service */
    @Autowired
    private IngredientService    ingredientService;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository     recipeRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        ingredientRepository.deleteAll();
        recipeRepository.deleteAll();
    }

    /**
     * Test method for creating recipe
     */
    @Test
    @Transactional
    void testCreateRecipe () {

        // create new ingredients
        final Long ingredientId1 = ingredientService.createIngredient( new IngredientRequestDto( "Coffee", 50 ) )
                .getId();
        final Long ingredientId2 = ingredientService.createIngredient( new IngredientRequestDto( "Sugar", 50 ) )
                .getId();

        // create recipe request
        final RecipeRequestDto recipeRequestDto1 = new RecipeRequestDto( 0L, "Sugar Coffee", 50,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId1, 5 ) );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId2, 10 ) );

        final RecipeResponseDto recipeResponseDto1 = recipeService.createRecipe( recipeRequestDto1 );

        // ensures the recipe contents are correct based on savedRecipe
        assertAll( "Recipe contents", () -> assertTrue( recipeResponseDto1.getId() >= 1L ),
                () -> assertEquals( "Sugar Coffee", recipeResponseDto1.getName() ),
                () -> assertEquals( 50, recipeResponseDto1.getPrice() ) );

        // ensures the recipe contents are correct based on searching by Id
        final RecipeResponseDto recipeResponseDto2 = recipeService.getRecipeById( recipeResponseDto1.getId() );
        assertAll( "Recipe contents", () -> assertEquals( recipeResponseDto2.getId(), recipeResponseDto1.getId() ),
                () -> assertEquals( "Sugar Coffee", recipeResponseDto2.getName() ),
                () -> assertEquals( 50, recipeResponseDto2.getPrice() ) );

        // ensures the recipe contents are correct based on searching by name
        final RecipeResponseDto recipeResponseDto3 = recipeService.getRecipeById( recipeResponseDto1.getId() );
        assertAll( "Recipe contents", () -> assertEquals( recipeResponseDto3.getId(), recipeResponseDto1.getId() ),
                () -> assertEquals( "Sugar Coffee", recipeResponseDto3.getName() ),
                () -> assertEquals( 50, recipeResponseDto3.getPrice() ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#isDuplicateName(java.lang.String)}.
     */
    @Test
    @Transactional
    void testIsDuplicateName () {

        // create new ingredients
        final Long ingredientId1 = ingredientService.createIngredient( new IngredientRequestDto( "Coffee", 50 ) )
                .getId();
        final Long ingredientId2 = ingredientService.createIngredient( new IngredientRequestDto( "Sugar", 50 ) )
                .getId();

        // create recipe request
        final RecipeRequestDto recipeRequestDto1 = new RecipeRequestDto( 0L, "Joe", 20,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId1, 5 ) );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId2, 10 ) );

        recipeService.createRecipe( recipeRequestDto1 );

        // checks if name already exists
        assertTrue( recipeService.isDuplicateName( "Joe" ) );
        // checks if another name is already present
        assertFalse( recipeService.isDuplicateName( "Hot Chocolate" ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#getAllRecipes()}.
     */
    @Test
    @Transactional
    void testGetAllRecipes () {

        // create new ingredients
        final Long ingredientId1 = ingredientService.createIngredient( new IngredientRequestDto( "Coffee", 50 ) )
                .getId();
        final Long ingredientId2 = ingredientService.createIngredient( new IngredientRequestDto( "Sugar", 50 ) )
                .getId();
        final Long ingredientId3 = ingredientService.createIngredient( new IngredientRequestDto( "Tea", 50 ) ).getId();
        final Long ingredientId4 = ingredientService.createIngredient( new IngredientRequestDto( "Cream", 50 ) )
                .getId();

        // create recipe request
        final RecipeRequestDto recipeRequestDto1 = new RecipeRequestDto( 0L, "Jane", 20,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId1, 5 ) );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId2, 10 ) );

        recipeService.createRecipe( recipeRequestDto1 );

        // create recipe request
        final RecipeRequestDto recipeRequestDto2 = new RecipeRequestDto( 0L, "Jack", 10,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto2.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId3, 11 ) );
        recipeRequestDto2.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId4, 11 ) );

        recipeService.createRecipe( recipeRequestDto1 );

        // ensures size is correct
        assertEquals( 2, recipeService.getAllRecipes().size() );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#updateRecipe(java.lang.Long, edu.ncsu.csc326.coffee_maker.dto.RecipeDto)}.
     */
    @Test
    @Transactional
    void testUpdateRecipe () {

        // create new ingredients
        final Long ingredientId1 = ingredientService.createIngredient( new IngredientRequestDto( "Coffee", 50 ) )
                .getId();
        final Long ingredientId2 = ingredientService.createIngredient( new IngredientRequestDto( "Sugar", 50 ) )
                .getId();
        final Long ingredientId3 = ingredientService.createIngredient( new IngredientRequestDto( "Tea", 50 ) ).getId();
        final Long ingredientId4 = ingredientService.createIngredient( new IngredientRequestDto( "Cream", 50 ) )
                .getId();

        // create recipe request
        final RecipeRequestDto recipeRequestDto1 = new RecipeRequestDto( 0L, "Old", 20,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId1, 5 ) );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId2, 10 ) );

        final Long recipeId = recipeService.createRecipe( recipeRequestDto1 ).getId();

        // create new recipe and update
        final RecipeRequestDto recipeRequestDto2 = new RecipeRequestDto( recipeId, "New", 10,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto2.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId3, 11 ) );
        recipeRequestDto2.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId4, 11 ) );

        final RecipeResponseDto recipeResponseDto = recipeService.updateRecipe( recipeRequestDto2 );

        // ensures the updated values are correct
        assertAll( "Recipe contents", () -> assertEquals( "New", recipeResponseDto.getName() ),
                () -> assertEquals( 10, recipeResponseDto.getPrice() ) );
    }

    /**
     * Test method for
     * {@link edu.ncsu.csc326.coffee_maker.services.RecipeService#deleteRecipe(java.lang.Long)}.
     */
    @Test
    @Transactional
    void testDeleteRecipe () {

        // create new ingredients
        final Long ingredientId1 = ingredientService.createIngredient( new IngredientRequestDto( "Coffee", 50 ) )
                .getId();
        final Long ingredientId2 = ingredientService.createIngredient( new IngredientRequestDto( "Sugar", 50 ) )
                .getId();

        // create recipe request
        final RecipeRequestDto recipeRequestDto = new RecipeRequestDto( 0L, "Special", 20,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId1, 5 ) );
        recipeRequestDto.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId2, 10 ) );

        final Long recipeId = recipeService.createRecipe( recipeRequestDto ).getId();

        assertEquals( 1, recipeService.getAllRecipes().size() );

        // deletes savedRecipe
        recipeService.deleteRecipe( recipeId );

        // ensures size is correct
        assertEquals( 0, recipeService.getAllRecipes().size() );
    }

}
