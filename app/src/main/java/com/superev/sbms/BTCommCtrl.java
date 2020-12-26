package com.superev.sbms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.InputDeviceCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.github.mikephil.charting.BuildConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import org.greenrobot.eventbus.EventBus;

@SuppressWarnings("ALL")
public final class BTCommCtrl {
    private static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int BLUETOOTH_MESSAGE_FEEDBACK_READ = 200;
    public static final int BLUETOOTH_REQUEST_ENABLE = 101;
    public static final int BLUETOOTH_SELECT_DEVICE = 100;
    public static final int BMS_APPLICATIONLOG_UPDATE = 5000;
    public static final int BMS_READ_DATA_CACHE = 4000;
    public static final int BTCOMM_DIAG_MSG = 99999;
    public static final int MAXIMUM_CELL_COUNT = 32;
    public static final int REQUEST_BMS_READING = 1000;
    public static final int STATUS_APP_SETTINGS_CHANGED = 6;
    public static final int STATUS_BT_Connected = 5;
    public static final int STATUS_BT_Connecting = 4;
    public static final int STATUS_BT_Disabled = 2;
    public static final int STATUS_BT_NoDeviceSelected = 1;
    public static final int STATUS_BT_NotAvailable = 0;
    public static final int STATUS_BT_NotConnected = 3;
    private static final String TAG = "BTCommCtrl";
    public static final int UI_UPDATE_TIME = 2000;
    public static final int UPDATE_RECORD_GPS_LOC = 3000;
    private static BTCommCtrl instance = null;
    ArrayList<ApplicationLogEntry> applicationLog;
    ApplicationTriggerVariables applicationTriggerVariables;
    private BluetoothAdapter mAdapter;
    private float mAmpsDrawn;
    private RunningThread mBTDisconnected;
    private String mBluetoothDeviceName;
    private RunningThread mConnectedBTDeviceComms;
    private RunningThread mConnectingThread;
    private int mConnectionStatus;
    private Context mContext;
    private Date mDateDiff;
    private DateFormat mDateFormatter;
    private RunningThread mGPSTrackingThread;
    private GPSAutoTrackVars mGpsAutoTrack;
    private RunningThread mInitConnectionThread;
    private ManageAppSettings mManageAppSettings;
    private Date mStartTime;
    private SummaryVarsManager mSummaryVarsManager;
    private Date mTimeNow;
    private boolean mUserDisconnectedBluetooth;
    private RunningThread mVBMSDataRequestPumpThread;
    byte[] read_data = new byte[600];
    private int read_data_140_i = 0;
    private int read_data_chu_li_140_i;
    private int read_data_chu_li_i;
    int[] read_data_data = new int[1024];
    private int read_data_i = 0;

    static int access$1708(BTCommCtrl bTCommCtrl) {
        int i = bTCommCtrl.read_data_i;
        bTCommCtrl.read_data_i = i + 1;
        return i;
    }

    static int access$1908(BTCommCtrl bTCommCtrl) {
        int i = bTCommCtrl.read_data_chu_li_i;
        bTCommCtrl.read_data_chu_li_i = i + 1;
        return i;
    }

    static int access$2008(BTCommCtrl bTCommCtrl) {
        int i = bTCommCtrl.read_data_chu_li_140_i;
        bTCommCtrl.read_data_chu_li_140_i = i + 1;
        return i;
    }

    public static BTCommCtrl getInstance(Context context) {
        if (instance == null) {
            instance = new BTCommCtrl(context);
        }
        return instance;
    }

    private BTCommCtrl(Context context) {
        this.mContext = context;
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothDeviceName = BuildConfig.FLAVOR;
        this.mConnectionStatus = 1;
        this.mUserDisconnectedBluetooth = false;
        this.mGpsAutoTrack = new GPSAutoTrackVars();
        refreshGPSAutoTrackValues();
        this.mDateDiff = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RouteFileParser.timeFormat);
        this.mDateFormatter = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        RunningThread runningThread = new RunningThread(GPSTracker.getInstance(context));
        this.mGPSTrackingThread = runningThread;
        runningThread.startThread();
        ManageAppSettings manageAppSettings = new ManageAppSettings(this.mContext);
        this.mManageAppSettings = manageAppSettings;
        this.mBluetoothDeviceName = manageAppSettings.getBluetoothDeviceName();
        SummaryVarsManager summaryVarsManager = new SummaryVarsManager();
        this.mSummaryVarsManager = summaryVarsManager;
        summaryVarsManager.setAmps(this.mManageAppSettings.getHighestAmps());
        this.mSummaryVarsManager.setFETtemp(this.mManageAppSettings.getHighestFETtemp());
        this.mSummaryVarsManager.setPower(this.mManageAppSettings.getHighestPower());
        this.mSummaryVarsManager.setSpeed(this.mManageAppSettings.getHighestSpeed_mps());
        this.mSummaryVarsManager.setLowestVoltage(this.mManageAppSettings.getLowestBatteryVoltage());
        this.mSummaryVarsManager.aggregateDistamceTravelled(this.mManageAppSettings.getDistanceTravelled());
        this.applicationLog = new ArrayList<>();
        InsertEntryIntoApplicationLog(context.getString(R.string.AppLog_AppStarted));
        InsertEntryIntoApplicationLog("Some other event occurred.");
        InsertEntryIntoApplicationLog("Mary had a little lamb");
        InsertEntryIntoApplicationLog("The internet crashed and some other shit");
        InsertEntryIntoApplicationLog("It's official, white dog shit is no longer a thing");
        this.applicationTriggerVariables = new ApplicationTriggerVariables();
        BluetoothAdapter bluetoothAdapter = this.mAdapter;
        if (bluetoothAdapter == null) {
            this.mConnectionStatus = 0;
        } else if (!bluetoothAdapter.isEnabled()) {
            this.mConnectionStatus = 2;
        }
    }

    public ArrayList<ApplicationLogEntry> GetApplicationLog() {
        return this.applicationLog;
    }

    public void ClearApplicationLog() {
        this.applicationLog.clear();
        InsertEntryIntoApplicationLog(this.mContext.getString(R.string.app_logCleared));
        Message obtain = Message.obtain();
        obtain.what = BMS_APPLICATIONLOG_UPDATE;
        EventBus.getDefault().post(obtain);
    }

    public String GetBTDeviceName() {
        return this.mBluetoothDeviceName;
    }

    public void SetBTDeviceName(String str) {
        this.mBluetoothDeviceName = str;
        this.mManageAppSettings.setBluetoothDeviceName(str);
        this.mManageAppSettings.saveAppSettings();
    }

    public void SetBatteryChargeCufOffVoltgae(float f) {
        this.mManageAppSettings.setmBatteryChargeCutOffVoltage(f);
        this.mManageAppSettings.saveAppSettings();
    }

    public void SetBatteryCapacity(int i) {
        this.mManageAppSettings.setmBatteryCapacity(i);
        this.mManageAppSettings.saveAppSettings();
    }

    public int GetBatteryCapacity() {
        return this.mManageAppSettings.getmBatteryCapacity();
    }

    public float GetBatteryChargeCufOffVoltage() {
        return this.mManageAppSettings.getBatteryChargeCutOffVoltage();
    }

    public void SetOvervoltAlarmEnabled(boolean z) {
        this.mManageAppSettings.setOvervoltAlarmEnabled(z);
        this.mManageAppSettings.saveAppSettings();
    }

    public boolean GetOvervoltAlarmEnabled() {
        return this.mManageAppSettings.getOvervoltAlarmEnabled();
    }

    public int[] GetReadDataData() {
        return this.read_data_data;
    }

    public int getConnectionStatus() {
        return this.mConnectionStatus;
    }

    public SummaryValues GetSummaryValues() {
        SummaryValues summaryValues = new SummaryValues();
        this.mSummaryVarsManager.GetSummaryValues(summaryValues);
        return summaryValues;
    }

    public void refreshGPSAutoTrackValues() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        this.mGpsAutoTrack.autoTrackEnabled = defaultSharedPreferences.getBoolean("switch_autotrack_enable", false);
        this.mGpsAutoTrack.triggerStartAmps = Short.parseShort(defaultSharedPreferences.getString("edit_text_amps_start_gps", "2"));
        this.mGpsAutoTrack.triggerStartAmpsDuration = Short.parseShort(defaultSharedPreferences.getString("edit_text_gps_start_ampseconds", "5"));
        this.mGpsAutoTrack.triggerEndZeroAmpsDuration = Short.parseShort(defaultSharedPreferences.getString("edit_text_gps_end_ampseconds", "0"));
    }

    public void endBTConnection() {
        helperEndBTConnection();
        this.mConnectionStatus = 3;
        this.mUserDisconnectedBluetooth = true;
        Message obtain = Message.obtain();
        obtain.what = this.mConnectionStatus;
        EventBus.getDefault().post(obtain);
    }

    public void RemoveConnectedBTDevice() {
        helperEndBTConnection();
        this.mConnectionStatus = 1;
        this.mBluetoothDeviceName = BuildConfig.FLAVOR;
        this.mUserDisconnectedBluetooth = true;
        Message obtain = Message.obtain();
        obtain.what = this.mConnectionStatus;
        EventBus.getDefault().post(obtain);
    }

    public void helperEndBTConnection() {
        if (this.mConnectionStatus == 5) {
            RunningThread runningThread = this.mVBMSDataRequestPumpThread;
            if (runningThread != null) {
                runningThread.endThread(true);
                this.mVBMSDataRequestPumpThread = null;
            }
            RunningThread runningThread2 = this.mConnectedBTDeviceComms;
            if (runningThread2 != null) {
                runningThread2.endThread(true);
                this.mConnectedBTDeviceComms = null;
            }
        }
        InsertEntryIntoApplicationLog(this.mContext.getString(R.string.app_bluetoothDisconnect));
        int i = 0;
        while (true) {
            int[] iArr = this.read_data_data;
            if (i < iArr.length) {
                iArr[i] = 0;
                i++;
            } else {
                this.mAmpsDrawn = 0.0f;
                ((GPSTracker) this.mGPSTrackingThread.getRunnable()).StopGPSTracking();
                return;
            }
        }
    }

    public void startBTConnection(String str, boolean z) {
        if (this.mConnectionStatus == 5 || str.isEmpty()) {
            return;
        }
        if (!this.mUserDisconnectedBluetooth || z) {
            this.mUserDisconnectedBluetooth = false;
            this.mConnectionStatus = 3;
            this.mBluetoothDeviceName = str;
            RunningThread runningThread = new RunningThread(new BTDeviceInitialiseConnection());
            this.mInitConnectionThread = runningThread;
            runningThread.startThread();
        }
    }

    public void StartVBMSDataRequestPump(int i) {
        RunningThread runningThread = this.mVBMSDataRequestPumpThread;
        if (runningThread != null) {
            runningThread.endThread(true);
            this.mVBMSDataRequestPumpThread = null;
        }
        RunningThread runningThread2 = new RunningThread(new BMSDataRequestPump(i));
        this.mVBMSDataRequestPumpThread = runningThread2;
        runningThread2.startThread();
    }

    public GPSTracker getGPSTracker() {
        return GPSTracker.getInstance(null);
    }

    public void StartGPSTracking() {
        if (this.mConnectionStatus == 5 && getGPSTracker().GetStatus() != 10012) {
            getGPSTracker().StartGPSTracking();
            InsertEntryIntoApplicationLog(this.mContext.getString(R.string.app_gpsTrackOn));
        }
    }

    public void StopGPSTracking() {
        this.mSummaryVarsManager.setSpeed(getGPSTracker().GetTopSpeed_mps());
        this.mSummaryVarsManager.aggregateDistamceTravelled(getGPSTracker().GetDistanceTravelled());
        getGPSTracker().StopGPSTracking();
        this.mGpsAutoTrack.AutoTrackInitiatedRecording = false;
        SaveSummaryValues();
        InsertEntryIntoApplicationLog(this.mContext.getString(R.string.app_gpsTrackOff));
    }

    public void ResetSummaryValues() {
        this.mSummaryVarsManager.resetSummaryValues();
        SaveSummaryValues();
        this.mManageAppSettings.setSummaryResetDate(this.mSummaryVarsManager.getResetDate());
        InsertEntryIntoApplicationLog(this.mContext.getString(R.string.app_summaryVarsReset));
    }

    public String GetGpsSpeedUnit() {
        return getGPSTracker().GetSpeedUnits();
    }

    public String GetGpsSpeed() {
        return getGPSTracker().GetSpeed();
    }

    public int GetGpsTrackerStatus() {
        return getGPSTracker().GetStatus();
    }

    public void manageBTConnectedComms(BluetoothSocket bluetoothSocket) {
        RunningThread runningThread = this.mConnectedBTDeviceComms;
        if (runningThread != null) {
            runningThread.endThread(true);
            this.mConnectedBTDeviceComms = null;
        }
        RunningThread runningThread2 = new RunningThread(new BTConnectedDevComms(bluetoothSocket));
        this.mConnectedBTDeviceComms = runningThread2;
        runningThread2.startThread();
    }

    public void InsertEntryIntoApplicationLog(String str) {
        ApplicationLogEntry applicationLogEntry = new ApplicationLogEntry();
        applicationLogEntry.dateLog = Calendar.getInstance().getTime();
        applicationLogEntry.logEntry = str;
        this.applicationLog.add(applicationLogEntry);
        Message obtain = Message.obtain();
        obtain.what = BMS_APPLICATIONLOG_UPDATE;
        EventBus.getDefault().post(obtain);
    }

    public void Send_6bit(int i, int i2, int i3) {
        if (this.mConnectionStatus == 5) {
            byte[] bArr = new byte[6];
            bArr[0] = (byte) ((i >> 8) & 255);
            bArr[1] = (byte) (i & 255);
            bArr[2] = (byte) (i2 & 255);
            bArr[3] = (byte) ((i3 >> 8) & 255);
            bArr[4] = (byte) (i3 & 255);
            bArr[5] = (byte) (bArr[2] + bArr[3] + bArr[4]);
            write(bArr);
        }
    }

    public synchronized void write(byte[] bArr) {
        ((BTConnectedDevComms) this.mConnectedBTDeviceComms.getRunnable()).write(bArr);
    }

    public void PowerOffBMS() {
        Send_6bit(42405, 249, 0);
    }

    public void PowerOnBMS() {
        Send_6bit(42405, 249, 1);
    }

    public void ChargeOffBMS() {
        Send_6bit(42405, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 0);
    }

    public void ChargeOnBMS() {
        Send_6bit(42405, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 1);
    }

    public void ZeroCurrentBMS() {
        Send_6bit(42405, 248, 0);
    }

    public void BalanceCellsBMS() {
        Send_6bit(42405, 252, 0);
    }

    public void FactoryResetBMS() {
        Send_6bit(42405, 253, 0);
    }

    public void RebootBMS() {
        Send_6bit(42405, 254, 0);
    }

    public int GetBTConntectionStatusColour() {
        int i = this.mConnectionStatus;
        if (i == 1) {
            return -3355444;
        }
        if (i == 2) {
            return SupportMenu.CATEGORY_MASK;
        }
        if (i == 3) {
            return InputDeviceCompat.SOURCE_ANY;
        }
        if (i != 4) {
            return i != 5 ? 0 : -16711936;
        }
        return -16711681;
    }

    public String GetBluetoothConnectionTime() {
        Date time = Calendar.getInstance().getTime();
        this.mTimeNow = time;
        this.mDateDiff.setTime(time.getTime() - this.mStartTime.getTime());
        return this.mDateFormatter.format(this.mDateDiff);
    }

    public void SaveSummaryValues() {
        this.mManageAppSettings.setHighestAmps(this.mSummaryVarsManager.getHighestAmps());
        this.mManageAppSettings.setHighestPower(this.mSummaryVarsManager.getHighestPower());
        this.mManageAppSettings.setHighestFETtemp(this.mSummaryVarsManager.getHighestFETtemp());
        this.mManageAppSettings.setHighestSpeed_mps(this.mSummaryVarsManager.getHighestSpeed_mps());
        this.mManageAppSettings.setDistanceTravelled(this.mSummaryVarsManager.getDistanceTravelled());
        this.mManageAppSettings.setLowestBatteryVoltage(this.mSummaryVarsManager.getLowestVoltage());
        this.mManageAppSettings.saveAppSettings();
    }

    private class BTDeviceInitialiseConnection extends ThreadBase {
        private BTDeviceInitialiseConnection() {
        }

        @Override
        public void run() {
            if (BTCommCtrl.this.mConnectionStatus == 0) {
                SendEmptyMessage(BTCommCtrl.this.mConnectionStatus);
            } else if (!BTCommCtrl.this.mAdapter.isEnabled()) {
                BTCommCtrl.this.mConnectionStatus = 2;
                SendEmptyMessage(BTCommCtrl.this.mConnectionStatus);
            } else if (BTCommCtrl.this.mBluetoothDeviceName == BuildConfig.FLAVOR) {
                BTCommCtrl.this.mConnectionStatus = 1;
                SendEmptyMessage(BTCommCtrl.this.mConnectionStatus);
            } else if (BTCommCtrl.this.mConnectionStatus == 3) {
                BluetoothDevice remoteDevice = BTCommCtrl.this.mAdapter.getRemoteDevice(BTCommCtrl.this.mBluetoothDeviceName);
                BTCommCtrl.this.mConnectionStatus = 4;
                SendEmptyMessage(BTCommCtrl.this.mConnectionStatus);
                BTCommCtrl.this.mAdapter.cancelDiscovery();
                BTCommCtrl.this.mConnectingThread = new RunningThread(new BTDeviceConnect(remoteDevice));
                BTCommCtrl.this.mConnectingThread.startThread();
            }
        }
    }

    private class BTDeviceConnect extends ThreadBase {
        private final BluetoothDevice mDevice;
        private final BluetoothSocket mSocket;

        public BTDeviceConnect(BluetoothDevice bluetoothDevice) {
            BluetoothSocket bluetoothSocket;
            this.mDevice = bluetoothDevice;
            try {
                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(BTCommCtrl.APP_UUID);
            } catch (IOException unused) {
                bluetoothSocket = null;
            }
            this.mSocket = bluetoothSocket;
        }

        @Override
        public void run() {
            BTCommCtrl.this.mAdapter.cancelDiscovery();
            try {
                this.mSocket.connect();
                BTCommCtrl.this.mConnectionStatus = 5;
                BTCommCtrl.this.InsertEntryIntoApplicationLog(BTCommCtrl.this.mContext.getString(R.string.app_bluetoothConnect));
                SendEmptyMessage(BTCommCtrl.this.mConnectionStatus);
                BTCommCtrl.this.mStartTime = Calendar.getInstance().getTime();
                BTCommCtrl.this.manageBTConnectedComms(this.mSocket);
            } catch (IOException e) {
                try {
                    SendDiagMessage(e.getLocalizedMessage());
                    this.mSocket.close();
                } catch (IOException unused) {
                    SendDiagMessage(e.getLocalizedMessage());
                }
                BTCommCtrl.this.endBTConnection();
            }
        }
    }

    private class BMSDataRequestPump extends ThreadBase {
        private int requestInterval;

        BMSDataRequestPump(int i) {
            this.requestInterval = i;
        }

        @Override
        public void run() {
            Process.setThreadPriority(10);
            while (!this.terminateThread) {
                try {
                    Thread.sleep((long) this.requestInterval);
                    BTCommCtrl.this.Send_6bit(56283, 0, 0);
                    Message obtain = Message.obtain();
                    obtain.what = BTCommCtrl.UI_UPDATE_TIME;
                    EventBus.getDefault().post(obtain);
                    if (BTCommCtrl.this.mConnectionStatus == 5) {
                        if (BTCommCtrl.this.mGpsAutoTrack.autoTrackEnabled) {
                            if (BTCommCtrl.this.getGPSTracker().GetStatus() != 10012) {
                                if (BTCommCtrl.this.mAmpsDrawn >= ((float) BTCommCtrl.this.mGpsAutoTrack.triggerStartAmps)) {
                                    GPSAutoTrackVars gPSAutoTrackVars = BTCommCtrl.this.mGpsAutoTrack;
                                    gPSAutoTrackVars.startAmpsDuration = (short) (gPSAutoTrackVars.startAmpsDuration + 1);
                                } else {
                                    BTCommCtrl.this.mGpsAutoTrack.startAmpsDuration = 0;
                                }
                                if (BTCommCtrl.this.mGpsAutoTrack.startAmpsDuration > BTCommCtrl.this.mGpsAutoTrack.triggerStartAmpsDuration) {
                                    BTCommCtrl.this.mGpsAutoTrack.AutoTrackInitiatedRecording = true;
                                    BTCommCtrl.this.StartGPSTracking();
                                }
                            }
                            if (BTCommCtrl.this.mGpsAutoTrack.AutoTrackInitiatedRecording && BTCommCtrl.this.getGPSTracker().GetStatus() == 10012) {
                                if (BTCommCtrl.this.mAmpsDrawn == 0.0f) {
                                    GPSAutoTrackVars gPSAutoTrackVars2 = BTCommCtrl.this.mGpsAutoTrack;
                                    gPSAutoTrackVars2.zeroAmpsDuration = (short) (gPSAutoTrackVars2.zeroAmpsDuration + 1);
                                } else {
                                    BTCommCtrl.this.mGpsAutoTrack.zeroAmpsDuration = 0;
                                }
                                if (BTCommCtrl.this.mGpsAutoTrack.zeroAmpsDuration >= BTCommCtrl.this.mGpsAutoTrack.triggerEndZeroAmpsDuration) {
                                    BTCommCtrl.this.StopGPSTracking();
                                    BTCommCtrl.this.mGpsAutoTrack.zeroAmpsDuration = 0;
                                    BTCommCtrl.this.mGpsAutoTrack.startAmpsDuration = 0;
                                }
                            }
                        }
                        if (BTCommCtrl.this.getGPSTracker().GetStatus() != 10012 && BTCommCtrl.this.mSummaryVarsManager.getIsDirty()) {
                            BTCommCtrl.this.SaveSummaryValues();
                            BTCommCtrl.this.mSummaryVarsManager.clearIsDirty();
                        }
                    }
                } catch (InterruptedException unused) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private class GPSAutoTrackVars {
        public boolean AutoTrackInitiatedRecording;
        public boolean autoTrackEnabled;
        public short startAmpsDuration;
        public short triggerEndZeroAmpsDuration;
        public short triggerStartAmps;
        public short triggerStartAmpsDuration;
        public short zeroAmpsDuration;

        private GPSAutoTrackVars() {
        }
    }

    public class SummaryValues {
        public float highestAmps;
        public short highestFETtemp;
        public short highestPower;
        public float highestSpeed_mps;
        public float lowestVoltage;
        public String resetDate;
        public float totalTrackedDistanceTravelled;

        public SummaryValues() {
        }
    }

    private class SummaryVarsManager {
        private boolean isDirty = false;
        private SummaryValues summaryValues;

        public SummaryVarsManager() {
            this.summaryValues = new SummaryValues();
            resetSummaryValues();
        }

        public void GetSummaryValues(SummaryValues summaryValues2) {
            summaryValues2.highestAmps = this.summaryValues.highestAmps;
            summaryValues2.highestFETtemp = this.summaryValues.highestFETtemp;
            summaryValues2.highestPower = this.summaryValues.highestPower;
            summaryValues2.highestSpeed_mps = this.summaryValues.highestSpeed_mps;
            summaryValues2.totalTrackedDistanceTravelled = this.summaryValues.totalTrackedDistanceTravelled;
            summaryValues2.lowestVoltage = this.summaryValues.lowestVoltage;
            summaryValues2.resetDate = new SimpleDateFormat(RouteFileParser.dateFormat).format(Calendar.getInstance().getTime());
        }

        public void resetSummaryValues() {
            this.summaryValues.totalTrackedDistanceTravelled = 0.0f;
            this.summaryValues.highestAmps = 0.0f;
            this.summaryValues.highestSpeed_mps = 0.0f;
            this.summaryValues.highestPower = 0;
            this.summaryValues.highestFETtemp = 0;
            this.summaryValues.lowestVoltage = 500.0f;
        }

        public boolean getIsDirty() {
            return this.isDirty;
        }

        public void clearIsDirty() {
            this.isDirty = false;
        }

        public String getResetDate() {
            return this.summaryValues.resetDate;
        }

        public float getLowestVoltage() {
            return this.summaryValues.lowestVoltage;
        }

        public void setLowestVoltage(float f) {
            if (f < this.summaryValues.lowestVoltage && f > 0.0f) {
                this.summaryValues.lowestVoltage = f;
                this.isDirty = true;
            }
        }

        public float getHighestAmps() {
            return this.summaryValues.highestAmps;
        }

        public void setAmps(float f) {
            if (f > this.summaryValues.highestAmps) {
                this.summaryValues.highestAmps = f;
                this.isDirty = true;
            }
        }

        public short getHighestPower() {
            return this.summaryValues.highestPower;
        }

        public void setPower(short s) {
            if (s > this.summaryValues.highestPower) {
                this.summaryValues.highestPower = s;
                this.isDirty = true;
            }
        }

        public float getHighestSpeed_mps() {
            return this.summaryValues.highestSpeed_mps;
        }

        public void setSpeed(float f) {
            if (f > this.summaryValues.highestSpeed_mps) {
                this.summaryValues.highestSpeed_mps = f;
                this.isDirty = true;
            }
        }

        public short getHighestFETtemp() {
            return this.summaryValues.highestFETtemp;
        }

        public void setFETtemp(short s) {
            if (s > this.summaryValues.highestFETtemp) {
                this.summaryValues.highestFETtemp = s;
                this.isDirty = true;
            }
        }

        public float getDistanceTravelled() {
            return this.summaryValues.totalTrackedDistanceTravelled;
        }

        public void aggregateDistamceTravelled(float f) {
            this.summaryValues.totalTrackedDistanceTravelled += f;
            this.isDirty = true;
        }
    }

    private class BTConnectedDevComms extends ThreadBase {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        BTConnectedDevComms(BluetoothSocket bluetoothSocket) {
            InputStream inputStream;
            IOException e;
            this.mmSocket = bluetoothSocket;
            OutputStream outputStream = null;
            try {
                inputStream = bluetoothSocket.getInputStream();
                try {
                    outputStream = bluetoothSocket.getOutputStream();
                } catch (IOException e2) {
                    e = e2;
                }
            } catch (IOException e3) {
                e = e3;
                inputStream = null;
                Log.e(BTCommCtrl.TAG, "temp sockets not created", e);
                this.mmInStream = inputStream;
                this.mmOutStream = outputStream;
            }
            this.mmInStream = inputStream;
            this.mmOutStream = outputStream;
        }

        /* WARN: Multi-variable type inference failed */
        @Override
        public void run() {
            int i;
            int i2;
            char c;
            int i3;
            int i4;
            char c2;
            char c3 = 1024;
            byte[] bArr = new byte[1024];
            int i5 = 6;
            int[] iArr = new int[6];
            Process.setThreadPriority(10);
            int i6 = 0;
            while (!this.terminateThread) {
                try {
                    int read = this.mmInStream.read(bArr);
                    for (int i7 = 0; i7 < read; i7++) {
                        BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_i] = bArr[i7];
                        BTCommCtrl.access$1708(BTCommCtrl.this);
                        if (BTCommCtrl.this.read_data_i >= 600) {
                            BTCommCtrl.this.read_data_i = 0;
                        }
                        BTCommCtrl.this.read_data_140_i = BTCommCtrl.this.read_data_i;
                    }
                    if (BTCommCtrl.this.read_data_i > BTCommCtrl.this.read_data_chu_li_i) {
                        i2 = BTCommCtrl.this.read_data_i;
                        i = BTCommCtrl.this.read_data_chu_li_i;
                    } else {
                        i2 = BTCommCtrl.this.read_data_i + 600;
                        i = BTCommCtrl.this.read_data_chu_li_i;
                    }
                    int i8 = i2 - i;
                    while (true) {
                        c = 2;
                        if (i8 <= 5) {
                            break;
                        }
                        byte[] bArr2 = new byte[2];
                        for (byte b = 0; b < 2; b = (byte) (b + 1)) {
                            if (BTCommCtrl.this.read_data_chu_li_i + b < 600) {
                                bArr2[b] = BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_i + b];
                            } else {
                                bArr2[b] = BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_i + b) - 600];
                            }
                        }
                        if ((bArr2[0] == -91 && bArr2[1] == -91) || ((bArr2[0] == 90 && bArr2[1] == 90) || (bArr2[0] == -37 && bArr2[1] == -37))) {
                            Log.i(BTCommCtrl.TAG, "4.1-run () method internal: header validation passed");
                            byte[] bArr3 = new byte[i5];
                            for (byte b2 = 0; b2 < i5; b2 = (byte) (b2 + 1)) {
                                if (BTCommCtrl.this.read_data_chu_li_i + b2 < 600) {
                                    bArr3[b2] = BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_i + b2];
                                } else {
                                    bArr3[b2] = BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_i + b2) - 600];
                                }
                            }
                            if (((byte) (bArr3[2] + bArr3[3] + bArr3[4])) == bArr3[5]) {
                                for (int i9 = 0; i9 < i5; i9++) {
                                    if (BTCommCtrl.this.read_data_chu_li_i + i9 < 600) {
                                        BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_i + i9] = 0;
                                    } else {
                                        BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_i + i9) - 600] = 0;
                                    }
                                }
                                if (BTCommCtrl.this.read_data_chu_li_i < 594) {
                                    BTCommCtrl.this.read_data_chu_li_i += i5;
                                } else {
                                    BTCommCtrl.this.read_data_chu_li_i -= 594;
                                }
                                i8 -= 6;
                                for (byte b3 = 0; b3 < i5; b3 = (byte) (b3 + 1)) {
                                    if (bArr3[b3] >= 0) {
                                        iArr[b3] = bArr3[b3];
                                    } else {
                                        iArr[b3] = (bArr3[b3] & Byte.MAX_VALUE) | 128;
                                    }
                                }
                                int i10 = iArr[2];
                                int i11 = (iArr[3] << 8) + iArr[4];
                                c2 = 1024;
                                if (i10 < 1024) {
                                    BTCommCtrl.this.read_data_data[i10] = i11;
                                }
                                Message obtain = Message.obtain();
                                obtain.what = 200;
                                obtain.obj = bArr3;
                                EventBus.getDefault().post(obtain);
                                c3 = c2;
                            } else {
                                c2 = 1024;
                                BTCommCtrl.access$1908(BTCommCtrl.this);
                                if (BTCommCtrl.this.read_data_chu_li_i > 599) {
                                    BTCommCtrl.this.read_data_chu_li_i = 0;
                                }
                            }
                        } else {
                            c2 = 1024;
                            BTCommCtrl.access$1908(BTCommCtrl.this);
                            if (BTCommCtrl.this.read_data_chu_li_i > 599) {
                                BTCommCtrl.this.read_data_chu_li_i = 0;
                            }
                        }
                        i8--;
                        c3 = c2;
                    }
                    char c4 = c3;
                    if (BTCommCtrl.this.read_data_140_i > BTCommCtrl.this.read_data_chu_li_140_i) {
                        i4 = BTCommCtrl.this.read_data_140_i;
                        i3 = BTCommCtrl.this.read_data_chu_li_140_i;
                    } else {
                        i4 = BTCommCtrl.this.read_data_140_i + 600;
                        i3 = BTCommCtrl.this.read_data_chu_li_140_i;
                    }
                    int i12 = i4 - i3;
                    while (i12 > 139) {
                        byte[] bArr4 = new byte[4];
                        byte b4 = 0;
                        for (byte b5 = 4; b4 < b5; b5 = 4) {
                            if (BTCommCtrl.this.read_data_chu_li_140_i + b4 < 600) {
                                bArr4[b4] = BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_140_i + b4];
                            } else {
                                bArr4[b4] = BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_140_i + b4) - 600];
                            }
                            b4 = (byte) (b4 + 1);
                        }
                        if (bArr4[0] == -86 && bArr4[1] == 85 && bArr4[c] == -86 && bArr4[3] == -1) {
                            Log.i(BTCommCtrl.TAG, "4.2-run () method Internal: 140 header validation passed");
                            short[] sArr = new short[140];
                            for (int i13 = 0; i13 < 140; i13++) {
                                if (BTCommCtrl.this.read_data_chu_li_140_i + i13 < 600) {
                                    if (BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_140_i + i13] >= 0) {
                                        sArr[i13] = (short) BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_140_i + i13];
                                    } else {
                                        sArr[i13] = (short) (BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_140_i + i13] + 256);
                                    }
                                } else if (BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_140_i + i13) - 600] >= 0) {
                                    sArr[i13] = (short) BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_140_i + i13) - 600];
                                } else {
                                    sArr[i13] = (short) (BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_140_i + i13) - 600] + 256);
                                }
                            }
                            int i14 = 0;
                            for (int i15 = 0; i15 < 138; i15++) {
                                if (i15 > 3) {
                                    i14 += sArr[i15];
                                }
                            }
                            if ((sArr[138] << 8) + sArr[139] == i14) {
                                for (int i16 = 0; i16 < 140; i16++) {
                                    if (BTCommCtrl.this.read_data_chu_li_140_i + i16 < 600) {
                                        BTCommCtrl.this.read_data[BTCommCtrl.this.read_data_chu_li_140_i + i16] = 0;
                                    } else {
                                        BTCommCtrl.this.read_data[(BTCommCtrl.this.read_data_chu_li_140_i + i16) - 600] = 0;
                                    }
                                }
                                if (BTCommCtrl.this.read_data_chu_li_140_i < 460) {
                                    BTCommCtrl.this.read_data_chu_li_140_i += 140;
                                } else {
                                    BTCommCtrl.this.read_data_chu_li_140_i -= 460;
                                }
                                i12 -= 140;
                                BTCommCtrl.this.mAmpsDrawn = ((float) ((short) ((sArr[72] << 8) | sArr[73]))) / 10.0f;
                                BTCommCtrl.this.mSummaryVarsManager.setLowestVoltage(((float) ((sArr[4] * 256) + sArr[5])) / 10.0f);
                                BTCommCtrl.this.mSummaryVarsManager.setAmps(BTCommCtrl.this.mAmpsDrawn);
                                BTCommCtrl.this.mSummaryVarsManager.setPower((short) ((sArr[113] << 8) | sArr[114]));
                                BTCommCtrl.this.mSummaryVarsManager.setFETtemp((short) ((sArr[91] << 8) | sArr[92]));
                                if (i6 == 5) {
                                    BTCommCtrl.this.mAmpsDrawn = 248.0f;
                                } else if (i6 == 60) {
                                    BTCommCtrl.this.mAmpsDrawn = 0.0f;
                                }
                                i6++;
                                SendDiagMessage(Float.toString(BTCommCtrl.this.mAmpsDrawn) + " ad | sa=" + Short.toString(BTCommCtrl.this.mGpsAutoTrack.triggerStartAmps) + "dbgcp = " + Integer.toString(i6));
                                CheckTriggersAndUpdateApplicationLog(bArr);
                                Message obtain2 = Message.obtain();
                                obtain2.what = BTCommCtrl.BMS_READ_DATA_CACHE;
                                obtain2.obj = sArr;
                                EventBus.getDefault().post(obtain2);
                            } else {
                                BTCommCtrl.access$2008(BTCommCtrl.this);
                                if (BTCommCtrl.this.read_data_chu_li_140_i > 599) {
                                    BTCommCtrl.this.read_data_chu_li_140_i = 0;
                                }
                                i12--;
                            }
                        } else {
                            BTCommCtrl.access$2008(BTCommCtrl.this);
                            if (BTCommCtrl.this.read_data_chu_li_140_i > 599) {
                                BTCommCtrl.this.read_data_chu_li_140_i = 0;
                            }
                            i12--;
                        }
                        i5 = 6;
                        c4 = 1024;
                        c = 2;
                    }
                    c3 = c4;
                } catch (IOException unused) {
                    this.terminateThread = true;
                    BTCommCtrl.this.mBTDisconnected = new RunningThread(new BTDisconnected());
                    BTCommCtrl.this.mBTDisconnected.startThread();
                    c3 = 1024;
                    i5 = 6;
                }
            }
            try {
                this.mmSocket.close();
                this.mmInStream.close();
                this.mmOutStream.close();
                Thread.sleep(200);
            } catch (IOException | InterruptedException unused2) {
            }
        }

        private void CheckTriggersAndUpdateApplicationLog(byte[] bArr) {
            float f = ((float) ((bArr[116] * 256) + bArr[117])) / 1000.0f;
            float f2 = ((float) ((short) ((bArr[72] << 8) | bArr[73]))) / 10.0f;
            byte b = bArr[115];
            if (f < ((float) BTCommCtrl.this.GetReadDataData()[1]) / 1000.0f || f2 >= 0.0f) {
                BTCommCtrl.this.applicationTriggerVariables.overVoltageCell = 0;
            } else if (BTCommCtrl.this.applicationTriggerVariables.overVoltageCell != b) {
                BTCommCtrl.this.applicationTriggerVariables.overVoltageCell = b;
                BTCommCtrl.this.InsertEntryIntoApplicationLog("Cell number " + ((int) b) + " went over-voltage @ " + Float.toString(f) + " v.");
            }
        }

        public void write(byte[] bArr) {
            try {
                this.mmOutStream.write(bArr);
            } catch (IOException e) {
                Log.e(BTCommCtrl.TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException e) {
                Log.e(BTCommCtrl.TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class BTDisconnected extends ThreadBase {
        private BTDisconnected() {
        }

        @Override
        public void run() {
            if (BTCommCtrl.this.GetGpsTrackerStatus() == 10012 || BTCommCtrl.this.GetGpsTrackerStatus() == 10010) {
                BTCommCtrl.this.StopGPSTracking();
            }
            BTCommCtrl.this.helperEndBTConnection();
            BTCommCtrl.this.mConnectionStatus = 3;
            SendEmptyMessage(BTCommCtrl.this.mConnectionStatus);
        }
    }

    public class ApplicationLogEntry {
        Date dateLog;
        String logEntry;

        public ApplicationLogEntry() {
        }
    }

    private class ApplicationTriggerVariables {
        int overVoltageCell = 0;

        ApplicationTriggerVariables() {
        }
    }
}
