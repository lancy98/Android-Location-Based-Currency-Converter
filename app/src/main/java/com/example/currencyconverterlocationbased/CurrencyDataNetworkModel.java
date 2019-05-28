package com.example.currencyconverterlocationbased;

import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;

import org.json.JSONObject;

public class CurrencyDataNetworkModel implements NetworkRequest.NetworkResponse {

    private NetworkRequest request;
    private Country fromCountry;
    private Country toCountry;
    public double conversionRate;
    private CurrencyDataNetworkModel.CurrencyDataNetworkModelResponse callback;

    public CurrencyDataNetworkModel(Country fromCountry,
                                    Country toCountry,
                                    CurrencyDataNetworkModel.CurrencyDataNetworkModelResponse callback) {

        this.fromCountry = fromCountry;
        this.toCountry = toCountry;
        this.callback = callback;

        String url = "https://free.currconv.com/api/v7/convert?apiKey=951c422a62c8611c2d58&q="
                + currencyConvertCode()
                +"&compact=ultra";
        request = new NetworkRequest(MainActivity.getAppContext(), url, this);
    }

    private String currencyConvertCode() {
        return fromCountry.currency.identification + "_" + toCountry.currency.identification;
    }

    public void networkResponse(JSONObject jsonObject , VolleyError error) {
        if (jsonObject != null) {
            try {
                conversionRate = jsonObject.getDouble(currencyConvertCode());
                callback.currencyDataFetched(true);
            } catch (Exception exception) {
                Log.d(MainActivity.TAG, "parser error" + exception.getLocalizedMessage());
                callback.currencyDataFetched(false);
            }
        } else {
            callback.currencyDataFetched(false);
        }
    }

    public interface CurrencyDataNetworkModelResponse {
        public void currencyDataFetched(boolean success);
    }
}
