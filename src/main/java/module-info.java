module com.cqu.hhms {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    opens com.cqu.hhms to javafx.fxml;
    opens com.cqu.hhms.model to javafx.base;
    exports com.cqu.hhms;
}
