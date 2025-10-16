/**
 *
 */
package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;

/**
 * Tests Ingredient repository
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
class IngredientRepositoryTest {

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
        ingredientRepository.deleteAll();

        final Ingredient ingredient1 = new Ingredient( 1L, "Coffee" );
        final Ingredient ingredient2 = new Ingredient( 2L, "Sugar" );

        ingredientRepository.save( ingredient1 );
        ingredientRepository.save( ingredient2 );
    }

    @Test
    public void testGetIngredientByName () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "Coffee" );
        final Ingredient actualIngredient = ingredient.get();
        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", actualIngredient.getName() ) );

        final Optional<Ingredient> newIngredient = ingredientRepository.findByName( "Sugar" );
        final Ingredient latteIngredient = newIngredient.get();
        assertEquals( "Sugar", latteIngredient.getName() );
    }

    @Test
    public void testGetIngredientByNameInvalid () {
        final Optional<Ingredient> ingredient = ingredientRepository.findByName( "Unknown" );
        assertTrue( ingredient.isEmpty() );
    }

}
