package ke.co.ideagalore.olyxadmin.models;

public class Stores {
    String store, storeId, location,agent, agentId;

    //Catalogue catalogue;

    public Stores() {
    }

    public Stores(String store, String storeId, String location, String agent, String agentId) {
        this.store = store;
        this.storeId = storeId;
        this.location = location;
        this.agent = agent;
        this.agentId = agentId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
