package com.superev.sbms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;
import com.github.mikephil.charting.BuildConfig;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BMSControlsActivity extends AppCompatActivity implements View.OnClickListener {
    private CheckBox chkClearBTDev;
    private ImageView imgConnStatus;
    private BTCommCtrl mbtCommCtrl;
    public int[] read_data_data = new int[1024];
    private Switch switchOvervoltAlarm;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_bms_controls);
        this.imgConnStatus = (ImageView) findViewById(R.id.imageConnStatusSettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        EventBus.getDefault().register(this);
        BTCommCtrl instance = BTCommCtrl.getInstance(null);
        this.mbtCommCtrl = instance;
        this.imgConnStatus.setBackgroundColor(instance.GetBTConntectionStatusColour());
        getIntent().putExtra(MainActivity.intentExtraClearBTDev, false);
        setResult(-1, getIntent());
        setTitle(R.string.settings_title);
        setupUISwitches();
        if (this.mbtCommCtrl.GetOvervoltAlarmEnabled()) {
            this.switchOvervoltAlarm.setChecked(true);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        setResult(-1, getIntent());
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnZeroCurrent) {
            this.mbtCommCtrl.ZeroCurrentBMS();
        } else if (id != R.id.chkClearBTDev) {
            switch (id) {
                case R.id.btnBalanceCells /*{ENCODED_INT: 2131361866}*/:
                    this.mbtCommCtrl.BalanceCellsBMS();
                    return;
                case R.id.btnChargeOFF /*{ENCODED_INT: 2131361867}*/:
                    this.mbtCommCtrl.ChargeOffBMS();
                    return;
                case R.id.btnChargeON /*{ENCODED_INT: 2131361868}*/:
                    this.mbtCommCtrl.ChargeOnBMS();
                    return;
                default:
                    switch (id) {
                        case R.id.btnFactoryReset /*{ENCODED_INT: 2131361871}*/:
                            new AlertDialog.Builder(this).setTitle("Caution!\nFactory reset selected.").setMessage("Press Yes to reset the BMS to factory defaults.\nPress back to cancel.").setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                                public void onClick(DialogInterface dialogInterface, int i) {
                                    BMSControlsActivity.this.mbtCommCtrl.FactoryResetBMS();
                                }
                            }).show();
                            return;
                        case R.id.btnPowerOFF /*{ENCODED_INT: 2131361872}*/:
                            this.mbtCommCtrl.PowerOffBMS();
                            return;
                        case R.id.btnPowerON /*{ENCODED_INT: 2131361873}*/:
                            this.mbtCommCtrl.PowerOnBMS();
                            return;
                        case R.id.btnReboot /*{ENCODED_INT: 2131361874}*/:
                            this.mbtCommCtrl.RebootBMS();
                            return;
                        default:
                            return;
                    }
            }
        } else {
            getIntent().putExtra(MainActivity.intentExtraClearBTDev, this.chkClearBTDev.isChecked());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Message message) {
        int i = message.what;
        if (i == 3) {
            this.imgConnStatus.setBackgroundColor(this.mbtCommCtrl.GetBTConntectionStatusColour());
        } else if (i == 200) {
            ShowFeedbackMessage((byte[]) message.obj);
        }
    }

    /* DEBUG: Multi-variable search result rejected for r9v0, resolved type: byte[] */
    /* DEBUG: Multi-variable search result rejected for r0v2, resolved type: byte */
    /* DEBUG: Multi-variable search result rejected for r9v1, resolved type: byte */
    /* DEBUG: Multi-variable search result rejected for r6v2, resolved type: byte */
    /* DEBUG: Multi-variable search result rejected for r4v4, resolved type: byte */
    /* DEBUG: Multi-variable search result rejected for r4v5, resolved type: byte */
    /* DEBUG: Multi-variable search result rejected for r4v8, resolved type: byte */
    /* WARN: Multi-variable type inference failed */
    private void ShowFeedbackMessage(byte[] bArr) {
        int[] iArr = new int[6];
        for (byte b = 0; b < 6; b = (byte) (b + 1)) {
            if (bArr[b] >= 0) {
                iArr[b] = bArr[b];
            } else {
                iArr[b] = (bArr[b] & 127) | 128;
            }
        }
        int i = iArr[2];
        int i2 = (iArr[3] << 8) + iArr[4];
        if (i < 1024) {
            this.read_data_data[i] = i2;
        }
        String str = BuildConfig.FLAVOR;
        String str2 = str;
        for (int i3 = 0; i3 < bArr.length; i3++) {
            str2 = str2 + ((int) bArr[i3]);
        }
        if (!(bArr[2] == 0 || bArr[0] == 90)) {
            if (i == 252 && i2 == 1) {
                str = getString(R.string.cellBalance);
            }
            if (i == 250 && i2 == 0) {
                str = getString(R.string.chargeDisable);
            }
            if (i == 250 && i2 == 1) {
                str = getString(R.string.chargeEnable);
            }
            if (i == 249 && i2 == 0) {
                str = getString(R.string.powerDisable);
            }
            if (i == 249 && i2 == 1) {
                str = getString(R.string.powerEnable);
            }
            if (i == 248 && i2 == 0) {
                str = getString(R.string.zeroCurrent);
            }
            if (!str.isEmpty()) {
                Toast.makeText(getBaseContext(), getString(R.string.confirm) + str, 0).show();
            }
        }
    }

    private boolean setupUISwitches() {
        ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{-16842910}, new int[]{16842912}, new int[0]}, new int[]{-7829368, -16711936, SupportMenu.CATEGORY_MASK});
        CheckBox checkBox = (CheckBox) findViewById(R.id.chkClearBTDev);
        this.chkClearBTDev = checkBox;
        checkBox.setOnClickListener(this);
        Switch r1 = (Switch) findViewById(R.id.switchOvervoltAlarm);
        this.switchOvervoltAlarm = r1;
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                BMSControlsActivity.this.AlarmswitchCheckedChanged();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            this.switchOvervoltAlarm.setThumbTintList(colorStateList);
        }
        return true;
    }

    public void AlarmswitchCheckedChanged() {
        Boolean bool = false;
        if (this.switchOvervoltAlarm.isChecked()) {
            bool = true;
        }
        this.mbtCommCtrl.SetOvervoltAlarmEnabled(bool.booleanValue());
    }
}
