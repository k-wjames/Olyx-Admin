package ke.co.ideagalore.olyxadmin.models;

public class Credit {
    String product,name, phone, creditId, date, time, attendant, store;
    int amount, quantity;

    public Credit() {
    }

    public Credit(String product, String name, String phone, String creditId, String date, String time,
                  String attendant, String store, int amount, int quantity) {
        this.product = product;
        this.name = name;
        this.phone = phone;
        this.creditId = creditId;
        this.date = date;
        this.time = time;
        this.attendant = attendant;
        this.store = store;
        this.amount = amount;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
