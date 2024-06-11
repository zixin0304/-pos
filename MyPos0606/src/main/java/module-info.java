module mypos {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql; //資料庫
    exports mypos;
    exports models;
    exports mypos_integration;
    exports mypos_db;
}
