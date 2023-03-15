package ke.co.ideagalore.olyxadmin.models;

public class Transaction {

    String transactionId, transactionType, productId, product, store, time, attendant, terminalId;
    int quantity, buyingPrice, sellingPrice, totalPrice, profit, updatedStock;
    long date;

    public Transaction() {
    }

    public Transaction(String transactionId, String transactionType, String productId, String product,
                       String store, String time, String attendant, String terminalId, int quantity,
                       int buyingPrice, int sellingPrice, int totalPrice, int profit, long date,int updatedStock) {
        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.productId = productId;
        this.product = product;
        this.store = store;
        this.time = time;
        this.attendant = attendant;
        this.terminalId = terminalId;
        this.quantity = quantity;
        this.buyingPrice = buyingPrice;
        this.sellingPrice = sellingPrice;
        this.totalPrice = totalPrice;
        this.profit = profit;
        this.date = date;
        this.updatedStock = updatedStock;
    }

    public int getUpdatedStock() {
        return updatedStock;
    }

    public void setUpdatedStock(int updatedStock) {
        this.updatedStock = updatedStock;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
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

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public int getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(int sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
