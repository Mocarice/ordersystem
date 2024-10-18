package com.project;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public interface OrderProcessInterface<T> {
    final Logger logger = Logger.getLogger(OrderProcessInterface.class.getName());
    // 외부 시스템에서 주문 데이터를 가져오는 메서드
    void fetchOrders(String url) throws IOException, InterruptedException;
    // 주문 데이터를 외부 시스템으로 전송하는 메서드
    default void sendOrders(List<T> orders, String url) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
        String jsonOrders = buildJsonFromOrder(orders); // 주문 리스트를 JSON 형식으로 변환

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonOrders))
                .build();
        int retryCount = 5;
        int attempt = 0;
        // 재시도 횟수 총 {retryCount}회
        while(attempt < retryCount) {
            try {
                attempt++;
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("Failed to send orders. HTTP Status: " + response.statusCode());
                }
            }catch (IOException | RuntimeException e) {
                logger.info("Attempt " + attempt + ", failed: " + e.getMessage());
                //재시도 횟수 {retryCount}회가 넘어가면 종료
                if(attempt >= retryCount) {
                    logger.info("Failed to send orders after " + retryCount + " attempts.");
                }
                try {
                    Thread.sleep(1000);  // 1초 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.info("InterruptedException occured while retrying: " + ie.getMessage());
                }
            }catch (InterruptedException ie){
                Thread.currentThread().interrupt();
                logger.info("InterruptedException occured while retrying: " + ie.getMessage());
            }catch (Exception e) {
                logger.info("An unexpected error occurred while retrying: " + e.getMessage());
            }
        }
    }
    // JSON 배열을 주문 리스트로 변환하는 메서드
    default List<T> parseOrdersFromJsonArray(String jsonData) {
        jsonData = jsonData.trim();

        // 대괄호 제거
        if (jsonData.startsWith("[") && jsonData.endsWith("]")) {
            jsonData = jsonData.substring(1, jsonData.length() - 1);
        } else {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        List<T> orders = new ArrayList<>();

        // 각 객체를 중괄호 기준으로 분리
        String[] jsonObjects = jsonData.split("(?<=}),\\s*(?=\\{)");

        for (String jsonObject : jsonObjects) {
            T order = parseOrderFromJsonObject(jsonObject); // 단일 객체 파싱
            orders.add(order);
        }

        return orders;
    }
    // JSON 객체 데이터를 파싱하여 주문 객체로 변환하는 메서드
    T parseOrderFromJsonObject(String jsonData);
    // 주문 데이터 리스트를 JSON 형식으로 변환하는 메서드
    String buildJsonFromOrder(List<T> orders);

    // JSON을 처리하기 위해 리스트 형태의 다건인지, 오브젝트 형태의 단건인지 확인하는 메소드들
    default boolean isJsonArray(String jsonData) {
        jsonData = jsonData.trim();
        return jsonData.startsWith("[");
    }
    default boolean isJsonObject(String jsonData) {
        jsonData = jsonData.trim();
        return jsonData.startsWith("{");
    }
}
