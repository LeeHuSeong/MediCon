package com.medicon.server.dao.chart;

import com.medicon.server.dto.chart.ChartDTO;
import java.util.List;

public interface ChartDAO {
    // 차트 전체 조회
    List<ChartDTO> findAllCharts();

    // 환자 이름으로 차트 조회
    List<ChartDTO> findChartByPatientName(String name);

    // 환자 UID로 차트 조회
    List<ChartDTO> findChartByPatientUid(String uid);

    // 차트 ID로 단일 차트 조회
    ChartDTO findChartByChartId(String chartId);

    // 차트 저장
    void saveChart(ChartDTO chart);
}
