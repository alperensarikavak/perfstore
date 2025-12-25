package com.perfstore.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfstore.domain.AccessToken;
import com.perfstore.domain.Category;
import com.perfstore.domain.Product;
import com.perfstore.domain.User;
import com.perfstore.dto.OrderDto;
import com.perfstore.dto.OrderDto.OrderItemRequest;
import com.perfstore.repository.AccessTokenRepository;
import com.perfstore.repository.CategoryRepository;
import com.perfstore.repository.OrderRepository;
import com.perfstore.repository.ProductRepository;
import com.perfstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderCheckoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Product testProduct;
    private String userToken = "TEST-TOKEN-123";

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        accessTokenRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        // 1. Create User
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("test@example.com");
        userRepository.save(testUser);

        // 2. Create Access Token for User
        AccessToken token = new AccessToken();
        token.setTokenValue(userToken);
        token.setOwnerName(testUser.getUsername());
        token.setAllowedCategory("ALL");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        token.setMaxUsageCount(1000);
        accessTokenRepository.save(token);

        // 3. Create Category & Product
        Category category = categoryRepository.save(new Category("Electronics", "Tech stuff"));

        testProduct = new Product("Test Laptop", "Fast laptop", new BigDecimal("1000.00"), 10, "TEST-SKU", category);
        productRepository.save(testProduct);
    }

    @Test
    void shouldCreateOrderAndDeductStock() throws Exception {
        // Prepare Order Request
        OrderDto.CreateRequest request = new OrderDto.CreateRequest();
        request.setShippingAddress("123 Test Street, Test City");
        request.setPaymentMethod("CASH_ON_DELIVERY");

        OrderDto.OrderItemRequest itemRequest = new OrderDto.OrderItemRequest();
        itemRequest.setProductId(testProduct.getId());
        itemRequest.setQuantity(2);
        request.setItems(Collections.singletonList(itemRequest));

        // Perform Checkout
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shippingAddress").value("123 Test Street, Test City"))
                .andExpect(jsonPath("$.paymentMethod").value("CASH_ON_DELIVERY"))
                .andExpect(jsonPath("$.totalAmount").value(2000.00));

        // Verify Database
        // 1. Order should be saved
        assertEquals(1, orderRepository.count());
        var savedOrder = orderRepository.findAll().get(0);
        assertEquals("123 Test Street, Test City", savedOrder.getShippingAddress());

        // 2. Stock should be deducted (10 - 2 = 8)
        Optional<Product> updatedProduct = productRepository.findById(testProduct.getId());
        assertEquals(8, updatedProduct.get().getStockQuantity());
    }

    @Test
    void shouldFailIfInsufficientStock() throws Exception {
        // Prepare Order Request with quantity > stock
        OrderDto.CreateRequest request = new OrderDto.CreateRequest();
        request.setShippingAddress("Home");
        request.setPaymentMethod("CASH");

        OrderDto.OrderItemRequest itemRequest = new OrderDto.OrderItemRequest();
        itemRequest.setProductId(testProduct.getId());
        itemRequest.setQuantity(15); // Stock is 10
        request.setItems(Collections.singletonList(itemRequest));

        // Perform Checkout
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // Expect 409 Conflict for insufficient stock

        // Verify Database - No order created
        assertEquals(0, orderRepository.count());

        // Stock remains same
        Optional<Product> p = productRepository.findById(testProduct.getId());
        assertEquals(10, p.get().getStockQuantity());
    }
}
