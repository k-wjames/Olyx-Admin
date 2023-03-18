package ke.co.ideagalore.olyxadmin.ui.activities;

import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ke.co.ideagalore.olyxadmin.R;
import ke.co.ideagalore.olyxadmin.models.AccessToken;
import ke.co.ideagalore.olyxadmin.models.Daraja;
import ke.co.ideagalore.olyxadmin.models.MpesaQuery;
import ke.co.ideagalore.olyxadmin.models.MpesaQueryResponse;
import ke.co.ideagalore.olyxadmin.models.STKPush;
import ke.co.ideagalore.olyxadmin.models.STKResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MPesa extends AppCompatActivity {

    TextView textView;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpesa);
        textView = findViewById(R.id.tvToken);
        getAccessToken();
    }

    private void getAccessToken() {
        String app_key = "EEDZKb41lKhliZd9zAelhJjmFQXikXxO";
        String app_secret = "bGqy4DGiyJfT0EJq";
        String appKeySecret = app_key + ":" + app_secret;
        String authorizationString = "Basic " + Base64.encodeToString((appKeySecret).getBytes(), Base64.DEFAULT);

        String auth = authorizationString.replace("\n", "");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sandbox.safaricom.co.ke/oauth/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Daraja daraja = retrofit.create(Daraja.class);

        Call<AccessToken> call = daraja.getToken(auth);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (!response.isSuccessful()) {
                    textView.setText("Response code: " + response.code());
                    return;
                }
                AccessToken accessToken = response.body();
                assert accessToken != null;
                token = accessToken.getAccess_token();
                textView.setText("Access token: " + token);

                initSTKPush(token);

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

                textView.setText(t.getMessage());
            }
        });
    }


    private void initSTKPush(String token) {

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String shortCode = "174379";
        String passKey = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
        String fullString = shortCode + passKey + timeStamp;
        String pass = Base64.encodeToString((fullString).getBytes(), Base64.DEFAULT);
        String password = pass.replace("\n", "");

        STKPush stkPush = new STKPush();
        stkPush.setBusinessShortCode(174379);
        stkPush.setPassword(password);
        stkPush.setTimestamp(timeStamp);
        stkPush.setTransactionType("CustomerPayBillOnline");
        stkPush.setAmount(1);
        stkPush.setPartyA("254714947370");
        stkPush.setPartyB(174379);
        stkPush.setPhoneNumber("254714947370");
        stkPush.setCallBackURL("https://us-central1-olyx-33199.cloudfunctions.net/api/");
        stkPush.setAccountReference("Ginger Ltd");
        stkPush.setTransactionDesc("Payment for Forbidden Ginger");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Daraja daraja = retrofit.create(Daraja.class);

        String bearer = "Bearer " + token;

        Call<STKResponse> pushSTK = daraja.postSTKPush(bearer, stkPush);
        pushSTK.enqueue(new Callback<STKResponse>() {
            @Override
            public void onResponse(Call<STKResponse> call, Response<STKResponse> response) {
                if (!response.isSuccessful()) {
                    String error = null;
                    try {
                        error = response.errorBody().string();
                        textView.setText("Response Code: " + response.code() + " message " + error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if(response.body() != null){
                    String checkoutId=response.body().getCheckoutRequestID();
                    queryTransaction(password, checkoutId, timeStamp,shortCode, bearer);

                }

            }

            @Override
            public void onFailure(Call<STKResponse> call, Throwable t) {
                textView.setText(t.getMessage());
            }
        });

    }

    private void queryTransaction( String password, String checkoutId, String timeStamp, String shortCode, String bearer) {

        MpesaQuery mpesaQuery= new MpesaQuery();
        mpesaQuery.setPassword(password);
        mpesaQuery.setCheckoutRequestID(checkoutId);
        mpesaQuery.setTimestamp(timeStamp);
        mpesaQuery.setBusinessShortCode(shortCode);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://sandbox.safaricom.co.ke/mpesa/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Daraja daraja = retrofit.create(Daraja.class);

        Call<MpesaQueryResponse> call=daraja.getMpesaQueryResponse(bearer,mpesaQuery);
        call.enqueue(new Callback<MpesaQueryResponse>() {
            @Override
            public void onResponse(Call<MpesaQueryResponse> call, Response<MpesaQueryResponse> response) {
                if (!response.isSuccessful()){
                    try {
                        textView.setText("Code: "+response.code()+ ""+response.errorBody().string() );
                        queryTransaction(password,checkoutId,timeStamp,shortCode,bearer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                if (response.body()!=null){
                    textView.setText(response.body().getResponseDescription() );
                }
            }

            @Override
            public void onFailure(Call<MpesaQueryResponse> call, Throwable t) {

            }
        });


    }
}