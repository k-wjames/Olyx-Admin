package ke.co.ideagalore.olyxadmin.models;

public class User {
    String name, business;
    Terminal terminal;

    public User() {
    }

    public User(String name, String business, Terminal terminal) {
        this.name = name;
        this.business = business;
        this.terminal = terminal;
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

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
}
