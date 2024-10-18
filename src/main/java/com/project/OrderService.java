package com.project;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class OrderService {
    private static final String API_URL = Config.getProperty("external.api.url.test1"); // 외부 API URL

    private final OrderRepository orderRepository;
    private final OrderProcessInterface<Order> orderProcessInterface;

    public OrderService(OrderRepository orderRepository, OrderProcessInterface<Order> orderProcessInterface) {
        this.orderRepository = orderRepository;
        this.orderProcessInterface = orderProcessInterface;
    }

    public void fetchOrdersFromExternal() {
        try {
            orderProcessInterface.fetchOrders(API_URL);
        } catch (Exception e) {
            Logger.getLogger("OrderService").info("Error fetching orders: " + e.getMessage());
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public void sendOrdersToExternal() {
        try {
            List<Order> orders = orderRepository.getAllOrders();
            orderProcessInterface.sendOrders(orders, API_URL);
        } catch (Exception e) {
            Logger.getLogger("OrderService").info("Error sending orders: " + e.getMessage());
        }
    }
}