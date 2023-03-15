package ke.co.ideagalore.olyxadmin.models;

public class STKPush {

    String  Password,Timestamp,  TransactionType,
            CallBackURL, AccountReference, TransactionDesc,PartyA, PhoneNumber;
    int Amount;
    long PartyB,BusinessShortCode;

    public STKPush() {
    }

    public STKPush(String password, String timestamp, String transactionType, String callBackURL, String accountReference,
                   String transactionDesc, String partyA, String phoneNumber, int amount, long partyB,
                   long businessShortCode) {
        Password = password;
        Timestamp = timestamp;
        TransactionType = transactionType;
        CallBackURL = callBackURL;
        AccountReference = accountReference;
        TransactionDesc = transactionDesc;
        PartyA = partyA;
        PhoneNumber = phoneNumber;
        Amount = amount;
        PartyB = partyB;
        BusinessShortCode = businessShortCode;
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

    public String getTransactionType() {
        return TransactionType;
    }

    public void setTransactionType(String transactionType) {
        TransactionType = transactionType;
    }

    public String getCallBackURL() {
        return CallBackURL;
    }

    public void setCallBackURL(String callBackURL) {
        CallBackURL = callBackURL;
    }

    public String getAccountReference() {
        return AccountReference;
    }

    public void setAccountReference(String accountReference) {
        AccountReference = accountReference;
    }

    public String getTransactionDesc() {
        return TransactionDesc;
    }

    public void setTransactionDesc(String transactionDesc) {
        TransactionDesc = transactionDesc;
    }

    public String getPartyA() {
        return PartyA;
    }

    public void setPartyA(String partyA) {
        PartyA = partyA;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public long getPartyB() {
        return PartyB;
    }

    public void setPartyB(long partyB) {
        PartyB = partyB;
    }

    public long getBusinessShortCode() {
        return BusinessShortCode;
    }

    public void setBusinessShortCode(long businessShortCode) {
        BusinessShortCode = businessShortCode;
    }


    @Override
    public String toString() {
        return "STKPush{" +
                "Password='" + Password + '\'' +
                ", Timestamp='" + Timestamp + '\'' +
                ", TransactionType='" + TransactionType + '\'' +
                ", CallBackURL='" + CallBackURL + '\'' +
                ", AccountReference='" + AccountReference + '\'' +
                ", TransactionDesc='" + TransactionDesc + '\'' +
                ", PartyA='" + PartyA + '\'' +
                ", PhoneNumber='" + PhoneNumber + '\'' +
                ", Amount=" + Amount +
                ", PartyB=" + PartyB +
                ", BusinessShortCode=" + BusinessShortCode +
                '}';
    }
}
