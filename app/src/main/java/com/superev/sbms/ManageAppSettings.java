package com.superev.sbms;

import android.content.Context;
import android.content.SharedPreferences;
import com.github.mikephil.charting.BuildConfig;

@SuppressWarnings("ALL")
public class ManageAppSettings {
    private static final String APP_SETTINGS = "VBMS_SETTINGS";
    private int mBatteryCapacity;
    private float mBatteryChargeCutOffVoltage;
    private String mBluetoothDev = BuildConfig.FLAVOR;
    private Context mContext;
    private float mDistanceTravelled;
    private float mHighestAmps;
    private short mHighestFETtemp;
    private short mHighestPower;
    private float mHighestSpeed_mps;
    private float mLowestVoltage;
    private boolean mOvervoltAlarmEnabled;
    private String mSummaryResetDate;

    private enum enumKEY {
        BluetoothDevice,
        HighestAmps,
        HighestPower,
        HighestFETtemp,
        HighestSpeed,
        DistanceTravelled,
        LowestVoltage,
        SummaryResetDate,
        OvervoltAlarmEnable,
        BatteryChargeCutOffVoltage,
        BatteryCapacity
    }

    public String getBluetoothDeviceName() {
        return this.mBluetoothDev;
    }

    public void setBluetoothDeviceName(String str) {
        this.mBluetoothDev = str;
    }

    public float getLowestBatteryVoltage() {
        return this.mLowestVoltage;
    }

    public void setLowestBatteryVoltage(float f) {
        this.mLowestVoltage = f;
    }

    public float getHighestAmps() {
        return this.mHighestAmps;
    }

    public void setHighestAmps(float f) {
        this.mHighestAmps = f;
    }

    public float getHighestSpeed_mps() {
        return this.mHighestSpeed_mps;
    }

    public void setHighestSpeed_mps(float f) {
        this.mHighestSpeed_mps = f;
    }

    public short getHighestFETtemp() {
        return this.mHighestFETtemp;
    }

    public void setHighestFETtemp(short s) {
        this.mHighestFETtemp = s;
    }

    public short getHighestPower() {
        return this.mHighestPower;
    }

    public void setHighestPower(short s) {
        this.mHighestPower = s;
    }

    public float getDistanceTravelled() {
        return this.mDistanceTravelled;
    }

    public void setDistanceTravelled(float f) {
        this.mDistanceTravelled = f;
    }

    public String getSummaryResetDate() {
        return this.mSummaryResetDate;
    }

    public void setSummaryResetDate(String str) {
        this.mSummaryResetDate = str;
    }

    public boolean getOvervoltAlarmEnabled() {
        return this.mOvervoltAlarmEnabled;
    }

    public void setOvervoltAlarmEnabled(boolean z) {
        this.mOvervoltAlarmEnabled = z;
    }

    public float getBatteryChargeCutOffVoltage() {
        return this.mBatteryChargeCutOffVoltage;
    }

    public void setmBatteryChargeCutOffVoltage(float f) {
        this.mBatteryChargeCutOffVoltage = f;
    }

    public int getmBatteryCapacity() {
        return this.mBatteryCapacity;
    }

    public void setmBatteryCapacity(int i) {
        this.mBatteryCapacity = i;
    }

    public ManageAppSettings(Context context) {
        this.mContext = context;
        loadAppSettings();
    }

    public boolean saveAppSettings() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(APP_SETTINGS, 0);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        setgetAppSetting(true, enumKEY.BluetoothDevice, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.HighestAmps, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.HighestFETtemp, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.HighestPower, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.HighestSpeed, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.DistanceTravelled, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.LowestVoltage, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.SummaryResetDate, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.OvervoltAlarmEnable, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.BatteryChargeCutOffVoltage, sharedPreferences, edit);
        setgetAppSetting(true, enumKEY.BatteryCapacity, sharedPreferences, edit);
        return edit.commit();
    }

    public void loadAppSettings() {
        SharedPreferences sharedPreferences = this.mContext.getSharedPreferences(APP_SETTINGS, 0);
        setgetAppSetting(false, enumKEY.BluetoothDevice, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.HighestAmps, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.HighestFETtemp, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.HighestPower, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.HighestSpeed, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.DistanceTravelled, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.LowestVoltage, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.SummaryResetDate, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.OvervoltAlarmEnable, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.BatteryChargeCutOffVoltage, sharedPreferences, null);
        setgetAppSetting(false, enumKEY.BatteryCapacity, sharedPreferences, null);
    }

    /* renamed from: com.vortecks.vbms.ManageAppSettings$1  reason: invalid class name */
    static class AnonymousClass1 {
        static final int[] $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY;

        /* WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            int[] iArr = new int[enumKEY.values().length];
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY = iArr;
            iArr[enumKEY.BluetoothDevice.ordinal()] = 1;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.HighestAmps.ordinal()] = 2;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.HighestPower.ordinal()] = 3;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.HighestSpeed.ordinal()] = 4;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.HighestFETtemp.ordinal()] = 5;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.DistanceTravelled.ordinal()] = 6;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.SummaryResetDate.ordinal()] = 7;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.LowestVoltage.ordinal()] = 8;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.OvervoltAlarmEnable.ordinal()] = 9;
            $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.BatteryChargeCutOffVoltage.ordinal()] = 10;
            try {
                $SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[enumKEY.BatteryCapacity.ordinal()] = 11;
            } catch (NoSuchFieldError unused) {
            }
        }
    }

    private void setgetAppSetting(boolean z, enumKEY enumkey, SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        switch (/*AnonymousClass1.$SwitchMap$com$vortecks$vbms$ManageAppSettings$enumKEY[*/enumkey.ordinal()/*]*/) {
            case 1:
                if (z) {
                    editor.putString("BluetoothDev", this.mBluetoothDev);
                    return;
                } else {
                    this.mBluetoothDev = sharedPreferences.getString("BluetoothDev", BuildConfig.FLAVOR);
                    return;
                }
            case 2:
                if (z) {
                    editor.putFloat("highestAmps", this.mHighestAmps);
                    return;
                } else {
                    this.mHighestAmps = sharedPreferences.getFloat("highestAmps", 0.0f);
                    return;
                }
            case 3:
                if (z) {
                    editor.putInt("highestPower", this.mHighestPower);
                    return;
                } else {
                    this.mHighestPower = (short) sharedPreferences.getInt("highestPower", 0);
                    return;
                }
            case 4:
                if (z) {
                    editor.putFloat("highestSpeed", this.mHighestSpeed_mps);
                    return;
                } else {
                    this.mHighestSpeed_mps = sharedPreferences.getFloat("highestSpeed", 0.0f);
                    return;
                }
            case 5:
                if (z) {
                    editor.putInt("highestFETtemp", this.mHighestFETtemp);
                    return;
                } else {
                    this.mHighestFETtemp = (short) sharedPreferences.getInt("highestFETtemp", 0);
                    return;
                }
            case 6:
                if (z) {
                    editor.putFloat("distanceTravelled", this.mDistanceTravelled);
                    return;
                } else {
                    this.mDistanceTravelled = sharedPreferences.getFloat("distanceTravelled", 0.0f);
                    return;
                }
            case 7:
                if (z) {
                    editor.putString("summaryResetDate", this.mSummaryResetDate);
                    return;
                } else {
                    this.mSummaryResetDate = sharedPreferences.getString("summaryResetDate", "Unknown");
                    return;
                }
            case 8:
                if (z) {
                    editor.putFloat("lowestVoltage", this.mLowestVoltage);
                    return;
                } else {
                    this.mLowestVoltage = sharedPreferences.getFloat("lowestVoltage", 500.0f);
                    return;
                }
            case 9:
                if (z) {
                    editor.putBoolean("overvoltAlarmEnable", this.mOvervoltAlarmEnabled);
                    return;
                } else {
                    this.mOvervoltAlarmEnabled = sharedPreferences.getBoolean("overvoltAlarmEnable", true);
                    return;
                }
            case 10:
                if (z) {
                    editor.putFloat("batteryChargeCutOffVoltage", this.mBatteryChargeCutOffVoltage);
                    return;
                } else {
                    this.mBatteryChargeCutOffVoltage = sharedPreferences.getFloat("batteryChargeCutOffVoltage", 0.0f);
                    return;
                }
            case 11:
                if (z) {
                    editor.putInt("batteryCapacity", this.mBatteryCapacity);
                    return;
                } else {
                    this.mBatteryCapacity = sharedPreferences.getInt("batteryCapacity", 100);
                    return;
                }
            default:
                return;
        }
    }
}
