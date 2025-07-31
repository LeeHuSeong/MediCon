package com.medicon.server.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.salary.SalaryRecordRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SalaryService {

    private static final String COLLECTION_DOCTORS = "doctors";
    private static final String COLLECTION_NURSES = "nurses";
    private static final String COLLECTION_SALARY = "salary";

    public void saveSalary(String uid, String role, SalaryRecordRequest req) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        long basePay = req.getBasePay();
        long bonus = req.getBonus();
        long totalPay = basePay + bonus;

        long pension = Math.round(totalPay * 0.09);
        long healthInsurance = Math.round(totalPay * 0.07);
        long employmentInsurance = Math.round(totalPay * 0.009);
        long incomeTax = Math.round(totalPay * 0.03);
        long localTax = Math.round(incomeTax * 0.1);

        long totalDeductions = pension + healthInsurance + employmentInsurance + incomeTax + localTax;
        long netPay = totalPay - totalDeductions;

        // doctor + nurse 모두 대응
        String roleCollection = role.equalsIgnoreCase("doctor") ? COLLECTION_DOCTORS :
                role.equalsIgnoreCase("nurse")  ? COLLECTION_NURSES  : null;

        if (roleCollection == null) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        String docId = String.format("%04d-%02d", req.getYear(), req.getMonth());

        Map<String, Object> deductions = Map.of(
                "pension", pension,
                "healthInsurance", healthInsurance,
                "employmentInsurance", employmentInsurance,
                "incomeTax", incomeTax,
                "localTax", localTax
        );

        Map<String, Object> salaryDoc = new HashMap<>();
        salaryDoc.put("year", req.getYear());
        salaryDoc.put("month", req.getMonth());
        salaryDoc.put("basePay", basePay);
        salaryDoc.put("bonus", bonus);
        salaryDoc.put("totalPay", totalPay);
        salaryDoc.put("deductions", deductions);
        salaryDoc.put("totalDeductions", totalDeductions);
        salaryDoc.put("netPay", netPay);
        salaryDoc.put("paidAt", FieldValue.serverTimestamp());

        // Firestore 경로: users/{uid}/{roleCollection}/salary/{YYYY-MM}
        db.collection("users")
                .document(uid)
                .collection(roleCollection)
                .document("salary") // 잘못된 중간 document 생성을 방지하기 위해 생략
                .collection(COLLECTION_SALARY)
                .document(docId)
                .set(salaryDoc)
                .get();
    }

}
