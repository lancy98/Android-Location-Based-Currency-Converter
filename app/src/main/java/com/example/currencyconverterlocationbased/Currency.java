package com.example.currencyconverterlocationbased;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Currency  implements Parcelable {
    public String name;
    public String identification;
    public String symbol;

    public Currency(JSONObject currencyJSON) {
        try {
            name = currencyJSON.getString("currencyName");
            identification = currencyJSON.getString("currencyId");
            symbol = currencyJSON.getString("currencySymbol");
        } catch (Exception exception) {

        }
    }

    // Below is the Parcelable code (Serialization code) for moving objects from one activity to another

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.identification);
        dest.writeString(this.symbol);
    }

    protected Currency(Parcel in) {
        this.name = in.readString();
        this.identification = in.readString();
        this.symbol = in.readString();
    }

    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
}
