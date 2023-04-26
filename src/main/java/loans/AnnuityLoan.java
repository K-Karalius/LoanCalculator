package loans;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
public class AnnuityLoan {

    protected double sum;
    protected int duration;
    protected double interest;
    protected ArrayList<Month> months;

    public AnnuityLoan(double sum, int yearCount, int monthCount, double interest) {
        this.sum = sum;
        this.duration = yearCount * 12 + monthCount;
        this.interest = interest / 100 / 12;
        months = new ArrayList<Month>();
    }


    public void calculatePayments(){
        Month newMonth;
        addFirstMonth();
        double temp;
        for(int i = 1; i < duration ; i++){
            newMonth = new Month();
            newMonth.setMonth(i + 1);
            temp = roundToTwoDecimal(months.get(i - 1).getMonthlyPay());
            newMonth.setMonthlyPay(temp);
            temp = roundToTwoDecimal(months.get(i - 1).getBalanceOwed() * interest);
            newMonth.setMonthlyInterest(temp);
            temp = roundToTwoDecimal(newMonth.getMonthlyPay() - newMonth.getMonthlyInterest());
            newMonth.setMonthlyPrincipal(temp);
            temp = roundToTwoDecimal(months.get(i - 1).getBalanceOwed() - newMonth.getMonthlyPrincipal());
            newMonth.setBalanceOwed(temp);
            months.add(newMonth);
        }
        fixLastMonth();
    }

    protected void addFirstMonth(){
        Month newMonth = new Month();
        newMonth.setMonth(1);
        double temp = roundToTwoDecimal((interest * sum)/(1 - Math.pow((1 + interest), (double)(duration * -1))));
        newMonth.setMonthlyPay(temp);
        temp = roundToTwoDecimal(sum * interest);
        newMonth.setMonthlyInterest(temp);
        temp = roundToTwoDecimal(newMonth.getMonthlyPay() - temp);
        newMonth.setMonthlyPrincipal(temp);
        temp = roundToTwoDecimal(sum - temp);
        newMonth.setBalanceOwed(temp);
        months.add(newMonth);
    }

    public void addPostponement(int start, int monthCount, double interest){
        Month month;
        duration += monthCount;
        interest = interest / 100 / 12;
        start -= 1;                                                         // indexes start from 0, so -1;
        double leftPayment;
        if(start == 0){
            leftPayment = roundToTwoDecimal(sum);
        }else{
            leftPayment = months.get(start - 1).getBalanceOwed();
        }
        double interestPay = roundToTwoDecimal(interest * leftPayment);
        int end = monthCount + start - 1;                                   // -1 because we don't include last month
        for(int i = start; i <= end; ++i){
            month = new Month();
            month.setMonth(i + 1);
            month.setMonthlyInterest(interestPay);
            month.setMonthlyPay(interestPay);
            month.setMonthlyPrincipal(0.00);
            month.setBalanceOwed(leftPayment);
            months.add(i, month);
        }
        for(int i = end + 1; i < months.size(); ++i){
            months.get(i).setMonth(i + 1);
        }
    }

    protected double roundToTwoDecimal(double num){
        return (Math.round(num * 100.0) / 100.0);
    }

    protected void fixLastMonth(){
        Month month = months.get(duration - 1); //or months.get(months.size() - 1);
        double temp = roundToTwoDecimal(month.getMonthlyPrincipal() + month.getBalanceOwed());
        month.setMonthlyPrincipal(temp);
        temp = roundToTwoDecimal(month.getMonthlyPay() + month.getBalanceOwed());
        month.setMonthlyPay(temp);
        month.setBalanceOwed(0.00);

    }

    public void printToFile() throws Exception {

        String[] columns = {"Month", "Principal", "Interest", "Payment", "Left unpaid"};
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("loan");

        Row headerRow = sheet.createRow(0);
        for(int i = 0; i < columns.length; i++){
            headerRow.createCell(i).setCellValue(columns[i]);
        }

        int rowNum = 1;
        Row row;
        for(Month month : months)
        {
            row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(month.getMonth());
            row.createCell(1).setCellValue(month.getMonthlyPrincipal());
            row.createCell(2).setCellValue(month.getMonthlyInterest());
            row.createCell(3).setCellValue(month.getMonthlyPay());
            row.createCell(4).setCellValue(month.getBalanceOwed());
        }

        for(int i = 0; i < columns.length; i++){
            sheet.autoSizeColumn(i);
        }

        File file = new File("loan.xlsx");
        FileOutputStream fileOut = new FileOutputStream(file);
        workbook.write(fileOut);
        workbook.close();
        fileOut.close();

    }
    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }
    public ArrayList<Month> getMonths() {
        return months;
    }

    public void setMonths(ArrayList<Month> months) {
        this.months = months;
    }


}
