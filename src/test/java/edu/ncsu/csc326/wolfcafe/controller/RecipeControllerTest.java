package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;

/**
 * Test class for recipe controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {
    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc          mvc;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        recipeRepository.deleteAll();
    }

    /**
     * Test method for getting recipes.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetRecipes () throws Exception {
        mvc.perform( get( "/api/recipes" ) ).andDo( print() ).andExpect( status().isOk() )
                .andExpect( content().json( "[]" ) );
    }

    /**
     * Test method for creating recipes.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testCreateRecipe () throws Exception {

        // Adding ingredients first
        String json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new IngredientRequestDto( "Coffee", 10 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final ObjectMapper mapper = new ObjectMapper();
        final Long ingredientId1 = mapper.readTree( json ).get( "id" ).asLong();

        json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new IngredientRequestDto( "Sugar", 10 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long ingredientId2 = mapper.readTree( json ).get( "id" ).asLong();

        final RecipeRequestDto recipeRequestDto = new RecipeRequestDto();
        recipeRequestDto.setName( "Mocha" );
        recipeRequestDto.setPrice( 50 );

        final List<IngredientAssociationRequestDto> ingredientAssociationRequestList = new ArrayList<>();
        ingredientAssociationRequestList.add( new IngredientAssociationRequestDto( ingredientId1, 4 ) );
        ingredientAssociationRequestList.add( new IngredientAssociationRequestDto( ingredientId2, 6 ) );

        recipeRequestDto.setRecipeIngredientsRequestList( ingredientAssociationRequestList );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isConflict() );

        recipeRequestDto.setName( "Mocha2" );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() );

        recipeRequestDto.setName( "Mocha3" );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() );

        recipeRequestDto.setName( "Mocha4" );

        mvc.perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isInsufficientStorage() );

    }

    /**
     * Test method for deleting recipe.
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testDeleteRecipe () throws Exception {

        // Adding ingredients first
        String json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new IngredientRequestDto( "Coffee", 10 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final ObjectMapper mapper = new ObjectMapper();
        final Long ingredientId1 = mapper.readTree( json ).get( "id" ).asLong();

        json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new IngredientRequestDto( "Sugar", 10 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long ingredientId2 = mapper.readTree( json ).get( "id" ).asLong();

        final RecipeRequestDto recipeRequestDto = new RecipeRequestDto();
        recipeRequestDto.setName( "Mocha" );
        recipeRequestDto.setPrice( 50 );

        final List<IngredientAssociationRequestDto> ingredientAssociationRequestList = new ArrayList<>();
        ingredientAssociationRequestList.add( new IngredientAssociationRequestDto( ingredientId1, 4 ) );
        ingredientAssociationRequestList.add( new IngredientAssociationRequestDto( ingredientId2, 6 ) );

        recipeRequestDto.setRecipeIngredientsRequestList( ingredientAssociationRequestList );

        json = mvc
                .perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long recipeId = mapper.readTree( json ).get( "id" ).asLong();

        mvc.perform( get( "/api/recipes/" + recipeId ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/recipes/" + recipeId ) ).andExpect( status().isNoContent() );

    }

}
