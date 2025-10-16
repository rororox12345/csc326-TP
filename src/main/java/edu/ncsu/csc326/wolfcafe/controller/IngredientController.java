package edu.ncsu.csc326.wolfcafe.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientResponseDto;
import edu.ncsu.csc326.wolfcafe.service.IngredientService;

/**
 * Controller for Ingredients.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/ingredients" )
public class IngredientController {

    /** Connection to IngredientService */
    @Autowired
    private IngredientService ingredientService;

    /**
     * REST API method to provide POST access to the Ingredient model.
     *
     * @param ingredientRequestDto
     *            The valid Ingredient to be saved.
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to the inventory, or an error if it could not be
     */
    @PostMapping
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<IngredientResponseDto> createIngredient (
            @RequestBody final IngredientRequestDto ingredientRequestDto ) {

        // name must not be duplicate
        if ( ingredientService.isDuplicateName( ingredientRequestDto.getName() ) ) {
            return new ResponseEntity<>( new IngredientResponseDto( 0L, ingredientRequestDto.getName() ),
                    HttpStatus.CONFLICT );
        }

        // create ingredient
        final IngredientResponseDto ingredientResponseDto = ingredientService.createIngredient( ingredientRequestDto );

        // send response
        return ResponseEntity.status( HttpStatus.CREATED ).body( ingredientResponseDto );

    }

}
