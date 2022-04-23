package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {


    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private CartController cartController;

    private UserOrder mockOrder= mock(UserOrder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        itemController = new ItemController();
        cartController = new CartController();

        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);

        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        // create dummy user data
        User user = new User();
        user.setId(1L);
        user.setUsername("Cristian");
        user.setPassword("Password");

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

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(itemList);
        cart.setUser(user);
        cart.setTotal(new BigDecimal("4.98"));

        user.setCart(cart);

        // create dummy user response
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.findByUsername("Cristian")).thenReturn(user);

        // create dummy item responses
        when(itemRepo.findAll()).thenReturn(itemList);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));
    }

    @Test
    public void add_to_cart(){

        // create dummy request cart data
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("Cristian");
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.addTocart(cartRequest);

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        Cart cart = res.getBody();

        assertNotNull(cart);
        assertEquals(1L, cart.getId().longValue());
        assertEquals("Cristian", cart.getUser().getUsername());
        assertEquals(3, cart.getItems().size());
        assertEquals(new BigDecimal("7.97"), cart.getTotal());
    }

    @Test
    public void remove_from_cart() {

        // create dummy request cart data
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("Cristian");
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.removeFromcart(cartRequest);

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        Cart cart = res.getBody();

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
    }

    @Test
    public void add_to_cart_failed_wrong_user() {
        // create dummy request cart data
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("Chris");
        cartRequest.setItemId(1L);
        cartRequest.setQuantity(1);

        final ResponseEntity<Cart> res = cartController.addTocart(cartRequest);

        assertNotNull(res);
        assertEquals(404, res.getStatusCodeValue());
    }
}
