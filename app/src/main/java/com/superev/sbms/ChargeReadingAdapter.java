package com.superev.sbms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.core.view.InputDeviceCompat;

public class ChargeReadingAdapter extends BaseAdapter {
    private int batteryCellsCount = 0;
    private Context c;
    private String[][] cellData;
    private int[] control_data;
    private String[] strMv;
    private String[] strTitle;
    private String[] strValue;

    public long getItemId(int i) {
        return (long) i;
    }

    public ChargeReadingAdapter(Context context, String[] strArr, String[] strArr2, String[] strArr3, int[] iArr, String[][] strArr4) {
        this.c = context;
        this.strTitle = strArr;
        this.strValue = strArr2;
        this.strMv = strArr3;
        this.control_data = iArr;
        this.cellData = strArr4;
    }

    public void setBatteryCellsCount(int i) {
        this.batteryCellsCount = i;
    }

    public int getCount() {
        int i = this.batteryCellsCount;
        return (i / 2) + 14 + (i % 2);
    }

    public Object getItem(int i) {
        return this.strValue[i];
    }

    class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        TextView tvCellRatingA;
        TextView tvCellRatingB;
        TextView tvCellTitleA;
        TextView tvCellTitleB;
        TextView tvCellValueA;
        TextView tvCellValueB;

        ViewHolder() {
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(this.c, R.layout.listviewrow_chargereading, null);
            viewHolder = new ViewHolder();
            viewHolder.tv1 = (TextView) view.findViewById(R.id.txtlvcr_cr_Title);
            viewHolder.tv2 = (TextView) view.findViewById(R.id.txtlvcr_cr_Info);
            viewHolder.tv3 = (TextView) view.findViewById(R.id.txtlvcr_cr_Infomv);
            viewHolder.tvCellTitleA = (TextView) view.findViewById(R.id.tvCellTitleA);
            viewHolder.tvCellValueA = (TextView) view.findViewById(R.id.tvCellValueA);
            viewHolder.tvCellRatingA = (TextView) view.findViewById(R.id.tvCellRatingA);
            viewHolder.tvCellTitleB = (TextView) view.findViewById(R.id.tvCellTitleB);
            viewHolder.tvCellValueB = (TextView) view.findViewById(R.id.tvCellValueB);
            viewHolder.tvCellRatingB = (TextView) view.findViewById(R.id.tvCellRatingB);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (i == 0 || i == 1) {
            viewHolder.tv1.setTextSize(12.0f);
            viewHolder.tv2.setTextSize(12.0f);
            viewHolder.tv3.setTextSize(12.0f);
        } else {
            viewHolder.tv1.setTextSize(12.0f);
            viewHolder.tv2.setTextSize(12.0f);
            viewHolder.tv3.setTextSize(12.0f);
        }
        viewHolder.tv1.setTextColor(-1);
        if (i > 13) {
            int i2 = i - 14;
            int i3 = (i2 * 2) + 1;
            viewHolder.tv1.setVisibility(4);
            viewHolder.tv2.setVisibility(4);
            viewHolder.tv3.setVisibility(4);
            viewHolder.tvCellTitleA.setVisibility(0);
            viewHolder.tvCellValueA.setVisibility(0);
            viewHolder.tvCellRatingA.setVisibility(0);
            viewHolder.tvCellTitleB.setVisibility(0);
            viewHolder.tvCellValueB.setVisibility(0);
            viewHolder.tvCellRatingB.setVisibility(0);
            viewHolder.tvCellTitleA.setText("Cell " + Integer.toString(i3));
            viewHolder.tvCellValueA.setText(this.cellData[i2][0]);
            TextView textView = viewHolder.tvCellTitleB;
            StringBuilder sb = new StringBuilder();
            sb.append("Cell ");
            int i4 = i3 + 1;
            sb.append(Integer.toString(i4));
            textView.setText(sb.toString());
            viewHolder.tvCellValueB.setText(this.cellData[i2][1]);
            if (((this.control_data[0] >> (i3 - 1)) & 1) != 0) {
                viewHolder.tvCellTitleA.setTextColor(-16711936);
            } else {
                viewHolder.tvCellTitleA.setTextColor(-1);
            }
            if (((this.control_data[0] >> i3) & 1) != 0) {
                viewHolder.tvCellTitleB.setTextColor(-16711936);
            } else {
                viewHolder.tvCellTitleB.setTextColor(-1);
            }
            int[] iArr = this.control_data;
            if (i3 != iArr[2] && i3 != iArr[3]) {
                viewHolder.tvCellValueA.setTextColor(-1);
            } else if (i3 == this.control_data[2]) {
                viewHolder.tvCellValueA.setTextColor(-16711936);
            } else {
                viewHolder.tvCellValueA.setTextColor(InputDeviceCompat.SOURCE_ANY);
            }
            int[] iArr2 = this.control_data;
            if (i4 != iArr2[2] && i4 != iArr2[3]) {
                viewHolder.tvCellValueB.setTextColor(-1);
            } else if (i4 == this.control_data[2]) {
                viewHolder.tvCellValueB.setTextColor(-16711936);
            } else {
                viewHolder.tvCellValueB.setTextColor(InputDeviceCompat.SOURCE_ANY);
            }
            if ((i2 + 1) * 2 > this.batteryCellsCount) {
                viewHolder.tvCellTitleB.setVisibility(4);
                viewHolder.tvCellValueB.setVisibility(4);
                viewHolder.tvCellRatingB.setVisibility(4);
            }
        } else {
            viewHolder.tv1.setVisibility(0);
            viewHolder.tv2.setVisibility(0);
            viewHolder.tv3.setVisibility(0);
            viewHolder.tvCellTitleA.setVisibility(4);
            viewHolder.tvCellValueA.setVisibility(4);
            viewHolder.tvCellRatingA.setVisibility(4);
            viewHolder.tvCellTitleB.setVisibility(4);
            viewHolder.tvCellValueB.setVisibility(4);
            viewHolder.tvCellRatingB.setVisibility(4);
            viewHolder.tv1.setText(this.strTitle[i]);
            viewHolder.tv2.setText(this.strValue[i]);
            viewHolder.tv3.setText(this.strMv[i]);
        }
        return view;
    }
}
