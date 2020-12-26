package com.superev.sbms;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GPSActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_ROUTEFILDE = "routeFilename";
    public static final int GET_ROUTEFILENAME = 100;
    ImageButton btnMaps;
    int dbg = 0;
    ImageView mImageConnStatus;
    ImageView mImageViewRecording;
    Switch mSwitchRecordGPS;
    TextView mTextViewGPSstatus;
    TextView mTextViewSpeed;
    TextView mTextViewSpeedUnits;
    BTCommCtrl mbtCommCtrl;
    TextView mtxtDiag;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_gps);
        setupUISwitches();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.mtxtDiag = (TextView) findViewById(R.id.txtGpsDiag);
        this.mbtCommCtrl = BTCommCtrl.getInstance(null);
        this.mTextViewSpeedUnits = (TextView) findViewById(R.id.txtSpeedUnit);
        this.mTextViewSpeed = (TextView) findViewById(R.id.txtSpeed);
        this.mTextViewGPSstatus = (TextView) findViewById(R.id.textViewGPSstatus);
        this.mImageViewRecording = (ImageView) findViewById(R.id.imageViewRecording);
        this.mImageConnStatus = (ImageView) findViewById(R.id.imageConnStatus);
        ImageButton imageButton = (ImageButton) findViewById(R.id.buttonMaps);
        this.btnMaps = imageButton;
        imageButton.setOnClickListener(this);
        this.mImageConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        setTitle(R.string.gps_title);
        InitialUISetup();
        this.mTextViewSpeedUnits.setText(this.mbtCommCtrl.GetGpsSpeedUnit());
        EventBus.getDefault().register(this);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Message message) {
        int i = message.what;
        if (i == 3) {
            this.mImageConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
            StopGPSTracking();
        } else if (i == 10000) {
            StopGPSTracking();
        } else if (i != 99999) {
            switch (i) {
                case GPSTracker.GPS_WAITING_LOCK:
                    this.mTextViewGPSstatus.setText(R.string.gps_wait_lock);
                    return;
                case GPSTracker.GPS_LOCK_ACQUIRED:
                    this.mTextViewGPSstatus.setText(R.string.gps_got_lock);
                    return;
                case GPSTracker.GPS_TRACKING_INPROGRESS:
                    StartGPSTracking();
                    return;
                case GPSTracker.GPS_LOCATION_UPDATE:
                    this.mTextViewGPSstatus.setText(R.string.gps_got_lock);
                    this.mTextViewSpeed.setText(this.mbtCommCtrl.GetGpsSpeed());
                    return;
                default:
                    return;
            }
        } else {
            this.mtxtDiag.setText((String) message.obj);
        }
    }

    public void GPSSwitchCheckedChanged() {
        if (this.mSwitchRecordGPS.isChecked()) {
            this.mTextViewGPSstatus.setVisibility(0);
            StartGPSTracking();
            return;
        }
        this.mTextViewGPSstatus.setVisibility(4);
        StopGPSTracking();
    }

    private void InitialUISetup() {
        BTCommCtrl bTCommCtrl = this.mbtCommCtrl;
        if (bTCommCtrl != null) {
            if (bTCommCtrl.getConnectionStatus() != 5) {
                this.mSwitchRecordGPS.setEnabled(false);
            }
            if (this.mbtCommCtrl.GetGpsTrackerStatus() == 10012 || this.mbtCommCtrl.GetGpsTrackerStatus() == 10010) {
                this.btnMaps.setVisibility(4);
                this.mSwitchRecordGPS.setChecked(true);
                this.mTextViewGPSstatus.setVisibility(0);
                return;
            }
            this.btnMaps.setVisibility(0);
            this.mSwitchRecordGPS.setChecked(false);
            this.mTextViewGPSstatus.setVisibility(4);
        }
    }

    private void StartGPSTracking() {
        if (this.mbtCommCtrl.GetGpsTrackerStatus() != 10012) {
            this.mbtCommCtrl.StartGPSTracking();
            this.mTextViewSpeedUnits.setText(this.mbtCommCtrl.GetGpsSpeedUnit());
        }
        this.mSwitchRecordGPS.setChecked(true);
        this.btnMaps.setVisibility(4);
    }

    private void StopGPSTracking() {
        if (this.mbtCommCtrl.GetGpsTrackerStatus() == 10012) {
            this.mbtCommCtrl.StopGPSTracking();
        }
        this.btnMaps.setVisibility(0);
        this.mSwitchRecordGPS.setChecked(false);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.buttonMaps) {
            ShowRouteDataMap();
        }
    }

    private void ShowRouteDataMap() {
        FileUtils fileUtils = new FileUtils();
        ArrayList<String> GetFilesInFolder = fileUtils.GetFilesInFolder(getExternalFilesDir(null).getPath(), GPSTracker.routeDataFileExt);
        if (fileUtils.getErrorIsSet().booleanValue()) {
            Toast.makeText(getBaseContext(), fileUtils.getLastErrorDescription(), 0).show();
        } else if (GetFilesInFolder == null) {
            Toast.makeText(getBaseContext(), (int) R.string.route_noDataFiles, 0).show();
        } else {
            startActivity(new Intent(this, RouteDataFileSelect.class));
        }
    }

    private boolean setupUISwitches() {
        ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{-16842910}, new int[]{16842912}, new int[0]}, new int[]{-7829368, -16711936, SupportMenu.CATEGORY_MASK});
        Switch r1 = (Switch) findViewById(R.id.switchTrackGPS);
        this.mSwitchRecordGPS = r1;
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                GPSActivity.this.GPSSwitchCheckedChanged();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            this.mSwitchRecordGPS.setThumbTintList(colorStateList);
        }
        return true;
    }
}
