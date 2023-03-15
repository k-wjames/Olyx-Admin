package ke.co.ideagalore.olyxadmin.models;

public class Terminal {
    String business, businessId, proprietor;
    Stores stores;

    public Terminal() {
    }

    public Terminal(String business, String businessId, String proprietor, Stores stores) {
        this.business = business;
        this.businessId = businessId;
        this.proprietor = proprietor;
        this.stores = stores;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getProprietor() {
        return proprietor;
    }

    public void setProprietor(String proprietor) {
        this.proprietor = proprietor;
    }

    public Stores getStore() {
        return stores;
    }

    public void setStore(Stores stores) {
        this.stores = stores;
    }
}
