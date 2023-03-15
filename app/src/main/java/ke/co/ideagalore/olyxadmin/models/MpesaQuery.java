package ke.co.ideagalore.olyxadmin.models;

public class MpesaQuery {

    String Password,Timestamp, BusinessShortCode, CheckoutRequestID ;

    public MpesaQuery() {
    }

    public MpesaQuery(String password, String timestamp, String businessShortCode, String checkoutRequestID) {
        Password = password;
        Timestamp = timestamp;
        BusinessShortCode = businessShortCode;
        CheckoutRequestID = checkoutRequestID;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(String businessShortCode) {
        BusinessShortCode = businessShortCode;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        CheckoutRequestID = checkoutRequestID;
    }
}
