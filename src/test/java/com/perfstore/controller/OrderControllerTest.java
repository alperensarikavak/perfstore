package com.perfstore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perfstore.domain.Category;
import com.perfstore.domain.Product;
import com.perfstore.dto.AuthDto;
import com.perfstore.dto.OrderDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void tearDown() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateOrderAndReduceStock() throws Exception {
        // 1. Register User & Login to get token
        AuthDto.RegisterRequest registerRequest = new AuthDto.RegisterRequest();
        registerRequest.setUsername("buyer");
        registerRequest.setPassword("pass123");
        registerRequest.setEmail("buyer@example.com");
        registerRequest.setPin("1234"); // PIN is now required for registration logic

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        AuthDto.LoginRequest loginRequest = new AuthDto.LoginRequest();
        loginRequest.setUsername("buyer");
        loginRequest.setPassword("pass123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = loginResult.getResponse().getContentAsString();
        AuthDto.AuthResponse authResponse = objectMapper.readValue(responseContent, AuthDto.AuthResponse.class);
        String token = "Bearer " + authResponse.getToken();

        // 2. Create Product
        Category category = new Category("Tech", "Gadgets");
        category = categoryRepository.save(category);

        Product product = new Product("Laptop", "Gaming Laptop", new BigDecimal("1500.00"), 10, "SKU-LAP-001", null,
                category);
        product = productRepository.save(product);

        // 3. Create Order Request
        OrderDto.OrderItemRequest itemRequest = new OrderDto.OrderItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setQuantity(2); // Buy 2

        OrderDto.CreateRequest orderRequest = new OrderDto.CreateRequest();
        orderRequest.setItems(List.of(itemRequest));

        // 4. Perform Order
        mockMvc.perform(post("/api/orders")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].productName").value("Laptop"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalAmount").value(3000.00));

        // 5. Verify Stock Reduction
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assert updatedProduct.getStockQuantity() == 8; // 10 - 2 = 8
    }
}
