package ke.co.ideagalore.olyxadmin.models;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Daraja {
    @GET("generate?grant_type=client_credentials")
    Call<AccessToken> getToken(@Header("Authorization") String auth);

    @POST("processrequest")
    Call<STKResponse> postSTKPush(@Header("Authorization") String token, @Body STKPush stkPush);

    @POST("stkpushquery/v1/query")
    Call<MpesaQueryResponse>getMpesaQueryResponse(@Header("Authorization") String token, @Body MpesaQuery query);

}
