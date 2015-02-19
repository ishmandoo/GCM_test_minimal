package com.example.ben.gcm_test_minimal;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private boolean mIsLoggedIn;

    private ListView mFriendList;
    private ArrayAdapter<String> mFriendAdapter;
    private Gson mGson;
    private String[] mFriendArray;

    private OkHttpClient mClient;
    private CookieManager mCookieManager;

    private MainActivityReceiver mReceiver;
    private IntentFilter mMainIntentFilter;

    @Override
    protected void onResume(){
        super.onResume();
        try {
            checkLogIn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIsLoggedIn = false;

        mReceiver = new MainActivityReceiver();
        mReceiver.setMain(this);
        mMainIntentFilter = new IntentFilter("com.example.ben.gcm_test_minimal.updateMain");
        registerReceiver(mReceiver, mMainIntentFilter);

        //HttpClient.init();
        //mClient = HttpClient.getClient();
        //mCookieManager = HttpClient.getCookieManager();

        mFriendList = (ListView) findViewById(R.id.friendList);
        mFriendArray = new String[] {};
        mFriendAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>());
        mFriendList.setAdapter(mFriendAdapter);
        mGson = new Gson();

        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mIsLoggedIn) {
                    String friendName = ((TextView) view).getText().toString();
                    try {
                        fu(friendName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please log in", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }


    void fu(String name) throws IOException {
        Log.i(TAG, name);
        RequestBody body = RequestBody.create(JSON, "{\"friendName\":\"" + name + "\"}");
        Log.i(TAG, "{\"friendName\":\"" + name + "\"}");
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/fu")
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
                    getFriends();
                }
                Log.i(TAG, String.valueOf(mCookieManager.getCookieStore().getCookies()));
            }
        });
    }

    void getFriends() throws IOException {
        Request request = new Request.Builder()
                .url("http://quiet-taiga-6899.herokuapp.com/friends")
                .get()
                .build();
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.i(TAG, "Failure");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                mFriendArray = mGson.fromJson(response.body().string(), String[].class);
                runOnUiThread (new Thread(new Runnable() {
                    public void run() {
                        mFriendAdapter.clear();
                        for (String friend : mFriendArray) {
                            Log.i(TAG, "FRIEND: " + friend);
                            mFriendAdapter.add(friend);
                        }
                        mFriendAdapter.notifyDataSetChanged();
                    }
                }));
                //Log.i(TAG, response.body().string());
            }
        });
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
                    mIsLoggedIn = true;
                    getFriends();
                } else {
                    Intent loginIntent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }



}

