package com.perfstore.service;

import com.perfstore.domain.Order;
import com.perfstore.domain.OrderItem;
import com.perfstore.domain.Product;
import com.perfstore.domain.User;
import com.perfstore.dto.OrderDto;
import com.perfstore.repository.OrderRepository;
import com.perfstore.repository.ProductRepository;
import com.perfstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;

        public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        UserRepository userRepository) {
                this.orderRepository = orderRepository;
                this.productRepository = productRepository;
                this.userRepository = userRepository;
        }

        @Transactional
        public OrderDto.Response createOrder(String username, OrderDto.CreateRequest request) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Rate Limiting: Check last order time
                orderRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                                .ifPresent(lastOrder -> {
                                        if (lastOrder.getCreatedAt()
                                                        .isAfter(java.time.LocalDateTime.now().minusMinutes(2))) {
                                                throw new com.perfstore.exception.RateLimitExceededException(
                                                                "Lütfen yeni sipariş vermeden önce 2 dakika bekleyiniz.");
                                        }
                                });

                Order order = new Order();
                order.setUser(user);
                order.setShippingAddress(request.getShippingAddress());
                order.setPaymentMethod(request.getPaymentMethod());

                BigDecimal totalAmount = BigDecimal.ZERO;

                for (OrderDto.OrderItemRequest itemRequest : request.getItems()) {
                        Product product = productRepository.findById(itemRequest.getProductId())
                                        .orElseThrow(() -> new RuntimeException("Product not found"));

                        // Atomic stock update (returns 1 if successful, 0 if stock insufficient)
                        int updatedRows = productRepository.decreaseStock(product.getId(), itemRequest.getQuantity());
                        if (updatedRows == 0) {
                                throw new com.perfstore.exception.InsufficientStockException(
                                                "Insufficient stock for product: " + product.getName());
                        }

                        // We don't set stock quantity on the object here because we want the DB to be
                        // the source of truth,
                        // and we updated the DB directly. The entity in logic is slightly stale
                        // regarding stock, but that's fine for creating order item.

                        OrderItem orderItem = new OrderItem(product, itemRequest.getQuantity(), product.getPrice());
                        order.addItem(orderItem);

                        totalAmount = totalAmount.add(
                                        product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
                }

                order.setTotalAmount(totalAmount);
                Order savedOrder = orderRepository.save(order);

                List<OrderDto.OrderItemResponse> itemResponses = savedOrder.getItems().stream()
                                .map(item -> new OrderDto.OrderItemResponse(
                                                item.getProduct().getName(),
                                                item.getQuantity(),
                                                item.getPriceAtPurchase()))
                                .collect(Collectors.toList());

                return new OrderDto.Response(
                                savedOrder.getId(),
                                savedOrder.getTotalAmount(),
                                savedOrder.getCreatedAt(),
                                savedOrder.getStatus(),
                                savedOrder.getShippingAddress(),
                                savedOrder.getPaymentMethod(),
                                itemResponses);
        }

        public List<OrderDto.Response> getUserOrders(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return orderRepository.findByUserId(user.getId()).stream()
                                .map(order -> {
                                        List<OrderDto.OrderItemResponse> itemResponses = order.getItems().stream()
                                                        .map(item -> new OrderDto.OrderItemResponse(
                                                                        item.getProduct().getName(),
                                                                        item.getQuantity(),
                                                                        item.getPriceAtPurchase()))
                                                        .collect(Collectors.toList());

                                        return new OrderDto.Response(
                                                        order.getId(),
                                                        order.getTotalAmount(),
                                                        order.getCreatedAt(),
                                                        order.getStatus(),
                                                        order.getShippingAddress(),
                                                        order.getPaymentMethod(),
                                                        itemResponses);
                                })
                                .collect(Collectors.toList());
        }
}
