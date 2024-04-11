package com.unirest.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unirest.api.BaseCallback;
import com.unirest.api.ICallback;
import com.unirest.data.models.ErrorMessage;
import com.unirest.data.models.Floor;
import com.unirest.data.models.Room;
import com.unirest.data.models.User;
import com.unirest.data.models.UserPermit;
import com.unirest.data.retrofit.ApiServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

@SuppressWarnings("unused")
public class DataNetHandler {
    private static final String tag = "DataNetHandler";
    // private final Retrofit retrofitMain = new Retrofit.Builder().baseUrl("https://unirest.site/").build(); // TODO: 16.03.2024
    private final Retrofit retrofitMain = new Retrofit.Builder().baseUrl("http://192.168.0.111:11111/").build();
    // >>> Hash verify
    private long lastVerify = 0;
    // <<< Hash verify

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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastVerify <= 30_000) {
            call(verifiedCallback, true);
        } else {
            ApiServices coreServices = retrofitMain.create(ApiServices.class);
            coreServices.verify(token).enqueue(new BaseCallback<ResponseBody>() {
                @Override
                public void onJson(Response<ResponseBody> response, String jsonString) {
                    boolean verified = response.code() == 202;
                    if (verified) {
                        lastVerify = System.currentTimeMillis();
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

    public void getFloors(Long dormitoryId, ICallback<List<Floor>> floors) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getFloors(dormitoryId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(floors, new Gson().fromJson(jsonString, new TypeToken<List<Floor>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(floors, null);
            }
        });
    }

    public void getRooms(Long floorId, ICallback<List<Room>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getRooms(floorId).enqueue(new BaseCallback<ResponseBody>() {
            @Override
            public void onJson(Response<ResponseBody> response, String jsonString) {
                call(callback, new Gson().fromJson(jsonString, new TypeToken<List<Room>>() {
                }));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                BaseCallback.super.onFailure(call, t);
                call(callback, null);
            }
        });
    }

    public void getUsers(Long roomId, ICallback<List<User>> callback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        apiServices.getUsers(roomId).enqueue(new BaseCallback<ResponseBody>() {
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

    public void getUrlImageUser(User user, ICallback<String> urlCallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        call(urlCallback, apiServices.getImage(user.getId()).request().url().toString());
    }

    public void getUrlImageUser(UserPermit user, ICallback<String> urlCallback) {
        ApiServices apiServices = retrofitMain.create(ApiServices.class);
        call(urlCallback, apiServices.getImage(user.getId()).request().url().toString());
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
