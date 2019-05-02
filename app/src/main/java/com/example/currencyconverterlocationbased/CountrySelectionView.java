package com.example.currencyconverterlocationbased;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CountrySelectionView extends AppCompatActivity
        implements CountryListAdapter.RecyclerViewSelection, NetworkRequest.NetworkResponse {

    private ProgressBar progressBar;
    private NetworkRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selection_view);

        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        disableUserInteraction();

        String url = "https://free.currconv.com/api/v7/countries?apiKey=YOUR_KEY_HERE";
        request = new NetworkRequest(this, url, this);

    }

    public void loadListView(List<Country> listData) {
        final Country[] countries = listData.toArray(new Country[listData.size()]);
        CountryListAdapter countryListAdapter = new CountryListAdapter(this, countries, this);

        RecyclerView countryRecyclerView = findViewById(R.id.country_list_recycler_view);

        countryRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        countryRecyclerView.setLayoutManager(linearLayoutManager);

        countryRecyclerView.setAdapter(countryListAdapter);
    }

    public void selected(Country country) {
        Intent intent = new Intent();
        intent.putExtra("Country", country);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void disableUserInteraction() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }

    private void enableUserInteraction() {
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }

    public void networkResponse(JSONObject jsonObject ,VolleyError error) {
        enableUserInteraction();
        progressBar.setVisibility(View.GONE);

        if (jsonObject != null) {
            parseJSONObject(jsonObject);
        } else {
            Toast.makeText(this, "Response Error", Toast.LENGTH_SHORT).show();
        }

        request = null;
    }

    private void parseJSONObject(JSONObject jsonObject) {
        try {
            JSONObject results = (JSONObject) jsonObject.get("results");
            Iterator<String> countryKeys = results.keys();
            List<Country> countries = new ArrayList<Country>();

            while (countryKeys.hasNext()) {
                String countryKey = countryKeys.next();
                JSONObject countryJson = results.getJSONObject(countryKey);
                Country country = new Country(countryJson);
                countries.add(country);
            }

            loadListView(countries);
        } catch (Exception exception) {
            Log.d(MainActivity.TAG, "parser error" + exception.getLocalizedMessage());
        }
    }
}
