package com.project;

public enum OrderStatus {
    PROCESSING("처리 중"),
    SHIPPED("배송 중"),
    COMPLETED("완료");

    private final String description;

    // 생성자를 통해 상태에 대한 설명 추가
    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
