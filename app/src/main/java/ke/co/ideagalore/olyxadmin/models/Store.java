package ke.co.ideagalore.olyxadmin.models;

public class Store {
    String store, storeId;

    public Store() {
    }

    public Store(String store, String storeId) {
        this.store = store;
        this.storeId = storeId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
