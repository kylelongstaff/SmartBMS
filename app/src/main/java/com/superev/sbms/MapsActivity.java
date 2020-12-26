package com.superev.sbms;

import android.os.Bundle;
import androidx.core.internal.view.SupportMenu;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String routeDataFilename;

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_maps);
        this.routeDataFilename = getIntent().getStringExtra(GPSActivity.EXTRA_ROUTEFILDE);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    @Override // com.google.android.gms.maps.OnMapReadyCallback
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        LatLng plotRouteData = plotRouteData();
        if (plotRouteData != null) {
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(plotRouteData, 15.0f));
        }
    }

    private LatLng plotRouteData() {
        ArrayList<RouteFileParser.RouteData> ParseGPSData;
        Boolean bool = false;
        RouteFileParser routeFileParser = new RouteFileParser(this);
        LatLng latLng = null;
        if (!routeFileParser.SetRouteDataFilename(this.routeDataFilename) || (ParseGPSData = routeFileParser.ParseGPSData()) == null) {
            return null;
        }
        LatLng latLng2 = null;
        for (int i = 0; i < ParseGPSData.size(); i++) {
            RouteFileParser.RouteData routeData = ParseGPSData.get(i);
            if (!bool) {
                LatLng latLng3 = new LatLng(routeData.latitude, routeData.longtitude);
                this.mMap.addMarker(new MarkerOptions().position(latLng3).title(getResources().getString(R.string.route_startpos)));
                latLng = latLng3;
                bool = true;
                latLng2 = latLng;
            } else {
                LatLng latLng4 = new LatLng(routeData.latitude, routeData.longtitude);
                this.mMap.addPolyline(new PolylineOptions().add(latLng2, latLng4).width(5.0f).color(SupportMenu.CATEGORY_MASK));
                if (i == ParseGPSData.size() - 1) {
                    this.mMap.addMarker(new MarkerOptions().position(latLng4).title(getResources().getString(R.string.route_endpos)));
                } else if (i % 60 == 0) {
                    MarkerOptions title = new MarkerOptions().position(latLng4).title("current: " + Float.toString(routeData.current));
                    title.icon(BitmapDescriptorFactory.fromResource(R.drawable.dot));
                    this.mMap.addMarker(title);
                }
                latLng2 = latLng4;
            }
        }
        return latLng;
    }
}
