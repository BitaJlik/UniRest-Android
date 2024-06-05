package com.unirest.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unirest.api.BaseCallback;
import com.unirest.api.ICallback;
import com.unirest.data.models.Cooker;
import com.unirest.data.models.Dormitory;
import com.unirest.data.models.ErrorMessage;
import com.unirest.data.models.Floor;
import com.unirest.data.models.Notification;
import com.unirest.data.models.NotificationRequest;
import com.unirest.data.models.Payment;
import com.unirest.data.models.Request;
import com.unirest.data.models.RequestRegister;
import com.unirest.data.models.Room;
import com.unirest.data.models.User;
import com.unirest.data.models.UserPermit;
import com.unirest.data.models.UserSearch;
import com.unirest.data.models.Washer;
import com.unirest.data.retrofit.ApiServices;
import com.unirest.utils.DataCacheHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("unused")
public class DataNetHandler {
    private static final String tag = "DataNetHandler";
    // TODO: 16.03.2024
//    private final Retrofit retrofitMain = new Retrofit.Builder().baseUrl("http://unirest.c1.is/").build();
    private final Retrofit retrofitMain = new Retrofit.Builder().baseUrl("http://192.168.43.240:11111/").build();
//    private final Retrofit retrofitMain = new Retrofit.Builder().baseUrl("http://192.168.0.111:11111/").build();

    public void getServerStatus(ICallback<Boolean> serverEnabledCallback) {
        ApiServices services = retrofitMain.create(ApiServices.class);
        if (!DataCacheHandler.getInstance().isKeyExpired("server")) {
            Boolean server = (Boolean) DataCacheHandler.getInstance().get("server");
            if (server != null && server) {
                call(serverEnabledCallback, true);
                return;
            }
        }
        services.getStatus().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                call(serverEnabledCallback, true);
                DataCacheHandler.getInstance().put("server", true, 60_000);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                call(serverEnabledCallback, false);
                DataCacheHandler.getInstance().put("server", false, 60_000);
            }
        });
    }

    public void login(String login, String password, ICallback<String> tokenCallback, ICallback<String> errorCallback) {
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.login(login, password).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                DataNetHandler.this.call(tokenCallback, jsonString);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                DataNetHandler.this.call(errorCallback, t.getMessage());
            }
        });
    }

    public void login(String token, ICallback<String> tokenCallback, ICallback<String> errorCallback) {
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.login(token).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                DataNetHandler.this.call(tokenCallback, jsonString);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                DataNetHandler.this.call(errorCallback, t.getMessage());
            }
        });
    }

    public void register(String email, String password /*SHA256*/, ICallback<String> callbackToken, ICallback<String> error) {
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.register(email, password).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                DataNetHandler.this.call(callbackToken, jsonString);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                DataNetHandler.this.call(error, t.getMessage());
            }
        });
    }

    public void verifyAuth(String token, ICallback<Boolean> verifiedCallback) {
        if (token == null) {
            call(verifiedCallback, false);
            return;
        }
        if (!DataCacheHandler.getInstance().isKeyExpired("lastVerify")) {
            call(verifiedCallback, true);
        } else {
            ApiServices coreServices = retrofitMain.create(ApiServices.class);
            coreServices.verify(token).enqueue(new BaseCallback<ResponseBody>() {
                @Override
                public void onJson(Response<ResponseBody> response, String jsonString) {
                    boolean verified = response.code() == 202;
                    if (verified) {
                        DataCacheHandler.getInstance().put("lastVerify", System.currentTimeMillis(), 30_000);
                    }
                    DataNetHandler.this.call(verifiedCallback, verified);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    BaseCallback.super.onFailure(call, t);
                    DataNetHandler.this.call(verifiedCallback, false);
                }
            });
        }
    }

    public void readNotification(Long notification) {
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.read(notification).enqueue((BaseCallback<ResponseBody>) (response, jsonString) -> {
        });
    }

    public void receiveNotification(Long notification) {
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.receive(notification).enqueue((BaseCallback<ResponseBody>) (response, jsonString) -> {
        });
    }

    public void checkEmail(String email, ICallback<Boolean> isEmailFree) {
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.checkEmail(email).enqueue((BaseCallback<ResponseBody>) (response, jsonString) -> call(isEmailFree, response.code() == 200));
    }

    public void sendError(ErrorMessage message) {
        Log.i(tag, "Sending error");
        ApiServices coreServices = retrofitMain.create(ApiServices.class);
        coreServices.sendError(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(message))).enqueue((BaseCallback<ResponseBody>) (response, jsonString) -> Log.i(tag, "Error sent"));
    }

    public void getUser(String token, ICallback<User> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getUserInfo(token).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                User user = new Gson().fromJson(jsonString, User.class);
                call(callback, user);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void searchUser(String name, Long dormitoryId, ICallback<List<UserSearch>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.searchUser(name, dormitoryId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                List<UserSearch> user = new Gson().fromJson(jsonString, new TypeToken<List<UserSearch>>() {
                });
                call(callback, user);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getUserPermit(Long id, ICallback<UserPermit> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getUserPermit(id).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                UserPermit user = new Gson().fromJson(jsonString, UserPermit.class);
                call(callback, user);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getFloors(Long dormitoryId, ICallback<List<Floor>> listICallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getFloors(dormitoryId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(listICallback, new Gson().fromJson(jsonString, new TypeToken<List<Floor>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(listICallback, null);
            }
        });
    }

    public void getRooms(Long floorId, ICallback<List<Room>> listICallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getRooms(floorId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(listICallback, new Gson().fromJson(jsonString, new TypeToken<List<Room>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(listICallback, null);
            }
        });
    }

    public void getUsers(Long roomId, ICallback<List<User>> listICallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getUsers(roomId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(listICallback, new Gson().fromJson(jsonString, new TypeToken<List<User>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(listICallback, null);
            }
        });
    }

    public void updateUser(Long userId, User userNew, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.updateUser(userId, RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(userNew))).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, false);
            }
        });
    }

    public void uploadUser(Long userId, MultipartBody.Part body, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.uploadImage(userId, body).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, false);
            }
        });
    }

    public void uploadPaymentCheck(String checkId, MultipartBody.Part body, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.uploadPaymentCheck(checkId, body).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, false);
            }
        });
    }

    public void updateStatus(Long userId) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.updateStatus(userId).enqueue((BaseCallback<ResponseBody>) (response, jsonString) -> {

        });
    }

    public void callToMe(Long userId, NotificationRequest request, ICallback<Boolean> sentCallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.callToMe(userId, RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(request))).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(sentCallback, true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(sentCallback, false);
            }
        });
    }

    public void getNotifications(Long userId, ICallback<List<Notification>> notificationsCall) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getNotifications(userId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(notificationsCall, new Gson().fromJson(jsonString, new TypeToken<List<Notification>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(notificationsCall, null);
            }
        });
    }

    public void getDormitoryInfo(Long dormitoryId, ICallback<Dormitory> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getDormitory(dormitoryId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, Dormitory.class));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getPayments(Long id, ICallback<List<Payment>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getPayments(id).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Payment>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getDormitoryPayments(Long dormitoryId, ICallback<List<Payment>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getDormitoryPayments(dormitoryId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Payment>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void moderatePayment(Payment payment, boolean validPayment, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.moderatePayment(payment.getId(), validPayment).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, response.code() == 202);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, false);
            }
        });
    }

    public void uploadPayment(User user, Payment payment, ICallback<String> callbackUUID) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.uploadPayment(user.getId(), RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(payment))).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callbackUUID, jsonString);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callbackUUID, null);
            }
        });
    }

    public void getWasher(Long id, ICallback<Washer> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getWasher(id).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<Washer>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getCooker(Long id, ICallback<Cooker> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getCooker(id).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<Cooker>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getWashers(Long id, ICallback<List<Washer>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getWashers(id).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Washer>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getCookers(Long id, ICallback<List<Cooker>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getCookers(id).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Cooker>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void setBusyWasher(Long id, Long userId, int minutes, boolean busy, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        if (busy) {
            apiServices.setBusyWasher(id, minutes, userId).enqueue(new BaseCallback<ResponseBody>() {
                @Override
                public void onJson(Response<ResponseBody> response, String jsonString) {
                    call(callback, response.code() == 202);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    BaseCallback.super.onFailure(call, t);
                    call(callback, false);
                }
            });
        } else {
            apiServices.setFreeWasher(id, userId).enqueue(new BaseCallback<ResponseBody>() {
                @Override
                public void onJson(Response<ResponseBody> response, String jsonString) {
                    call(callback, response.code() == 202);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    BaseCallback.super.onFailure(call, t);
                    call(callback, false);
                }
            });
        }
    }

    public void setBusyCooker(Long id, Long userId, int minutes, boolean busy, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        if (busy) {
            apiServices.setBusyCooker(id, minutes, userId).enqueue(new BaseCallback<ResponseBody>() {
                @Override
                public void onJson(Response<ResponseBody> response, String jsonString) {
                    call(callback, response.code() == 202);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    BaseCallback.super.onFailure(call, t);
                    call(callback, false);
                }
            });
        } else {
            apiServices.setFreeCooker(id, userId).enqueue(new BaseCallback<ResponseBody>() {
                @Override
                public void onJson(Response<ResponseBody> response, String jsonString) {
                    call(callback, response.code() == 202);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    BaseCallback.super.onFailure(call, t);
                    call(callback, false);
                }
            });
        }
    }

    public void getUrlImageUser(User user, ICallback<String> urlCallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        call(urlCallback, apiServices.getImage(user.getId()).request().url().toString());
    }

    public void getUrlImageUser(UserPermit user, ICallback<String> urlCallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        call(urlCallback, apiServices.getImage(user.getId()).request().url().toString());
    }

    public void getUrlPayment(Payment payment, ICallback<String> urlCallback) {
        if (payment.getCheckId() == null) {
            urlCallback.call(null);
            return;
        }
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        call(urlCallback, apiServices.getImagePayment(payment.getCheckId()).request().url().toString());
    }

    public void getDormitoryRequests(Long dormitoryId, ICallback<List<Request>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);

        apiServices.getDormitoryRequests(dormitoryId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Request>>() {
                }.getType()));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getUserRequests(Long userId, ICallback<List<Request>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);

        apiServices.getUserRequests(userId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Request>>() {
                }.getType()));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void sendRequest(Long dormitoryId, Long userId, Long templateId, ICallback<ResponseBody> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.sendRequest(dormitoryId, userId, templateId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void sendGenerationRequest(Long requestId, HashMap<String, String> replacements, ICallback<ResponseBody> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(replacements));
        apiServices.sendGenerationRequest(requestId, body).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void registerNewRequest(RequestRegister requestObject, ICallback<ResponseBody> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(requestObject));
        apiServices.registerNewRequest(body).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getRequestKeys(Long templateId, ICallback<List<String>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);

        apiServices.getRequestKeys(templateId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<String>>() {
                }.getType()));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getRequestFile(String requestId, ICallback<List<String>> callback) {
        // TODO: 01.06.2024 File
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getRequestFile(requestId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<String>>() {
                }.getType()));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void removeUserFromRoom(Long userId, Long roomId, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.removeUserFromRoom(userId, roomId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, false);
            }
        });
    }

    public void addUserToRoom(Long userId, Long roomId, ICallback<Boolean> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.addUserToRoom(userId, roomId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, false);
            }
        });
    }

    public void searchUsersForRoom(boolean withoutRoom, String keyWord, ICallback<List<User>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.searchUsersForRoom(withoutRoom, keyWord).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<User>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    private <T> void call(ICallback<T> callback, T value) {
        if (callback != null) {
            callback.call(value);
        }
    }

    private JSONObject createJson(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException ignored) {
        }
        return null;
    }

    private JSONObject getJson(String jsonString, String nodeName) {
        try {
            return createJson(jsonString).getJSONObject(nodeName);
        } catch (JSONException ignored) {
        }
        return null;
    }

    private JSONArray getJsonArray(String jsonString, String nodeName) {
        try {
            return createJson(jsonString).getJSONArray(nodeName);
        } catch (JSONException ignored) {
        }
        return null;
    }

    private String fromBase64(String base64) {
        return new String(android.util.Base64.decode(base64, android.util.Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    //
    private static DataNetHandler instance;

    private DataNetHandler() {
    }

    public static DataNetHandler getInstance() {
        if (instance == null) {
            instance = new DataNetHandler();
        }
        return instance;
    }

}
