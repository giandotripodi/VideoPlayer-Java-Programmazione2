module com.example.pointingplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.opencsv;

    opens com.example.pointingplayer to javafx.fxml;
    exports com.example.pointingplayer;
}