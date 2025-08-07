package com.medicon.server.controller;

import com.medicon.server.dao.chart.ChartDAO;
import com.medicon.server.dao.chart.ChartDAOImpl;
import com.medicon.server.dto.chart.ChartDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chart")
public class ChartController {
    private final ChartDAO chartDAO = new ChartDAOImpl();

    // 전체 차트 조회
    @GetMapping("/all")
    public List<ChartDTO> getAllCharts() {
        return chartDAO.findAllCharts();
    }

    // 단일 차트 조회
    @GetMapping("/{chartId}")
    public ChartDTO getChart(@PathVariable String chartId) {
        return chartDAO.findChartByChartId(chartId);
    }


    // 차트 저장
    @PostMapping("/save")
    public String saveChart(@RequestBody ChartDTO chart) {
        chartDAO.saveChart(chart);
        return "ok";
    }

    // 환자 이름으로 차트 전체 이력 조회
    @GetMapping("/by-name/{name}")
    public List<ChartDTO> getChartsByPatientName(@PathVariable String name) {
        return chartDAO.findChartByPatientName(name);
    }

    // 환자 UID로 차트 전체 이력 조회
    @GetMapping("/by-patient/{uid}")
    public List<ChartDTO> getChartsByPatientUid(@PathVariable String uid) {
        return chartDAO.findChartByPatientUid(uid);
    }
}