package edu.ncsu.csc326.wolfcafe.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeResponseDto;
import edu.ncsu.csc326.wolfcafe.service.RecipeService;

/**
 * Controller for Recipes.
 */
@CrossOrigin ( "*" )
@RestController
@RequestMapping ( "/api/recipes" )
public class RecipeController {

    /** Connection to RecipeService */
    @Autowired
    private RecipeService recipeService;

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @return JSON representation of all recipes
     */
    @GetMapping
    @PreAuthorize ( "hasAnyRole('STAFF', 'CUSTOMER', 'GUEST')" )
    public List<RecipeResponseDto> getRecipes () {
        return recipeService.getAllRecipes();
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the id of the recipe desired)
     *
     * @param id
     *            recipe id
     * @return response to the request
     */
    @GetMapping ( "{id}" )
    @PreAuthorize ( "hasAnyRole('STAFF', 'CUSTOMER', 'GUEST')" )
    public ResponseEntity<RecipeResponseDto> getRecipe ( @PathVariable ( "id" ) final Long id ) {
        return ResponseEntity.ok( recipeService.getRecipeById( id ) );
    }

    /**
     * REST API method to provide POST access to the Recipe model.
     *
     * @param recipeRequestDto
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<RecipeResponseDto> createRecipe ( @RequestBody final RecipeRequestDto recipeRequestDto ) {

        // name must not be duplicate
        if ( recipeService.isDuplicateName( recipeRequestDto.getName() ) ) {
            return new ResponseEntity<>( HttpStatus.CONFLICT );
        }

        if ( recipeService.getAllRecipes().size() < 3 ) {

            return ResponseEntity.status( HttpStatus.CREATED ).body( recipeService.createRecipe( recipeRequestDto ) );

            // cannot have more than 3 recipes
        }
        else {
            return new ResponseEntity<>( HttpStatus.INSUFFICIENT_STORAGE );
        }
    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable)
     *
     * @param recipeId
     *            The id of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( "{id}" )
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<Void> deleteRecipe ( @PathVariable ( "id" ) final Long recipeId ) {
        recipeService.deleteRecipe( recipeId );
        return ResponseEntity.noContent().build();
    }

    /**
     * Update the recipe
     *
     * @param recipeId
     *            the id
     * @param recipeRequestDto
     *            the recipe
     * @return response entity
     */
    @PutMapping ( "{id}" )
    @PreAuthorize ( "hasRole('STAFF')" )
    public ResponseEntity<RecipeResponseDto> updateRecipe ( @PathVariable ( "id" ) final Long recipeId,
            @RequestBody final RecipeRequestDto recipeRequestDto ) {

        recipeRequestDto.setId( recipeId );

        return ResponseEntity.ok( recipeService.updateRecipe( recipeRequestDto ) );

    }

}
