package com.project;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;

public class ExternalSystemOrderApi implements OrderProcessInterface<Order> {

    private final OrderRepository orderRepository;
    private final Logger logger = Logger.getLogger(ExternalSystemOrderApi.class.getName());
    public ExternalSystemOrderApi(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 외부 시스템에서 주문 데이터를 가져오는 메서드 (HTTP GET)
    @Override
    public void fetchOrders(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        int retryCount = 5;
        int attempt = 0;
        // HTTP 요청 보내기, 재시도 횟수 총 {retryCount}회
        while(attempt < retryCount) {
            try{
                attempt++;
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // 응답이 성공적일 때만 처리
                if (response.statusCode() == 200) {
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
                } else {
                    throw new RuntimeException("Failed to fetch orders. HTTP Status: " + response.statusCode() + ", Response Body: " + response.body());
                }
            }catch (IOException | RuntimeException e) {
                logger.info("Attempt " + attempt + ", failed: " + e.getMessage());

                //재시도 횟수 {retryCount}회가 넘어가면 종료
                if(attempt >= retryCount) {
                    logger.info("Failed to fetch orders after " + retryCount + " attempts.");
                }
                try {
                    Thread.sleep(1000);  // 1초 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.info("InterruptedException occured while retrying: " + ie.getMessage());
                }
            }catch (InterruptedException ie){
                Thread.currentThread().interrupt();
                logger.info("InterruptedException occured while fetching order: " + ie.getMessage());
            }catch (Exception e) {
                logger.info("An unexpected error occurred while fetching order: " + e.getMessage());
            }
        }
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
