package com.project;

import java.util.List;
import java.util.logging.Logger;


public class OrderService {
    private static final String API_URL = Config.getProperty("external.api.url.test1"); // 외부 API URL
    private final Logger logger = Logger.getLogger(OrderService.class.getName());

    private final OrderRepository orderRepository;
    private final OrderProcessInterface<Order> orderProcessInterface;

    public OrderService(OrderRepository orderRepository, OrderProcessInterface<Order> orderProcessInterface) {
        this.orderRepository = orderRepository;
        this.orderProcessInterface = orderProcessInterface;
    }

    public void fetchOrdersFromExternal() {
        try{
            orderProcessInterface.fetchOrders(API_URL);
        }catch(Exception e){
            // 에러 핸들링은 try문 내 호출된 메서드에서 처리하므로 출력만 실행.
            logger.info("Exception occured while fetching orders: " + e.getMessage());
        }
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public void sendOrdersToExternal() {
        List<Order> orders = orderRepository.getAllOrders();
        try{
            orderProcessInterface.sendOrders(orders, API_URL);
        }catch(Exception e){
            // 에러 핸들링은 try문 내 호출된 메서드에서 처리하므로 출력만 실행.
            logger.info("Exception occured while sending orders: " + e.getMessage());
        }
    }
}