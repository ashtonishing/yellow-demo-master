package com.buyopic.android.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FinishActivityReceiver extends BroadcastReceiver {

    private Activity activity = null;

    public FinishActivityReceiver(Context context) {
            this.activity = (Activity) context;
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
            if (activity != null) {
                    activity.finish();
            }

    }

}