/**
 *
 */
package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;
import edu.ncsu.csc326.wolfcafe.entity.RecipeIngredient;
import edu.ncsu.csc326.wolfcafe.entity.RecipeIngredientId;

/**
 * Tests Recipe repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class RecipeRepositoryTest {

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository     recipeRepository;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();

        final Ingredient ingredient1 = ingredientRepository.save( new Ingredient( 0L, "Sugar" ) );
        final Ingredient ingredient2 = ingredientRepository.save( new Ingredient( 0L, "Coffee" ) );
        final Ingredient ingredient3 = ingredientRepository.save( new Ingredient( 0L, "Tea" ) );
        final Ingredient ingredient4 = ingredientRepository.save( new Ingredient( 0L, "Cream" ) );

        final Recipe recipe1 = new Recipe();
        recipe1.setName( "Coffee" );
        recipe1.setPrice( 50 );
        final List<RecipeIngredient> recipeIngredient1 = new ArrayList<>();
        recipeIngredient1.add( new RecipeIngredient( new RecipeIngredientId(), new Recipe(), ingredient1, 5 ) );
        recipeIngredient1.add( new RecipeIngredient( new RecipeIngredientId(), new Recipe(), ingredient2, 4 ) );
        recipe1.setRecipeIngredientList( recipeIngredient1 );

        final Recipe recipe2 = new Recipe();
        recipe2.setName( "Latte" );
        recipe2.setPrice( 100 );
        final List<RecipeIngredient> recipeIngredient2 = new ArrayList<>();
        recipeIngredient2.add( new RecipeIngredient( new RecipeIngredientId(), new Recipe(), ingredient3, 2 ) );
        recipeIngredient2.add( new RecipeIngredient( new RecipeIngredientId(), new Recipe(), ingredient4, 3 ) );
        recipe2.setRecipeIngredientList( recipeIngredient2 );

        recipeRepository.save( recipe1 );
        recipeRepository.save( recipe2 );
    }

    /**
     * Test for getting a recipe with its name
     */
    @Test
    public void testGetRecipeByName () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Coffee" );
        final Recipe actualRecipe = recipe.get();
        assertAll( "Recipe contents", () -> assertEquals( "Coffee", actualRecipe.getName() ),
                () -> assertEquals( 50, actualRecipe.getPrice() ) );

        final Optional<Recipe> newRecipe = recipeRepository.findByName( "Latte" );
        final Recipe latteRecipe = newRecipe.get();
        assertEquals( "Latte", latteRecipe.getName() );
        assertEquals( 100, latteRecipe.getPrice() );
        latteRecipe.setPrice( 10 );
        assertEquals( 10, latteRecipe.getPrice() );
    }

    /**
     * Test method for getting a recipe with an invalid name
     */
    @Test
    public void testGetRecipeByNameInvalid () {
        final Optional<Recipe> recipe = recipeRepository.findByName( "Unknown" );
        assertTrue( recipe.isEmpty() );
    }

}
