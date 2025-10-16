/**
 *
 */
package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryIngredientRepository;

/**
 * Test class for inventory controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc                       mvc;

    /** Reference to inventory ingredient repository */
    @Autowired
    private InventoryIngredientRepository inventoryIngredientRepository;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        inventoryIngredientRepository.deleteAll();
        ingredientRepository.deleteAll();
    }

    /**
     * Tests the GET /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testGetInventory () throws Exception {

        // test empty inventory
        mvc.perform( get( "/api/inventory" ) ).andExpect( content().json( "[]" ) ).andExpect( status().isOk() );
    }

    /**
     * Tests the PUT /api/inventory endpoint.
     *
     * @throws Exception
     *             if issue when running the test.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testUpdateInventory () throws Exception {

        final IngredientRequestDto ingredientRequestDto = new IngredientRequestDto( "Sugar", 10 );

        // create ingredient (and inventory ingredient) first
        final String json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( ingredientRequestDto ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final ObjectMapper mapper = new ObjectMapper();
        final Long id = mapper.readTree( json ).get( "id" ).asLong();

        final List<IngredientAssociationRequestDto> inventoryRequestDtoList = new ArrayList<>();
        inventoryRequestDtoList.add( new IngredientAssociationRequestDto( id, 5 ) );

        final List<IngredientAssociationResponseDto> inventoryResponseDtoList = new ArrayList<>();
        inventoryResponseDtoList.add( new IngredientAssociationResponseDto( id, "Sugar", 5 ) );

        // update the inventory ingredients
        mvc.perform( put( "/api/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( inventoryRequestDtoList ) ).accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( content().string( TestUtils.asJsonString( inventoryResponseDtoList ) ) )
                .andExpect( status().isOk() );

        // check the inventory
        mvc.perform( get( "/api/inventory" ) ).andDo( print() )
                .andExpect( content().string( TestUtils.asJsonString( inventoryResponseDtoList ) ) )
                .andExpect( status().isOk() );

    }

}
