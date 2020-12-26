package com.superev.sbms;

import android.media.ToneGenerator;

public class AlarmSound extends ThreadBase {
    private ToneGenerator toneGenerator = new ToneGenerator(3, 100);
    private int waitCount;
    private final int waitDelay = 300;

    @Override
    public void run() {
        this.waitCount = 300;
        while (!this.terminateThread) {
            if (this.waitCount == 300) {
                this.toneGenerator.startTone(28);
            }
            try {
                Thread.sleep(10);
                int i = this.waitCount - 1;
                this.waitCount = i;
                if (i == 0) {
                    this.waitCount = 300;
                }
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
