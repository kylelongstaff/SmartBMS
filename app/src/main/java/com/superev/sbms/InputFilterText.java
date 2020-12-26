package com.superev.sbms;

import android.text.InputFilter;
import android.text.Spanned;
import com.github.mikephil.charting.BuildConfig;

public class InputFilterText implements InputFilter {
    private int max;
    private int min;

    private boolean isInRange(int i, int i2, int i3) {
        if (i2 > i) {
            if (i3 >= i && i3 <= i2) {
                return true;
            }
        } else if (i3 >= i2 && i3 <= i) {
            return true;
        }
        return false;
    }

    InputFilterText(int i, int i2) {
        this.min = i;
        this.max = i2;
    }

    public InputFilterText(String str, String str2) {
        this.min = Integer.parseInt(str);
        this.max = Integer.parseInt(str2);
    }

    public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        try {
            if (isInRange(this.min, this.max, Integer.parseInt(spanned.toString() + charSequence.toString()))) {
                return null;
            }
            return BuildConfig.FLAVOR;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }
}
