package ke.co.ideagalore.olyxadmin.models;

public class CreditRepayment {

    int amount, balance;
    String repaymentId, customer, phone, product, time, attendant, store;
    long date;

    public CreditRepayment() {
    }

    public CreditRepayment(int amount, int balance, String repaymentId, String customer, String phone,
                           String product, String time, String attendant, String store, long date) {
        this.amount = amount;
        this.balance = balance;
        this.repaymentId = repaymentId;
        this.customer = customer;
        this.phone = phone;
        this.product = product;
        this.time = time;
        this.attendant = attendant;
        this.store = store;
        this.date = date;
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
