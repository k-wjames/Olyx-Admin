package ke.co.ideagalore.olyxadmin.models;

public class TransactionItem {

    int buyingPrice, markedPrice, availableStock;
    String product, productId;

    public TransactionItem() {
    }

    public TransactionItem(int buyingPrice, int markedPrice, int availableStock, String product, String productId) {
        this.buyingPrice = buyingPrice;
        this.markedPrice = markedPrice;
        this.availableStock = availableStock;
        this.product = product;
        this.productId = productId;
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

    public int getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(int availableStock) {
        this.availableStock = availableStock;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String toString()
    {
        return product;
    }
}
