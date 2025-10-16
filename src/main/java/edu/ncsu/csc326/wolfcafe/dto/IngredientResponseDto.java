package edu.ncsu.csc326.wolfcafe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to transfer Ingredient data between the client and server. This class
 * will serve as the response in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponseDto {

    /** Ingredient id */
    private Long   id;

    /** Ingredient name */
    private String name;

}
