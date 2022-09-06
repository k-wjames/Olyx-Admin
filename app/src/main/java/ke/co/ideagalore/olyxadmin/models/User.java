package ke.co.ideagalore.olyxadmin.models;

public class User {
    String name, business;

    public User() {
    }

    public User(String name, String business, Terminal terminal) {
        this.name = name;
        this.business = business;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

}
