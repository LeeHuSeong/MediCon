package com.medicon.server.service;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.salary.SalaryRecordRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SalaryService {

    private static final String COLLECTION_DOCTORS = "doctors";
    private static final String COLLECTION_NURSES = "nurses";
    private static final String SALARY_DOC = "salary";
    private static final String SALARY_LIST = "salaryList";

    public void saveSalary(String uid, String role, SalaryRecordRequest req) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String roleCollection = getRoleCollection(role);
        String docId = String.format("%04d-%02d", req.getYear(), req.getMonth());

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

        Map<String, Object> deductions = Map.of(
                "pension", pension,
                "healthInsurance", healthInsurance,
                "employmentInsurance", employmentInsurance,
                "incomeTax", incomeTax,
                "localTax", localTax
        );

        Map<String, Object> salaryData = new HashMap<>();
        salaryData.put("year", req.getYear());
        salaryData.put("month", req.getMonth());
        salaryData.put("basePay", basePay);
        salaryData.put("bonus", bonus);
        salaryData.put("totalPay", totalPay);
        salaryData.put("deductions", deductions);
        salaryData.put("totalDeductions", totalDeductions);
        salaryData.put("netPay", netPay);
        salaryData.put("paidAt", FieldValue.serverTimestamp());

        DocumentReference salaryDocRef = db.collection("users")
                .document(uid)
                .collection(roleCollection)
                .document(SALARY_DOC);

        // 강제로 salary 문서 존재시킴
        salaryDocRef.set(new HashMap<>()).get();

        salaryDocRef.collection(SALARY_LIST)
                .document(docId)
                .set(salaryData)
                .get();
    }

    public Map<String, Object> getSalaryByMonth(String uid, String role, int year, int month) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String roleCollection = getRoleCollection(role);
        String docId = String.format("%04d-%02d", year, month);

        DocumentReference docRef = db.collection("users")
                .document(uid)
                .collection(roleCollection)
                .document(SALARY_DOC)
                .collection(SALARY_LIST)
                .document(docId);

        DocumentSnapshot snapshot = docRef.get().get();
        if (!snapshot.exists()) {
            throw new NoSuchElementException("해당 월의 급여 기록이 존재하지 않습니다.");
        }

        return snapshot.getData();
    }

    public List<Map<String, Object>> getAllSalary(String uid, String role) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String roleCollection = getRoleCollection(role);

        CollectionReference salaryListRef = db.collection("users")
                .document(uid)
                .collection(roleCollection)
                .document(SALARY_DOC)
                .collection(SALARY_LIST);

        List<QueryDocumentSnapshot> docs = salaryListRef.get().get().getDocuments();
        List<Map<String, Object>> result = new ArrayList<>();

        for (QueryDocumentSnapshot doc : docs) {
            Map<String, Object> data = doc.getData();
            data.put("id", doc.getId());
            result.add(data);
        }

        return result;
    }

    public void editSalary(String uid, String role, String yearMonth, SalaryRecordRequest req) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String roleCollection = getRoleCollection(role);

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

        Map<String, Object> deductions = Map.of(
                "pension", pension,
                "healthInsurance", healthInsurance,
                "employmentInsurance", employmentInsurance,
                "incomeTax", incomeTax,
                "localTax", localTax
        );

        Map<String, Object> salaryData = new HashMap<>();
        salaryData.put("year", req.getYear());
        salaryData.put("month", req.getMonth());
        salaryData.put("basePay", basePay);
        salaryData.put("bonus", bonus);
        salaryData.put("totalPay", totalPay);
        salaryData.put("deductions", deductions);
        salaryData.put("totalDeductions", totalDeductions);
        salaryData.put("netPay", netPay);
        salaryData.put("paidAt", FieldValue.serverTimestamp());

        DocumentReference salaryDocRef = db.collection("users")
                .document(uid)
                .collection(roleCollection)
                .document(SALARY_DOC);

        salaryDocRef.set(new HashMap<>()).get(); // salary 문서 생성

        salaryDocRef.collection(SALARY_LIST)
                .document(yearMonth)
                .set(salaryData)
                .get();
    }

    public void deleteSalary(String uid, String role, String yearMonth) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        String roleCollection = getRoleCollection(role);

        DocumentReference docRef = db.collection("users")
                .document(uid)
                .collection(roleCollection)
                .document(SALARY_DOC)
                .collection(SALARY_LIST)
                .document(yearMonth);

        docRef.delete().get();
    }

    private String getRoleCollection(String role) {
        return switch (role.toLowerCase()) {
            case "doctor" -> COLLECTION_DOCTORS;
            case "nurse" -> COLLECTION_NURSES;
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}
