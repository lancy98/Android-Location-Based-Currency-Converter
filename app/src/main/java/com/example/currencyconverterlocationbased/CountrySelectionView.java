package com.example.currencyconverterlocationbased;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import java.util.List;

public class CountrySelectionView extends AppCompatActivity
        implements CountryListAdapter.RecyclerViewSelection, CountriesNetworkModel.CountriesNetworkModelResponse {

    private ProgressBar progressBar;
    private CountryListAdapter countryListAdapter;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.countries_search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                countryListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    public void loadListView(List<Country> listData) {
        final Country[] countries = listData.toArray(new Country[listData.size()]);
        countryListAdapter = new CountryListAdapter(this, countries, this);

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
