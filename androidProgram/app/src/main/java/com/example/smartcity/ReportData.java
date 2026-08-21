package com.example.smartcity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Hanjian Jin
 * UID: u7905060
 */
public class ReportData implements Parcelable {
    private Map<String, String> data;

    public ReportData() {
        data = new HashMap<>();
    }

    protected ReportData(Parcel in) {
        int size = in.readInt();
        data = new HashMap<>();
        for (int i = 0; i < size; i++) {
            String key = in.readString();
            String value = in.readString();
            data.put(key, value);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(data.size());
        for (Map.Entry<String, String> entry : data.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReportData> CREATOR = new Creator<ReportData>() {
        @Override
        public ReportData createFromParcel(Parcel in) {
            return new ReportData(in);
        }

        @Override
        public ReportData[] newArray(int size) {
            return new ReportData[size];
        }
    };

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}