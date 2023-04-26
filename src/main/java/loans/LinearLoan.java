package loans;

public class LinearLoan extends AnnuityLoan{

    public LinearLoan(double sum, int yearCount, int monthCount, double interest){
        super(sum, yearCount, monthCount, interest);
    }
    @Override
    public void calculatePayments(){
        Month newMonth;
        addFirstMonth();
        double temp;
        for(int i = 1; i < duration; i++)
        {
            newMonth = new Month();
            newMonth.setMonth(i + 1);
            temp = roundToTwoDecimal(months.get(i - 1).getMonthlyPrincipal());
            newMonth.setMonthlyPrincipal(temp);
            temp = roundToTwoDecimal(months.get(i - 1).getBalanceOwed() * interest);
            newMonth.setMonthlyInterest(temp);
            temp = roundToTwoDecimal(newMonth.getMonthlyPrincipal() + newMonth.getMonthlyInterest());
            newMonth.setMonthlyPay(temp);
            temp = roundToTwoDecimal(months.get(i - 1).getBalanceOwed() - newMonth.getMonthlyPrincipal());
            newMonth.setBalanceOwed(temp);
            months.add(newMonth);
        }
        fixLastMonth();
    }

    @Override
    protected void addFirstMonth(){
        Month newMonth = new Month();
        newMonth.setMonth(1);
        double temp = roundToTwoDecimal(sum / duration);
        newMonth.setMonthlyPrincipal(temp);
        temp = roundToTwoDecimal(sum * interest);
        newMonth.setMonthlyInterest(temp);
        temp = roundToTwoDecimal(newMonth.getMonthlyPrincipal() + newMonth.getMonthlyInterest());
        newMonth.setMonthlyPay(temp);
        temp = roundToTwoDecimal(sum - newMonth.getMonthlyPrincipal());
        newMonth.setBalanceOwed(temp);
        months.add(newMonth);
    }
}
