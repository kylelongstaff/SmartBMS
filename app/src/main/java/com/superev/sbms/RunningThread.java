package com.superev.sbms;

public class RunningThread {
    private ThreadBase runnable;
    private Thread thread = new Thread(this.runnable);

    public RunningThread(ThreadBase threadBase) {
        this.runnable = threadBase;
    }

    public void endThread(boolean z) {
        this.runnable.terminate();
        if (z) {
            try {
                this.thread.join();
            } catch (InterruptedException unused) {
            }
        }
    }

    public ThreadBase getRunnable() {
        return this.runnable;
    }

    public boolean startThread() {
        Thread thread2 = this.thread;
        if (thread2 == null) {
            return false;
        }
        thread2.start();
        return true;
    }
}
