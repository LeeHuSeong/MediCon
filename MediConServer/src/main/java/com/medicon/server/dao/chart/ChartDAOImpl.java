package com.medicon.server.dao.chart;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.medicon.server.dto.chart.ChartDTO;

import java.util.ArrayList;
import java.util.List;

public class ChartDAOImpl implements ChartDAO {

    private static final String CHARTS_COLLECTION = "charts";
    private static final String USERS_COLLECTION = "users";
    private final Firestore db;

    public ChartDAOImpl() {
        this.db = FirestoreClient.getFirestore();
    }

    // 🔸 유틸: ChartDTO List로 변환
    private List<ChartDTO> mapToChartList(List<QueryDocumentSnapshot> docs) {
        List<ChartDTO> charts = new ArrayList<>();
        for (QueryDocumentSnapshot doc : docs) {
            ChartDTO chart = doc.toObject(ChartDTO.class);
            charts.add(chart);
        }
        return charts;
    }

    @Override
    public List<ChartDTO> findAllCharts() {
        try {
            ApiFuture<QuerySnapshot> future = db.collection(CHARTS_COLLECTION).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            return mapToChartList(docs);
        } catch (Exception e) {
            System.err.println("[findAllCharts] Firestore read error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public List<ChartDTO> findChartByPatientName(String name) {
        List<ChartDTO> charts = new ArrayList<>();
        try {
            // 이름으로 uid 리스트 조회
            ApiFuture<QuerySnapshot> userFuture = db.collection(USERS_COLLECTION)
                    .whereEqualTo("name", name)
                    .get();
            List<QueryDocumentSnapshot> userDocs = userFuture.get().getDocuments();

            // 각 uid에 대해 차트 검색
            for (QueryDocumentSnapshot userDoc : userDocs) {
                String uid = userDoc.getString("uid");
                if (uid == null) continue;
                charts.addAll(findChartByPatientUid(uid));
            }
        } catch (Exception e) {
            System.err.println("[findChartByPatientName] Firestore query error: " + e.getMessage());
        }
        return charts;
    }

    @Override
    public List<ChartDTO> findChartByPatientUid(String uid) {
        try {
            ApiFuture<QuerySnapshot> chartFuture = db.collection(CHARTS_COLLECTION)
                    .whereEqualTo("patient_uid", uid)
                    .get();
            List<QueryDocumentSnapshot> chartDocs = chartFuture.get().getDocuments();
            return mapToChartList(chartDocs);
        } catch (Exception e) {
            System.err.println("[findChartByPatientUid] Firestore query error: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public ChartDTO findChartByChartId(String chartId) {
        try {
            DocumentSnapshot doc = db.collection(CHARTS_COLLECTION)
                    .document(chartId)
                    .get().get();
            if (doc.exists()) {
                return doc.toObject(ChartDTO.class);
            }
        } catch (Exception e) {
            System.err.println("[findByChartId] Firestore read error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void saveChart(ChartDTO chart) {
        System.out.println("저장하려는 차트: " + chart.getChart_id());
        try {
            db.collection(CHARTS_COLLECTION)
                    .document(chart.getChart_id())
                    .set(chart)
                    .get(); // 동기처리
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
