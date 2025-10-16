package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to transfer associated ingredient data between the client and server.
 * This class will serve as the response in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientAssociationResponseDto {

    /** Ingredient Id */
    private Long   id;

    /** Ingredient name */
    private String name;

    /** InventoryIngredient quantity */
    private int    quantity;

}
