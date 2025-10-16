package edu.ncsu.csc326.wolfcafe.service;

import java.util.List;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;

/**
 * Interface defining the inventory behaviors.
 */
public interface InventoryService {

    /**
     * Returns the inventory.
     *
     * @return the inventory
     */
    List<IngredientAssociationResponseDto> getInventory ();

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryRequestDto
     *            values to update
     * @return updated inventory
     */
    List<IngredientAssociationResponseDto> updateInventory ( List<IngredientAssociationRequestDto> inventoryRequestDto );

}
