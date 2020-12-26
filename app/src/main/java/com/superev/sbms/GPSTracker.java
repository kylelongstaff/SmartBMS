package com.superev.sbms;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import com.github.mikephil.charting.BuildConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings("ALL")
public class GPSTracker extends ThreadBase implements LocationListener {
    private static final short AMPSNOR = 0;
    private static final short AMPSREV = 1;
    public static final int GPS_LOCATION_UPDATE = 10013;
    public static final int GPS_LOCK_ACQUIRED = 10011;
    public static final int GPS_OFF = 10000;
    public static final int GPS_ROUTEDATA_BASIC_HEADER_COUNT = 12;
    public static final int GPS_TRACKING_INPROGRESS = 10012;
    public static final int GPS_WAITING_LOCK = 10010;
    private static final short SPEED_UNIT_KPH = 1;
    private static final short SPEED_UNIT_MPH = 2;
    private static final short SPEED_UNIT_MPS = 0;
    private static final short TEMP_C = 0;
    private static final short TEMP_F = 1;
    private static GPSTracker instance = null;
    private static final String newline = String.format("%n", new Object[0]);
    public static final String routeDataFileExt = ".rdf";
    private String cellVolts;
    private final String delimiter = "|";
    Boolean displayFah = false;
    private Context mContext;
    private String mCurrent;
    private DateFormat mDateFormatter;
    private float mDistanceTravelled;
    private boolean mEndGPSTracking = false;
    private short mFETtemperature;
    private short mGPSMinimumDistance = 0;
    private short mGPSMinimumInterval = 5000;
    private FileOutputStream mGPSOutputStream;
    private int mGPSStatus = GPS_OFF;
    private GnssStatus.Callback mGnssStatusCallback;
    private GpsStatus.Listener mGpsListener;
    private boolean mInitialGPSLocation;
    private LocationManager mLocManager;
    private boolean mLogBatteryCellData = false;
    private String mPower;
    private Location mPrevLocation;
    private String mRemainingCapacity;
    private String mSpeed;
    private int mSpeedUnitID;
    private String mSpeedUnits;
    private boolean mStartGPSTracking = false;
    private Date mStartTrackDate;
    private int mTempUnitID;
    private Boolean mTempUnits;
    private DateFormat mTimeFormatter;
    private float mTopSpeed_mps;
    private String mVoltage;

    public void onProviderDisabled(String str) {
    }

    public void onProviderEnabled(String str) {
    }

    public void onStatusChanged(String str, int i, Bundle bundle) {
    }

    public static GPSTracker getInstance(Context context) {
        if (instance == null) {
            instance = new GPSTracker(context);
        }
        return instance;
    }

    private GPSTracker(Context context) {
        this.mContext = context;
        this.mGPSOutputStream = null;
        this.mStartTrackDate = null;
        this.mLocManager = (LocationManager) context.getSystemService("location");
        if (Build.VERSION.SDK_INT >= 24) {
            this.mGnssStatusCallback = new GnssStatus.Callback() {


                public void onFirstFix(int i) {
                    super.onFirstFix(i);
                    GPSTracker.this.mGPSStatus = GPSTracker.GPS_TRACKING_INPROGRESS;
                    GPSTracker.this.SendEmptyMessage(GPSTracker.GPS_LOCK_ACQUIRED);
                }
            };
        } else {
            GpsStatus.Listener r3 = new GpsStatus.Listener() {


                public void onGpsStatusChanged(int i) {
                    if (i == 1) {
                        GPSTracker.this.mGPSStatus = GPSTracker.GPS_WAITING_LOCK;
                        GPSTracker.this.SendEmptyMessage(GPSTracker.GPS_WAITING_LOCK);
                    } else if (i == 3) {
                        GPSTracker.this.mGPSStatus = GPSTracker.GPS_TRACKING_INPROGRESS;
                        GPSTracker.this.SendEmptyMessage(GPSTracker.GPS_LOCK_ACQUIRED);
                    }
                }
            };
            this.mGpsListener = r3;
            try {
                this.mLocManager.addGpsStatusListener(r3);
            } catch (SecurityException e) {
                SendDiagMessage(e.getLocalizedMessage());
            }
        }
        this.mSpeedUnits = "mps";
        this.mDateFormatter = new SimpleDateFormat(RouteFileParser.dateFormat);
        this.mTimeFormatter = new SimpleDateFormat(RouteFileParser.timeFormat);
        GetSpeedUnitPreference();
        EventBus.getDefault().register(this);
    }

    private void GetSpeedUnitPreference() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.pref_SpeedUnits_list_titles);
        int parseInt = Integer.parseInt(defaultSharedPreferences.getString("speedUnits_list", "1"));
        this.mSpeedUnitID = parseInt;
        this.mSpeedUnits = stringArray[parseInt];
        this.displayFah = Boolean.valueOf(defaultSharedPreferences.getBoolean("pref_title_cel_fah", false));
        short parseShort = Short.parseShort(defaultSharedPreferences.getString("edit_text_gps_interval", "5"));
        this.mGPSMinimumInterval = parseShort;
        this.mGPSMinimumInterval = (short) (parseShort * 1000);
    }

    @Override
    public void run() {
        while (!this.terminateThread) {
            if (this.mStartGPSTracking) {
                this.mStartGPSTracking = false;
                EnableGPSTracking();
            }
            if (this.mEndGPSTracking) {
                this.mEndGPSTracking = false;
                DisableGPSTracking();
            }
        }
        EventBus.getDefault().unregister(this);
    }

    public int GetStatus() {
        return this.mGPSStatus;
    }

    public void StopGPSTracking() {
        if (this.mGPSStatus != 10000) {
            this.mEndGPSTracking = true;
        }
    }

    public void StartGPSTracking() {
        if (this.mGPSStatus == 10000) {
            GetSpeedUnitPreference();
            this.mSpeed = "0";
            this.mInitialGPSLocation = true;
            this.mDistanceTravelled = 0.0f;
            this.mStartGPSTracking = true;
        }
    }

    public String GetSpeed() {
        return this.mSpeed;
    }

    public String GetSpeedUnits() {
        if (this.mGPSStatus != 10012) {
            GetSpeedUnitPreference();
        }
        return this.mSpeedUnits;
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventBusMessage(Message message) {
        int i = message.what;
        if (i != 3) {
            if (i == 4000) {
                DumpBMSData((short[]) message.obj);
            }
        } else if (this.mGPSStatus == 10012) {
            this.mEndGPSTracking = true;
        }
    }

    /* WARNING: Removed duplicated region for block: B:19:0x0049  */
    public void onLocationChanged(Location location) {
        float f;
        if (this.mGPSOutputStream != null) {
            if (this.mInitialGPSLocation) {
                this.mPrevLocation = location;
                this.mInitialGPSLocation = false;
            } else {
                this.mDistanceTravelled += location.distanceTo(this.mPrevLocation);
                this.mPrevLocation = location;
            }
            float speed = location.getSpeed();
            if (speed > this.mTopSpeed_mps) {
                this.mTopSpeed_mps = speed;
            }
            int i = this.mSpeedUnitID;
            if (i != 1) {
                if (i == 2) {
                    f = 2.23694f;
                }
                this.mSpeed = Integer.toString((int) speed);
                SendEmptyMessage(GPS_LOCATION_UPDATE);
                if (this.mStartTrackDate == null) {
                    this.mStartTrackDate = Calendar.getInstance().getTime();
                }
                RecordGPSLocation(location.getLongitude(), location.getLatitude(), location.getAltitude());
            }
            f = 3.6f;
            speed *= f;
            this.mSpeed = Integer.toString((int) speed);
            SendEmptyMessage(GPS_LOCATION_UPDATE);
            if (this.mStartTrackDate == null) {
            }
            RecordGPSLocation(location.getLongitude(), location.getLatitude(), location.getAltitude());
        }
    }

    public float GetTopSpeed_mps() {
        return this.mTopSpeed_mps;
    }

    public float GetDistanceTravelled() {
        return this.mDistanceTravelled;
    }

    private boolean RecordGPSLocation(double d, double d2, double d3) {
        Boolean bool = false;
        try {
            Date time = Calendar.getInstance().getTime();
            String d4 = Double.toString(d2);
            String d5 = Double.toString(d);
            String d6 = Double.toString(d3);
            long time2 = (time.getTime() - this.mStartTrackDate.getTime()) / 1000;
            if (this.cellVolts.length() < 10) {
                this.cellVolts = "Something went wrong: RecordGPSLocation called with a truncated cellVolts string";
            }
            this.mGPSOutputStream.write((Long.toString(time2) + "|" + d4 + "|" + d5 + "|" + d6 + "|" + this.mVoltage + "|" + this.mCurrent + "|" + this.mPower + "|" + this.mRemainingCapacity + "|" + ((int) this.mFETtemperature) + "|" + this.mSpeed + "|" + this.mDateFormatter.format(time) + "|" + this.mTimeFormatter.format(time) + "|" + this.cellVolts + newline).getBytes());
            bool = true;
        } catch (IOException e) {
            SendDiagMessage(e.getLocalizedMessage());
            DisableGPSTracking();
        }
        return bool.booleanValue();
    }

    private void EnableGPSTracking() {
        this.mLogBatteryCellData = PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("check_box_LogCellData", false);
        this.cellVolts = BuildConfig.FLAVOR;
        this.mTopSpeed_mps = 0.0f;
        try {
            this.mLocManager.requestLocationUpdates("gps", (long) this.mGPSMinimumInterval, (float) this.mGPSMinimumDistance, this, Looper.getMainLooper());
            try {
                File externalFilesDir = this.mContext.getExternalFilesDir(null);
                if (this.mGPSOutputStream == null) {
                    File file = new File(externalFilesDir, GenerateRouteDataFilename());
                    file.createNewFile();
                    this.mGPSOutputStream = new FileOutputStream(file);
                    StringBuilder sb = new StringBuilder(String.format("Time Stamp| Latitude| Longtitude| Altitude| Voltage| Current| Power| Remaining Capacity| FET temperature| Speed(%s)| Date| Time|", this.mSpeedUnits));
                    if (this.mLogBatteryCellData) {
                        int i = 0;
                        while (i < 32) {
                            i++;
                            sb.append(String.format("Cell %02d%s ", Integer.valueOf(i), "|"));
                        }
                    }
                    sb.append(newline);
                    this.mGPSOutputStream.write(sb.toString().getBytes());
                }
                this.mGPSStatus = GPS_TRACKING_INPROGRESS;
                SendEmptyMessage(GPS_TRACKING_INPROGRESS);
            } catch (IOException e) {
                SendDiagMessage(e.getLocalizedMessage());
                DisableGPSTracking();
            }
        } catch (IllegalArgumentException | SecurityException e2) {
            SendDiagMessage(e2.getLocalizedMessage());
            DisableGPSTracking();
        }
    }

    private void DisableGPSTracking() {
        LocationManager locationManager = this.mLocManager;
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        FileOutputStream fileOutputStream = this.mGPSOutputStream;
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                this.mGPSStatus = GPS_OFF;
                SendEmptyMessage(GPS_OFF);
                this.mGPSOutputStream = null;
            } catch (IOException e) {
                SendDiagMessage(e.getLocalizedMessage());
            }
            this.mStartTrackDate = null;
        }
    }

    private String GenerateRouteDataFilename() {
        String format = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return format + routeDataFileExt;
    }

    private void DumpBMSData(short[] sArr) {
        this.cellVolts = BuildConfig.FLAVOR;
        try {
            this.mVoltage = Float.toString(((float) ((sArr[4] * 256) + sArr[5])) / 10.0f);
            this.mCurrent = Float.toString(((float) ((short) ((sArr[72] << 8) | sArr[73]))) / 10.0f);
            this.mPower = Float.toString((float) ((short) ((sArr[113] << 8) | sArr[114])));
            this.mRemainingCapacity = String.format("%.02f", Float.valueOf(((float) (((((((sArr[79] << 8) | sArr[80]) << 8) | sArr[81]) << 8) | sArr[82]) / 1000)) / 1000.0f));
            this.mFETtemperature = (short) ((sArr[93] << 8) | sArr[94]);
            if (this.displayFah.booleanValue()) {
                this.mFETtemperature = (short) ((int) ((((double) this.mFETtemperature) * 1.8d) + 32.0d));
            }
            Short.toString(this.mFETtemperature);
            if (this.mLogBatteryCellData) {
                for (byte b = 0; b < 32; b = (byte) (b + 1)) {
                    int i = b * 2;
                    int i2 = (sArr[i + 6] * 256) + sArr[i + 7];
                    this.cellVolts += (i2 / 1000) + "." + ((i2 % 1000) / 100) + ((i2 % 100) / 10) + (i2 % 10) + " | ";
                }
            }
        } catch (Exception e) {
            SendDiagMessage(e.getLocalizedMessage());
            this.cellVolts = e.getLocalizedMessage();
        }
    }
}
