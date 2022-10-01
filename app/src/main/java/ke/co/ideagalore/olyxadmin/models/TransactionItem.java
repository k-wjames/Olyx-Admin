package ke.co.ideagalore.olyxadmin.models;

public class TransactionItem {

    int buyingPrice, markedPrice;
    String product;

    public TransactionItem() {
    }

    public TransactionItem(int buyingPrice, int markedPrice, String product) {
        this.buyingPrice = buyingPrice;
        this.markedPrice = markedPrice;
        this.product = product;
    }

    public int getBuyingPrice() {
        return buyingPrice;
    }

    public void setBuyingPrice(int buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public int getMarkedPrice() {
        return markedPrice;
    }

    public void setMarkedPrice(int markedPrice) {
        this.markedPrice = markedPrice;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String toString()
    {
        return product;
    }
}
