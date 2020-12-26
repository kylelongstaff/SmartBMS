package com.superev.sbms;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import com.github.mikephil.charting.BuildConfig;
import java.util.List;

@SuppressWarnings("ALL")
public class AppSettingsActivity extends AppCompatPreferenceActivity {
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {


        public boolean onPreferenceChange(Preference preference, Object obj) {
            String obj2 = obj.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int findIndexOfValue = listPreference.findIndexOfValue(obj2);
                preference.setSummary(findIndexOfValue >= 0 ? listPreference.getEntries()[findIndexOfValue] : null);
                return true;
            }
            preference.setSummary(obj2);
            return true;
        }
    };

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 4;
    }

    public static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), BuildConfig.FLAVOR));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override // android.preference.PreferenceActivity
    public void onBuildHeaders(List<Header> list) {
        loadHeadersFromResource(R.xml.pref_headers, list);
    }

    protected boolean isValidFragment(String str) {
        return PreferenceFragment.class.getName().equals(str) || GeneralPreferenceFragment.class.getName().equals(str);
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.pref_gps_settings);
            setHasOptionsMenu(true);
            AppSettingsActivity.bindPreferenceSummaryToValue(findPreference("speedUnits_list"));
            AppSettingsActivity.bindPreferenceSummaryToValue(findPreference("edit_text_amps_start_gps"));
            AppSettingsActivity.bindPreferenceSummaryToValue(findPreference("edit_text_gps_start_ampseconds"));
            AppSettingsActivity.bindPreferenceSummaryToValue(findPreference("edit_text_gps_end_ampseconds"));
            AppSettingsActivity.bindPreferenceSummaryToValue(findPreference("edit_text_gps_interval"));
            InputFilterText inputFilterText = new InputFilterText("1", "99");
            ((EditTextPreference) findPreference("edit_text_amps_start_gps")).getEditText().setFilters(new InputFilter[]{inputFilterText});
            ((EditTextPreference) findPreference("edit_text_gps_start_ampseconds")).getEditText().setFilters(new InputFilter[]{inputFilterText});
            ((EditTextPreference) findPreference("edit_text_gps_interval")).getEditText().setFilters(new InputFilter[]{inputFilterText});
        }

        public boolean onOptionsItemSelected(MenuItem menuItem) {
            return super.onOptionsItemSelected(menuItem);
        }
    }
}
