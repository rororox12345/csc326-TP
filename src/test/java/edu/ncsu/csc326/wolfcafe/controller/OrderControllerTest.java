package edu.ncsu.csc326.wolfcafe.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ncsu.csc326.wolfcafe.TestUtils;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.LoginDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderItemRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RegisterDto;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.InventoryIngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    /** Mock MVC for testing controller */
    @Autowired
    private MockMvc                       mvc;

    /** given admin pass */
    @Value ( "${app.admin-user-password}" )
    private String                        adminUserPassword;

    /** Connection to InventoryIngredientRepository */
    @Autowired
    private InventoryIngredientRepository inventoryIngredientRepository;

    /** Connection to IngredientRepository */
    @Autowired
    private IngredientRepository          ingredientRepository;

    /** Connection to RecipeRepository */
    @Autowired
    private RecipeRepository              recipeRepository;

    /** Connection to ItemRepository */
    @Autowired
    private ItemRepository                itemRepository;

    /** Connection to OrderRepository */
    @Autowired
    private OrderRepository               orderRepository;

    /** Connection to TaxRateRepository */
    @Autowired
    private TaxRateRepository             taxRateRepository;

    @BeforeEach
    void setUp () throws Exception {
        inventoryIngredientRepository.deleteAll();
        ingredientRepository.deleteAll();
        recipeRepository.deleteAll();
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        taxRateRepository.deleteAll();
    }

    /**
     * Tests placing order
     *
     * @throws Exception
     *             if error
     */
    @Test
    @Transactional
    void placeOrdertest () throws Exception {

        taxRateRepository.save( new TaxRate( 0L, 2.0 ) );

        // create customer
        String json = mvc
                .perform( post( "/api/auth/register" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new RegisterDto( "Cus", "cus1", "cus1@ncsu.edu", "CUS!" ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final ObjectMapper mapper = new ObjectMapper();

        // login as admin
        final String adminToken = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new LoginDto( "admin", adminUserPassword ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // create staff
        mvc.perform( post( "/api/auth/staff" )
                .header( "Authorization",
                        "Bearer " + adminToken.substring( adminToken.indexOf( "accessToken\":\"" ) + 14,
                                adminToken.indexOf( "\",\"tokenType\"" ) ) )
                .contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new RegisterDto( "Stf", "stf1", "stf1@ncsu.edu", "STF!" ) ) )
                .accept( MediaType.APPLICATION_JSON ) ).andExpect( status().isCreated() );

        // login as staff
        final String staffToken = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new LoginDto( "stf1", "STF!" ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Adding ingredients first
        json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .header( "Authorization",
                                "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                        staffToken.indexOf( "\",\"tokenType\"" ) ) )
                        .content( TestUtils.asJsonString( new IngredientRequestDto( "Coffee", 500 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long ingredientId1 = mapper.readTree( json ).get( "id" ).asLong();

        // same as above
        json = mvc
                .perform( post( "/api/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                        .header( "Authorization",
                                "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                        staffToken.indexOf( "\",\"tokenType\"" ) ) )
                        .content( TestUtils.asJsonString( new IngredientRequestDto( "Sugar", 100 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andDo( print() ).andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long ingredientId2 = mapper.readTree( json ).get( "id" ).asLong();

        // create recipe
        final RecipeRequestDto recipeRequestDto = new RecipeRequestDto();
        recipeRequestDto.setName( "Mocha" );
        recipeRequestDto.setPrice( 50 );

        final List<IngredientAssociationRequestDto> ingredientAssociationRequestList = new ArrayList<>();
        ingredientAssociationRequestList.add( new IngredientAssociationRequestDto( ingredientId1, 4 ) );
        ingredientAssociationRequestList.add( new IngredientAssociationRequestDto( ingredientId2, 6 ) );

        recipeRequestDto.setRecipeIngredientsRequestList( ingredientAssociationRequestList );

        // get recipe id
        json = mvc
                .perform( post( "/api/recipes" ).contentType( MediaType.APPLICATION_JSON )
                        .header( "Authorization",
                                "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                        staffToken.indexOf( "\",\"tokenType\"" ) ) )
                        .content( TestUtils.asJsonString( recipeRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long recipeId = mapper.readTree( json ).get( "id" ).asLong();

        // create item
        json = mvc
                .perform( post( "/api/items" ).contentType( MediaType.APPLICATION_JSON )
                        .header( "Authorization",
                                "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                        staffToken.indexOf( "\",\"tokenType\"" ) ) )
                        .content( TestUtils.asJsonString( new ItemDto( 0L, "agua", "water bottle", 10 ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andReturn().getResponse().getContentAsString();

        final Long itemId = mapper.readTree( json ).get( "id" ).asLong();

        // login as cutomer
        final String customerToken = mvc
                .perform( post( "/api/auth/login" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( new LoginDto( "cus1", "CUS!" ) ) )
                        .accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // build new order
        final OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setTip( 17.5 );
        orderRequestDto.setAmountPaid( 323.5 );
        orderRequestDto.setOrderRecipeRequestList( new ArrayList<>() );
        orderRequestDto.setOrderItemRequestList( new ArrayList<>() );

        orderRequestDto.getOrderRecipeRequestList().add( new OrderRecipeRequestDto( recipeId, 5 ) );
        orderRequestDto.getOrderItemRequestList().add( new OrderItemRequestDto( itemId, 5 ) );

        // get order list
        mvc.perform( get( "/api/orders/customer" ).header( "Authorization",
                "Bearer " + customerToken.substring( customerToken.indexOf( "accessToken\":\"" ) + 14,
                        customerToken.indexOf( "\",\"tokenType\"" ) ) ) )
                .andDo( print() ).andExpect( status().isOk() ).andExpect( content().json( "[]" ) );

        // place order
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .header( "Authorization",
                        "Bearer " + customerToken.substring( customerToken.indexOf( "accessToken\":\"" ) + 14,
                                customerToken.indexOf( "\",\"tokenType\"" ) ) )
                .content( TestUtils.asJsonString( orderRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isCreated() ).andDo( print() );

        orderRequestDto.setAmountPaid( 320.0 );

        // place order (insufficient money)
        mvc.perform( post( "/api/orders" ).contentType( MediaType.APPLICATION_JSON )
                .header( "Authorization",
                        "Bearer " + customerToken.substring( customerToken.indexOf( "accessToken\":\"" ) + 14,
                                customerToken.indexOf( "\",\"tokenType\"" ) ) )
                .content( TestUtils.asJsonString( orderRequestDto ) ).accept( MediaType.APPLICATION_JSON ) )
                .andExpect( status().isPaymentRequired() ).andDo( print() );

        // get orders for staff
        json = mvc
                .perform( get( "/api/orders/staff" ).header( "Authorization",
                        "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                staffToken.indexOf( "\",\"tokenType\"" ) ) ) )
                .andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        System.out.println( "THE JSON IS " + json );
        final Long orderId = mapper.readTree( json.substring( 1, json.length() - 1 ) ).get( "orderId" ).asLong();

        // Fulfill the order
        mvc.perform(
                put( "/api/orders/" + orderId + "/fulfill" )
                        .header( "Authorization",
                                "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                        staffToken.indexOf( "\",\"tokenType\"" ) ) ) )
                .andExpect( status().isNoContent() );
        // try again
        assertThrows( jakarta.servlet.ServletException.class,
                () -> mvc.perform( put( "/api/orders/" + orderId + "/fulfill" ).header( "Authorization",
                        "Bearer " + staffToken.substring( staffToken.indexOf( "accessToken\":\"" ) + 14,
                                staffToken.indexOf( "\",\"tokenType\"" ) ) ) ) );

        // pickup order
        mvc.perform(
                put( "/api/orders/" + orderId + "/pickup" )
                        .header( "Authorization",
                                "Bearer " + customerToken.substring( customerToken.indexOf( "accessToken\":\"" ) + 14,
                                        customerToken.indexOf( "\",\"tokenType\"" ) ) ) )
                .andExpect( status().isNoContent() );

        // try again
        mvc.perform(
                put( "/api/orders/" + orderId + "/pickup" )
                        .header( "Authorization",
                                "Bearer " + customerToken.substring( customerToken.indexOf( "accessToken\":\"" ) + 14,
                                        customerToken.indexOf( "\",\"tokenType\"" ) ) ) )
                .andExpect( status().isBadRequest() );

        taxRateRepository.deleteAll();
    }

}
