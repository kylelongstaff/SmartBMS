package com.superev.sbms;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class VbmsService extends Service {
    private BTCommCtrl btCommCtrl;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        startForeground(1, new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID).setContentTitle("SBMS Service").setContentText("Super-EV smart battery monitoring").setSmallIcon(R.drawable.vbms_service).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0)).build());
        return Service.START_STICKY;
    }

    public void onCreate() {
        super.onCreate();
        this.btCommCtrl = BTCommCtrl.getInstance(this);
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
