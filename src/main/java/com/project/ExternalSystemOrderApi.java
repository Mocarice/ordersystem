package com.project;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ExternalSystemOrderApi implements OrderProcessInterface<Order> {
    private static final String API_URL = "http://test.com/orders"; // 외부 API URL

    private final OrderRepository orderRepository;

    public ExternalSystemOrderApi(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 외부 시스템에서 주문 데이터를 가져오는 메서드 (HTTP GET)
    @Override
    public void fetchOrders() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        // HTTP 요청 보내기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // 응답이 성공적일 때만 처리
        if (response.statusCode() == 200) {
            String jsonData = response.body();

            // JSON이 배열인지 객체인지 확인
            if (JsonTypeChecker.isJsonArray(jsonData)) {
                List<Order> orders = parseOrdersFromJsonArray(jsonData);
                orders.forEach(orderRepository::saveOrder); // 주문 저장
            } else if (JsonTypeChecker.isJsonObject(jsonData)) {
                Order order = parseOrderFromJsonObject(jsonData);
                orderRepository.saveOrder(order); // 주문 저장
            } else {
                throw new RuntimeException("Invalid JSON format received from API.");
            }
        } else {
            throw new RuntimeException("Failed to fetch orders. HTTP Status: " + response.statusCode());
        }
    }

    // 주문 데이터를 외부 시스템으로 전송하는 메서드 (HTTP POST)
    @Override
    public void sendOrders(List<Order> orders) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String jsonOrders = buildJsonFromOrders(orders); // 주문 리스트를 JSON 형식으로 변환

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonOrders))
                .build();

        // HTTP POST 요청 보내기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to send orders. HTTP Status: " + response.statusCode());
        }
    }

    @Override
    public List<Order> parseOrdersFromJsonArray(String jsonData) {
        return List.of();
    }

    @Override
    public Order parseOrderFromJsonObject(String jsonData) {
        return null;
    }

    @Override
    public String buildJsonFromOrders(List<Order> orders) {
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
