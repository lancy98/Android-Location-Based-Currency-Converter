package com.example.currencyconverterlocationbased;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Country implements Parcelable {
    public String name;
    public String identification;
    public Currency currency;

    public Country(JSONObject countryJSON) {
        try {
            name = countryJSON.getString("name");
            identification = countryJSON.getString("id");
            currency = new Currency(countryJSON);
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
        dest.writeParcelable(this.currency, flags);
    }

    protected Country(Parcel in) {
        this.name = in.readString();
        this.identification = in.readString();
        this.currency = in.readParcelable(Currency.class.getClassLoader());
    }

    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };
}

