package com.example.demo.controller;


import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private UserOrder mockOrder= mock(UserOrder.class);

    @Before
    public void setUp() {
        userController = new UserController();

        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

        // create dummy user data
        User user = new User();
        user.setId(1L);
        user.setUsername("Cristian");
        user.setPassword("Password");

        // create dummy user response
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.findByUsername("Cristian")).thenReturn(user);

        // Create dummy item data
        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal("2.99"));
        item.setDescription("A widget that is round");

        // create dummy item responses
        when(itemRepo.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
    }

    @Test
    public void create_user_happy_path() {
        // stubbing example
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void find_user_by_id() {
        final ResponseEntity<User> res = userController.findById(1L);
        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());

        User user = res.getBody();

        assertEquals(1L, user.getId());
        assertEquals("Cristian", user.getUsername());
    }

    @Test
    public void user_password_too_short() {
        CreateUserRequest req = new CreateUserRequest();

        req.setUsername("test");
        req.setPassword("pass");
        req.setConfirmPassword("pass");

        final ResponseEntity<User> res = userController.createUser(req);

        assertNotNull(res);
        assertEquals(400, res.getStatusCodeValue()); // 400 bad Request
    }

    @Test
    public void user_not_found_by_id() {
        final ResponseEntity<User> res = userController.findById(99L);

        assertNotNull(res);
        assertEquals(404, res.getStatusCodeValue()); //404 Not Found
    }

    @Test
    public void user_find_by_name() {
        final ResponseEntity<User> res = userController.findByUserName("Cristian");

        assertNotNull(res);
        assertEquals(200, res.getStatusCodeValue());
    }
}

