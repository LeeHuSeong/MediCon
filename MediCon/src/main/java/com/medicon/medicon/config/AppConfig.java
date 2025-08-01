package com.medicon.medicon.config;

public class AppConfig {

    //Firebase Web API Key (Firebase 콘솔 > 프로젝트 설정 > 일반 > Web API Key)
    public static final String FIREBASE_WEB_API_KEY = "AIzaSyAfu-EKiBrWqK58Q_M-CmQR6DlhVxSwmug";

    // Spring 서버 주소
    public static final String SERVER_BASE_URL = "http://localhost:8080";

    // 로그인 엔드포인트( 조합해서 사용)
    public static final String LOGIN_ENDPOINT = "/auth/login";

    // JWT 저장
    public static final String JWT_FILE_PATH = "jwt.json";

    //관리자 비밀번호
    public static final String adminPassword = "123"; // 실제 비밀번호

}
