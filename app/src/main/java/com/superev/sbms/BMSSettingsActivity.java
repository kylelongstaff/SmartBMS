package com.superev.sbms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;
import com.github.mikephil.charting.BuildConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@SuppressWarnings("ALL")
public class BMSSettingsActivity extends AppCompatActivity {
    private MyAdapter adapter = null;
    public final int[] address_table = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 37, 38, 39, 40, 31, 33, 35, 41, 42, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74};
    public float[] float_ji_xian_zhi = {4.6f, 4.6f, 4.6f, 4.6f, 4.6f, 4.6f, 150.0f, 150.0f, 600.0f, 100.0f, 600.0f, 100.0f, 4.6f, 4.6f, 4.6f, 280.0f, 3.2f, 60000.0f, 60.0f, 600.0f, 60000.0f, 65535.0f, 5000.0f, 32.0f, 70.0f, 65.0f, 70.0f, 65.0f, 80.0f, 75.0f, 80.0f, 80.0f, 80.0f, 80.0f, 65535.0f, 65535.0f, 65535.0f, 10000.0f, 100.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f, 65535.0f};
    private GridView gridView;
    Handler handler = new Handler() {


        public void handleMessage(Message message) {
            if (message.what == 1) {
                boolean z = false;
                for (int i = 0; i < 1024; i++) {
                    if (BMSSettingsActivity.this.read_data_data_2[i] != BMSSettingsActivity.this.mbtCommCtrl.GetReadDataData()[i]) {
                        BMSSettingsActivity.this.read_data_data_2[i] = BMSSettingsActivity.this.mbtCommCtrl.GetReadDataData()[i];
                        z = true;
                    }
                }
                if (z) {
                    for (int i2 = 0; i2 < BMSSettingsActivity.this.string__name.length; i2++) {
                        ((Map) BMSSettingsActivity.this.mData.get(i2)).put("info", BMSSettingsActivity.this.read_data_zhuan_huan_string(i2));
                        ((Map) BMSSettingsActivity.this.mData.get(i2)).put("set_text", BMSSettingsActivity.this.read_data_zhuan_huan_string(i2));
                    }
                    BMSSettingsActivity.this.adapter.notifyDataSetChanged();
                }
            }
            super.handleMessage(message);
        }
    };
    Handler handler1 = new Handler() {


        public void handleMessage(Message message) {
            boolean z = false;
            if (message.what == 1) {
                BTCommCtrl bTCommCtrl = BMSSettingsActivity.this.mbtCommCtrl;
                BMSSettingsActivity bMSSettingsActivity = BMSSettingsActivity.this;
                int i = bMSSettingsActivity.read_data_i;
                bMSSettingsActivity.read_data_i = i + 1;
                bTCommCtrl.Send_6bit(23130, i, 0);
                if (BMSSettingsActivity.this.read_data_i > 80) {
                    BMSSettingsActivity.this.read_data_i = 1;
                    if (BMSSettingsActivity.this.timer1 != null) {
                        BMSSettingsActivity.this.timer1.cancel();
                        BMSSettingsActivity.this.timer1 = null;
                        BMSSettingsActivity.this.task1 = null;
                    }
                }
                z = true;
            }
            if (!z) {
                super.handleMessage(message);
            }
        }
    };
    private ImageView imgConnStatus;
    private int mBatteryCapacity;
    private List<Map<String, Object>> mData;
    private BTCommCtrl mbtCommCtrl = null;
    public int[] read_data_data_2 = new int[1024];
    public int read_data_i = 1;
    int send_parameter_dat;
    int send_parameter_position;
    private String[] string__name;
    public String[] string__name_help;
    public String[] string_dan_wei = {"V ", "V ", "V ", "V ", "V ", "V ", "V ", "V ", "A ", "S ", "A ", "S ", "V ", "V ", "V ", "mA", "V ", "A ", "A ", "A ", "MS", "S ", "N", "S ", "℃", "℃", "℃", "℃ ", "℃", "℃", "℃ ", "℃", "℃ ", "℃ ", "AH ", "AH ", "AH ", "M ", "N ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR ", "MR "};
    TimerTask task = new TimerTask() {


        public void run() {
            Message message = new Message();
            message.what = 1;
            BMSSettingsActivity.this.handler.sendMessage(message);
        }
    };
    TimerTask task1 = null;
    Timer timer = new Timer();
    Timer timer1 = null;
    public float[] zhuan_huan_xi_shu = {1000.0f, 1000.0f, 1000.0f, 1000.0f, 1000.0f, 1000.0f, 10.0f, 10.0f, 10.0f, 1.0f, 10.0f, 1.0f, 1000.0f, 1000.0f, 1000.0f, 0.1f, 1000.0f, 10.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 100.0f, 100.0f, 100.0f, 1.0f, 1.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f, 10.0f};

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bms_settings);
        this.string__name = getResources().getStringArray(R.array.BmsSettingNames);
        this.string__name_help = getResources().getStringArray(R.array.bmsSettingHelp);
        this.mbtCommCtrl = BTCommCtrl.getInstance(null);
        this.mData = getData();
        EventBus.getDefault().register(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.bmsSettingTitle);
        this.gridView = (GridView) findViewById(R.id.lvAppSettings);
        this.imgConnStatus = (ImageView) findViewById(R.id.imageConnStatusBMS);
        MyAdapter myAdapter = new MyAdapter(this);
        this.adapter = myAdapter;
        this.gridView.setAdapter((ListAdapter) myAdapter);
        this.imgConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        this.timer.schedule(this.task, 500, 200);
        if (this.timer1 == null && this.task1 == null) {
            this.timer1 = new Timer();
            TimerTask r1 = new TimerTask() {


                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    BMSSettingsActivity.this.handler1.sendMessage(message);
                }
            };
            this.task1 = r1;
            this.timer1.schedule(r1, 80, 8);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onStart() {
        super.onStart();
    }

    @Override // androidx.appcompat.app.AppCompatActivity
    protected void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UpdateGPSTrackingVariables();
        UpdateOtherTrackingVariables();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    void SizeTheListView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ViewGroup.LayoutParams layoutParams = this.gridView.getLayoutParams();
        int[] iArr = new int[2];
        this.gridView.getLocationOnScreen(iArr);
        int i = iArr[1];
        layoutParams.height = displayMetrics.heightPixels - (iArr[1] + 650);
        this.gridView.setLayoutParams(layoutParams);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Message message) {
        if (message.what == 3) {
            this.imgConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        }
    }

    public void OnbtnRefreshParams_click(View view) {
        if (this.timer1 == null && this.task1 == null) {
            this.timer1 = new Timer();
            TimerTask r1 = new TimerTask() {


                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    BMSSettingsActivity.this.handler1.sendMessage(message);
                }
            };
            this.task1 = r1;
            this.timer1.schedule(r1, 80, 8);
        }
    }

    private List<Map<String, Object>> getData() {
        ArrayList arrayList = new ArrayList();
        new HashMap();
        for (int i = 0; i < this.string__name.length; i++) {
            HashMap hashMap = new HashMap();
            hashMap.put("title", this.string__name[i]);
            hashMap.put("info", "0");
            hashMap.put("set_text", "0");
            hashMap.put("info_mv", this.string_dan_wei[i]);
            arrayList.add(hashMap);
        }
        return arrayList;
    }

    private void UpdateGPSTrackingVariables() {
        this.mbtCommCtrl.refreshGPSAutoTrackValues();
    }

    private void UpdateOtherTrackingVariables() {
        this.mbtCommCtrl.SetBatteryChargeCufOffVoltgae(Float.parseFloat(read_data_zhuan_huan_string(6)));
        this.mbtCommCtrl.SetBatteryCapacity(this.mBatteryCapacity);
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return 0;
        }

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        public int getCount() {
            return BMSSettingsActivity.this.mData.size();
        }

        public View getView(final int i, View view, ViewGroup viewGroup) {
            View view2;
            final ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view2 = this.mInflater.inflate(R.layout.listviewrow_appsetting, (ViewGroup) null);
                viewHolder.title = (TextView) view2.findViewById(R.id.txtASTitle);
                viewHolder.info = (TextView) view2.findViewById(R.id.txtASInfo);
                viewHolder.info_mv = (TextView) view2.findViewById(R.id.txtASInfomv);
                viewHolder.viewBtn = (Button) view2.findViewById(R.id.btnASView);
                viewHolder.set_text = (EditText) view2.findViewById(R.id.editASText1);
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText((String) ((Map) BMSSettingsActivity.this.mData.get(i)).get("title"));
            viewHolder.info.setText((String) ((Map) BMSSettingsActivity.this.mData.get(i)).get("info"));
            viewHolder.info_mv.setText((String) ((Map) BMSSettingsActivity.this.mData.get(i)).get("info_mv"));
            viewHolder.set_text.setText((String) ((Map) BMSSettingsActivity.this.mData.get(i)).get("set_text"));
            viewHolder.viewBtn.setOnClickListener(new View.OnClickListener() {


                public void onClick(View view) {
                    try {
                        BMSSettingsActivity.this.shu_ru_show_confirm(i, BMSSettingsActivity.this.shu_ru_zhuan_huan(i, Float.parseFloat(viewHolder.set_text.getText().toString())));
                    } catch (Exception unused) {
                    }
                }
            });
            viewHolder.title.setOnClickListener(new View.OnClickListener() {


                public void onClick(View view) {
                    BMSSettingsActivity.this.show_help(i);
                }
            });
            viewHolder.info.setOnClickListener(new View.OnClickListener() {


                public void onClick(View view) {
                    BMSSettingsActivity.this.mbtCommCtrl.Send_6bit(23130, BMSSettingsActivity.this.address_table[i], 0);
                    BMSSettingsActivity.this.mbtCommCtrl.Send_6bit(23130, BMSSettingsActivity.this.address_table[i] + 1, 0);
                }
            });
            return view2;
        }
    }

    public final class ViewHolder {
        public TextView info;
        public TextView info_mv;
        public EditText set_text;
        public TextView title;
        public Button viewBtn;

        public ViewHolder() {
        }
    }

    public void show_help(int i) {
        String str = this.string__name[i];
        new AlertDialog.Builder(this).setTitle(str).setMessage(this.string__name_help[i]).setPositiveButton("Close ", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();
    }

    public void shu_ru_show_confirm(int i, int i2) {
        this.send_parameter_position = i;
        this.send_parameter_dat = i2;
        new AlertDialog.Builder(this).setTitle("Are you sure you want to set the parameters?").setMessage(this.string__name_help[i]).setPositiveButton("Yes", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialogInterface, int i) {
                if (BMSSettingsActivity.this.send_parameter_dat >= -40) {
                    BMSSettingsActivity bMSSettingsActivity = BMSSettingsActivity.this;
                    bMSSettingsActivity.send_data_zhuan_huan(bMSSettingsActivity.send_parameter_position, BMSSettingsActivity.this.send_parameter_dat);
                    return;
                }
                BMSSettingsActivity bMSSettingsActivity2 = BMSSettingsActivity.this;
                bMSSettingsActivity2.shu_ru_show_help(bMSSettingsActivity2.send_parameter_position);
            }
        }).show();
    }

    public void shu_ru_show_help(int i) {
        new AlertDialog.Builder(this).setTitle("Out of range").setMessage(this.string__name_help[i]).setPositiveButton("Yes", new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();
    }

    /* WARNING: Removed duplicated region for block: B:11:0x0066  */
    /* WARNING: Removed duplicated region for block: B:26:0x00c1  */
    /* WARNING: Removed duplicated region for block: B:8:0x0044  */
    public void send_data_zhuan_huan(int i, int i2) {
        switch (i) {
            case 30:
                if (i2 < 0) {
                    i2 |= (short) i2;
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            case 31:
                if (i2 < 0) {
                    i2 |= (short) i2;
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            case 32:
                if (i2 < 0) {
                    i2 |= (short) i2;
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            case 33:
                if (i2 < 0) {
                    i2 |= (short) i2;
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            case 34:
                if (i2 >= 0) {
                    this.mBatteryCapacity = i2;
                    int i3 = i2 * GPSTracker.GPS_OFF;
                    int i4 = (i3 >> 16) & SupportMenu.USER_MASK;
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i3 & SupportMenu.USER_MASK);
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i] + 1, i4);
                    this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                    return;
                }
                if (i2 >= 0) {
                    int i5 = i2 * GPSTracker.GPS_OFF;
                    int i6 = (i5 >> 16) & SupportMenu.USER_MASK;
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i5 & SupportMenu.USER_MASK);
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i] + 1, i6);
                    this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                    return;
                }
                if (i2 >= 0) {
                    int i7 = i2 * GPSTracker.GPS_OFF;
                    int i8 = (i7 >> 16) & SupportMenu.USER_MASK;
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i7 & SupportMenu.USER_MASK);
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i] + 1, i8);
                    this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                    return;
                }
                if (i2 < 0) {
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            case 35:
                if (i2 >= 0) {
                }
                if (i2 >= 0) {
                }
                if (i2 < 0) {
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            case 36:
                if (i2 >= 0) {
                }
                if (i2 < 0) {
                }
                this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                return;
            default:
                if (i2 >= 0) {
                    this.mbtCommCtrl.Send_6bit(42405, this.address_table[i], i2);
                    this.mbtCommCtrl.Send_6bit(42405, 255, 0);
                    return;
                }
                return;
        }
    }

    public int read_data_zhuan_huan(int i) {
        switch (i) {
            case 30:
                return (short) this.read_data_data_2[this.address_table[i]];
            case 31:
                return (short) this.read_data_data_2[this.address_table[i]];
            case 32:
                return (short) this.read_data_data_2[this.address_table[i]];
            case 33:
                return (short) this.read_data_data_2[this.address_table[i]];
            case 34:
                int[] iArr = this.read_data_data_2;
                int[] iArr2 = this.address_table;
                int i2 = ((iArr[iArr2[i] + 1] << 16) + iArr[iArr2[i]]) / GPSTracker.GPS_OFF;
                this.mBatteryCapacity = i2;
                return i2;
            case 35:
                int[] iArr3 = this.read_data_data_2;
                int[] iArr4 = this.address_table;
                return ((iArr3[iArr4[i] + 1] << 16) + iArr3[iArr4[i]]) / GPSTracker.GPS_OFF;
            case 36:
                int[] iArr5 = this.read_data_data_2;
                int[] iArr6 = this.address_table;
                return ((iArr5[iArr6[i] + 1] << 16) + iArr5[iArr6[i]]) / GPSTracker.GPS_OFF;
            default:
                return this.read_data_data_2[this.address_table[i]];
        }
    }

    public String read_data_zhuan_huan_string(int i) {
        if (this.zhuan_huan_xi_shu[i] > 1.0f) {
            float read_data_zhuan_huan = ((float) read_data_zhuan_huan(i)) / this.zhuan_huan_xi_shu[i];
            return BuildConfig.FLAVOR + read_data_zhuan_huan;
        }
        int read_data_zhuan_huan2 = (int) (((float) read_data_zhuan_huan(i)) / this.zhuan_huan_xi_shu[i]);
        return BuildConfig.FLAVOR + read_data_zhuan_huan2;
    }

    public int shu_ru_zhuan_huan(int i, float f) {
        if (f <= this.float_ji_xian_zhi[i]) {
            return (int) (f * this.zhuan_huan_xi_shu[i]);
        }
        return -50;
    }
}
