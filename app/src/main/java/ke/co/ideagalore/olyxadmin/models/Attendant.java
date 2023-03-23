package ke.co.ideagalore.olyxadmin.models;

public class Attendant {
    String attendant, attendantId, store, status,emailId,terminal;

    public Attendant() {
    }

    public Attendant(String attendant, String attendantId, String store, String status, String emailId, String terminal) {
        this.attendant = attendant;
        this.attendantId = attendantId;
        this.store = store;
        this.status = status;
        this.emailId = emailId;
        this.terminal = terminal;
    }

    public String getAttendant() {
        return attendant;
    }

    public void setAttendant(String attendant) {
        this.attendant = attendant;
    }

    public String getAttendantId() {
        return attendantId;
    }

    public void setAttendantId(String attendantId) {
        this.attendantId = attendantId;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
}
