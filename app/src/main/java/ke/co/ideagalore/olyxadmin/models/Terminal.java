package ke.co.ideagalore.olyxadmin.models;

public class Terminal {
    String terminal, terminalId;

    public Terminal() {
    }

    public Terminal(String terminal, String terminalId) {
        this.terminal = terminal;
        this.terminalId = terminalId;
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
}
