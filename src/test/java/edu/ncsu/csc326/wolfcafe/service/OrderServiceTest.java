package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderItemRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.RecipeResponseDto;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.entity.User;
import edu.ncsu.csc326.wolfcafe.repository.IngredientRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.repository.RoleRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;

/**
 * Test Class for Order Service
 */
@SpringBootTest
public class OrderServiceTest {

    /** Reference to order repository */
    @Autowired
    private OrderRepository      orderRepository;

    /** Reference to order service */
    @Autowired
    private OrderService         orderService;
    /** Reference to recipe service */
    @Autowired
    private RecipeService        recipeService;

    /** Reference to ingredient repository */
    @Autowired
    private IngredientRepository ingredientRepository;

    /** Reference to ingredient service */
    @Autowired
    private IngredientService    ingredientService;

    /** Reference to recipe repository */
    @Autowired
    private RecipeRepository     recipeRepository;

    /** Reference to itemService */
    @Autowired
    private ItemService          itemService;

    /** Reference to inventoryService */
    @Autowired
    private InventoryService     inventoryService;

    /** Reference to itemService */
    @Autowired
    private UserRepository       userRepository;

    /** Reference to itemService */
    @Autowired
    private TaxRateRepository    taxRateRepository;

    /** Reference to RoleRepository */
    @Autowired
    private RoleRepository       roleRepository;

    /**
     * Sets up the test case.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        orderRepository.deleteAll();
        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();
        userRepository.deleteAll();
        taxRateRepository.deleteAll();
    }

    @Test
    @Transactional
    void testPlaceOrder () {

        final TaxRate taxRate = new TaxRate();
        taxRate.setPercent( 2.0 );
        taxRateRepository.save( taxRate );

        final User user = new User();
        user.setName( "name" );
        user.setUsername( "s" );
        user.setEmail( "email" );
        user.setPassword( "password" );
        user.setRole( roleRepository.findByName( "ROLE_CUSTOMER" ) );

        final User user2 = new User();
        user2.setName( "name" );
        user2.setUsername( "fire" );
        user2.setEmail( "other" );
        user2.setPassword( "password" );
        user2.setRole( roleRepository.findByName( "ROLE_CUSTOMER" ) );

        userRepository.save( user );
        userRepository.save( user2 );
        // create new ingredients
        final Long ingredientId1 = ingredientService.createIngredient( new IngredientRequestDto( "Coffee", 50 ) )
                .getId();
        final Long ingredientId2 = ingredientService.createIngredient( new IngredientRequestDto( "Sugar", 50 ) )
                .getId();

        // create recipe request
        final RecipeRequestDto recipeRequestDto1 = new RecipeRequestDto( 0L, "Sugar Coffee", 50,
                new ArrayList<IngredientAssociationRequestDto>() );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId1, 5 ) );
        recipeRequestDto1.getRecipeIngredientsRequestList()
                .add( new IngredientAssociationRequestDto( ingredientId2, 10 ) );

        final RecipeResponseDto recipeResponseDto1 = recipeService.createRecipe( recipeRequestDto1 );

        final ItemDto itemDto1 = new ItemDto( 1L, "Water", "Water Bottle", 10 );

        final ItemDto itemResponse = itemService.addItem( itemDto1 );

        final OrderRecipeRequestDto recipe = new OrderRecipeRequestDto( recipeResponseDto1.getId(), 5 );
        final OrderItemRequestDto item = new OrderItemRequestDto( itemResponse.getId(), 5 );
        final List<OrderRecipeRequestDto> recipeList = new ArrayList<OrderRecipeRequestDto>();
        recipeList.add( recipe );
        final List<OrderItemRequestDto> itemList = new ArrayList<OrderItemRequestDto>();
        itemList.add( item );
        final double tip = 17.5;
        final OrderRequestDto request = new OrderRequestDto();
        request.setUserId( user.getId() );
        request.setOrderRecipeRequestList( recipeList );
        request.setOrderItemRequestList( itemList );
        request.setTip( tip );
        orderService.placeOrder( request );
        final List<OrderResponseDto> orders = orderService.getAllOrdersByUser( user.getId() );
        assertEquals( 1, orders.size() );

        OrderResponseDto response = orders.get( 0 );
        assertEquals( response.getOrderStatus(), OrderStatus.PLACED );
        assertEquals( user.getId(), response.getUserId() );
        assertEquals( 1, response.getOrderItemResponseList().size() );
        assertEquals( 1, response.getOrderRecipeResponseList().size() );
        assertEquals( itemDto1.getName(), response.getOrderItemResponseList().get( 0 ).getItemName() );
        assertEquals( recipeResponseDto1.getName(), response.getOrderRecipeResponseList().get( 0 ).getRecipeName() );
        assertEquals( tip, response.getTip() );
        assertEquals( 323.5, response.getTotal() );
        final List<IngredientAssociationResponseDto> inventoryList = inventoryService.getInventory();
        assertEquals( inventoryList.get( 0 ).getQuantity(), 50 );
        assertFalse( orderService.pickupOrder( response.getOrderId() ) );
        orderService.fulfillOrder( response.getOrderId() );
        final List<IngredientAssociationResponseDto> inventoryList1 = inventoryService.getInventory();
        assertEquals( inventoryList1.get( 0 ).getQuantity(), 25 );
        assertEquals( inventoryList1.get( 1 ).getQuantity(), 0 );
        response = orderService.getAllOrders().get( 0 );
        assertEquals( response.getOrderStatus(), OrderStatus.FULFILLED );

        final OrderRequestDto request2 = new OrderRequestDto();
        request2.setUserId( user2.getId() );
        request2.setOrderRecipeRequestList( recipeList );
        request2.setOrderItemRequestList( itemList );
        request2.setTip( tip );

        orderService.placeOrder( request2 );

        final List<OrderResponseDto> allOrders = orderService.getAllOrders();
        assertEquals( 2, allOrders.size() );
        final List<OrderResponseDto> orders2 = orderService.getAllOrdersByUser( user.getId() );
        assertEquals( 1, orders2.size() );
        final List<OrderResponseDto> orders3 = orderService.getAllOrdersByUser( user2.getId() );
        assertEquals( 1, orders3.size() );
        assertFalse( orderService.fulfillOrder( orderService.getAllOrders().get( 1 ).getOrderId() ) );

        assertTrue( orderService.pickupOrder( response.getOrderId() ) );
        response = orderService.getAllOrders().get( 0 );
        assertEquals( OrderStatus.PICKED_UP, response.getOrderStatus() );
    }

}
