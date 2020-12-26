package com.superev.sbms;

import java.io.File;
import java.util.ArrayList;

public class FileUtils {
    private Boolean errorIsSet = false;
    private String lastErrorDescription;

    public Boolean getErrorIsSet() {
        if (!this.errorIsSet.booleanValue()) {
            return false;
        }
        this.errorIsSet = false;
        return true;
    }

    public String getLastErrorDescription() {
        return this.lastErrorDescription;
    }

    public void GetFilesInFolder(ArrayList<String> arrayList, String str, String str2) {
        arrayList.clear();
        try {
            File[] listFiles = new File(str).listFiles();
            for (File file : listFiles) {
                if (file.isFile()) {
                    if (str2.isEmpty()) {
                        arrayList.add(new String(file.getName()));
                    } else if (file.getName().endsWith(str2)) {
                        arrayList.add(new String(file.getName()));
                    }
                }
            }
        } catch (Exception e) {
            this.errorIsSet = true;
            this.lastErrorDescription = e.getLocalizedMessage();
        }
    }

    public ArrayList<String> GetFilesInFolder(String str, String str2) {
        ArrayList<String> arrayList = new ArrayList<>();
        GetFilesInFolder(arrayList, str, str2);
        return arrayList;
    }
}
