package com.medicon.medicon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/medicon/medicon/view/medic/medic_main/Main.fxml"
        ));
        Scene scene = new Scene(loader.load());
        stage.setTitle("MediCon 로그인");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
