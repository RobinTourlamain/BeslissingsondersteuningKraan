module com.example.beslissingsondersteuningkraan {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens com.example.beslissingsondersteuningkraan to javafx.fxml;
    exports com.example.beslissingsondersteuningkraan;
}