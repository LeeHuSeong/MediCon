package com.medicon.medicon.controller.medic.patient;

import com.medicon.medicon.model.PatientDTO;

/**
 * 환자 정보 검증 기능을 담당하는 클래스
 */
public class PatientValidator {

    /**
     * 환자 정보 입력 검증
     */
    public String validatePatientInfo(String name, String gender, String phone, String email, String address) {
        // 필수 필드 검증
        if (name.isEmpty()) {
            return "환자 이름을 입력해주세요.";
        }

        if (gender.isEmpty()) {
            return "성별을 입력해주세요.";
        }

        if (phone.isEmpty()) {
            return "연락처를 입력해주세요.";
        }

        // 전화번호 형식 검증
        if (!phone.matches("^[0-9-]+$")) {
            return "연락처는 숫자와 하이픈(-)만 입력 가능합니다.";
        }

        // 이메일 형식 검증 (비어있지 않은 경우에만)
        if (!email.isEmpty() && !email.contains("@")) {
            return "올바른 이메일 형식을 입력해주세요.";
        }

        // 이름 길이 검증
        if (name.length() > 50) {
            return "환자 이름은 50자 이내로 입력해주세요.";
        }

        // 성별 값 검증
        if (!gender.equals("남") && !gender.equals("여") && 
            !gender.equals("남성") && !gender.equals("여성") &&
            !gender.equals("M") && !gender.equals("F") &&
            !gender.equals("Male") && !gender.equals("Female")) {
            return "성별은 '남', '여', '남성', '여성', 'M', 'F' 중 하나로 입력해주세요.";
        }

        // 전화번호 길이 검증
        if (phone.length() > 20) {
            return "연락처는 20자 이내로 입력해주세요.";
        }

        // 이메일 길이 검증
        if (email.length() > 100) {
            return "이메일은 100자 이내로 입력해주세요.";
        }

        // 주소 길이 검증
        if (address.length() > 200) {
            return "주소는 200자 이내로 입력해주세요.";
        }

        return null; // 검증 통과
    }

    /**
     * 환자 정보 변경사항 확인
     */
    public String getChanges(PatientDTO currentPatient, String name, String gender, String phone, String email, String address) {
        boolean hasChanges = false;
        StringBuilder changedFields = new StringBuilder();

        if (!name.equals(currentPatient.getName())) {
            hasChanges = true;
            changedFields.append("• 이름: ").append(currentPatient.getName()).append(" → ").append(name).append("\n");
        }
        
        if (!gender.equals(currentPatient.getGender())) {
            hasChanges = true;
            changedFields.append("• 성별: ").append(currentPatient.getGender()).append(" → ").append(gender).append("\n");
        }
        
        if (!phone.equals(currentPatient.getPhone())) {
            hasChanges = true;
            changedFields.append("• 연락처: ").append(currentPatient.getPhone()).append(" → ").append(phone).append("\n");
        }
        
        String currentEmail = currentPatient.getEmail() != null ? currentPatient.getEmail() : "";
        if (!email.equals(currentEmail)) {
            hasChanges = true;
            changedFields.append("• 이메일: ").append(currentEmail.isEmpty() ? "(없음)" : currentEmail)
                        .append(" → ").append(email.isEmpty() ? "(없음)" : email).append("\n");
        }
        
        String currentAddress = currentPatient.getAddress() != null ? currentPatient.getAddress() : "";
        if (!address.equals(currentAddress)) {
            hasChanges = true;
            changedFields.append("• 주소: ").append(currentAddress.isEmpty() ? "(없음)" : currentAddress)
                        .append(" → ").append(address.isEmpty() ? "(없음)" : address).append("\n");
        }

        return hasChanges ? changedFields.toString() : null;
    }

    /**
     * 이름 형식 검증
     */
    public boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        // 한글, 영어, 공백만 허용 (숫자, 특수문자 제외)
        return name.matches("^[가-힣a-zA-Z\\s]+$") && name.length() <= 50;
    }

    /**
     * 전화번호 형식 검증 (더 엄격한 검증)
     */
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // 한국 전화번호 형식: 010-1234-5678, 02-1234-5678 등
        return phone.matches("^(01[016789]|02|0[3-9][0-9])-?[0-9]{3,4}-?[0-9]{4}$");
    }

    /**
     * 이메일 형식 검증 (더 엄격한 검증)
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // 이메일은 선택사항
        }
        
        // 기본적인 이메일 형식 검증
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * 주민번호 앞자리 검증 (생년월일)
     */
    public boolean isValidBirthDate(String birthDate) {
        if (birthDate == null || birthDate.trim().isEmpty()) {
            return true; // 선택사항
        }
        
        // YYMMDD 형식 검증
        if (!birthDate.matches("^[0-9]{6}$")) {
            return false;
        }
        
        try {
            int year = Integer.parseInt(birthDate.substring(0, 2));
            int month = Integer.parseInt(birthDate.substring(2, 4));
            int day = Integer.parseInt(birthDate.substring(4, 6));
            
            // 월 검증 (1-12)
            if (month < 1 || month > 12) {
                return false;
            }
            
            // 일 검증 (1-31, 간단한 검증)
            if (day < 1 || day > 31) {
                return false;
            }
            
            // 2월 29일 검증 (윤년 고려하지 않은 간단한 검증)
            if (month == 2 && day > 29) {
                return false;
            }
            
            // 30일까지만 있는 월 검증
            if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
                return false;
            }
            
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 성별 정규화 (다양한 입력을 표준 형식으로 변환)
     */
    public String normalizeGender(String gender) {
        if (gender == null) return null;
        
        String normalized = gender.trim().toLowerCase();
        
        switch (normalized) {
            case "남":
            case "남성":
            case "m":
            case "male":
                return "남";
            case "여":
            case "여성":
            case "f":
            case "female":
                return "여";
            default:
                return gender.trim(); // 원본 반환
        }
    }

    /**
     * 전화번호 정규화 (하이픈 추가)
     */
    public String normalizePhoneNumber(String phone) {
        if (phone == null) return null;
        
        // 숫자만 추출
        String numbersOnly = phone.replaceAll("[^0-9]", "");
        
        // 11자리 휴대폰 번호인 경우
        if (numbersOnly.length() == 11 && numbersOnly.startsWith("010")) {
            return numbersOnly.substring(0, 3) + "-" + 
                   numbersOnly.substring(3, 7) + "-" + 
                   numbersOnly.substring(7);
        }
        
        // 10자리 일반 전화번호인 경우 (02-1234-5678)
        if (numbersOnly.length() == 10 && numbersOnly.startsWith("02")) {
            return numbersOnly.substring(0, 2) + "-" + 
                   numbersOnly.substring(2, 6) + "-" + 
                   numbersOnly.substring(6);
        }
        
        // 11자리 일반 전화번호인 경우 (031-123-4567)
        if (numbersOnly.length() == 11 && !numbersOnly.startsWith("01")) {
            return numbersOnly.substring(0, 3) + "-" + 
                   numbersOnly.substring(3, 6) + "-" + 
                   numbersOnly.substring(6);
        }
        
        return phone; // 형식을 인식할 수 없으면 원본 반환
    }
}