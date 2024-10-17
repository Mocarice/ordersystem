package com.project;

public class JsonTypeChecker {
    //JSON을 처리하기 위해 리스트 형태의 다건인지, 오브젝트 형태의 단건인지 확인하는 메소드
    public static boolean isJsonArray(String jsonData) {
        jsonData = jsonData.trim(); // 앞뒤 공백 제거
        return jsonData.startsWith("["); // 대괄호로 시작하면 배열
    }

    public static boolean isJsonObject(String jsonData) {
        jsonData = jsonData.trim();
        return jsonData.startsWith("{");
    }
}
