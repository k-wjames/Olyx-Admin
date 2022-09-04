package ke.co.ideagalore.olyxadmin.models;

public class Terminal {
    String terminal, terminalId;
    Store store;

    public Terminal() {
    }

    public Terminal(String terminal, String terminalId, Store store) {
        this.terminal = terminal;
        this.terminalId = terminalId;
        this.store = store;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
