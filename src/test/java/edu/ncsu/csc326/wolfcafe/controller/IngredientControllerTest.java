/**
 *
 */
package edu.ncsu.csc326.wolfcafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;

/**
 * Test class for ingredient controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
class IngredientControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc              mvc;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    public void setUp () throws Exception {
        ingredientRepository.deleteAll();
    }

    /**
     * Test method for creating ingredients.
     */
    @Test
    @Transactional
    @WithMockUser ( username = "staff", roles = "STAFF" )
    public void testCreateIngredient () throws Exception {

        final IngredientRequestDto ingredientRequestDto = new IngredientRequestDto( "Coffee", 10 );

        // create a new ingredient
        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andExpect( jsonPath( "$.name" ).value( "Coffee" ) );

        // create a ingredient with same name
        mvc.perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ingredientRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isConflict() );
    }

}
