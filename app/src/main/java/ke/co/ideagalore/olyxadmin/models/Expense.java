package ke.co.ideagalore.olyxadmin.models;

public class Expense {

    String category, expenseId, time, description;
    int price;
    long date;

    public Expense() {
    }

    public Expense(String category, String expenseId, String time, String description, int price, long date) {
        this.category = category;
        this.expenseId = expenseId;
        this.time = time;
        this.description = description;
        this.price = price;
        this.date = date;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
