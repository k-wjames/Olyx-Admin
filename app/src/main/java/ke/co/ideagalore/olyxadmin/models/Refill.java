package ke.co.ideagalore.olyxadmin.models;

public class Refill {

    int buyingPrice, markedPrice, numberStocked;
    String product, prodId;

    public Refill() {
    }

    public Refill(int buyingPrice, int markedPrice, int numberStocked, String product, String prodId) {
        this.buyingPrice = buyingPrice;
        this.markedPrice = markedPrice;
        this.numberStocked = numberStocked;
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

    public int getNumberStocked() {
        return numberStocked;
    }

    public void setNumberStocked(int numberStocked) {
        this.numberStocked = numberStocked;
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
