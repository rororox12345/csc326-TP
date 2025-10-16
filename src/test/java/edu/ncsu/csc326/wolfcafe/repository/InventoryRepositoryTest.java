package edu.ncsu.csc326.wolfcafe.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.InventoryIngredient;

/**
 * Tests InventoryRepository. Uses the real database - not an embedded one.
 */
@DataJpaTest
@AutoConfigureTestDatabase ( replace = Replace.NONE )
public class InventoryRepositoryTest {

    /** Reference to inventory repository */
    @Autowired
    private InventoryIngredientRepository inventoryIngredientRepository;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /** Reference to ingredient stored */
    private Ingredient                    ingredient;

    /**
     * Sets up the test case. We assume only one inventory row.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {

        ingredientRepository.deleteAll();
        inventoryIngredientRepository.deleteAll();

        ingredient = ingredientRepository.save( new Ingredient( 0L, "Coffee" ) );
        inventoryIngredientRepository.save( new InventoryIngredient( ingredient.getId(), 10 ) );

    }

    /**
     * Test saving the inventory and retrieving from the repository.
     */
    @Test
    public void testSaveAndGetInventory () {

        final InventoryIngredient fetchedInventoryIngredient = inventoryIngredientRepository.findAll().get( 0 );
        assertAll( "Inventory contents", () -> assertEquals( ingredient.getId(), fetchedInventoryIngredient.getId() ),
                () -> assertEquals( 10, fetchedInventoryIngredient.getQuantity() ) );
    }

}
