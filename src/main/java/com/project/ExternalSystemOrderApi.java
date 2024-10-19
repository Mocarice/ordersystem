package com.project;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ExternalSystemOrderApi implements OrderProcessInterface<Order> {

    private final OrderRepository orderRepository;
    public ExternalSystemOrderApi(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order parseOrderFromJsonObject(String jsonData) {
        jsonData = jsonData.trim();
        // 중괄호 제거
        if (jsonData.startsWith("{") && jsonData.endsWith("}")) {
            jsonData = jsonData.substring(1, jsonData.length() - 1);
        } else {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        String[] keyValuePairs = jsonData.split(",");

        Long orderId = null;
        String customerName = null;
        String orderDate = null;
        String orderStatus = null;

        for (String pair : keyValuePairs) {
            String[] entry = pair.split(":");
            String key = entry[0].trim().replace("\"", "");
            String value = entry[1].trim().replace("\"", "");

            switch (key) {
                case "orderId" -> orderId = Long.valueOf(value);
                case "customerName" -> customerName = value;
                case "orderDate" -> orderDate = value;
                case "orderStatus" -> orderStatus = value;
                default -> {
                    // 다른 값이 섞여있어도 Order 객체로 정의한 값들만 불러오면 되므로 별도의 처리 없이 넘어감.
                }
            }
        }
        // Order 객체 생성 및 반환
        return new Order(orderId, customerName, orderDate, orderStatus);
    }
    @Override
    public HttpRequest buildFetchOrderRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }
    @Override
    public HttpRequest buildSendOrderRequest(String url, String jsonOrders) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonOrders))
                .build();
    }
    @Override
    public void handleSuccessSendOrderResponse(HttpResponse<String> response) {
        // SendOrderResponse의 Status가 200일 경우 처리 로직 별도 작성
        // 현 프로젝트에선 별도의 처리가 필요 없음.
    }
    @Override
    public void handleSuccessFetchOrderResponse(HttpResponse<String> response) {
        String jsonData = response.body();
        // JSON이 배열인지 객체인지 확인
        if (isJsonArray(jsonData)) {
            List<Order> orders = parseOrdersFromJsonArray(jsonData);
            orders.forEach(orderRepository::saveOrder);
        } else if (isJsonObject(jsonData)) {
            Order order = parseOrderFromJsonObject(jsonData);
            orderRepository.saveOrder(order);
        } else {    // JSON 형태 이상
            throw new IllegalArgumentException("Invalid JSON format");
        }
    }

    @Override
    public String buildJsonFromOrder(List<Order> orders) {
        // Order 객체 리스트를 JSON 문자열로 변환하는 로직
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            jsonBuilder.append("{")
                    .append("\"orderId\":\"").append(order.getOrderId()).append("\",")
                    .append("\"customerName\":\"").append(order.getCustomerName()).append("\",")
                    .append("\"orderDate\":\"").append(order.getOrderDate()).append("\",")
                    .append("\"orderStatus\":\"").append(order.getOrderStatus()).append("\"")
                    .append("}");
            if (i < orders.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
}
