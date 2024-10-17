package com.project;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProcessInterface orderProcessInterface;

    public OrderService(OrderRepository orderRepository, OrderProcessInterface orderProcessInterface) {
        this.orderRepository = orderRepository;
        this.orderProcessInterface = orderProcessInterface;
    }

    public void syncOrdersFromExternal() {
        try {
            orderProcessInterface.fetchOrders();
        } catch (Exception e) {
            System.err.println("Error syncing orders: " + e.getMessage());
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
            orderProcessInterface.sendOrders(orders);
        } catch (Exception e) {
            System.err.println("Error sending orders: " + e.getMessage());
        }
    }
}