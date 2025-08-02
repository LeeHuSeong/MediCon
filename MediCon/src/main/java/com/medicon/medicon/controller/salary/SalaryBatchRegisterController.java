package com.medicon.medicon.controller.salary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medicon.medicon.config.AppConfig;
import com.medicon.medicon.model.BatchSalaryRow;
import com.medicon.medicon.model.SalaryRecordRequest;
import com.medicon.medicon.util.SalaryBaseTable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LongStringConverter;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SalaryBatchRegisterController {

    @FXML private TableView<BatchSalaryRow> salaryTable;
    @FXML private TableColumn<BatchSalaryRow, String> nameColumn;
    @FXML private TableColumn<BatchSalaryRow, String> rankColumn;
    @FXML private TableColumn<BatchSalaryRow, String> roleColumn;
    @FXML private TableColumn<BatchSalaryRow, Number> basePayColumn;
    @FXML private TableColumn<BatchSalaryRow, Long> bonusColumn;
    @FXML private TableColumn<BatchSalaryRow, String> statusColumn;
    @FXML private ComboBox<String> monthSelector;
    @FXML private Button payAllButton;

    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupMonthSelector();
        loadStaffUsers();
        payAllButton.setOnAction(e -> handlePayAll());
    }

    private void setupMonthSelector() {
        List<String> options = new ArrayList<>();
        for (int y = 2020; y <= 2030; y++) {
            for (int m = 1; m <= 12; m++) {
                options.add(String.format("%d-%02d", y, m));
            }
        }
        monthSelector.getItems().addAll(options);

        // 기본값: 오늘 날짜
        Calendar cal = Calendar.getInstance();
        String today = String.format("%d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
        monthSelector.setValue(today);
    }

    private void setupTableColumns() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        rankColumn.setCellValueFactory(cellData -> cellData.getValue().rankProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        basePayColumn.setCellValueFactory(cellData -> cellData.getValue().basePayProperty());
        bonusColumn.setCellValueFactory(cellData -> cellData.getValue().bonusProperty().asObject());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        bonusColumn.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));
        bonusColumn.setOnEditCommit(event -> {
            BatchSalaryRow row = event.getRowValue();
            row.setBonus(event.getNewValue());
        });

        salaryTable.setEditable(true);
    }

    private void loadStaffUsers() {
        new Thread(() -> {
            try {
                URL url = new URL(AppConfig.SERVER_BASE_URL + "/api/staff/list?role=doctor,nurse");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String response = reader.readLine();
                reader.close();

                Type responseType = new TypeToken<Map<String, Object>>(){}.getType();
                Map<String, Object> parsed = gson.fromJson(response, responseType);
                List<LinkedHashMap<String, Object>> data = (List<LinkedHashMap<String, Object>>) parsed.get("data");

                List<BatchSalaryRow> rowList = new ArrayList<>();

                for (Map<String, Object> obj : data) {
                    String uid = (String) obj.get("uid");
                    String name = (String) obj.get("name");
                    String role = (String) obj.get("role");
                    String rank = (String) obj.get("rank");

                    long basePay = SalaryBaseTable.getBasePay(role, rank);
                    rowList.add(new BatchSalaryRow(uid, name, role, rank, basePay));
                }

                Platform.runLater(() -> salaryTable.getItems().setAll(rowList));

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("직원 목록을 불러오지 못했습니다.");
            }
        }).start();
    }

    private void handlePayAll() {
        String[] ym = monthSelector.getValue().split("-");
        int year = Integer.parseInt(ym[0]);
        int month = Integer.parseInt(ym[1]);

        List<BatchSalaryRow> rows = salaryTable.getItems();

        for (BatchSalaryRow row : rows) {
            new Thread(() -> {
                try {
                    String uid = row.getUid();
                    String role = row.getRole();

                    SalaryRecordRequest req = new SalaryRecordRequest(year, month, row.getBasePay(), row.getBonus());

                    URL url = new URL(AppConfig.SERVER_BASE_URL + "/api/staff/salary/" + uid + "?role=" + role);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(gson.toJson(req).getBytes(StandardCharsets.UTF_8));
                    }

                    int responseCode = conn.getResponseCode();

                    Platform.runLater(() -> {
                        if (responseCode == 200) {
                            row.setStatus("지급 완료");
                        } else {
                            row.setStatus("실패 (" + responseCode + ")");
                        }
                        salaryTable.refresh();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        row.setStatus("실패 (예외)");
                        salaryTable.refresh();
                    });
                }
            }).start();
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("오류");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
