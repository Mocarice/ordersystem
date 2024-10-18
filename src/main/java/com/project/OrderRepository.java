package com.project;

import java.util.*;

public class OrderRepository {
    // 주문 데이터를 메모리에 저장하는 Map (orderId -> Order)
    private final Map<Long, Order> orderStorage = new HashMap<>();

    // 주문 데이터를 저장하는 메서드
    public void saveOrder(Order order) {
        orderStorage.put(order.getOrderId(), order); // 주문 ID를 키로, Order 객체를 값으로 저장
    }

    // 주문 ID를 통해 주문 데이터를 조회하는 메서드
    public Order getOrderById(Long orderId) {
        return Optional.ofNullable(orderStorage.get(orderId)).orElseThrow(() -> new RuntimeException("Order not found")); // 주문 ID로 Order 객체를 조회
    }

    // 모든 주문 데이터를 리스트 형식으로 반환하는 메서드
    public List<Order> getAllOrders() {
        return new ArrayList<>(orderStorage.values()); // 저장된 모든 주문을 리스트로 반환
    }
}
