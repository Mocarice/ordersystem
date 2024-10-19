package com.project;

public class Order {
    private final Long orderId;
    private final String customerName;
    private final String orderDate;
    private final String orderStatus; //"처리 중, 배송 중, 완료"

    public Order(Long orderId, String customerName, String orderDate, String orderStatus) {
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
    public String getOrderDate() {
        return orderDate;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
}
