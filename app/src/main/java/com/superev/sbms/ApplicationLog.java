package com.superev.sbms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ApplicationLog extends AppCompatActivity implements View.OnClickListener {
    Button btnClearLog;
    GridView gridView;
    private BTCommCtrl mbtCommCtrl = null;
    CustomListRowAdapter myAdapter = null;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_application_log);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.mbtCommCtrl = BTCommCtrl.getInstance(null);
        this.gridView = (GridView) findViewById(R.id.lvApplicationLog);
        CustomListRowAdapter customListRowAdapter = new CustomListRowAdapter(this, this.mbtCommCtrl.GetApplicationLog());
        this.myAdapter = customListRowAdapter;
        this.gridView.setAdapter((ListAdapter) customListRowAdapter);
        Button button = (Button) findViewById(R.id.btnClearAppLog);
        this.btnClearLog = button;
        button.setOnClickListener(this);
        EventBus.getDefault().register(this);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMessage(Message message) {
        if (message.what == 5000) {
            this.myAdapter.notifyDataSetChanged();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btnClearAppLog) {
            this.mbtCommCtrl.ClearApplicationLog();
        }
    }

    private class CustomListRowAdapter extends ArrayAdapter {
        @SuppressLint("SimpleDateFormat")
        DateFormat DateFormatter = new SimpleDateFormat(RouteFileParser.dateFormat);
        @SuppressLint("SimpleDateFormat")
        DateFormat TimeFormatter = new SimpleDateFormat(RouteFileParser.timeFormat);
        private ArrayList<BTCommCtrl.ApplicationLogEntry> applicationLogEntries;
        private Context mContext;
        private ArrayList<String> mFilenames;
        private LayoutInflater mInflate;

        public long getItemId(int i) {
            return 0;
        }

        public CustomListRowAdapter(Context context, ArrayList<BTCommCtrl.ApplicationLogEntry> arrayList) {
            super(context, R.layout.listviewrow_applicationlog);
            this.mContext = context;
            this.mInflate = ApplicationLog.this.getLayoutInflater();
            this.applicationLogEntries = arrayList;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            View view2;
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view2 = this.mInflate.inflate(R.layout.listviewrow_applicationlog, (ViewGroup) null);
                viewHolder.txtDate = (TextView) view2.findViewById(R.id.txtviewLogDate);
                viewHolder.txtTime = (TextView) view2.findViewById(R.id.txtviewLogTime);
                viewHolder.txtLogEntry = (TextView) view2.findViewById(R.id.txtviewLogEntry);
                view2.setTag(viewHolder);
            } else {
                view2 = view;
                viewHolder = (ViewHolder) view.getTag();
            }
            BTCommCtrl.ApplicationLogEntry applicationLogEntry = this.applicationLogEntries.get(i);
            viewHolder.txtLogEntry.setText(applicationLogEntry.logEntry);
            viewHolder.txtDate.setText(this.DateFormatter.format(applicationLogEntry.dateLog));
            viewHolder.txtTime.setText(this.TimeFormatter.format(applicationLogEntry.dateLog));
            return view2;
        }

        public int getCount() {
            return this.applicationLogEntries.size();
        }

        @Override // android.widget.ArrayAdapter
        public Object getItem(int i) {
            return this.applicationLogEntries.get(i);
        }

        class ViewHolder {
            TextView txtDate;
            TextView txtLogEntry;
            TextView txtTime;

            ViewHolder() {
            }
        }
    }
}
