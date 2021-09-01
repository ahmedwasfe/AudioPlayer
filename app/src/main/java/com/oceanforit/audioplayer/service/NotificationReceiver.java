package com.oceanforit.audioplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.oceanforit.audioplayer.common.Common;

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, AudioService.class);
        if (actionName != null) {
            switch (actionName) {
                case Common.ACTION_PLAY:
                    serviceIntent.putExtra(Common.ACTION_NAME, "playPause");
                    context.startService(serviceIntent);
                    break;
                case Common.ACTION_NEXT:
                    serviceIntent.putExtra(Common.ACTION_NAME, "next");
                    context.startService(serviceIntent);
                    break;
                case Common.ACTION_PREVIOUS:
                    serviceIntent.putExtra(Common.ACTION_NAME, "previous");
                    context.startService(serviceIntent);
                    break;
//                case Common.ACTION_QUIT:
//                    serviceIntent.putExtra(Common.ACTION_NAME, "quit");
//                    context.startService(serviceIntent);
//                    break;
            }
        }

    }
}
