package com.superev.sbms;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    private TextView txtAboutContact;
    private TextView txtAboutEmail;
    private TextView txtAboutPatreon;
    private TextView txtAboutYoutube;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_about);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.txtAboutEmail = (TextView) findViewById(R.id.txtAboutWebsite);
        this.txtAboutYoutube = (TextView) findViewById(R.id.txtAboutYoutube);
        this.txtAboutContact = (TextView) findViewById(R.id.txtAboutContact);
        this.txtAboutPatreon = (TextView) findViewById(R.id.txtAboutPatreon);
        this.txtAboutEmail.setText(Html.fromHtml("SBMS support: <font color='yellow'>https://github.com/kylelongstaff/sbms/</font>"), TextView.BufferType.SPANNABLE);
        this.txtAboutYoutube.setText(Html.fromHtml("YouTube: <font color='yellow'>https://youtube.com/super-ev/</font>"), TextView.BufferType.SPANNABLE);
        this.txtAboutPatreon.setText(Html.fromHtml("Instagram: <font color='yellow'>https://instagram.com/superfast_to/</font>"), TextView.BufferType.SPANNABLE);
        this.txtAboutContact.setText(Html.fromHtml("Email: <font color='yellow'>support@super-ev.com</font>"), TextView.BufferType.SPANNABLE);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
