package com.superev.sbms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity implements View.OnClickListener {
    private BTCommCtrl mbtCommCtrl;
    private Button mbtnResetSummary;
    private TextView mtxtHighAmps;
    private TextView mtxtHighFET;
    private TextView mtxtHighPower;
    private TextView mtxtHighSpeed;
    private TextView mtxtLowestVoltage;
    private TextView mtxtSummarySubTitle;
    private TextView mtxtTravelled;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_summary);
        this.mbtCommCtrl = BTCommCtrl.getInstance(null);
        this.mtxtHighAmps = (TextView) findViewById(R.id.txtHighAmps);
        this.mtxtHighPower = (TextView) findViewById(R.id.txtHighPower);
        this.mtxtHighFET = (TextView) findViewById(R.id.txtHighFET);
        this.mtxtHighSpeed = (TextView) findViewById(R.id.txtHighSpeed);
        this.mtxtTravelled = (TextView) findViewById(R.id.txtTravelled);
        this.mtxtSummarySubTitle = (TextView) findViewById(R.id.txtSummarySubTitle);
        this.mtxtLowestVoltage = (TextView) findViewById(R.id.txtLowestVolt);
        Button button = (Button) findViewById(R.id.btnResetSummary);
        this.mbtnResetSummary = button;
        button.setOnClickListener(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.summaryTitle);
        DisplaySummaryValues();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnResetSummary) {
            new AlertDialog.Builder(this).setTitle(R.string.summaryResetTitle).setMessage(R.string.summaryResetDesc).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                /* class com.vortecks.vbms.SummaryActivity.AnonymousClass1 */

                public void onClick(DialogInterface dialogInterface, int i) {
                    SummaryActivity.this.mbtCommCtrl.ResetSummaryValues();
                    SummaryActivity.this.DisplaySummaryValues();
                }
            }).show();
        }
    }

    public void DisplaySummaryValues() {
        double d;
        String str;
        float f;
        BTCommCtrl.SummaryValues GetSummaryValues = this.mbtCommCtrl.GetSummaryValues();
        this.mtxtSummarySubTitle.setText(String.format("%s (%s)", getString(R.string.summaryDesc), GetSummaryValues.resetDate));
        this.mtxtHighAmps.setText(String.format("%.2f amps", Float.valueOf(GetSummaryValues.highestAmps)));
        this.mtxtHighPower.setText(String.format("%d watts", Short.valueOf(GetSummaryValues.highestPower)));
        this.mtxtLowestVoltage.setText(String.format("%.2f volts", Float.valueOf(GetSummaryValues.lowestVoltage)));
        this.mtxtHighFET.setText(String.format("%d °C", Short.valueOf(GetSummaryValues.highestFETtemp)));
        String GetGpsSpeedUnit = this.mbtCommCtrl.GetGpsSpeedUnit();
        if (GetGpsSpeedUnit.equals("kph")) {
            f = 3.6f;
            d = 0.001d;
            str = "km";
        } else if (GetGpsSpeedUnit.equals("mph")) {
            f = 2.23694f;
            d = 6.21371E-4d;
            str = "miles";
        } else {
            f = 1.0f;
            d = 1.0d;
            str = "metres";
        }
        this.mtxtHighSpeed.setText(String.format("%.2f %s", Float.valueOf(GetSummaryValues.highestSpeed_mps * f), GetGpsSpeedUnit));
        this.mtxtTravelled.setText(String.format("%.2f %s", Double.valueOf(((double) GetSummaryValues.totalTrackedDistanceTravelled) * d), str));
    }
}
