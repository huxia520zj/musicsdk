package com.eryanet.m85musicsdk.tools;

import android.util.Log;

import com.eryanet.m85musicsdk.constants.CommonConstants;
import com.eryanet.m85musicsdk.constants.NetWorkUrLConstants;
import com.eryanet.m85musicsdk.inter.IHttpCallback;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RequestNet {
    private static final String TAG = "zhangjiao";

    public static void baseGetRequest(final String finalUrl, final IHttpCallback iHttpCallback) {
        try {
            Request.Builder getBuilder = new Request.Builder()
                    .addHeader("appID", CommonConstants.APP_ID)
                    .addHeader("Authorization", CommonConstants.TOKEN)
                    .url(finalUrl).get();
            Request build = getBuilder.build();
            new OkHttpClient().newCall(build).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG,"onFailure "+e.getMessage());
                    iHttpCallback.onFailure(0, e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code() == 200) {
                        String result = response.body().string();
                        Log.e(TAG, "finalUrl = " + finalUrl);
                        Log.e(TAG, "result = " + result);
                        try {
                            JSONObject object = new JSONObject(result);
                            int statusCode = object.getInt("statusCode");
                            String errorMsg = object.getString("errorMsg");
                            Log.e(TAG, "statusCode = " + statusCode);
                            Log.e(TAG, "errorMsg = " + errorMsg);
                            String data = object.getString("data");
                            if (statusCode == 200) {
                                iHttpCallback.onResponse(data, finalUrl);
                            }else if(statusCode == 304){
                                iHttpCallback.onFailure(304, "user不能为空");
                            }else if(statusCode == 513) {
                                iHttpCallback.onFailure(513, "token信息无效");
                            } else {
                                iHttpCallback.onFailure(0, errorMsg);
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "baseGetRequest JSONException");
                            iHttpCallback.onFailure(0, e.getMessage());
                        }

                    }
                }
            });
        } catch (Exception e) {
            iHttpCallback.onFailure(0, e.getMessage());
        }


    }

    //type 0 添加收藏 1删除收藏
    public static void basePostRequest(final String finalUrl, int type, String param, final IHttpCallback iHttpCallback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map map = new HashMap();
        map.put("userID",CommonConstants.USER_ID);
        if(type == 0) {
            map.put("content",param);
        }else if(type == 1) {
            map.put("favoriteID",param);
        }
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(JSON,gson.toJson(map));

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("appID", CommonConstants.APP_ID)
                .addHeader("Authorization", CommonConstants.TOKEN)
                .url(finalUrl)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                iHttpCallback.onFailure(0, e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e(TAG, "finalUrl = " + finalUrl);
                    Log.e(TAG, "result = " + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        int statusCode = object.getInt("statusCode");
                        String errorMsg = object.getString("errorMsg");
                        Log.e(TAG, "statusCode = " + statusCode);
                        Log.e(TAG, "errorMsg = " + errorMsg);
                        String data = object.getString("data");
                        if (statusCode == 200) {
                            iHttpCallback.onResponse(data, finalUrl);
                        }else if(statusCode == 513) {
                            iHttpCallback.onFailure(513, "token信息无效");
                        }else {
                            iHttpCallback.onFailure(0, errorMsg);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "basePostRequest JSONException");
                        iHttpCallback.onFailure(0, e.getMessage());
                    }
                }
            }
        });
    }

    public static void register(final IHttpCallback iHttpCallback) {
        String finalUrl = NetWorkUrLConstants.VIN_LOGIN_URL;
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map map = new HashMap();
        map.put("vin",CommonConstants.VIN);
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(JSON,gson.toJson(map));

        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .url(finalUrl)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                iHttpCallback.onFailure(0, e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e(TAG, "result = " + result);
                    try {
                        JSONObject object = new JSONObject(result);
                        int statusCode = object.getInt("statusCode");
                        String errorMsg = object.getString("errorMsg");
                        Log.e(TAG, "statusCode = " + statusCode);
                        Log.e(TAG, "errorMsg = " + errorMsg);
                        String data = object.getString("data");
                        if (statusCode == 200) {
                            iHttpCallback.onResponse(data, "");
                        } else {
                            iHttpCallback.onFailure(0, errorMsg);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "loginVIN JSONException");
                        iHttpCallback.onFailure(0, e.getMessage());
                    }
                }
            }
        });

    }

    public static void updateToken() {
        String finalUrl = NetWorkUrLConstants.TOKEN_BASE_URL + "/user/refreshToken";
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Map map = new HashMap();
        map.put("userID", CommonConstants.USER_ID);
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(JSON, gson.toJson(map));
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/json")
                .addHeader("appID", CommonConstants.APP_ID)
                .url(finalUrl)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateToken();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    Log.e(TAG,"result = "+result);
                    try {
                        JSONObject object = new JSONObject(result);
                        int statusCode = object.getInt("statusCode");
                        String errorMsg = object.getString("errorMsg");
                        String data = object.getString("data");
                        Log.e(TAG, "statusCode = " + statusCode);
                        Log.e(TAG, "errorMsg = " + errorMsg);
                        Log.e(TAG, "data = " + data);

                        if (statusCode == 200) {
                            JSONObject jsonObject = new JSONObject(data);
                            String token = jsonObject.getString("token");
                            CommonConstants.TOKEN = token;
                            Log.e(TAG, "token = " + token);
                        } else {
                            updateToken();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "updateToken JSONException");

                    }
                }
            }
        });
    }

}
