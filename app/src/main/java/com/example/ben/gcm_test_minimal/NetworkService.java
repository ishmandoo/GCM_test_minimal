package com.example.ben.gcm_test_minimal;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class NetworkService extends IntentService {
    public static final String ACTION_UPDATE_FRIENDS = "com.example.ben.gcm_test_minimal.update_friends";
    public static final String ACTION_FU = "com.example.ben.gcm_test_minimal.fu";
    public static final String ACTION_LOG_IN = "com.example.ben.gcm_test_minimal.log_in";


    private static OkHttpClient mClient;
    private static CookieManager mCookieManager;
    private static String TAG = HttpClient.class.getSimpleName();

    private String regid;
    private String SENDER_ID = "1070363014238";


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private GoogleCloudMessaging gcm;

    private SharedPreferences mSharedPref;

    public NetworkService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");


        mClient = new OkHttpClient();
        mCookieManager = new CookieManager();
        mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        mClient.setCookieHandler(mCookieManager);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        if(ACTION_UPDATE_FRIENDS.equals(action)){

        }
    }

    void logIn(final String username, final String password) throws IOException {
        Log.i(TAG, "log in function start" + password);
        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/login")
                .post(formBody)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "Failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i(TAG, response.body().string());
                if (response.isSuccessful()) {
                    register();
                    SharedPreferences.Editor editor = mSharedPref.edit();
                    Log.i(TAG, username);
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.putBoolean("is_user_saved",true);
                    editor.commit();
                }
                Log.i(TAG, String.valueOf(mCookieManager.getCookieStore().getCookies()));
            }
        });
    }

    private void register() {
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + regid;
                    registerId();
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the message
                    // using the 'from' address in the message.

                    // Save the regid - no need to register again.

                    //setRegistrationId(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
                finish();
            }
        }.execute(null, null, null);
    }

    void registerId() throws IOException {
        RequestBody body = RequestBody.create(JSON, "{\"regId\":\"" + regid + "\"}");
        Log.i(TAG, "{\"regId\":\"" + regid + "\"}");
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/android/register")
                .post(body)
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "Failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i(TAG, response.body().string());
                if (response.isSuccessful()) {
                }
                Log.i(TAG, String.valueOf(mCookieManager.getCookieStore().getCookies()));
            }
        });
    }
}
