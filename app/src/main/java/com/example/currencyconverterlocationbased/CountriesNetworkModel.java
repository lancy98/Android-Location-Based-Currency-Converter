package com.example.currencyconverterlocationbased;

import android.util.Log;
import com.android.volley.VolleyError;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CountriesNetworkModel implements NetworkRequest.NetworkResponse {
    private NetworkRequest request;
    public List<Country> countries;
    private static final CountriesNetworkModel SINGLE_INSTANCE = new CountriesNetworkModel();

    public CountriesNetworkModel.CountriesNetworkModelResponse callbackObject;

    private CountriesNetworkModel() {
        String url = "https://free.currconv.com/api/v7/countries?apiKey=YOUR_API_KEY";
        request = new NetworkRequest(MainActivity.getAppContext(), url, this);
    }

    public static CountriesNetworkModel getInstance() {
        return SINGLE_INSTANCE;
    }

    public Country countryWithName(String countryName) {
        for (int i = 0; i < countries.size(); i++) {
            Country country = countries.get(i);
            if (country.name.equals(countryName)) {
                return country;
            }
        }

        return null;
    }

    public void networkResponse(JSONObject jsonObject , VolleyError error) {
        if (jsonObject != null) {
            parseJSONObject(jsonObject);
        } else {
            if (callbackObject != null) {
                callbackObject.countriesDataFetched(false);
            }
        }

        request = null;
    }

    private void parseJSONObject(JSONObject jsonObject) {
        boolean success;
        try {
            JSONObject results = (JSONObject) jsonObject.get("results");
            Iterator<String> countryKeys = results.keys();
            ArrayList<Country> countries = new ArrayList<Country>();

            while (countryKeys.hasNext()) {
                String countryKey = countryKeys.next();
                JSONObject countryJson = results.getJSONObject(countryKey);
                Country country = new Country(countryJson);
                countries.add(country);
            }

            // sort the countries in ascending order
            Collections.sort(countries, new Comparator<Country>() {
                @Override
                public int compare(Country lhs, Country rhs) {
                    return lhs.name.compareTo(rhs.name);
                }
            });

            this.countries = countries;
            success = true;
        } catch (Exception exception) {
            Log.d(MainActivity.TAG, "parser error" + exception.getLocalizedMessage());
            success = false;
        }

        if (callbackObject != null) {
            callbackObject.countriesDataFetched(success);
        }
    }

    public interface CountriesNetworkModelResponse {
        public void countriesDataFetched(boolean success);
    }
}
