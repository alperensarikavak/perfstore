package com.perfstore.service;

import com.perfstore.domain.Order;
import com.perfstore.domain.Product;
import com.perfstore.repository.OrderRepository;
import com.perfstore.repository.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderCleanupScheduler {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderCleanupScheduler(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    // Run every minute
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelUnpaidOrders() {
        // Find PENDING orders older than 30 minutes
        LocalDateTime cutOffTime = LocalDateTime.now().minusMinutes(30);
        List<Order> pendingOrders = orderRepository.findByStatusAndCreatedAtBefore("PENDING", cutOffTime);

        if (!pendingOrders.isEmpty()) {
            System.out.println("Found " + pendingOrders.size() + " expired orders. Cancelling...");
        }

        for (Order order : pendingOrders) {
            // 1. Update status
            order.setStatus("CANCELLED");
            orderRepository.save(order);

            // 2. Restore Stock
            order.getItems().forEach(item -> {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
                System.out.println("Restored stock for: " + product.getName() + " (+ " + item.getQuantity() + ")");
            });

            System.out.println("Order " + order.getId() + " cancelled.");
        }
    }
}
