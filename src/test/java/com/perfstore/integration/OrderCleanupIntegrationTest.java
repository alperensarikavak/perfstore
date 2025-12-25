package com.perfstore.integration;

import com.perfstore.domain.Category;
import com.perfstore.domain.Order;
import com.perfstore.domain.OrderItem;
import com.perfstore.domain.Product;
import com.perfstore.domain.User;
import com.perfstore.repository.CategoryRepository;
import com.perfstore.repository.OrderRepository;
import com.perfstore.repository.ProductRepository;
import com.perfstore.repository.UserRepository;
import com.perfstore.service.OrderCleanupScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("test")
public class OrderCleanupIntegrationTest {

    @Autowired
    private OrderCleanupScheduler orderCleanupScheduler;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        // Setup Data
        User user = new User("cleanupTest", "pass", "clean@example.com");
        userRepository.save(user);

        Category cat = categoryRepository.save(new Category("Misc", "Misc"));
        testProduct = new Product("Cleanable Product", "Desc", BigDecimal.valueOf(100), 10, "CLN-01", cat);
        productRepository.save(testProduct);

        // Create an OLD order (e.g. 40 mins ago) - Status PENDING
        Order oldOrder = new Order();
        oldOrder.setUser(user);
        oldOrder.setCreatedAt(LocalDateTime.now().minusMinutes(40));
        oldOrder.setTotalAmount(BigDecimal.valueOf(200));
        oldOrder.setStatus("PENDING");

        OrderItem item = new OrderItem(testProduct, 2, BigDecimal.valueOf(100)); // Bought 2
        oldOrder.addItem(item);

        // Simulating stock deduction that happens at checkout
        testProduct.setStockQuantity(8);
        productRepository.save(testProduct);

        orderRepository.save(oldOrder);
    }

    @Test
    @Transactional
    void shouldCancelExpiredOrdersAndRestoreStock() {
        // Initial Check
        assertEquals(1, orderRepository.count());
        assertEquals("PENDING", orderRepository.findAll().get(0).getStatus());
        assertEquals(8, productRepository.findById(testProduct.getId()).get().getStockQuantity());

        // Run Scheduler Logic Manually
        orderCleanupScheduler.cancelUnpaidOrders();

        // Verify Status - Should be CANCELLED
        Order updatedOrder = orderRepository.findAll().get(0);
        assertEquals("CANCELLED", updatedOrder.getStatus());

        // Verify Stock - Should be restored (8 + 2 = 10)
        Product updatedProduct = productRepository.findById(testProduct.getId()).get();
        assertEquals(10, updatedProduct.getStockQuantity());
    }

    @Test
    @Transactional
    void shouldNotCancelRecentOrders() {
        // Create a RECENT order
        User user = userRepository.findAll().get(0);
        Order recentOrder = new Order();
        recentOrder.setUser(user);
        recentOrder.setCreatedAt(LocalDateTime.now().minusMinutes(10)); // Only 10 mins ago
        recentOrder.setTotalAmount(BigDecimal.TEN);
        recentOrder.setStatus("PENDING");
        orderRepository.save(recentOrder);

        // Run Scheduler
        orderCleanupScheduler.cancelUnpaidOrders();

        // Verify status is still PENDING
        Optional<Order> savedRecent = orderRepository.findById(recentOrder.getId());
        assertEquals("PENDING", savedRecent.get().getStatus());
    }
}
