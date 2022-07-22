package ke.co.ideagalore.olyxadmin.models;

public class Product {
    String name, brand, shop, prodId;
    int quantity, capacity;

    public Product() {
    }

    public Product(String name, String brand, String shop, String prodId, int quantity, int capacity) {
        this.name = name;
        this.brand = brand;
        this.shop = shop;
        this.prodId = prodId;
        this.quantity = quantity;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
