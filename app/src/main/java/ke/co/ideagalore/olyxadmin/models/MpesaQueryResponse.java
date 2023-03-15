package ke.co.ideagalore.olyxadmin.models;

public class MpesaQueryResponse {
    int ResponseCode;
    String ResponseDescription,MerchantRequestID, CheckoutRequestID, ResultCode,ResultDesc;

    public MpesaQueryResponse() {
    }

    public MpesaQueryResponse(int responseCode, String responseDescription, String merchantRequestID,
                              String checkoutRequestID, String resultCode, String resultDesc) {
        ResponseCode = responseCode;
        ResponseDescription = responseDescription;
        MerchantRequestID = merchantRequestID;
        CheckoutRequestID = checkoutRequestID;
        ResultCode = resultCode;
        ResultDesc = resultDesc;
    }

    public int getResponseCode() {
        return ResponseCode;
    }

    public String getResponseDescription() {
        return ResponseDescription;
    }

    public String getMerchantRequestID() {
        return MerchantRequestID;
    }

    public String getCheckoutRequestID() {
        return CheckoutRequestID;
    }

    public String getResultCode() {
        return ResultCode;
    }

    public String getResultDesc() {
        return ResultDesc;
    }
}
