package ke.co.ideagalore.olyxadmin.models;

public class Expense {

    String category, expenseId, date, time, description;
    int price;

    public Expense() {
    }

    public Expense(String category, String expenseId, String date, String time, String description, int price) {
        this.category = category;
        this.expenseId = expenseId;
        this.date = date;
        this.time = time;
        this.description = description;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
}
