package com.example.ben.gcm_test_minimal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.CookieManager;


public class LoginActivity extends ActionBarActivity {
    private TextView mUsername;
    private TextView mPassword;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private Button mLoginButton;

    private GoogleCloudMessaging gcm;

    private String SENDER_ID = "1070363014238";
    private String regid;

    private OkHttpClient mClient;
    private CookieManager mCookieManager;

    public static final String TAG = LoginActivity.class.getSimpleName();

    private SharedPreferences mSharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HttpClient.init();
        mClient = HttpClient.getClient();
        mCookieManager = HttpClient.getCookieManager();

        gcm = GoogleCloudMessaging.getInstance(this);

        mSharedPref = this.getPreferences(Context.MODE_PRIVATE);

        try {
            checkLogIn();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String saved_username = mSharedPref.getString("username", "");
        String saved_password = mSharedPref.getString("password", "");
        Boolean is_user_saved =  mSharedPref.getBoolean("is_user_saved", false);
        Log.i(TAG, "Saved username and password " +saved_username + saved_password);

        if (is_user_saved) {
            try {
                logIn(saved_username, saved_password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setContentView(R.layout.activity_login);
            mUsername = (TextView) findViewById(R.id.username);
            mPassword = (TextView) findViewById(R.id.password);
            mLoginButton = (Button) findViewById(R.id.loginButton);
            mLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        logIn(mUsername.getText().toString(), mPassword.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
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
                    registerBackground();
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

    private void registerBackground() {
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

    void checkLogIn() throws IOException {
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/loggedIn")
                .get()
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "Failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i(TAG, String.valueOf(response.isSuccessful()));
                if (response.isSuccessful()) {
                    Intent loginIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(loginIntent);
                } else {
                }
            }
        });
    }




}
