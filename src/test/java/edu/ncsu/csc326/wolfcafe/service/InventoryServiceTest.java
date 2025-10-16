package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryIngredientRepository;

/**
 * Tests InventoryServiceImpl.
 */
@SpringBootTest
public class InventoryServiceTest {

    /** Reference to InventoryService (and InventoryServiceImpl). */
    @Autowired
    private InventoryService              inventoryService;

    /** Reference to ingredient service */
    @Autowired
    private IngredientService             ingredientService;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /** Reference to InventoryIngredientRepository */
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
     * Tests InventoryService.getInventory()
     */
    @Test
    @Transactional
    public void testGetInventory () {

        final List<IngredientAssociationResponseDto> inventoryResponseDtoList = inventoryService.getInventory();
        assertEquals( 0, inventoryResponseDtoList.size() );
    }

    /**
     * Tests InventoryService.updateInventory()
     */
    @Test
    @Transactional
    public void testUpdateInventory () {

        // try update when no ingredients added yey
        assertThrows( ResourceNotFoundException.class,
                () -> inventoryService.updateInventory( new ArrayList<IngredientAssociationRequestDto>() ) );

        final IngredientResponseDto ingredientResponseDto1 = ingredientService
                .createIngredient( new IngredientRequestDto( "Coffee", 5 ) );
        final IngredientResponseDto ingredientResponseDto2 = ingredientService
                .createIngredient( new IngredientRequestDto( "Sugar", 6 ) );

        final List<IngredientAssociationRequestDto> inventoryRequestDtoList = new ArrayList<>();
        inventoryRequestDtoList.add( new IngredientAssociationRequestDto( ingredientResponseDto1.getId(), 10 ) );
        inventoryRequestDtoList.add( new IngredientAssociationRequestDto( ingredientResponseDto2.getId(), 15 ) );

        final List<IngredientAssociationResponseDto> inventoryResponseDto = inventoryService
                .updateInventory( inventoryRequestDtoList );

        assertAll( "Inventory contents", () -> assertEquals( 2, inventoryResponseDto.size() ),
                () -> assertEquals( "Coffee", inventoryResponseDto.get( 0 ).getName() ),
                () -> assertEquals( "Sugar", inventoryResponseDto.get( 1 ).getName() ),
                () -> assertEquals( 10, inventoryResponseDto.get( 0 ).getQuantity() ),
                () -> assertEquals( 15, inventoryResponseDto.get( 1 ).getQuantity() ) );

        // empty request
        assertThrows( IllegalArgumentException.class,
                () -> inventoryService.updateInventory( new ArrayList<IngredientAssociationRequestDto>() ) );
        inventoryRequestDtoList.remove( 0 );

    }
}
