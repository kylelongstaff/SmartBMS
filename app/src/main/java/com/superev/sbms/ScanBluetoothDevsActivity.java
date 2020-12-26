package com.superev.sbms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Set;

@SuppressWarnings("ALL")
public class ScanBluetoothDevsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String EXTRA_MAC_ADDRESS = "mac_address";
    BluetoothAdapter mBTAdapter;
    ArrayList<BTdeviceInfo> mDeviceNames;
    CustomListRowAdapter mLVAdapter;
    ListView mListViewBluetoothDevs;
    ProgressBar mProgressBar;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /* class com.vortecks.vbms.ScanBluetoothDevsActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                BTdeviceInfo bTdeviceInfo = new BTdeviceInfo();
                Boolean.valueOf(false);
                bTdeviceInfo.deviceName = bluetoothDevice.getName();
                bTdeviceInfo.macAddress = bluetoothDevice.getAddress();
                if (!ScanBluetoothDevsActivity.this.IsMACDuplicated(bTdeviceInfo.macAddress).booleanValue()) {
                    ScanBluetoothDevsActivity.this.mDeviceNames.add(bTdeviceInfo);
                    ScanBluetoothDevsActivity.this.mLVAdapter.notifyDataSetChanged();
                    ScanBluetoothDevsActivity.this.mTextViewClickToConnect.setVisibility(0);
                }
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                ScanBluetoothDevsActivity.this.mTextViewScanStatus.setText(R.string.scan_bluetooth_finished);
                ScanBluetoothDevsActivity.this.mProgressBar.setVisibility(4);
            }
        }
    };
    TextView mTextViewClickToConnect;
    TextView mTextViewScanStatus;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_scan_bluetooth_devs);
        this.mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.mProgressBar = (ProgressBar) findViewById(R.id.progressBarScanBTdevices);
        this.mTextViewClickToConnect = (TextView) findViewById(R.id.txtClickToConnect);
        this.mTextViewScanStatus = (TextView) findViewById(R.id.txtScanStatus);
        this.mDeviceNames = new ArrayList<>();
        this.mLVAdapter = new CustomListRowAdapter(this, this.mDeviceNames);
        ListView listView = (ListView) findViewById(R.id.listBluetoothDevices);
        this.mListViewBluetoothDevs = listView;
        listView.setAdapter((ListAdapter) this.mLVAdapter);
        this.mListViewBluetoothDevs.setOnItemClickListener(this);
        GetPairedDevices();
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.FOUND"));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED"));
        if (this.mBTAdapter.isDiscovering()) {
            this.mBTAdapter.cancelDiscovery();
        }
        setTitle(getResources().getString(R.string.title_activity_scan_bluetooth_dev));
        this.mTextViewClickToConnect.setVisibility(4);
        this.mBTAdapter.startDiscovery();
        setResult(0);
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.mBTAdapter.isDiscovering()) {
            this.mBTAdapter.cancelDiscovery();
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_MAC_ADDRESS, ((BTdeviceInfo) adapterView.getItemAtPosition(i)).macAddress);
        setResult(-1, intent);
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            if (this.mBTAdapter.isDiscovering()) {
                this.mBTAdapter.cancelDiscovery();
            }
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void GetPairedDevices() {
        Set<BluetoothDevice> bondedDevices = this.mBTAdapter.getBondedDevices();
        BTdeviceInfo bTdeviceInfo = new BTdeviceInfo();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                bTdeviceInfo.deviceName = bluetoothDevice.getName();
                bTdeviceInfo.macAddress = bluetoothDevice.getAddress();
                if (!IsMACDuplicated(bTdeviceInfo.macAddress).booleanValue()) {
                    this.mDeviceNames.add(bTdeviceInfo);
                    this.mLVAdapter.notifyDataSetChanged();
                    this.mTextViewClickToConnect.setVisibility(0);
                }
            }
        }
    }

    public Boolean IsMACDuplicated(String str) {
        for (int i = 0; i < this.mDeviceNames.size(); i++) {
            if (this.mDeviceNames.get(i).macAddress.equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onDestroy() {
        super.onDestroy();
        if (this.mBTAdapter.isDiscovering()) {
            this.mBTAdapter.cancelDiscovery();
        }
        unregisterReceiver(this.mReceiver);
    }

    private class BTdeviceInfo {
        String deviceName;
        String macAddress;

        private BTdeviceInfo() {
        }
    }

    private class CustomListRowAdapter extends ArrayAdapter {
        private Context mContext;
        private ArrayList<BTdeviceInfo> mDeviceNames;
        private LayoutInflater mInflate;

        public long getItemId(int i) {
            return 0;
        }

        public CustomListRowAdapter(Context context, ArrayList<BTdeviceInfo> arrayList) {
            super(context, (int) R.layout.listviewrow_whiteonblack, arrayList);
            this.mContext = context;
            this.mDeviceNames = arrayList;
            this.mInflate = ScanBluetoothDevsActivity.this.getLayoutInflater();
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = this.mInflate.inflate(R.layout.listviewrow_whiteonblack, (ViewGroup) null, false);
                viewHolder = new ViewHolder();
                viewHolder.deviceName = (TextView) view.findViewById(R.id.txtBTDeviceName);
                viewHolder.macAddress = (TextView) view.findViewById(R.id.txtBTMacAddr);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.deviceName.setText(this.mDeviceNames.get(i).deviceName);
            viewHolder.macAddress.setText(this.mDeviceNames.get(i).macAddress);
            return view;
        }

        public int getCount() {
            return this.mDeviceNames.size();
        }

        @Override // android.widget.ArrayAdapter
        public Object getItem(int i) {
            return this.mDeviceNames.get(i);
        }

        class ViewHolder {
            TextView deviceName;
            TextView macAddress;

            ViewHolder() {
            }
        }
    }
}
