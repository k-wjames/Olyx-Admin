package ke.co.ideagalore.olyxadmin.models;

public class Refill {

    int buyingPrice, markedPrice;
    String product, prodId;

    public Refill() {
    }

    public Refill(int buyingPrice, int markedPrice, String product, String prodId) {
        this.buyingPrice = buyingPrice;
        this.markedPrice = markedPrice;
        this.product = product;
        this.prodId = prodId;
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

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }
}
