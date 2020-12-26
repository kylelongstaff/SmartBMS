package com.superev.sbms;

import android.os.Message;
import org.greenrobot.eventbus.EventBus;

public abstract class ThreadBase implements Runnable {
    protected volatile boolean terminateThread = false;

    public abstract void run();

    public void SendEmptyMessage(int i) {
        Message obtain = Message.obtain();
        obtain.what = i;
        EventBus.getDefault().post(obtain);
    }

    public void SendDiagMessage(String str) {
        Message obtain = Message.obtain();
        obtain.what = BTCommCtrl.BTCOMM_DIAG_MSG;
        obtain.obj = str;
        EventBus.getDefault().post(obtain);
    }

    public void terminate() {
        this.terminateThread = true;
    }
}
