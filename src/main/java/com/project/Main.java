package com.project;

import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

        OrderRepository orderRepository = new OrderRepository();
        OrderProcessInterface<Order> orderProcessInterface = new ExternalSystemOrderApi(orderRepository);
        OrderService orderService = new OrderService(orderRepository, orderProcessInterface);

        // 외부 시스템에서 주문 데이터 동기화
        orderService.fetchOrdersFromExternalAndSaveOrders();

        // 모든 주문 데이터 출력
        for (Order order : orderService.getAllOrders()) {
            Logger.getLogger("Main").info("Order ID: " + order.getOrderId() + ", Customer: " + order.getCustomerName());
        }

        // 주문 데이터 외부 시스템에 전송
        orderService.sendOrdersToExternal();
    }
}