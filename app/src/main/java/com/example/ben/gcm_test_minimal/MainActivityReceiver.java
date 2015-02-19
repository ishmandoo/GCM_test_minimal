package com.example.ben.gcm_test_minimal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;

/**
 * Created by ben on 2/15/15.
 */
public class MainActivityReceiver extends BroadcastReceiver{
    private MainActivity mMain;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            mMain.getFriends();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMain(MainActivity main){
        mMain = main;
    }


}
