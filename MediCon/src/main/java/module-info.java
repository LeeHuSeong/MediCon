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
    requires java.desktop;

    opens com.medicon.medicon.controller.medic to javafx.fxml;
    opens com.medicon.medicon.controller.patient to javafx.fxml;

    opens com.medicon.medicon.controller.medic.patient to javafx.fxml;
    opens com.medicon.medicon.controller;

    opens com.medicon.medicon.controller.sale to javafx.fxml;
    opens com.medicon.medicon.controller.salary;
    opens com.medicon.medicon.model to com.google.gson;
    opens com.medicon.medicon.controller.staff;
    exports com.medicon.medicon.controller;
    exports com.medicon.medicon;
    exports com.medicon.medicon.model to com.fasterxml.jackson.databind;
    opens com.medicon.medicon.controller.medic.form to javafx.fxml;
}