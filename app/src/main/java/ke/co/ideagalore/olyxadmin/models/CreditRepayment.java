package ke.co.ideagalore.olyxadmin.models;

public class CreditRepayment {

    int amount, balance;
    String repaymentId,customer, phone, product,date, time, attendant, store;

    public CreditRepayment() {
    }

    public CreditRepayment(int amount, int balance, String repaymentId, String customer, String phone,
                           String product, String date, String time, String attendant, String store) {
        this.amount = amount;
        this.balance = balance;
        this.repaymentId = repaymentId;
        this.customer = customer;
        this.phone = phone;
        this.product = product;
        this.date = date;
        this.time = time;
        this.attendant = attendant;
        this.store = store;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(String repaymentId) {
        this.repaymentId = repaymentId;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
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

    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
