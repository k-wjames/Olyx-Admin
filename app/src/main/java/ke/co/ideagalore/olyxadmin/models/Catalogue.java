package ke.co.ideagalore.olyxadmin.models;

public class Catalogue {

    String prodId, product, category, shop;
    int stockedQuantity, buyingPrice, markedPrice,  soldItems,availableStock;

    public Catalogue() {
    }

    public Catalogue(String prodId, String product, String category, String shop,
                     int stockedQuantity, int buyingPrice, int markedPrice,
                     int soldItems, int availableStock) {
        this.prodId = prodId;
        this.product = product;
        this.category = category;
        this.shop = shop;
        this.stockedQuantity = stockedQuantity;
        this.buyingPrice = buyingPrice;
        this.markedPrice = markedPrice;
        this.soldItems = soldItems;
        this.availableStock = availableStock;
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

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
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

    public int getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(int soldItems) {
        this.soldItems = soldItems;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }
}
