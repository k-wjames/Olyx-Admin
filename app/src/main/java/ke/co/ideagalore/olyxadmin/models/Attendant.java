package ke.co.ideagalore.olyxadmin.models;

public class Attendant {
    String attendant, attendantId, store, status;

    public Attendant() {
    }

    public Attendant(String attendant, String attendantId, String store, String status) {
        this.attendant = attendant;
        this.attendantId = attendantId;
        this.store = store;
        this.status = status;
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
}
