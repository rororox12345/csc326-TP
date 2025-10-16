
package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.entity.Ingredient;
import edu.ncsu.csc326.wolfcafe.entity.InventoryIngredient;
import edu.ncsu.csc326.wolfcafe.exception.ResourceNotFoundException;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryIngredientRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    /** Connection to the repository to work with the DAO + database */
    @Autowired
    private InventoryIngredientRepository inventoryIngredientRepository;

    /** ingredient service object */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /**
     * Returns the inventory.
     *
     * @return the inventory
     */
    @Override
    public List<IngredientAssociationResponseDto> getInventory () {

        // get list of inventory items
        final List<InventoryIngredient> inventoryIngredientList = inventoryIngredientRepository.findAll();

        // map each inventory item to a response DTO
        return inventoryIngredientList.stream().map( inventoryIngredient -> {
            final IngredientAssociationResponseDto inventoryResponseDto = new IngredientAssociationResponseDto();
            inventoryResponseDto.setId( inventoryIngredient.getId() );
            inventoryResponseDto
                    .setName(
                            ingredientRepository.findById( inventoryIngredient.getId() )
                                    .orElseThrow( () -> new ResourceNotFoundException(
                                            "Ingredient id" + inventoryIngredient.getId() + " does not exist" ) )
                                    .getName() );
            inventoryResponseDto.setQuantity( inventoryIngredient.getQuantity() );
            return inventoryResponseDto;
        } ).collect( Collectors.toList() );
    }

    /**
     * Updates the contents of the inventory.
     *
     * @param inventoryRequestDtoList
     *            values to update
     * @return updated inventory
     */
    @Override
    public List<IngredientAssociationResponseDto> updateInventory (
            final List<IngredientAssociationRequestDto> inventoryRequestDtoList ) {

        final List<InventoryIngredient> inventoryIngredientList = inventoryIngredientRepository.findAll();

        // inventory must not be empty
        if ( inventoryIngredientList.size() == 0 ) {
            throw new ResourceNotFoundException( "Inventory does not contain anything." );
        }

        // inventory update must contain all ingredients
        if ( inventoryRequestDtoList.size() != inventoryIngredientList.size() ) {
            throw new IllegalArgumentException(
                    "Update inventory request must contain the same number of ingredients as original." );
        }

        final List<IngredientAssociationResponseDto> inventoryResponseDtoList = new ArrayList<>();

        // iterating over request list to validate
        for ( int i = 0; i < inventoryRequestDtoList.size(); i++ ) {

            final Long id = inventoryRequestDtoList.get( i ).getId();
            final int quantity = inventoryRequestDtoList.get( i ).getQuantity();

            // check ingredient id
            final Ingredient ingredient = ingredientRepository.findById( id )
                    .orElseThrow( () -> new ResourceNotFoundException( "Ingredient id " + id + " does not exist" ) );

            // check quantity
            if ( quantity < 0 ) {
                throw new IllegalArgumentException(
                        "InventoryIngredient id " + id + " quantity cannot have a negative value." );
            }

            // start building response DTO
            inventoryResponseDtoList
                    .add( new IngredientAssociationResponseDto( ingredient.getId(), ingredient.getName(), quantity ) );

        }

        // iterate to save changes post validation
        for ( int i = 0; i < inventoryRequestDtoList.size(); i++ ) {

            final Long id = inventoryRequestDtoList.get( i ).getId();
            final int quantity = inventoryRequestDtoList.get( i ).getQuantity();

            // save updated quantity
            final InventoryIngredient inventoryIngredient = inventoryIngredientRepository.findById( id ).get();
            inventoryIngredient.setQuantity( quantity );
            inventoryIngredientRepository.save( inventoryIngredient );
        }

        return inventoryResponseDtoList;
    }

}
