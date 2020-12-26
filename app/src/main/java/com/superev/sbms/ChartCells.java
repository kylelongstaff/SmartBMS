package com.superev.sbms;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.List;

public class ChartCells extends AppCompatActivity implements View.OnClickListener {
    private static final int[] COLOR_TABLE = {-19712, -8372619, -38912, -5849641, -4128736, -3235230, -8294298, -16745164, -625010, -16755830, -34212, -11323526, -29184, -5035951, -735232, -8447987, -7099904, -10931435, -968173, -14472170, 822063872, 813710965, 822044672, 816233943, 817954848, 818848354, 813789286, 805338420, 821458574, 805327754, 822049372, 810760058};
    private static final int checkBoxCellBaseID = 10000;
    int cellCountForCurrentRouteData;
    LineChart lineChart;
    List<ILineDataSet> lineDataSets;
    LinearLayout llCells;
    String routeDataFilename;
    RouteFileParser routeFileParser;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_chart_cells);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.chartTitle);
        this.routeDataFilename = getIntent().getStringExtra(GPSActivity.EXTRA_ROUTEFILDE);
        this.routeFileParser = new RouteFileParser(this);
        this.llCells = (LinearLayout) findViewById(R.id.hsvllCells);
        LineChart lineChart2 = (LineChart) findViewById(R.id.lineChart);
        this.lineChart = lineChart2;
        lineChart2.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.routeFileParser.SetRouteDataFilename(this.routeDataFilename);
        this.cellCountForCurrentRouteData = this.routeFileParser.GetCellCountFromCurrentRouteDataFile();
        for (int i = 1; i <= this.cellCountForCurrentRouteData; i++) {
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setText(String.format("Cell %d", Integer.valueOf(i)));
            checkBox.setTextColor(-1);
            checkBox.setTextSize(10.0f);
            checkBox.setWidth(160);
            checkBox.setId(i + 10000);
            checkBox.setTextAlignment(4);
            checkBox.setOnClickListener(this);
            this.llCells.addView(checkBox);
        }
        this.lineDataSets = new ArrayList();
        SetupChartInformation();
        GenerateChartData();
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id >= 10000 && id <= this.cellCountForCurrentRouteData + 10000) {
            GenerateChartData();
        }
    }

    private void GenerateChartData() {
        LineDataSet GetCellDataSet;
        if (!this.lineChart.isEmpty()) {
            this.lineChart.clearValues();
        }
        LineDataSet GetBatteryVoltageDataSet = GetBatteryVoltageDataSet();
        if (GetBatteryVoltageDataSet != null) {
            this.lineDataSets.add(GetBatteryVoltageDataSet);
        }
        LineDataSet GetBatteryCurrentDataSet = GetBatteryCurrentDataSet();
        if (GetBatteryCurrentDataSet != null) {
            this.lineDataSets.add(GetBatteryCurrentDataSet);
        }
        for (int i = 1; i <= this.cellCountForCurrentRouteData; i++) {
            CheckBox checkBox = (CheckBox) findViewById(i + 10000);
            if (!(checkBox == null || !checkBox.isChecked() || (GetCellDataSet = GetCellDataSet(i)) == null)) {
                this.lineDataSets.add(GetCellDataSet);
            }
        }
        this.lineChart.setData(new LineData(this.lineDataSets));
        this.lineChart.invalidate();
    }

    private void SetupChartInformation() {
        YAxis axisLeft = this.lineChart.getAxisLeft();
        axisLeft.setTextColor(-1);
        axisLeft.setDrawTopYLabelEntry(true);
        this.lineChart.getXAxis().setTextColor(-1);
        this.lineChart.getLegend().setTextColor(-1);
        Description description = this.lineChart.getDescription();
        description.setText(getBaseContext().getString(R.string.chartBattData));
        description.setTextColor(-1);
    }

    private LineDataSet GetBatteryVoltageDataSet() {
        ArrayList<Entry> ParseBatteryVoltage = this.routeFileParser.ParseBatteryVoltage();
        int i = COLOR_TABLE[this.lineDataSets.size()];
        LineDataSet lineDataSet = new LineDataSet(ParseBatteryVoltage, getBaseContext().getString(R.string.chartBattVolt));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(i);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setValueTextColor(i);
        return lineDataSet;
    }

    private LineDataSet GetBatteryCurrentDataSet() {
        ArrayList<Entry> ParseBatteryCurrent = this.routeFileParser.ParseBatteryCurrent();
        int i = COLOR_TABLE[this.lineDataSets.size()];
        LineDataSet lineDataSet = new LineDataSet(ParseBatteryCurrent, getBaseContext().getString(R.string.chartBattCurr));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(i);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setValueTextColor(i);
        return lineDataSet;
    }

    private LineDataSet GetCellDataSet(int i) {
        ArrayList<Entry> ParseCellData = this.routeFileParser.ParseCellData(i);
        int i2 = COLOR_TABLE[this.lineDataSets.size()];
        LineDataSet lineDataSet = new LineDataSet(ParseCellData, String.format("Cell %d", Integer.valueOf(i)));
        lineDataSet.setDrawCircles(false);
        lineDataSet.setColor(i2);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setValueTextColor(i2);
        return lineDataSet;
    }
}
