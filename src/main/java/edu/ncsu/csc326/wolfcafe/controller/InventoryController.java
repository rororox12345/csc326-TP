package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;

/**
 * Controller for CoffeeMaker's inventory. The inventory is a singleton; there's
 * only one row in the database that contains the current inventory for the
 * system.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/inventory" )
public class InventoryController {

    /**
     * Connection to inventory service for manipulating the Inventory model.
     */
    @Autowired
    private InventoryService inventoryService;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @return response to the request
     */
    @GetMapping
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<List<IngredientAssociationResponseDto>> getInventory () {
        final List<IngredientAssociationResponseDto> inventoryResponseDtoList = inventoryService.getInventory();
        return ResponseEntity.ok( inventoryResponseDtoList );
    }

    /**
     * REST API endpoint to provide update access to the CoffeeMaker's singleton
     * Inventory.
     *
     * @param inventoryRequestDtoList
     *            amounts to add to inventory
     * @return response to the request
     */
    @PutMapping
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<List<IngredientAssociationResponseDto>> updateInventory (
            @RequestBody final List<IngredientAssociationRequestDto> inventoryRequestDtoList ) {
        final List<IngredientAssociationResponseDto> inventoryResponseDtoList = inventoryService
                .updateInventory( inventoryRequestDtoList );
        return ResponseEntity.ok( inventoryResponseDtoList );
    }

}
