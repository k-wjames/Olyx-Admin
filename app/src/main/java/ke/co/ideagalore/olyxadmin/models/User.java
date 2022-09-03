package ke.co.ideagalore.olyxadmin.models;

public class User {
    String name;
    Terminal terminal;

    public User() {
    }

    public User(String name, Terminal terminal) {
        this.name = name;
        this.terminal = terminal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public void setTerminal(Terminal terminal) {
        this.terminal = terminal;
    }
}
