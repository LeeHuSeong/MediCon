package com.medicon.medicon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        //Test
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(
//                "/com/medicon/medicon/view/medic/medic_main/MedicalCertificateForm.fxml"
//        ));


//        //Main_view
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(
//                "/com/medicon/medicon/view/medic/medic_main/MedicMain.fxml"
//        ));

        //Patient_view
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(
//                "/com/medicon/medicon/view/patient/patient_main/PatientMainView.fxml"
//        ));

        //login_view
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/medicon/medicon/view/login.fxml"
        ));
        Scene scene = new Scene(loader.load());
        stage.setTitle("MediCon 로그인");
        stage.setScene(scene);

        // 최소 크기 설정
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
