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
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {


    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private CartController cartController;
    private OrderController orderController;

    private OrderRepository orderRepo = mock(OrderRepository.class);

    @Before
    public void setUp() {
        userController = new UserController();
        itemController = new ItemController();
        cartController = new CartController();
        orderController = new OrderController();

        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);

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

        UserOrder userOrder1 = new UserOrder();
        userOrder1.setUser(user);
        userOrder1.setItems(cart.getItems());
        userOrder1.setTotal(cart.getTotal());
        userOrder1.setId(1L);

        // user order dummy data
        ArrayList<UserOrder> userOrderList = new ArrayList<>();
        userOrderList.add(userOrder1);

        when(orderRepo.findByUser(user)).thenReturn(userOrderList);
    }

    @Test
    public void add_order() {
        final ResponseEntity<UserOrder> res = orderController.submit("Cristian");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        UserOrder order = res.getBody();

        assertNotNull(order);
        assertEquals(new BigDecimal("4.98"), order.getTotal());
        assertEquals(2L, order.getItems().size());
    }

    @Test
    public void get_order_for_user() {
        final ResponseEntity<List<UserOrder>> res = orderController.getOrdersForUser("Cristian");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        List<UserOrder> userOrderList = res.getBody();

        assertNotNull(userOrderList);

        UserOrder userOrder = userOrderList.get(0);

        assertNotNull(userOrder);
        assertEquals(1L, userOrder.getId().longValue());
        assertEquals(new BigDecimal("4.98"), userOrder.getTotal());
    }

    @Test
    public void get_order_wrong_user() {
        final ResponseEntity<List<UserOrder>> res = orderController.getOrdersForUser("Chris");

        assertNotNull(res);
        assertEquals(404, res.getStatusCodeValue());
    }
}

