package com.example.davidperezramirez.geopendoor.service;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkServiceImp implements NetworkService {

    private static final String CONTEXT_TAG = NetworkServiceImp.class.getSimpleName() + " ";

    Context context;
    String serverPassword;
    OkHttpClient client;
    String connectionLostMessage;

    public NetworkServiceImp(Context context) {
        this.context = context;
        connectionLostMessage = "Conexión perdida";
        initHttpClient();
    }

    private void initHttpClient() {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        File cacheDir = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
        Cache cache = new Cache(cacheDir, cacheSize);

        client = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

    }

    private String getBasicAuthProperty() {
        return Credentials.basic("admin", this.serverPassword);
    }

    @Override
    public Single<Boolean> getJson(String url) {
        final String newUrl = url;

        return Single.defer(new Callable<SingleSource<? extends Boolean>>() {
            @Override
            public Single<Boolean> call() {
                return Single.just(doGetJson(newUrl));
            }
        });
    }

    private Boolean doGetJson(String url) {
        boolean result = false;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", getBasicAuthProperty())
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.code() == HttpURLConnection.HTTP_OK) {
                result = true;
            } else if (response.code() == HttpURLConnection.HTTP_FORBIDDEN) {
                result = false;
            }
            response.body().close();
        } catch (SocketTimeoutException timeOutException) {
            Log.e("GE", CONTEXT_TAG + "getJson: " + url + ", Message: " + timeOutException.getMessage());
            result = false;
        } catch (UnknownHostException unknownHostException) {
            Log.e("GE", CONTEXT_TAG + "getJson: " + url + ", Message: " + unknownHostException.getMessage());
            result = false;
        } catch (Exception exception) {
            Log.e("GE", CONTEXT_TAG + "getJson: " + url + ", Message: " + exception.getMessage());
            result = false;
        }

        return result;
    }

}
