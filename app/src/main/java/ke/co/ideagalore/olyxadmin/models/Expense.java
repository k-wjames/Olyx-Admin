package ke.co.ideagalore.olyxadmin.models;

public class Expense {

    String expense, expenseId,date, time, description;
    int price, quantity;

    public Expense() {
    }

    public Expense(String expense, String expenseId, String date, String time, String description, int price, int quantity) {
        this.expense = expense;
        this.expenseId = expenseId;
        this.date = date;
        this.time = time;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getExpense() {
        return expense;
    }

    public void setExpense(String expense) {
        this.expense = expense;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
