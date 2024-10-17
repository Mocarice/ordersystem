package com.project;

import java.io.IOException;
import java.util.List;

public interface OrderProcessInterface<T> {
    // 외부 시스템에서 주문 데이터를 가져오는 메서드
    void fetchOrders() throws IOException, InterruptedException;
    // 주문 데이터를 외부 시스템으로 전송하는 메서드
    void sendOrders(List<T> orders) throws IOException, InterruptedException;

    // JSON 배열을 주문 리스트로 변환하는 메서드
    List<T> parseOrdersFromJsonArray(String jsonData);
    // JSON 객체 데이터를 파싱하여 주문 객체로 변환하는 메서드
    T parseOrderFromJsonObject(String jsonData);

    // 주문 데이터를 JSON 형식으로 변환하는 메서드
    String buildJsonFromOrders(List<T> orders);
}
