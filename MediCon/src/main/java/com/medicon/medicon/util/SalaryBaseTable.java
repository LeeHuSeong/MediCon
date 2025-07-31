package com.medicon.medicon.util;

import java.util.HashMap;
import java.util.Map;

public class SalaryBaseTable {

    private static final Map<String, Long> doctorBasePayMap = new HashMap<>();
    private static final Map<String, Long> nurseBasePayMap = new HashMap<>();

    static {
        // 의사 직급
        doctorBasePayMap.put("인턴", 4000000L);
        doctorBasePayMap.put("레지던트", 5000000L);
        doctorBasePayMap.put("전임의", 6000000L);
        doctorBasePayMap.put("조교수", 7000000L);
        doctorBasePayMap.put("부교수", 8000000L);
        doctorBasePayMap.put("교수", 9000000L);

        // 간호사 직급
        nurseBasePayMap.put("간호사", 3500000L);
        nurseBasePayMap.put("수간호사", 4200000L);
        nurseBasePayMap.put("책임간호사", 4800000L);
        nurseBasePayMap.put("수석간호사", 5500000L);
    }

    public static long getBasePay(String role, String rank) {
        if (role == null || rank == null) return 0L;

        switch (role.toLowerCase()) {
            case "doctor":
                return doctorBasePayMap.getOrDefault(rank, 0L);
            case "nurse":
                return nurseBasePayMap.getOrDefault(rank, 0L);
            default:
                return 0L;
        }
    }
}
