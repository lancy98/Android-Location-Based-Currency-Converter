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
        implements CountryListAdapter.RecyclerViewSelection, CountriesNetworkModel.CountriesNetworkModelResponse {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_selection_view);

        progressBar = findViewById(R.id.progressBar);

        List<Country> countries = CountriesNetworkModel.getInstance().countries;

        if (countries != null && countries.size() > 0) {
            loadListView(countries);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            UIHelper.disableUserInteraction(this);
            CountriesNetworkModel.getInstance().callbackObject = this;
        }
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

    @Override
    public void countriesDataFetched(boolean success) {
        UIHelper.enableUserInteraction(this);
        progressBar.setVisibility(View.GONE);

        List<Country> countries = CountriesNetworkModel.getInstance().countries;

        if (countries.size() > 0) {
            loadListView(countries);
        }
    }
}
