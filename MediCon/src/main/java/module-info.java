module com.medicon.medicon {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;


    opens com.medicon.medicon.controller to javafx.fxml;
    opens com.medicon.medicon.controller.salary;
    opens com.medicon.medicon.model to com.google.gson;
    exports com.medicon.medicon;
    opens com.medicon.medicon.controller.staff;

    opens com.medicon.medicon.controller.medic to javafx.fxml;
    opens com.medicon.medicon.controller.patient to javafx.fxml;

    exports com.medicon.medicon.model to com.fasterxml.jackson.databind;
}