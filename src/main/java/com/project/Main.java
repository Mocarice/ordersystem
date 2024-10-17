package com.project;

public class Main {
    public static void main(String[] args) {

        OrderRepository orderRepository = new OrderRepository();
        OrderProcessInterface orderProcessInterface = new ExternalSystemOrderApi(orderRepository);
        OrderService orderService = new OrderService(orderRepository, orderProcessInterface);

        // 외부 시스템에서 주문 데이터 동기화
        orderService.syncOrdersFromExternal();

        // 모든 주문 데이터 출력
        for (Order order : orderService.getAllOrders()) {
            System.out.println("Order ID: " + order.getOrderId() + ", Customer: " + order.getCustomerName());
        }

        // 주문 데이터 외부 시스템에 전송
        orderService.sendOrdersToExternal();
    }
}