package com.superev.sbms;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TroubleshootActivity extends AppCompatActivity {
    private TextView txtAboutEmail;
    private TextView txtAboutYoutube;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_troubleshoot);
        setContentView(R.layout.activity_troubleshoot);
        ((TextView) findViewById(R.id.textView23)).setMovementMethod(new ScrollingMovementMethod());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
