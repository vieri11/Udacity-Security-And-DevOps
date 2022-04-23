package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {


    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private UserOrder mockOrder= mock(UserOrder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        itemController = new ItemController();

        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        // create dummy user data
        User user = new User();
        user.setId(1L);
        user.setUsername("Cristian");
        user.setPassword("Password");

        // create dummy user response
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.findByUsername("Cristian")).thenReturn(user);

        // Create dummy item data
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Round Widget");
        item1.setPrice(new BigDecimal("2.99"));
        item1.setDescription("A widget that is round");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Square Widget");
        item2.setPrice(new BigDecimal("1.99"));
        item2.setDescription("A widget that is square");

        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);

        // create dummy item responses
        when(itemRepo.findAll()).thenReturn(itemList);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepo.findByName("Round Widget")).thenReturn(Collections.singletonList(item2));
    }

    @Test
    public void get_all_items () {
        ResponseEntity<List<Item>> res = itemController.getItems();

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue()); // 200 ok response

        List<Item> items = res.getBody();
        assertNotNull(items);
        assertEquals(2,items.size());
    }

    @Test
    public void find_item_by_id() {
        final ResponseEntity<Item> res = itemController.getItemById(1L);

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        Item item = res.getBody();

        assertNotNull(item);
        assertEquals(1L, item.getId().longValue());
    }

    @Test
    public void get_item_by_name() {
        final ResponseEntity<List<Item>> res = itemController.getItemsByName("Round Widget");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        List<Item> listItem = res.getBody();

        assertNotNull(listItem);
        assertEquals(1, listItem.size());
    }

    @Test
    public void item_by_id_not_found() {
        final ResponseEntity<Item> res = itemController.getItemById(5L);

        assertNotNull(res);
        assertEquals(404, res.getStatusCodeValue());

        Item item = res.getBody();

        assertNull(item);
    }
}