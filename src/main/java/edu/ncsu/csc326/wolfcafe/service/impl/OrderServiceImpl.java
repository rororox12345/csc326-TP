package edu.ncsu.csc326.wolfcafe.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.IngredientAssociationResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderItemRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderItemResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRecipeRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRecipeResponseDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderRequestDto;
import edu.ncsu.csc326.wolfcafe.dto.OrderResponseDto;
import edu.ncsu.csc326.wolfcafe.entity.Item;
import edu.ncsu.csc326.wolfcafe.entity.Order;
import edu.ncsu.csc326.wolfcafe.entity.OrderItem;
import edu.ncsu.csc326.wolfcafe.entity.OrderItemId;
import edu.ncsu.csc326.wolfcafe.entity.OrderRecipe;
import edu.ncsu.csc326.wolfcafe.entity.OrderRecipeId;
import edu.ncsu.csc326.wolfcafe.entity.OrderStatus;
import edu.ncsu.csc326.wolfcafe.entity.Recipe;
import edu.ncsu.csc326.wolfcafe.entity.RecipeIngredient;
import edu.ncsu.csc326.wolfcafe.entity.TaxRate;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;
import edu.ncsu.csc326.wolfcafe.repository.OrderRepository;
import edu.ncsu.csc326.wolfcafe.repository.RecipeRepository;
import edu.ncsu.csc326.wolfcafe.repository.TaxRateRepository;
import edu.ncsu.csc326.wolfcafe.repository.UserRepository;
import edu.ncsu.csc326.wolfcafe.service.InventoryService;
import edu.ncsu.csc326.wolfcafe.service.OrderService;

/**
 * Implementation of the Order Service Class
 */
@Service
public class OrderServiceImpl implements OrderService {
    /** Reference to the item repository */
    @Autowired
    private ItemRepository    itemRepository;

    /** Reference to the recipe repository */
    @Autowired
    private RecipeRepository  recipeRepository;

    /** Reference to the order repository */
    @Autowired
    private OrderRepository   orderRepository;

    /** Reference to the user repository */
    @Autowired
    private UserRepository    userRepository;

    /** Reference to the taxRate repository */
    @Autowired
    private TaxRateRepository taxRateRepository;

    /** inventory service */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * Saves the order to the repository
     *
     * @param orderRequestDto
     *            order that was placed
     */
    @Override
    public void placeOrder ( final OrderRequestDto orderRequestDto ) {

        final Order order = new Order();
        order.setOrderRecipeList( new ArrayList<OrderRecipe>() );
        order.setOrderItemList( new ArrayList<OrderItem>() );

        if ( orderRequestDto.getTip() < 0 ) {
            throw new IllegalArgumentException();
        }

        order.setTip( orderRequestDto.getTip() );

        orderRequestDto.getOrderItemRequestList();
        for ( final OrderItemRequestDto orderItemRequestDto : orderRequestDto.getOrderItemRequestList() ) {
            final int quantity = orderItemRequestDto.getQuantity();
            if ( quantity < 0 ) {
                throw new IllegalArgumentException();
            }
            final Item item = itemRepository.findById( orderItemRequestDto.getItemId() ).get();
            final OrderItem orderItem = new OrderItem( new OrderItemId(), order, item, quantity );
            order.getOrderItemList().add( orderItem );
        }

        for ( final OrderRecipeRequestDto orderRecipeRequestDto : orderRequestDto.getOrderRecipeRequestList() ) {
            final int quantity = orderRecipeRequestDto.getQuantity();
            if ( quantity < 0 ) {
                throw new IllegalArgumentException();
            }
            final Recipe recipe = recipeRepository.findById( orderRecipeRequestDto.getRecipeId() ).get();
            final OrderRecipe orderRecipe = new OrderRecipe( new OrderRecipeId(), order, recipe, quantity );
            order.getOrderRecipeList().add( orderRecipe );
        }

        order.setTotal( getTotal( orderRequestDto ) );
        order.setUser( userRepository.findById( orderRequestDto.getUserId() ).get() );
        order.setOrderStatus( OrderStatus.PLACED );

        orderRepository.save( order );
    }

    /**
     * Returns all of the orders of a specific user
     *
     * @param userId
     *            the id used to find the list of orders
     * @return List of all orders associated with the param id
     */
    @Override
    public List<OrderResponseDto> getAllOrdersByUser ( final Long userId ) {

        final List<OrderResponseDto> userList = new ArrayList<OrderResponseDto>();
        final List<Order> userOrders = orderRepository.findAllByUserId( userId );

        for ( final Order userOrder : userOrders ) {

            final OrderResponseDto orderResponse = new OrderResponseDto();
            orderResponse.setOrderId( userOrder.getId() );
            orderResponse.setTip( userOrder.getTip() );
            orderResponse.setTotal( userOrder.getTotal() );
            orderResponse.setUserId( userId );
            orderResponse.setUsername( userOrder.getUser().getUsername() );
            orderResponse.setOrderStatus( userOrder.getOrderStatus() );
            orderResponse.setOrderItemResponseList( new ArrayList<OrderItemResponseDto>() );
            orderResponse.setOrderRecipeResponseList( new ArrayList<OrderRecipeResponseDto>() );

            for ( final OrderItem orderItem : userOrder.getOrderItemList() ) {
                final OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto();
                orderItemResponseDto.setItemId( orderItem.getItem().getId() );
                orderItemResponseDto.setItemName( orderItem.getItem().getName() );
                orderItemResponseDto.setQuantity( orderItem.getQuantity() );
                orderResponse.getOrderItemResponseList().add( orderItemResponseDto );
            }

            for ( final OrderRecipe orderRecipe : userOrder.getOrderRecipeList() ) {
                final OrderRecipeResponseDto orderRecipeResponseDto = new OrderRecipeResponseDto();
                orderRecipeResponseDto.setRecipeId( orderRecipe.getRecipe().getId() );
                orderRecipeResponseDto.setRecipeName( orderRecipe.getRecipe().getName() );
                orderRecipeResponseDto.setQuantity( orderRecipe.getQuantity() );
                orderResponse.getOrderRecipeResponseList().add( orderRecipeResponseDto );
            }

            userList.add( orderResponse );

        }
        return userList;
    }

    /**
     * returns all the orders
     *
     * @return list of response Dtos
     */
    @Override
    public List<OrderResponseDto> getAllOrders () {

        final List<Order> orders = orderRepository.findAll();
        final List<OrderResponseDto> orderList = new ArrayList<OrderResponseDto>();

        for ( final Order order : orders ) {
            final OrderResponseDto orderResponse = new OrderResponseDto();
            orderResponse.setOrderId( order.getId() );
            orderResponse.setTip( order.getTip() );
            orderResponse.setTotal( order.getTotal() );
            orderResponse.setUserId( order.getUser().getId() );
            orderResponse.setUsername( order.getUser().getUsername() );
            orderResponse.setOrderStatus( order.getOrderStatus() );
            orderResponse.setOrderItemResponseList( new ArrayList<OrderItemResponseDto>() );
            orderResponse.setOrderRecipeResponseList( new ArrayList<OrderRecipeResponseDto>() );

            for ( final OrderItem orderItem : order.getOrderItemList() ) {
                final OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto();
                orderItemResponseDto.setItemId( orderItem.getItem().getId() );
                orderItemResponseDto.setItemName( orderItem.getItem().getName() );
                orderItemResponseDto.setQuantity( orderItem.getQuantity() );
                orderResponse.getOrderItemResponseList().add( orderItemResponseDto );
            }

            for ( final OrderRecipe orderRecipe : order.getOrderRecipeList() ) {
                final OrderRecipeResponseDto orderRecipeResponseDto = new OrderRecipeResponseDto();
                orderRecipeResponseDto.setRecipeId( orderRecipe.getRecipe().getId() );
                orderRecipeResponseDto.setRecipeName( orderRecipe.getRecipe().getName() );
                orderRecipeResponseDto.setQuantity( orderRecipe.getQuantity() );
                orderResponse.getOrderRecipeResponseList().add( orderRecipeResponseDto );
            }

            orderList.add( orderResponse );
        }
        return orderList;
    }

    /**
     * Returns the total of the order
     *
     * @param orderRequestDto
     *            the order containing the items and recipes
     * @return the total with tax and tip
     */
    @Override
    public Double getTotal ( final OrderRequestDto orderRequestDto ) {

        double total = 0;
        final int itemSize = orderRequestDto.getOrderItemRequestList().size();

        for ( int i = 0; i < itemSize; i++ ) {
            final OrderItemRequestDto holder = orderRequestDto.getOrderItemRequestList().get( i );
            final int quantity = holder.getQuantity();
            final double price = itemRepository.findById( holder.getItemId() ).get().getPrice();
            final double itemPrice = price * quantity;
            total += itemPrice;
        }

        final int recipeSize = orderRequestDto.getOrderRecipeRequestList().size();
        for ( int i = 0; i < recipeSize; i++ ) {
            final OrderRecipeRequestDto holder = orderRequestDto.getOrderRecipeRequestList().get( i );
            final int quantity = holder.getQuantity();
            final double price = recipeRepository.findById( holder.getRecipeId() ).get().getPrice();
            final double recipePrice = quantity * price;
            total += recipePrice;
        }

        final TaxRate taxRate = taxRateRepository.findAll().get( 0 );
        final double tax = total * ( taxRate.getPercent() / 100 );
        total += tax;
        total += orderRequestDto.getTip();
        return total;
    }

    /**
     * checks to see if the order can be fulfilled and returns true or false if
     * there is enough inventory
     *
     * @param orderId
     *            id of the order
     * @return true or false
     */
    @Override
    public boolean fulfillOrder ( final Long orderId ) {

        final List<IngredientAssociationResponseDto> inventoryList = inventoryService.getInventory();
        final Order order = orderRepository.findById( orderId ).get();

        if ( order.getOrderStatus() != OrderStatus.PLACED ) {
            throw new IllegalStateException(
                    "Cannot fulfill order in current state: " + order.getOrderStatus().toString() );
        }

        order.setOrderStatus( OrderStatus.FULFILLED );
        final List<OrderRecipe> orderList = order.getOrderRecipeList();

        final Map<Long, IngredientAssociationResponseDto> inventoryMap = new HashMap<>();
        for ( final IngredientAssociationResponseDto ingredient : inventoryList ) {
            inventoryMap.put( ingredient.getId(), ingredient );
        }

        for ( final OrderRecipe recipe : orderList ) {
            final List<RecipeIngredient> recipeIngredient = recipe.getRecipe().getRecipeIngredientList();
            for ( final RecipeIngredient ingredient : recipeIngredient ) {
                final Long ingredientId = ingredient.getIngredient().getId();
                final int amountNeeded = ingredient.getQuantity() * recipe.getQuantity();
                final IngredientAssociationResponseDto inventory = inventoryMap.get( ingredientId );
                final int used = inventory.getQuantity() - amountNeeded;
                if ( used < 0 ) {
                    return false;
                }
                inventory.setQuantity( used );
            }
        }

        final List<IngredientAssociationRequestDto> updatedInventory = new ArrayList<>();
        for ( final IngredientAssociationResponseDto updated : inventoryMap.values() ) {
            final IngredientAssociationRequestDto requestDto = new IngredientAssociationRequestDto();
            requestDto.setId( updated.getId() );
            requestDto.setQuantity( updated.getQuantity() );
            updatedInventory.add( requestDto );
        }
        inventoryService.updateInventory( updatedInventory );
        return true;

    }

    /**
     * sets the status of the order to picked up
     *
     * @param orderId
     *            id of the order
     * @return true if the order was set to picked up
     */
    public boolean pickupOrder ( final long orderId ) {
        final Order order = orderRepository.findById( orderId ).get();
        if ( order.getOrderStatus() != OrderStatus.FULFILLED ) {
            return false;
        }
        order.setOrderStatus( OrderStatus.PICKED_UP );

        return true;
    }
}
