package com.example.ben.gcm_test_minimal;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

/**
 * Created by ben on 2/15/15.
 */
public class HttpClient {
    private static OkHttpClient mClient;
    private static CookieManager mCookieManager;
    private static String TAG = HttpClient.class.getSimpleName();

    public static void init() {
        mClient = new OkHttpClient();
        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        mClient.setCookieHandler(mCookieManager);
    }

    public static OkHttpClient getClient() {
        return mClient;
    }

    public static CookieManager getCookieManager() {
        return mCookieManager;
    }



}
