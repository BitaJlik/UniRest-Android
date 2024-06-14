package com.unirest.data.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {

    @GET("server/status")
    Call<ResponseBody> getStatus();

    @POST("user/login")
    Call<ResponseBody> login(@Header("Username") String username, @Header("Password") String password /*SHA256*/);

    @POST("user/login")
    Call<ResponseBody> login(@Header("Token") String token);

    @POST("user/register")
    Call<ResponseBody> register(@Header("Email") String email, @Header("Password") String password);

    @GET("user/check/token")
    Call<ResponseBody> verify(@Header("Token") String token);

    @POST("server/error")
    Call<ResponseBody> sendError(@Body RequestBody jsonError);

    @GET("user/check/email")
    Call<ResponseBody> checkEmail(@Query("email") String email);

    @GET("user/info")
    Call<ResponseBody> getUserInfo(@Header("Token") String token);

    @GET("user/permit")
    Call<ResponseBody> getUserPermit(@Query("Id") Long id);

    @GET("floor/of")
    Call<ResponseBody> getFloors(@Query("id") Long dormitoryId);

    @GET("room/of")
    Call<ResponseBody> getRooms(@Query("id") Long dormitoryId);

    @GET("user/of")
    Call<ResponseBody> getUsers(@Query("id") Long roomId);

    @POST("user/update")
    Call<ResponseBody> updateUser(@Query("id") Long roomId, @Body RequestBody requestBody);

    @GET("user/image/{id}")
    Call<ResponseBody> getImage(@Path("id") Long id);

    @GET("user/search")
    Call<ResponseBody> searchUser(@Query("name") String name, @Query("id") Long id);

    @GET("payment/check/{id}")
    Call<ResponseBody> getImagePayment(@Path("id") String checkId);

    @Multipart
    @POST("user/image/upload")
    Call<ResponseBody> uploadImage(@Query("id") Long id, @Part MultipartBody.Part image);

    @Multipart
    @POST("payment/check/upload")
    Call<ResponseBody> uploadPaymentCheck(@Query("id") String id, @Part MultipartBody.Part image);

    @POST("payment/upload")
    Call<ResponseBody> uploadPayment(@Query("id") Long userId, @Body RequestBody payment);

    @GET("notification/list")
    Call<ResponseBody> getNotifications(@Query("id") Long id);

    @POST("notification/call")
    Call<ResponseBody> callToMe(@Query("id") Long id, @Body RequestBody request);

    @POST("notification/read")
    Call<ResponseBody> read(@Query("id") Long id);

    @POST("notification/receive")
    Call<ResponseBody> receive(@Query("id") Long id);

    @GET("dormitory/get")
    Call<ResponseBody> getDormitory(@Query("id") Long id);

    @POST("user/update/status")
    Call<ResponseBody> updateStatus(@Query("id") Long id);

    @GET("payment/list")
    Call<ResponseBody> getPayments(@Query("id") Long id);

    @GET("payment/moderate/list")
    Call<ResponseBody> getDormitoryPayments(@Query("id") Long id);

    @POST("payment/moderate")
    Call<ResponseBody> moderatePayment(@Query("id") Long id, @Query("valid") boolean validPayment);

    @GET("washer/get")
    Call<ResponseBody> getWasher(@Query("id") Long id);

    @GET("cooker/get")
    Call<ResponseBody> getCooker(@Query("id") Long id);

    @GET("washer/of")
    Call<ResponseBody> getWashers(@Query("id") Long floorId);

    @GET("cooker/of")
    Call<ResponseBody> getCookers(@Query("id") Long floorId);

    @POST("washer/busy")
    Call<ResponseBody> setBusyWasher(@Query("id") Long id, @Query("time") int minutes, @Query("userId") Long userId);

    @POST("cooker/busy")
    Call<ResponseBody> setBusyCooker(@Query("id") Long id, @Query("time") int minutes, @Query("userId") Long userId);

    @POST("washer/free")
    Call<ResponseBody> setFreeWasher(@Query("id") Long id, @Query("userId") Long userId);

    @POST("cooker/free")
    Call<ResponseBody> setFreeCooker(@Query("id") Long id, @Query("userId") Long userId);

    @GET("request/list")
    Call<ResponseBody> getDormitoryRequests(@Query("id") Long dormitoryId);

    @GET("request/of")
    Call<ResponseBody> getUserRequests(@Query("id") Long userId);

    @POST("request/put")
    Call<ResponseBody> sendRequest(@Query("id") Long dormitoryId, @Query("userId") Long userId, @Query("templateId") Long templateId);

    @GET("request/keys")
    Call<ResponseBody> getRequestKeys(@Query("id") Long templateId);

    @POST("request/generate")
    Call<ResponseBody> sendGenerationRequest(@Query("id") Long requestId, @Body RequestBody body);

    @GET("request/get/{request}")
    Call<ResponseBody> getRequestFile(@Path("request") String requestId);

    @POST("request/new")
    Call<ResponseBody> registerNewRequest(@Body RequestBody body);

    @POST("user/room/add")
    Call<ResponseBody> addUserToRoom(@Query("id") Long userId, @Query("roomId") Long roomId);

    @POST("user/room/remove")
    Call<ResponseBody> removeUserFromRoom(@Query("id") Long userId, @Query("roomId") Long roomId);

    @GET("user/room/searching")
    Call<ResponseBody> searchUsersForRoom(@Query("hasRoom") boolean hasRoom, @Query("keyWord") String keyWord);

    @POST("dormitory/admin/update")
    Call<ResponseBody> adminUpdateDormitory(@Body RequestBody body);

    @POST("floor/admin/update")
    Call<ResponseBody> adminUpdateFloor(@Body RequestBody body);

    @POST("room/admin/update")
    Call<ResponseBody> adminUpdateRoom(@Body RequestBody body);

    @POST("washer/admin/update")
    Call<ResponseBody> adminUpdateWasher(@Query("remove") boolean remove, @Body RequestBody body);

    @POST("cooker/admin/update")
    Call<ResponseBody> adminUpdateCooker(@Query("remove") boolean remove, @Body RequestBody body);

    @POST("paid/send")
    Call<ResponseBody> sendPaid(@Body RequestBody body);

}
