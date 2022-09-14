package ke.co.ideagalore.olyxadmin.models;

public class Catalogue {

    String prodId, product, category;
    int stockedQuantity, buyingPrice, markedPrice;

    public Catalogue() {
    }

    public Catalogue(String prodId, String product, String category, int stockedQuantity, int buyingPrice, int markedPrice) {
        this.prodId = prodId;
        this.product = product;
        this.category = category;
        this.stockedQuantity = stockedQuantity;
        this.buyingPrice = buyingPrice;
        this.markedPrice = markedPrice;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStockedQuantity() {
        return stockedQuantity;
    }

    public void setStockedQuantity(int stockedQuantity) {
        this.stockedQuantity = stockedQuantity;
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
}
