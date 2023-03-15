package ke.co.ideagalore.olyxadmin.models;

public class STKResponse {
    String MerchantRequestID, CheckoutRequestID,ResponseDescription,CustomerMessage;
    int ResponseCode;

    public STKResponse() {
    }

    public STKResponse(String merchantRequestID, String checkoutRequestID, String responseDescription,
                       String customerMessage, int responseCode) {
        MerchantRequestID = merchantRequestID;
        CheckoutRequestID = checkoutRequestID;
        ResponseDescription = responseDescription;
        CustomerMessage = customerMessage;
        ResponseCode = responseCode;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public void setMerchantRequestID(String merchantRequestID) {
        MerchantRequestID = merchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public void setCheckoutRequestID(String checkoutRequestID) {
        CheckoutRequestID = checkoutRequestID;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        ResponseDescription = responseDescription;
    }

    public String getCustomerMessage() {
        return CustomerMessage;
    }

    public void setCustomerMessage(String customerMessage) {
        CustomerMessage = customerMessage;
    }

    public int getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(int responseCode) {
        ResponseCode = responseCode;
    }
}
