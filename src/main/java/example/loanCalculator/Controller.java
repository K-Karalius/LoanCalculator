package example.loanCalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.LineChart;
import loans.AnnuityLoan;
import loans.LinearLoan;
import loans.Month;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class Controller implements Initializable {

    private AnnuityLoan annuityLoan;
    private LinearLoan linearLoan;
    private boolean chosenLoanTypeIsLinear;
    @FXML
    private TextField loanSum;
    @FXML
    private TextField loanInterest;
    @FXML
    private ComboBox<String> loanType;
    @FXML
    private ComboBox<Integer> loanMonths;
    @FXML
    private ComboBox<Integer> loanYears;
    @FXML
    private CheckBox checkBox;
    @FXML
    private TextField postponementFrom;
    @FXML
    private TextField postponementPerc;
    @FXML
    private TextField postponementDuration;


    @FXML
    private Button reportButton;

    @FXML
    private TableView<Month> table;
    @FXML
    private TableColumn<Month, Integer> columnMonth;
    @FXML
    private TableColumn<Month, Double> rawColumn;
    @FXML
    private TableColumn<Month, Double> payLeftColumn;
    @FXML
    private TableColumn<Month, Double> wholeColumn;
    @FXML
    private TableColumn<Month, Double> interestColumn;
    @FXML
    private LineChart<String, Number> chart;

    @FXML
    private Label filterLabel;
    @FXML
    private TextField fromFilter;
    @FXML
    private TextField toFilter;
    @FXML
    private Button filterButton;
    @FXML
    private Label filterRangeDisplay;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        columnMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        rawColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyPrincipal"));
        interestColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyInterest"));
        wholeColumn.setCellValueFactory(new PropertyValueFactory<>("monthlyPay"));
        payLeftColumn.setCellValueFactory(new PropertyValueFactory<>("balanceOwed"));

        loanMonths.setItems(FXCollections.observableArrayList(0,1,2,3,4,5,6,7,8,9,10,11));
        loanYears.setItems(FXCollections.observableArrayList(
                5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30));
        loanType.setItems(FXCollections.observableArrayList("Linear", "Annuity"));
        postponementFrom.setVisible(false);
        postponementDuration.setVisible(false);
        postponementPerc.setVisible(false);
        setComponentsVisibility(false);
    }
    @FXML
    public void submit() {
        chart.getData().clear();
        double sum;
        double interest;
        try{
            sum = Double.parseDouble(loanSum.getText());
            interest = Double.parseDouble(loanInterest.getText());
            if(loanMonths.getValue() != null && loanYears.getValue() != null && loanType.getValue() != null &&
                    (Double.compare(0.00, interest) <= 0 && Double.compare(100.00, interest) >= 0) &&
                    (Double.compare(50000.00, sum) <= 0 && Double.compare(1000000.00, sum) >= 0))
            {
                checkAndSaveLoanType();
                annuityLoan = new AnnuityLoan(sum, loanYears.getValue(), loanMonths.getValue(), interest);
                annuityLoan.calculatePayments();
                linearLoan = new LinearLoan(sum, loanYears.getValue(), loanMonths.getValue(), interest);
                linearLoan.calculatePayments();
                checkAndAddPostponement();
                fillTheTable();
                createCharts();
                filterRangeDisplay.setText("Choose between 1 - " + annuityLoan.getDuration());
                setComponentsVisibility(true);
            } else{
                throw new Exception();
            }
        }catch(Exception e){
            showAlertBox("Check if all the fields/boxes are filled out correctly!");
        }

    }

    private void checkAndAddPostponement() throws Exception {
        if(checkBox.isSelected()){
            int from = Integer.parseInt(postponementFrom.getText());
            int duration = Integer.parseInt(postponementDuration.getText());
            double percentage = Double.parseDouble(postponementPerc.getText());
            if((from > 0 && from <= annuityLoan.getDuration()) && duration > 0
                    && (Double.compare(0.00, percentage) <= 0 && Double.compare(100.00, percentage) >= 0)){
                linearLoan.addPostponement(from, duration, percentage);
                annuityLoan.addPostponement(from, duration, percentage);
            }else{
                throw new Exception();
            }
        }
    }

    public void fillTheTable(){
        if(chosenLoanTypeIsLinear)
        {
            table.setItems(FXCollections.observableArrayList(linearLoan.getMonths()));
        }else{
            table.setItems(FXCollections.observableArrayList(annuityLoan.getMonths()));
        }
    }


    public void createCharts(){
        XYChart.Series<String, Number> annuityChart = new XYChart.Series<>();
        XYChart.Series<String, Number> linearChart = new XYChart.Series<>();
        for(Month month : annuityLoan.getMonths())
        {
            int monthCount = month.getMonth();
            double payment = month.getMonthlyPay();
            annuityChart.getData().add(new XYChart.Data<>(Integer.toString(monthCount), payment));
        }
        for(Month month : linearLoan.getMonths()){
            int monthCount = month.getMonth();
            double payment = month.getMonthlyPay();
            linearChart.getData().add(new XYChart.Data<>(Integer.toString(monthCount), payment));
        }
        chart.getData().add(annuityChart);
        chart.getData().add(linearChart);
    }

    public void onActionCheckBox()
    {
        if(checkBox.isSelected())
        {
            postponementFrom.setVisible(true);
            postponementDuration.setVisible(true);
            postponementPerc.setVisible(true);
        }else{
            postponementFrom.setVisible(false);
            postponementDuration.setVisible(false);
            postponementPerc.setVisible(false);
        }

    }

    @FXML
    public void exportToFile() {
        try{
            if(chosenLoanTypeIsLinear){
                linearLoan.printToFile();
            }else{
                annuityLoan.printToFile();
            }
        }catch (Exception e){
            showAlertBox("Unexpected error when creating a file!");
        }
    }

    public void setComponentsVisibility(boolean sus){
        table.setVisible(sus);
        chart.setVisible(sus);
        reportButton.setVisible(sus);
        filterLabel.setVisible(sus);
        fromFilter.setVisible(sus);
        toFilter.setVisible(sus);
        filterButton.setVisible(sus);
        filterRangeDisplay.setVisible(sus);

    }
    @FXML
    public void filter() {
        int monthCount;
        ObservableList<Month> months;
        if(chosenLoanTypeIsLinear)
        {
            monthCount = linearLoan.getDuration();
            months = FXCollections.observableArrayList(linearLoan.getMonths());
        }else {
            monthCount = annuityLoan.getDuration();
            months = FXCollections.observableArrayList(annuityLoan.getMonths());
        }
        int monthFrom;
        int monthUntil;
        try{
            monthFrom = Integer.parseInt(fromFilter.getText());
            monthUntil = Integer.parseInt(toFilter.getText());
            if (monthFrom < 1 || monthUntil > monthCount)
            {
                showAlertBox("Out of interval bounds");
            }else if(monthFrom > monthUntil){
                showAlertBox("Month from cannot be bigger than month until");
            }else{
                Predicate<Month> filter = month -> month.getMonth() >= monthFrom && month.getMonth() <= monthUntil;
                months = months.filtered(filter);
                table.setItems(months);

            }
        }catch(Exception e){
            showAlertBox("Bad format!");
        }
    }

    public void showAlertBox(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void checkAndSaveLoanType()
    {
        if(loanType.getValue().equals("Linear"))
        {
            chosenLoanTypeIsLinear = true;
        }else{
            chosenLoanTypeIsLinear = false;
        }

    }

}