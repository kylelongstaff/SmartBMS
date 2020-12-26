package com.superev.sbms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.BuildConfig;
import java.lang.reflect.Array;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ChargeReadingActivity extends AppCompatActivity {
    Boolean AA = true;
    private ChargeReadingAdapter adpt;
    private String[][] cellData = ((String[][]) Array.newInstance(String.class, 16, 2));
    TextView ch_mos_Test;
    int ch_mos_state_mack;
    private String[] ch_mos_value;
    private int[] control_data = {0, 0, 0, 0};
    boolean dbgHandlerSwitch = true;
    TextView dis_mos_Test;
    int dis_mos_state_mack;
    private String[] dis_mos_value;
    boolean displayFah = false;
    private boolean gotCellCount = false;
    Handler handler = new Handler() {


        @SuppressLint("HandlerLeak")
        public void handleMessage(Message message) {
            if (ChargeReadingActivity.this.dbgHandlerSwitch) {
                ChargeReadingActivity.this.lv.setAdapter((ListAdapter) ChargeReadingActivity.this.adpt);
                ChargeReadingActivity.this.adpt.setBatteryCellsCount(32);
                ChargeReadingActivity.this.adpt.notifyDataSetChanged();
                ChargeReadingActivity.this.dbgHandlerSwitch = false;
                for (int i = 0; i < 16; i++) {
                    String[] strArr = ChargeReadingActivity.this.cellData[i];
                    StringBuilder sb = new StringBuilder();
                    int i2 = i / 1000;
                    sb.append(i2);
                    sb.append(".");
                    int i3 = (i % 1000) / 100;
                    sb.append(i3);
                    int i4 = (i % 100) / 10;
                    sb.append(i4);
                    int i5 = i % 10;
                    sb.append(i5);
                    strArr[0] = sb.toString();
                    ChargeReadingActivity.this.cellData[i][1] = i2 + "." + i3 + i4 + i5;
                }
            }
            if (ChargeReadingActivity.this.control_data[1] == 0 || ChargeReadingActivity.this.gotCellCount) {
                ChargeReadingActivity.this.adpt.notifyDataSetChanged();
                if (ChargeReadingActivity.this.ch_mos_state_mack < 20) {
                    ChargeReadingActivity.this.ch_mos_Test.setText(ChargeReadingActivity.this.ch_mos_value[ChargeReadingActivity.this.ch_mos_state_mack]);
                    if (ChargeReadingActivity.this.ch_mos_state_mack == 1) {
                        ChargeReadingActivity.this.ch_mos_Test.setTextColor(-16720128);
                    } else {
                        ChargeReadingActivity.this.ch_mos_Test.setTextColor(-2162688);
                    }
                }
                if (ChargeReadingActivity.this.dis_mos_state_mack < 20) {
                    ChargeReadingActivity.this.dis_mos_Test.setText(ChargeReadingActivity.this.dis_mos_value[ChargeReadingActivity.this.dis_mos_state_mack]);
                    if (ChargeReadingActivity.this.dis_mos_state_mack == 1) {
                        ChargeReadingActivity.this.dis_mos_Test.setTextColor(-16720128);
                    } else {
                        ChargeReadingActivity.this.dis_mos_Test.setTextColor(-2162688);
                    }
                }
                if (ChargeReadingActivity.this.jun_heng_state_mack < 20) {
                    ChargeReadingActivity.this.j_h_Test.setText(ChargeReadingActivity.this.j_h_value[ChargeReadingActivity.this.jun_heng_state_mack]);
                }
                ChargeReadingActivity.this.run_temp.setText("Total runtime: " + (((ChargeReadingActivity.this.timer_s_32_zhi / 60) / 60) / 24) + "D " + (((ChargeReadingActivity.this.timer_s_32_zhi / 60) / 60) % 24) + "H " + ((ChargeReadingActivity.this.timer_s_32_zhi / 60) % 60) + "M " + (ChargeReadingActivity.this.timer_s_32_zhi % 60) + "S");
                return;
            }
            ChargeReadingActivity.this.adpt.setBatteryCellsCount(ChargeReadingActivity.this.control_data[1]);
            ChargeReadingActivity.this.lv.setAdapter((ListAdapter) ChargeReadingActivity.this.adpt);
            ChargeReadingActivity.this.gotCellCount = true;
        }
    };
    ImageView imgConnStatus;
    TextView j_h_Test;
    private String[] j_h_value;
    int jun_heng_state_mack;
    GridView lv;
    private Context mContext;
    BTCommCtrl mbtCommCtrl;
    private int p = 0;
    private short[] read_data_cache140 = new short[140];
    boolean reverseAmps = false;
    TextView run_temp;
    private String[] strMv = {"Volts", "Amps", "Ah", "Watts", "Volts", "Volts", "Volts", "Volts", "Degrees", "Degrees", "Degrees", "Degrees", "Degrees", "Degrees", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "Volts", "A", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V", "V"};
    private String[] strResult2 = {"000.0", "000.0", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000", "0.000"};
    private String[] strTitle_en;
    int timer_s_32_zhi;

    static int access$708(ChargeReadingActivity chargeReadingActivity) {
        int i = chargeReadingActivity.p;
        chargeReadingActivity.p = i + 1;
        return i;
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_charge_reading);
        this.mbtCommCtrl = BTCommCtrl.getInstance(null);
        EventBus.getDefault().register(this);
        this.ch_mos_value = getResources().getStringArray(R.array.charge_ch_mos_value);
        this.dis_mos_value = getResources().getStringArray(R.array.charge_dis_mos_value);
        this.j_h_value = getResources().getStringArray(R.array.charge_j_h_value);
        this.strTitle_en = getResources().getStringArray(R.array.ChargeTitles);
        this.adpt = new ChargeReadingAdapter(this, this.strTitle_en, this.strResult2, this.strMv, this.control_data, this.cellData);
        new Thread(new MyThread()).start();
        this.imgConnStatus = (ImageView) findViewById(R.id.imageConnStatusChg);
        this.ch_mos_Test = (TextView) findViewById(R.id.txtch_mos);
        this.dis_mos_Test = (TextView) findViewById(R.id.txtdis_mos);
        this.j_h_Test = (TextView) findViewById(R.id.txtj_h);
        this.run_temp = (TextView) findViewById(R.id.txtrun_temp);
        this.lv = (GridView) findViewById(R.id.gvChargeReading);
        this.imgConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.chargeReadings);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.displayFah = defaultSharedPreferences.getBoolean("pref_title_cel_fah", false);
        this.reverseAmps = defaultSharedPreferences.getBoolean("pref_title_revamps", false);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        this.AA = false;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            setResult(-1, getIntent());
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Message message) {
        int i = message.what;
        if (i == 3) {
            this.imgConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        } else if (i == 4000) {
            DumpBMSData((short[]) message.obj);
        }
    }

    /* DEBUG: Multi-variable search result rejected for r1v40, resolved type: int[] */
    /* DEBUG: Multi-variable search result rejected for r4v1, resolved type: short[] */
    /* DEBUG: Multi-variable search result rejected for r4v2, resolved type: short */
    /* DEBUG: Multi-variable search result rejected for r1v48, resolved type: int[] */
    /* DEBUG: Multi-variable search result rejected for r6v1, resolved type: short[] */
    /* DEBUG: Multi-variable search result rejected for r6v2, resolved type: short */
    /* DEBUG: Multi-variable search result rejected for r1v108, resolved type: int[] */
    /* DEBUG: Multi-variable search result rejected for r2v12, resolved type: short[] */
    /* DEBUG: Multi-variable search result rejected for r2v13, resolved type: short */
    /* WARN: Multi-variable type inference failed */
    private void DumpBMSData(short[] sArr) {
        try {
            this.read_data_cache140 = sArr;
            this.strResult2[0] = BuildConfig.FLAVOR + (((float) ((sArr[4] * 256) + sArr[5])) / 10.0f);
            float f = ((float) ((short) ((this.read_data_cache140[72] << 8) | this.read_data_cache140[73]))) / 10.0f;
            if (this.reverseAmps) {
                f = -f;
            }
            this.strResult2[1] = BuildConfig.FLAVOR + f;
            this.strResult2[2] = BuildConfig.FLAVOR + (((float) (((((((this.read_data_cache140[79] << 8) | this.read_data_cache140[80]) << 8) | this.read_data_cache140[81]) << 8) | this.read_data_cache140[82]) / 1000)) / 1000.0f);
            short s = (short) ((this.read_data_cache140[113] << 8) | this.read_data_cache140[114]);
            if (this.reverseAmps) {
                s = (short) (-s);
            }
            this.strResult2[3] = BuildConfig.FLAVOR + ((int) s);
            int i = (this.read_data_cache140[116] * 256) + this.read_data_cache140[117];
            this.strResult2[4] = (i / 1000) + "." + ((i % 1000) / 100) + ((i % 100) / 10) + (i % 10);
            String[] strArr = this.strMv;
            StringBuilder sb = new StringBuilder();
            sb.append(BuildConfig.FLAVOR);
            sb.append((int) this.read_data_cache140[115]);
            strArr[4] = sb.toString();
            this.control_data[2] = this.read_data_cache140[115];
            int i2 = (this.read_data_cache140[119] * 256) + this.read_data_cache140[120];
            this.strResult2[5] = (i2 / 1000) + "." + ((i2 % 1000) / 100) + ((i2 % 100) / 10) + (i2 % 10);
            String[] strArr2 = this.strMv;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(BuildConfig.FLAVOR);
            sb2.append((int) this.read_data_cache140[118]);
            strArr2[5] = sb2.toString();
            this.control_data[3] = this.read_data_cache140[118];
            int i3 = (this.read_data_cache140[121] * 256) + this.read_data_cache140[122];
            this.strResult2[6] = (i3 / 1000) + "." + ((i3 % 1000) / 100) + ((i3 % 100) / 10) + (i3 % 10);
            int i4 = ((this.read_data_cache140[116] * 256) + this.read_data_cache140[117]) - ((this.read_data_cache140[119] * 256) + this.read_data_cache140[120]);
            this.strResult2[7] = (i4 / 1000) + "." + ((i4 % 1000) / 100) + ((i4 % 100) / 10) + (i4 % 10);
            short s2 = (short) ((this.read_data_cache140[91] << 8) | this.read_data_cache140[92]);
            if (this.displayFah) {
                this.strResult2[8] = BuildConfig.FLAVOR + Math.round((((double) s2) * 1.8d) + 32.0d);
            } else {
                this.strResult2[8] = BuildConfig.FLAVOR + ((int) s2);
            }
            short s3 = (short) ((this.read_data_cache140[93] << 8) | this.read_data_cache140[94]);
            if (this.displayFah) {
                this.strResult2[9] = BuildConfig.FLAVOR + Math.round((((double) s3) * 1.8d) + 32.0d);
            } else {
                this.strResult2[9] = BuildConfig.FLAVOR + ((int) s3);
            }
            short s4 = (short) ((this.read_data_cache140[95] << 8) | this.read_data_cache140[96]);
            if (this.displayFah) {
                this.strResult2[10] = BuildConfig.FLAVOR + Math.round((((double) s4) * 1.8d) + 32.0d);
            } else {
                this.strResult2[10] = BuildConfig.FLAVOR + ((int) s4);
            }
            short s5 = (short) ((this.read_data_cache140[97] << 8) | this.read_data_cache140[98]);
            if (this.displayFah) {
                this.strResult2[11] = BuildConfig.FLAVOR + Math.round((((double) s5) * 1.8d) + 32.0d);
            } else {
                this.strResult2[11] = BuildConfig.FLAVOR + ((int) s5);
            }
            short s6 = (short) ((this.read_data_cache140[99] << 8) | this.read_data_cache140[100]);
            if (this.displayFah) {
                this.strResult2[12] = BuildConfig.FLAVOR + Math.round((((double) s6) * 1.8d) + 32.0d);
            } else {
                this.strResult2[12] = BuildConfig.FLAVOR + ((int) s6);
            }
            short s7 = (short) ((this.read_data_cache140[101] << 8) | this.read_data_cache140[102]);
            if (this.displayFah) {
                this.strResult2[13] = BuildConfig.FLAVOR + Math.round((((double) s7) * 1.8d) + 32.0d);
            } else {
                this.strResult2[13] = BuildConfig.FLAVOR + ((int) s7);
            }
            for (byte b = 0; b < 16; b = (byte) (b + 1)) {
                int i5 = b * 2;
                int i6 = i5 * 2;
                int i7 = (this.read_data_cache140[i6 + 6] * 256) + this.read_data_cache140[i6 + 7];
                this.cellData[b][0] = (i7 / 1000) + "." + ((i7 % 1000) / 100) + ((i7 % 100) / 10) + (i7 % 10);
                int i8 = (i5 + 1) * 2;
                int i9 = (this.read_data_cache140[i8 + 6] * 256) + this.read_data_cache140[i8 + 7];
                this.cellData[b][1] = (i9 / 1000) + "." + ((i9 % 1000) / 100) + ((i9 % 100) / 10) + (i9 % 10);
            }
            this.ch_mos_state_mack = this.read_data_cache140[103];
            this.dis_mos_state_mack = this.read_data_cache140[104];
            this.jun_heng_state_mack = this.read_data_cache140[105];
            this.control_data[0] = (((((this.read_data_cache140[132] << 8) | this.read_data_cache140[133]) << 8) | this.read_data_cache140[134]) << 8) | this.read_data_cache140[135];
            this.control_data[1] = this.read_data_cache140[123];
            this.timer_s_32_zhi = (((((this.read_data_cache140[87] << 8) | this.read_data_cache140[88]) << 8) | this.read_data_cache140[89]) << 8) | this.read_data_cache140[90];
        } catch (Exception unused) {
        }
    }

    public class MyThread implements Runnable {
        public MyThread() {
        }

        public void run() {
            while (ChargeReadingActivity.this.AA.booleanValue()) {
                try {
                    ChargeReadingActivity.access$708(ChargeReadingActivity.this);
                    Thread.sleep(200);
                    Message obtain = Message.obtain();
                    obtain.what = 1;
                    ChargeReadingActivity.this.handler.sendMessage(obtain);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
