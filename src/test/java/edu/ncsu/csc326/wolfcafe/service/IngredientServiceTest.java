/**
 *
 */
package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientResponseDto;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryIngredientRepository;

/**
 * Test class for ingredient service.
 */
@SpringBootTest
class IngredientServiceTest {

    /** Reference to ingredient service */
    @Autowired
    private IngredientService             ingredientService;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /** Reference to inventory ingredient repository */
    @Autowired
    private InventoryIngredientRepository inventoryIngredientRepository;

    /**
     * Clear repositories.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        ingredientRepository.deleteAll();
        inventoryIngredientRepository.deleteAll();
    }

    /**
     * Test method for creating ingredient.
     */
    @Test
    @Transactional
    void testCreateIngredient () {

        // creates a new ingredient
        final IngredientRequestDto ingredientRequestDto = new IngredientRequestDto( "Coffee", 10 );
        final IngredientResponseDto ingredientResponseDto = ingredientService.createIngredient( ingredientRequestDto );

        // ensures the ingredient contents are correct based on savedIngredient
        assertAll( "Ingredient contents", () -> assertEquals( "Coffee", ingredientResponseDto.getName() ) );

        // ensures the inventory ingredient contents are correct
        assertAll( "Inventory Ingredient contents",
                () -> assertEquals( 1, inventoryIngredientRepository.findAll().size() ),
                () -> assertEquals( ingredientResponseDto.getId(),
                        inventoryIngredientRepository.findAll().get( 0 ).getId() ),
                () -> assertEquals( 10, inventoryIngredientRepository.findAll().get( 0 ).getQuantity() ) );
    }

    /**
     * Test method for checking duplicate name.
     */
    @Test
    @Transactional
    void testIsDuplicateName () {

        // creates a new ingredient
        final IngredientRequestDto ingredientRequestDto = new IngredientRequestDto( "Coffee", 10 );
        ingredientService.createIngredient( ingredientRequestDto );

        // checks if name already exists
        assertTrue( ingredientService.isDuplicateName( "Coffee" ) );
        // checks if another name is already present
        assertFalse( ingredientService.isDuplicateName( "Hot Chocolate" ) );
    }

}
