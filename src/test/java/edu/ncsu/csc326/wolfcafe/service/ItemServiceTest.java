/**
 *
 */
package edu.ncsu.csc326.wolfcafe.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc326.wolfcafe.dto.ItemDto;
import edu.ncsu.csc326.wolfcafe.repository.ItemRepository;

/**
 * Test class for ingredient service.
 */
@SpringBootTest
class ItemServiceTest {

    /** Reference to item service */
    @Autowired
    private ItemService    itemService;

    /** Reference to item repository */
    @Autowired
    private ItemRepository itemRepository;

    /**
     * Clear repositories.
     *
     * @throws java.lang.Exception
     *             if error
     */
    @BeforeEach
    void setUp () throws Exception {
        itemRepository.deleteAll();
    }

    /**
     * Test method for creating ingredient.
     */
    @Test
    @Transactional
    void testCreateItem () {

        final ItemDto itemDto = new ItemDto( 0L, "Agua", "Water bottle", 50 );
        final ItemDto savedItemDto = itemService.addItem( itemDto );

        // ensures the item contents are correct based on savedItemDto
        assertAll( "Item contents", () -> assertEquals( "Agua", savedItemDto.getName() ),
                () -> assertEquals( "Water bottle", savedItemDto.getDescription() ),
                () -> assertEquals( 50, savedItemDto.getPrice() ) );

        final ItemDto savedItemDto2 = itemService.getItem( savedItemDto.getId() );

        // ensures the item contents are correct based on get
        assertAll( "Item contents", () -> assertEquals( "Agua", savedItemDto2.getName() ),
                () -> assertEquals( "Water bottle", savedItemDto2.getDescription() ),
                () -> assertEquals( 50, savedItemDto2.getPrice() ) );

        assertEquals( 1, itemService.getAllItems().size() );
    }

    /**
     * Test method for checking duplicate name.
     */
    @Test
    @Transactional
    void testUpadteDeleteItem () {

        final ItemDto itemDto = new ItemDto( 0L, "Agua", "Water bottle", 50 );
        final ItemDto savedItemDto = itemService.addItem( itemDto );

        itemDto.setName( "Dasani" );
        itemDto.setId( savedItemDto.getId() );
        itemDto.setPrice( 5 );

        final ItemDto savedItemDto2 = itemService.updateItem( itemDto.getId(), itemDto );

        // ensures the item contents are correct based on update
        assertAll( "Item contents", () -> assertEquals( "Dasani", savedItemDto2.getName() ),
                () -> assertEquals( "Water bottle", savedItemDto2.getDescription() ),
                () -> assertEquals( 5, savedItemDto2.getPrice() ) );

        itemService.deleteItem( savedItemDto2.getId() );

        assertEquals( 0, itemService.getAllItems().size() );
    }

}
