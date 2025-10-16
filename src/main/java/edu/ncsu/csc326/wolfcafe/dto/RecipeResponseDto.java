package edu.ncsu.csc326.wolfcafe.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Used to transfer recipe and associated ingredient data between the client and
 * server. This class will serve as the response in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponseDto {

    /** Recipe id */
    private Long                                   id;

    /** Recipe name */
    private String                                 name;

    /** Recipe price */
    private Integer                                price;

    /** Associated recipe ingredients */
    private List<IngredientAssociationResponseDto> recipeIngredientsResponseList;

}
