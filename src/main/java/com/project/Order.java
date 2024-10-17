package com.project;

import java.time.LocalDate;

public class Order {
    private Long orderId;
    private String customerName;
    private LocalDate orderDate;
    private OrderStatus orderStatus; //"처리 중, 배송 중, 완료"

    public Order(Long orderId, String customerName, LocalDate orderDate, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    // Getter 및 Setter
    public Long getOrderId() {
        return orderId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public LocalDate getOrderDate() {
        return orderDate;
    }
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
