package com.superev.sbms;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String CHANNEL_ID = "VbmsServiceChannel";
    public static final String intentExtraClearBTDev = "clearBTDev";
    public int SOC;
    RunningThread alarmSoundThread;
    int dbgCounter;
    DecimalFormat decimalFormat;
    Boolean displayFah = false;
    Boolean gpsTrackingInProgress = false;
    Locale locale;
    ImageButton mBtnAlarm;
    ImageButton mBtnAppSettings;
    ImageButton mBtnBMScontrols;
    ImageButton mBtnBluetooth;
    ImageButton mBtnChargeReadings;
    ImageButton mBtnGPS;
    ImageButton mBtnSummary;
    TextView mDiagMsg;
    ImageView mImgBike;
    ImageView mImgRecording;
    int mbatteryCapacity;
    BTCommCtrl mbtCommCtrl = null;
    TextView mtextFETtemperature;
    TextView mtextLabelAmps;
    TextView mtextSOC;
    TextView mtextViewAmpHoursRemaining;
    TextView mtextViewAmps;
    TextView mtextViewElapsedTime;
    TextView mtextViewVoltage;
    TextView mtextViewWatts;
    Boolean overvoltAlarmEnabled = false;
    Boolean reverseAmps = false;
    Boolean silenceAlarmSound = false;
    Boolean swapAmpsForSpeed = false;

    public MainActivity() {
        Locale locale2 = new Locale("en", "US");
        this.locale = locale2;
        this.decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale2);
        this.mbatteryCapacity = 1;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        this.decimalFormat.applyPattern("###.##");
        this.silenceAlarmSound = false;
        this.mDiagMsg = (TextView) findViewById(R.id.txtDebugInfo);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imgbtnGPS);
        this.mBtnGPS = imageButton;
        imageButton.setOnClickListener(this);
        ImageButton imageButton2 = (ImageButton) findViewById(R.id.imgbtnAppSettings);
        this.mBtnAppSettings = imageButton2;
        imageButton2.setOnClickListener(this);
        ImageButton imageButton3 = (ImageButton) findViewById(R.id.imgbtnBluetooth);
        this.mBtnBluetooth = imageButton3;
        imageButton3.setOnClickListener(this);
        ImageButton imageButton4 = (ImageButton) findViewById(R.id.btnBMScontrols);
        this.mBtnBMScontrols = imageButton4;
        imageButton4.setOnClickListener(this);
        ImageButton imageButton5 = (ImageButton) findViewById(R.id.imgbtnSummary);
        this.mBtnSummary = imageButton5;
        imageButton5.setOnClickListener(this);
        ImageButton imageButton6 = (ImageButton) findViewById(R.id.imgbtnChargeReadings);
        this.mBtnChargeReadings = imageButton6;
        imageButton6.setOnClickListener(this);
        ImageButton imageButton7 = (ImageButton) findViewById(R.id.btnAlarm);
        this.mBtnAlarm = imageButton7;
        imageButton7.setOnClickListener(this);
        this.mtextLabelAmps = (TextView) findViewById(R.id.txtLabelAmps);
        this.mtextViewVoltage = (TextView) findViewById(R.id.txtVoltageReading);
        this.mtextViewAmps = (TextView) findViewById(R.id.txtAmpsReading);
        this.mtextViewWatts = (TextView) findViewById(R.id.txtWattsReading);
        this.mtextViewAmpHoursRemaining = (TextView) findViewById(R.id.txtAmpHoursReading);
        this.mtextViewElapsedTime = (TextView) findViewById(R.id.txtViewElapsedTime);
        this.mtextFETtemperature = (TextView) findViewById(R.id.txtFETtemp);
        this.mtextSOC = (TextView) findViewById(R.id.txtFETtemp2);
        ImageView imageView = (ImageView) findViewById(R.id.imgRecording);
        this.mImgRecording = imageView;
        imageView.setVisibility(4);
        ImageView imageView2 = (ImageView) findViewById(R.id.invisible);
        this.mImgBike = imageView2;
        imageView2.setVisibility(4);
        EventBus.getDefault().register(this);
        BTCommCtrl instance = BTCommCtrl.getInstance(this);
        this.mbtCommCtrl = instance;
        if (instance.getConnectionStatus() == 0) {
            this.mBtnBluetooth.setEnabled(false);
            Toast.makeText(getBaseContext(), getResources().getString(R.string.toast_noBluetooth), 0).show();
            this.mBtnBluetooth.setBackgroundColor(-7829368);
        }
        if (!(ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0 || Build.VERSION.SDK_INT < 23)) {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"}, 10);
        }
        this.mbatteryCapacity = this.mbtCommCtrl.GetBatteryCapacity();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(new NotificationChannel(CHANNEL_ID, "VBMS Service Channel", 3));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_settings_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.main_menuTroubleshoot) {
            startActivity(new Intent(this, TroubleshootActivity.class));
            return true;
        } else if (itemId == R.id.main_menuAbout) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        } else if (itemId == R.id.main_menuLog) {
            startActivity(new Intent(this, ApplicationLog.class));
            return true;
        } else if (itemId != R.id.main_menuSettings) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            Intent intent = new Intent(this, AppSettingsActivity.class);
            intent.putExtra(":android:show_fragment", AppSettingsActivity.GeneralPreferenceFragment.class.getName());
            intent.putExtra(":android:no_headers", true);
            startActivity(intent);
            return true;
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
    }

    @Override // androidx.fragment.app.FragmentActivity
    protected void onResume() {
        super.onResume();
        setupBluetoothConnection(false);
        setBluetoothButtonStatus();
        this.overvoltAlarmEnabled = Boolean.valueOf(this.mbtCommCtrl.GetOvervoltAlarmEnabled());
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.swapAmpsForSpeed = Boolean.valueOf(defaultSharedPreferences.getBoolean("check_box_showspeed", false));
        this.displayFah = Boolean.valueOf(defaultSharedPreferences.getBoolean("pref_title_cel_fah", false));
        this.reverseAmps = Boolean.valueOf(defaultSharedPreferences.getBoolean("pref_title_revamps", false));
        UpdateAmpSpeedLabel();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, VbmsService.class));
    }

    @Override
    public void onBackPressed() {
        if (this.gpsTrackingInProgress.booleanValue()) {
            new AlertDialog.Builder(this).setMessage(R.string.end_app_warning).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {


                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.super.onBackPressed();
                }
            }).setNegativeButton(R.string.no, (DialogInterface.OnClickListener) null).show();
        } else {
            super.onBackPressed();
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.imgbtnSummary) {
            switch (id) {
                case R.id.btnAlarm:
                    SoundAlarm(false);
                    this.silenceAlarmSound = true;
                    return;
                case R.id.btnBMScontrols:
                    startActivityForResult(new Intent(this, BMSControlsActivity.class), 6);
                    return;
                default:
                    switch (id) {
                        case R.id.imgbtnAppSettings:
                            startActivity(new Intent(this, BMSSettingsActivity.class));
                            GetChargeOverVoltageValue();
                            this.mbatteryCapacity = this.mbtCommCtrl.GetBatteryCapacity();
                            return;
                        case R.id.imgbtnBluetooth:
                            OnBluetoothButtonClick();
                            return;
                        case R.id.imgbtnChargeReadings:
                            startActivity(new Intent(this, ChargeReadingActivity.class));
                            return;
                        case R.id.imgbtnGPS:
                            startActivity(new Intent(this, GPSActivity.class));
                            return;
                        default:
                            return;
                    }
            }
        } else {
            try {
                startActivity(new Intent(this, SummaryActivity.class));
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), 0).show();
            }
        }
    }

    private void OnBluetoothButtonClick() {
        int connectionStatus = this.mbtCommCtrl.getConnectionStatus();
        if (connectionStatus == 1) {
            startActivityForResult(new Intent(this, ScanBluetoothDevsActivity.class), 100);
        } else if (connectionStatus == 3) {
            setupBluetoothConnection(true);
        } else if (connectionStatus == 5) {
            this.mbtCommCtrl.endBTConnection();
        }
    }

    private void setupBluetoothConnection(boolean z) {
        if (this.mbtCommCtrl.getConnectionStatus() != 0) {
            BTCommCtrl bTCommCtrl = this.mbtCommCtrl;
            bTCommCtrl.startBTConnection(bTCommCtrl.GetBTDeviceName(), z);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == 6) {
            Boolean valueOf = Boolean.valueOf(intent.getExtras().getBoolean(intentExtraClearBTDev));
            this.mDiagMsg.setText(Boolean.toString(valueOf.booleanValue()));
            if (valueOf.booleanValue()) {
                this.mbtCommCtrl.RemoveConnectedBTDevice();
                setBluetoothButtonStatus();
            }
        } else if (i != 100) {
            if (i == 101 && i2 == -1) {
                setupBluetoothConnection(true);
            }
        } else if (i2 == -1) {
            this.mbtCommCtrl.SetBTDeviceName(intent.getExtras().getString(ScanBluetoothDevsActivity.EXTRA_MAC_ADDRESS));
            setupBluetoothConnection(true);
        }
    }

    private void setBluetoothButtonStatus() {
        int connectionStatus = this.mbtCommCtrl.getConnectionStatus();
        boolean z = true;
        if (!(connectionStatus == 1 || connectionStatus == 2 || connectionStatus == 3)) {
            z = false;
        }
        this.mBtnBluetooth.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        if (z) {
            this.mtextViewVoltage.setText("0.0");
            this.mtextViewAmps.setText("0.0");
            this.mtextViewAmpHoursRemaining.setText("0.0");
            this.mtextViewWatts.setText("0.0");
            this.mtextFETtemperature.setText("0");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            this.mDiagMsg.setText("No device selected event received");
            setBluetoothButtonStatus();
        } else if (i == 2) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 101);
        } else if (i == 3) {
            StopVbmsService();
            setBluetoothButtonStatus();
        } else if (i == 4) {
            setBluetoothButtonStatus();
        } else if (i == 5) {
            setBluetoothButtonStatus();
            this.mbtCommCtrl.StartVBMSDataRequestPump(500);
            GetChargeOverVoltageValue();
            StartVbmsService();
        } else if (i == 2000) {
            this.mtextViewElapsedTime.setText(this.mbtCommCtrl.GetBluetoothConnectionTime());
        } else if (i == 4000) {
            DumpBMSData((short[]) message.obj);
        } else if (i == 10000) {
            this.mImgRecording.setVisibility(4);
            this.gpsTrackingInProgress = false;
            UpdateAmpSpeedLabel();
        } else if (i == 10012) {
            this.mImgRecording.setVisibility(0);
            this.gpsTrackingInProgress = true;
            if (this.swapAmpsForSpeed.booleanValue()) {
                UpdateAmpSpeedLabel();
            }
        } else if (i == 99999) {
            this.mDiagMsg.setText((String) message.obj);
        }
    }

    private void UpdateAmpSpeedLabel() {
        if (!this.gpsTrackingInProgress.booleanValue() || !this.swapAmpsForSpeed.booleanValue()) {
            this.mtextLabelAmps.setText(R.string.main_amps);
            this.mImgBike.setVisibility(4);
            return;
        }
        this.mtextLabelAmps.setText(this.mbtCommCtrl.GetGpsSpeedUnit());
        this.mImgBike.setVisibility(0);
    }

    private void StartVbmsService() {
        startService(new Intent(this, VbmsService.class));
    }

    private void StopVbmsService() {
        stopService(new Intent(this, VbmsService.class));
    }

    private void DumpBMSData(short[] sArr) {
        float f;
        try {
            this.mtextViewVoltage.setText(Float.toString(((float) ((sArr[4] * 256) + sArr[5])) / 10.0f));
            if (!this.gpsTrackingInProgress.booleanValue()) {
                f = ((float) ((short) ((sArr[72] << 8) | sArr[73]))) / 10.0f;
                if (this.reverseAmps.booleanValue()) {
                    f = -f;
                }
                this.mtextViewAmps.setText(Float.toString(f));
            } else {
                if (this.swapAmpsForSpeed.booleanValue()) {
                    this.mtextViewAmps.setText(this.mbtCommCtrl.GetGpsSpeed());
                }
                f = 0.0f;
            }
            short s = (short) ((sArr[113] << 8) | sArr[114]);
            if (this.reverseAmps.booleanValue()) {
                s = (short) (-s);
            }
            this.mtextViewWatts.setText(Float.toString((float) s));
            this.mtextViewAmpHoursRemaining.setText(this.decimalFormat.format((double) (((float) (((((((sArr[79] << 8) | sArr[80]) << 8) | sArr[81]) << 8) | sArr[82]) / 1000)) / 1000.0f)));
            short s2 = (short) ((sArr[91] << 8) | sArr[92]);
            if (this.displayFah.booleanValue()) {
                s2 = (short) ((int) ((((double) s2) * 1.8d) + 32.0d));
            }
            this.mtextFETtemperature.setText(Integer.toString(s2));
            int GetBatteryCapacity = (int) (((((float) (((((((sArr[79] << 8) | sArr[80]) << 8) | sArr[81]) << 8) | sArr[82]) / 1000)) / 1000.0f) / ((float) (this.mbtCommCtrl.GetBatteryCapacity() / 100))) * 100.0f);
            this.SOC = GetBatteryCapacity;
            this.mtextSOC.setText(String.valueOf(GetBatteryCapacity));
            float f2 = ((float) ((sArr[116] * 256) + sArr[117])) / 1000.0f;
            float f3 = ((float) this.mbtCommCtrl.GetReadDataData()[1]) / 1000.0f;
            if (f == 0.0f && this.silenceAlarmSound.booleanValue()) {
                this.silenceAlarmSound = false;
            }
            if (!this.overvoltAlarmEnabled.booleanValue()) {
                return;
            }
            if (f2 < f3 || this.alarmSoundThread != null) {
                if (f2 < f3 && this.alarmSoundThread != null) {
                    SoundAlarm(false);
                }
            } else if (!this.silenceAlarmSound.booleanValue()) {
                SoundAlarm(true);
            }
        } catch (Exception e) {
            this.mDiagMsg.setText(e.toString());
        }
    }

    private void GetChargeOverVoltageValue() {
        this.mbtCommCtrl.Send_6bit(23130, 1, 0);
    }

    private void SoundAlarm(boolean z) {
        if (!z) {
            RunningThread runningThread = this.alarmSoundThread;
            if (runningThread != null) {
                runningThread.endThread(true);
                this.alarmSoundThread = null;
            }
            this.mBtnAlarm.setVisibility(4);
            return;
        }
        this.mBtnAlarm.setVisibility(0);
        if (this.alarmSoundThread == null) {
            RunningThread runningThread2 = new RunningThread(new AlarmSound());
            this.alarmSoundThread = runningThread2;
            runningThread2.startThread();
        }
    }
}
