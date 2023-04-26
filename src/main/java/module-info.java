module example.loadcalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.ooxml;

    opens example.loanCalculator to javafx.fxml;
    exports example.loanCalculator;
    exports loans;
    opens loans to javafx.fxml;
}