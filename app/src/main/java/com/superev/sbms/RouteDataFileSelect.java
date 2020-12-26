package com.superev.sbms;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

public class RouteDataFileSelect extends AppCompatActivity {
    CustomListRowAdapter arrayAdapter;
    Button btnDeleteFile;
    ListView lvRouteFiless;
    ArrayList<String> routeFiles;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_route_data_file_select);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.routeFiles = new ArrayList<>();
        setTitle(R.string.routeSelectTitle);
        Button button = (Button) findViewById(R.id.btnDeleteSelRouteFile);
        this.btnDeleteFile = button;
        button.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                RouteDataFileSelect.this.DeleteSelectedRouteFiles();
            }
        });
        this.lvRouteFiless = (ListView) findViewById(R.id.lstRouteFiles);
        CustomListRowAdapter customListRowAdapter = new CustomListRowAdapter(this, this.routeFiles);
        this.arrayAdapter = customListRowAdapter;
        this.lvRouteFiless.setAdapter((ListAdapter) customListRowAdapter);
        getRouteDataFiles();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void DeleteSelectedRouteFiles() {
        new AlertDialog.Builder(this).setTitle(R.string.delSelFiles).setMessage(R.string.delConfirm).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {


            public void onClick(DialogInterface dialogInterface, int i) {
                RouteDataFileSelect.this.arrayAdapter.DeleteSelectedFiles();
                RouteDataFileSelect.this.getRouteDataFiles();
            }
        }).show();
    }

    public void getRouteDataFiles() {
        new FileUtils().GetFilesInFolder(this.routeFiles, getExternalFilesDir(null).getPath(), GPSTracker.routeDataFileExt);
        if (this.routeFiles.size() == 0) {
            this.arrayAdapter.notifyDataSetChanged();
            return;
        }
        Collections.sort(this.routeFiles, new Comparator<String>() {
            /* class com.vortecks.vbms.RouteDataFileSelect.AnonymousClass3 */

            public int compare(String str, String str2) {
                try {
                    return new SimpleDateFormat(RouteFileParser.dateFileTimeFormat, Locale.ENGLISH).parse(str2.substring(0, str2.length() - 4)).compareTo(new SimpleDateFormat(RouteFileParser.dateFileTimeFormat, Locale.ENGLISH).parse(str.substring(0, str.length() - 4)));
                } catch (ParseException unused) {
                    return 0;
                }
            }
        });
        this.arrayAdapter.notifyDataSetChanged();
    }

    private class CustomListRowAdapter extends ArrayAdapter {
        private View.OnClickListener OnImageShowChartClickListener = new View.OnClickListener() {
            /* class com.vortecks.vbms.RouteDataFileSelect.CustomListRowAdapter.AnonymousClass3 */

            public void onClick(View view) {
                CustomListRowAdapter.this.ShowChartDataForSelectedFile((String) view.getTag());
            }
        };
        private ArrayList<DeleteRouteFiles> filesForDeletion;
        private Context mContext;
        private ArrayList<String> mFilenames;
        private LayoutInflater mInflate;
        private View.OnClickListener onImageButtonShowMapListener = new View.OnClickListener() {
            /* class com.vortecks.vbms.RouteDataFileSelect.CustomListRowAdapter.AnonymousClass2 */

            public void onClick(View view) {
                CustomListRowAdapter.this.ShowMapActivity((String) view.getTag());
            }
        };

        public long getItemId(int i) {
            return 0;
        }

        public CustomListRowAdapter(Context context, ArrayList<String> arrayList) {
            super(context, (int) R.layout.listviewrow_whiteonblack, arrayList);
            this.mContext = context;
            this.mFilenames = arrayList;
            this.mInflate = RouteDataFileSelect.this.getLayoutInflater();
            this.filesForDeletion = new ArrayList<>();
        }

        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            boolean z = false;
            if (view == null) {
                view = this.mInflate.inflate(R.layout.listviewrow_routefileselect, (ViewGroup) null, false);
                viewHolder = new ViewHolder();
                viewHolder.fileName = (TextView) view.findViewById(R.id.txtRouteFilename);
                viewHolder.chkDeleteFile = (CheckBox) view.findViewById(R.id.chkDeleteRouteFile);
                viewHolder.imgbtnShowChart = (ImageButton) view.findViewById(R.id.imgbtnShowChart);
                viewHolder.imgbtnShowChart.setOnClickListener(this.OnImageShowChartClickListener);
                viewHolder.imgbtnShowMap = (ImageButton) view.findViewById(R.id.imgbtnShowMap);
                viewHolder.imgbtnShowMap.setOnClickListener(this.onImageButtonShowMapListener);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.fileName.setText(this.mFilenames.get(i));
            viewHolder.chkDeleteFile.setTag(this.mFilenames.get(i));
            viewHolder.chkDeleteFile.setOnCheckedChangeListener(null);
            viewHolder.imgbtnShowChart.setTag(this.mFilenames.get(i));
            viewHolder.imgbtnShowMap.setTag(this.mFilenames.get(i));
            Iterator<DeleteRouteFiles> it = this.filesForDeletion.iterator();
            while (true) {
                if (it.hasNext()) {
                    if (i == it.next().chkboxPosition) {
                        z = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            viewHolder.chkDeleteFile.setChecked(z);
            viewHolder.chkDeleteFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                /* class com.vortecks.vbms.RouteDataFileSelect.CustomListRowAdapter.AnonymousClass1 */

                public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                    if (z) {
                        DeleteRouteFiles deleteRouteFiles = new DeleteRouteFiles();
                        deleteRouteFiles.chkboxPosition = i;
                        deleteRouteFiles.filename = (String) compoundButton.getTag();
                        CustomListRowAdapter.this.filesForDeletion.add(deleteRouteFiles);
                        return;
                    }
                    for (int i = 0; i < CustomListRowAdapter.this.filesForDeletion.size(); i++) {
                        if (((DeleteRouteFiles) CustomListRowAdapter.this.filesForDeletion.get(i)).chkboxPosition == i) {
                            CustomListRowAdapter.this.filesForDeletion.remove(i);
                            return;
                        }
                    }
                }
            });
            return view;
        }

        public void ShowChartDataForSelectedFile(String str) {
            Intent intent = new Intent(this.mContext, ChartCells.class);
            intent.putExtra(GPSActivity.EXTRA_ROUTEFILDE, RouteDataFileSelect.this.getExternalFilesDir(null) + "/" + str);
            RouteDataFileSelect.this.startActivity(intent);
        }

        public void ShowMapActivity(String str) {
            Intent intent = new Intent(this.mContext, MapsActivity.class);
            intent.putExtra(GPSActivity.EXTRA_ROUTEFILDE, RouteDataFileSelect.this.getExternalFilesDir(null).getPath() + "/" + str);
            RouteDataFileSelect.this.startActivity(intent);
        }

        public int getCount() {
            return this.mFilenames.size();
        }

        @Override // android.widget.ArrayAdapter
        public Object getItem(int i) {
            return this.mFilenames.get(i);
        }

        public void DeleteSelectedFiles() {
            if (this.filesForDeletion.size() != 0) {
                for (int i = 0; i < this.filesForDeletion.size(); i++) {
                    File file = new File(RouteDataFileSelect.this.getExternalFilesDir(null) + "/" + this.filesForDeletion.get(i).filename);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                this.filesForDeletion.clear();
            }
        }

        class ViewHolder {
            CheckBox chkDeleteFile;
            TextView fileName;
            ImageButton imgbtnShowChart;
            ImageButton imgbtnShowMap;

            ViewHolder() {
            }
        }

        class DeleteRouteFiles {
            int chkboxPosition;
            String filename;

            DeleteRouteFiles() {
            }
        }
    }
}
