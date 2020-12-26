package com.superev.sbms;

import android.content.Context;
import android.widget.Toast;
import com.github.mikephil.charting.data.Entry;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class RouteFileParser {
    public static final String dateFileFormat = "dMMyy";
    public static final String dateFileTimeFormat = "HH-mm-ss dMMyy";
    public static final String dateFormat = "d/MM/yy";
    public static final String timeFileFormat = "HH-mm-ss";
    public static final String timeFormat = "HH:mm:ss";
    private int cellCountForCurrentRouteDataFile = 0;
    private final int chp_Altitude = 3;
    private final int chp_Cell01 = 12;
    private final int chp_Current = 5;
    private final int chp_Date = 10;
    private final int chp_FETtemp = 8;
    private final int chp_Latitude = 1;
    private final int chp_Longtitude = 2;
    private final int chp_Power = 6;
    private final int chp_RemainingCapacity = 7;
    private final int chp_Speed = 9;
    private final int chp_Time = 11;
    private final int chp_Voltage = 4;
    private final int chp_timeStamp = 0;
    private Context context;
    private String routeDataFilename;
    private boolean validRouteDataFile = false;

    public RouteFileParser(Context context2) {
        this.context = context2;
    }

    public boolean SetRouteDataFilename(String str) {
        this.routeDataFilename = str;
        this.validRouteDataFile = false;
        try {
            FileInputStream fileInputStream = new FileInputStream(this.routeDataFilename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                readLine = bufferedReader.readLine();
            }
            String[] split = readLine.split("\\|");
            if (split.length == 12 || split.length == 45) {
                this.validRouteDataFile = true;
            }
            fileInputStream.close();
        } catch (IOException unused) {
            this.validRouteDataFile = false;
        }
        return this.validRouteDataFile;
    }

    public int GetCellCountFromCurrentRouteDataFile() {
        this.cellCountForCurrentRouteDataFile = 0;
        if (!this.validRouteDataFile) {
            return 0;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.routeDataFilename)));
            bufferedReader.readLine();
            String[] split = bufferedReader.readLine().split("\\|");
            if (split.length != 45) {
                return this.cellCountForCurrentRouteDataFile;
            }
            int i = 12;
            while (true) {
                if (i >= split.length) {
                    break;
                }
                String str = split[i];
                if (split[i].startsWith(" 0.0")) {
                    break;
                }
                i++;
            }
            int i2 = i - 12;
            this.cellCountForCurrentRouteDataFile = i2;
            return i2;
        } catch (IOException unused) {
            return 0;
        }
    }

    private ArrayList<Entry> ParseFloatsFromRouteDataFile(int i) {
        if (!this.validRouteDataFile) {
            return null;
        }
        ArrayList<Entry> arrayList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.routeDataFilename)));
            bufferedReader.readLine();
            for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                String[] split = readLine.split("\\|");
                try {
                    arrayList.add(new Entry(Float.parseFloat(split[0]), Float.parseFloat(split[i])));
                } catch (Exception unused) {
                }
            }
        } catch (Exception e) {
            Toast.makeText(this.context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return arrayList;
    }

    public ArrayList<Entry> ParseBatteryCurrent() {
        return ParseFloatsFromRouteDataFile(5);
    }

    public ArrayList<Entry> ParseBatteryVoltage() {
        return ParseFloatsFromRouteDataFile(4);
    }

    public ArrayList<Entry> ParseCellData(int i) {
        return ParseFloatsFromRouteDataFile((i + 12) - 1);
    }

    public ArrayList<RouteData> ParseGPSData() {
        ArrayList<RouteData> arrayList;
        Exception e;
        try {
            arrayList = new ArrayList<>();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.routeDataFilename)));
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    readLine = bufferedReader.readLine();
                }
                while (readLine != null) {
                    String[] split = readLine.split("\\|");
                    RouteData routeData = new RouteData();
                    try {
                        if (split.length != 12) {
                            if (split.length != 45) {
                                Toast.makeText(this.context, "Invalid route data", Toast.LENGTH_SHORT).show();
                                readLine = bufferedReader.readLine();
                            }
                        }
                        routeData.latitude = Double.parseDouble(split[1]);
                        routeData.longtitude = Double.parseDouble(split[2]);
                        arrayList.add(routeData);
                    } catch (NullPointerException | NumberFormatException e2) {
                        Toast.makeText(this.context, "Exception: " + e2.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                    readLine = bufferedReader.readLine();
                }
            } catch (Exception e3) {
                e = e3;
                Toast.makeText(this.context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                return arrayList;
            }
        } catch (Exception e4) {
            arrayList = null;
            e = e4;
            Toast.makeText(this.context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return arrayList;
        }
        return arrayList;
    }

    public class RouteData {
        public float current;
        public double latitude;
        public double longtitude;

        public RouteData() {
        }
    }

    public class CellData {
        public float timeStamp;
        public float voltage;

        public CellData() {
        }
    }
}
