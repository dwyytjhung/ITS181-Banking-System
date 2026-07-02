module com.gabriel.twoforms {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.gabriel.twoforms to javafx.fxml;
    exports com.gabriel.twoforms;
    
    opens com.gabriel.twoforms.controllers to javafx.fxml;
    exports com.gabriel.twoforms.controllers;
    
    opens com.gabriel.twoforms.models to javafx.base, javafx.fxml;
    exports com.gabriel.twoforms.models;
    
    exports com.gabriel.twoforms.services;
}