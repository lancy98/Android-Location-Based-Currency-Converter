package com.example.currencyconverterlocationbased;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity
        implements CurrencyDataNetworkModel.CurrencyDataNetworkModelResponse,
        CountriesNetworkModel.CountriesNetworkModelResponse {

    public static final String TAG = "Currency-Converter-TAG";
    private static final int REQUEST_CODE_COUNTRY1 = 100;
    private static final int REQUEST_CODE_COUNTRY2 = 101;
    private Country fromCountry;
    private Country toCountry;
    private NetworkRequest request;
    private ProgressBar progressBar;
    private TextView country1CurrencyTextView;
    private TextView country2CurrencyTextView;
    private static Context context;
    private CurrencyDataNetworkModel currencyDataNetworkModel;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String countryName;

    private double conversionRate() {
        return currencyDataNetworkModel.conversionRate;
    }

    private void setConversionRate(double rate) {
        currencyDataNetworkModel.conversionRate = rate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        progressBar = findViewById(R.id.progressBar);
        country1CurrencyTextView = findViewById(R.id.country1CurrencyTextView);
        country2CurrencyTextView = findViewById(R.id.country2CurrencyTextView);

        setSwitchImage();

        invokeCountriesAPI();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocations();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        locationPermissionDeniedToast();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    private void updateLocations() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    countryName = getCountryName(latitude, longitude);
                    loadDefaultCountries();
                }
            }
        });
    }

    public static String getCountryName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return null;
        } catch (IOException ignored) {
            return null;
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    private void locationPermissionDeniedToast() {
        Toast.makeText(this, "Location Permission Denied!", Toast.LENGTH_SHORT)
                .show();
    }

    private void setSwitchImage() {
        try {
            InputStream ims = getAssets().open("switchImage.png");
            Drawable drawable = Drawable.createFromStream(ims, null);

            ImageView switchImageView = findViewById(R.id.switchImageView);
            switchImageView.setImageDrawable(drawable);
        }
        catch(IOException ex) {
            return;
        }
    }

    public void countrySwitch(View view) {
        Country country = fromCountry;
        fromCountry = toCountry;
        toCountry = country;

        updateCountryInfo(fromCountry, true, false);
        updateCountryInfo(toCountry, false, false);

        setConversionRate(1.0 / conversionRate());

        String currentValue = country1CurrencyTextView.getText().toString();
        double convertedCurrencyValue = Double.valueOf(currentValue) * conversionRate();
        country2CurrencyTextView.setText(String.format("%.2f", convertedCurrencyValue));
    }

    public void numericPadTapped(View view) {
        String currentValue = country1CurrencyTextView.getText().toString();
        String newNumber = ((Button)view).getText().toString();

        if (getString(R.string.backspace).equals(newNumber)) {
            if (currentValue.equals("0")) {
                // do nothing.
            } else if (currentValue.length() == 1) {
                country1CurrencyTextView.setText("0");
            } else if (currentValue.length() > 1) {
                country1CurrencyTextView.setText(currentValue.substring(0, currentValue.length() - 1));
            }

            updateToCountryCurrencyValue();
            return;
        }

        if (currentValue.equals("0")) {
            country1CurrencyTextView.setText(newNumber);
        } else if (currentValue.length() >= 1) {
            country1CurrencyTextView.append(newNumber);
        }

        updateToCountryCurrencyValue();
    }

    public void locationButtonTapped(View view) {
        Intent mapIntent = new Intent(MainActivity.this, UserMapActivity.class);
        startActivity(mapIntent);
    }

    private void updateToCountryCurrencyValue() {
        String currentValueString = country1CurrencyTextView.getText().toString();
        double currencyValue = Double.valueOf(currentValueString);

        double result = currencyValue * conversionRate();
        country2CurrencyTextView.setText(String.format("%.2f", result));
    }

    public void changeCountryButtonClicked(View view) {
        int buttonId = view.getId();
        int requestCode = (buttonId == R.id.country1SelectionButton) ? REQUEST_CODE_COUNTRY1 : REQUEST_CODE_COUNTRY2;

        Intent intent = new Intent(this, CountrySelectionView.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (REQUEST_CODE_COUNTRY1) : {
                if (resultCode == Activity.RESULT_OK) {
                    Country country = data.getParcelableExtra("Country");
                    fromCountry = country;
                    updateCountryInfo(fromCountry, true, true);
                }
                break;
            }
            case (REQUEST_CODE_COUNTRY2) : {
                if (resultCode == Activity.RESULT_OK) {
                    Country country = data.getParcelableExtra("Country");
                    toCountry = country;
                    updateCountryInfo(toCountry, false, true);
                }
                break;
            }
        }
    }

    private void updateCountryInfo(Country country, Boolean isFromCountry, boolean updateCurrencyData) {
        TextView countryNameTextView;
        ImageView countryImageView;

        if (isFromCountry) {
            countryNameTextView = findViewById(R.id.country1NameTextView);
            countryImageView = findViewById(R.id.country1FlagImageView);
        } else {
            countryNameTextView = findViewById(R.id.country2NameTextView);
            countryImageView = findViewById(R.id.country2FlagImageView);
        }

        countryNameTextView.setText(country.currency.identification + "(" + country.currency.symbol +")");

        try {
            InputStream ims = getAssets().open("imagePlaceholder.png");
            Drawable drawable = Drawable.createFromStream(ims, null);

            String imageURL = "https://www.countryflags.io/" + country.identification + "/shiny/64.png";
            Picasso.get().load(imageURL).placeholder(drawable).into(countryImageView);
        }
        catch(IOException ex) {
            return;
        }

        if (fromCountry != null && toCountry != null && updateCurrencyData) {
            invokeCurrencyConvertAPI();
        }
    }

    private void invokeCurrencyConvertAPI() {
        progressBar.setVisibility(View.VISIBLE);
        UIHelper.disableUserInteraction(this);

        currencyDataNetworkModel =
                new CurrencyDataNetworkModel(fromCountry,
                        toCountry,
                        this);
    }

    @Override
    public void currencyDataFetched(boolean success) {
        UIHelper.enableUserInteraction(this);
        progressBar.setVisibility(View.GONE);

        country1CurrencyTextView.setText("1");
        String string = String.format("%.2f", conversionRate());
        country2CurrencyTextView.setText(string);
    }

    private void invokeCountriesAPI() {
        progressBar.setVisibility(View.VISIBLE);
        UIHelper.disableUserInteraction(this);

        CountriesNetworkModel.getInstance().callbackObject = this;
        loadDefaultCountries();
    }

    @Override
    public void countriesDataFetched(boolean success) {
        CountriesNetworkModel.getInstance().callbackObject = null;
        loadDefaultCountries();
    }

    private void loadDefaultCountries() {
        CountriesNetworkModel countriesNetworkModel = CountriesNetworkModel.getInstance();
        List<Country> countries = countriesNetworkModel.countries;

        if (countries != null &&
                countries.size() > 0) {

            UIHelper.enableUserInteraction(this);
            progressBar.setVisibility(View.GONE);

            if (countryName != null) {
                fromCountry = countriesNetworkModel.countryWithName(countryName);
            }

            Random rand = new Random();

            if (fromCountry == null) {
                int randomNumber = rand.nextInt(countries.size());
                fromCountry = countries.get(randomNumber);
            }

            updateCountryInfo(fromCountry, true, false);

            if (toCountry == null) {
                int randomNumber = rand.nextInt(countries.size());
                toCountry = countries.get(randomNumber);
            }
            
            updateCountryInfo(toCountry, false, true);
        }
    }

}
