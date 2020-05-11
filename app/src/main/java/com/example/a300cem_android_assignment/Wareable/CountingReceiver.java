package com.example.a300cem_android_assignment.Wareable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.a300cem_android_assignment.Login;

public class CountingReceiver extends BroadcastReceiver {
    public CountingReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(Login.DEBUG_KEY, "on receive");
        String timeElapsed = intent.getStringExtra(CountingService.REPORT_KEY);
        Log.d(Login.DEBUG_KEY, "time elapsed: " + timeElapsed);

        Intent intentNew = new Intent(context, Login.class);
        intentNew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentNew.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentNew.putExtra(CountingService.REPORT_KEY, timeElapsed);
        context.startActivity(intentNew);
    }
}