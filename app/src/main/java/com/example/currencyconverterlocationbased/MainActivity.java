package com.example.currencyconverterlocationbased;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NetworkRequest.NetworkResponse {

    public static final String TAG = "Currency-Converter-TAG";
    private static final int REQUEST_CODE_COUNTRY1 = 100;
    private static final int REQUEST_CODE_COUNTRY2 = 101;
    private Country fromCountry;
    private Country toCountry;
    private double conversionRate;
    private NetworkRequest request;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
    }

    public void numericPadTapped(View view) {
        String buttonText = ((Button)view).getText().toString();

        if (getString(R.string.backspace).equals(buttonText)) {
            Log.d(TAG, "backspace tapped");
        }

        Log.d(TAG, buttonText);
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
                    updateCountryInfo(fromCountry, true);
                }
                break;
            }
            case (REQUEST_CODE_COUNTRY2) : {
                if (resultCode == Activity.RESULT_OK) {
                    Country country = data.getParcelableExtra("Country");
                    toCountry = country;
                    updateCountryInfo(toCountry, false);
                }
                break;
            }
        }
    }

    private void updateCountryInfo(Country country, Boolean isFromCountry) {
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

        if (fromCountry != null && toCountry != null) {
            invokeCurrencyConvertAPI();
        }
    }

    private String currencyConvertCode() {
        return fromCountry.currency.identification + "_" + toCountry.currency.identification;
    }

    private void invokeCurrencyConvertAPI() {
        progressBar.setVisibility(View.VISIBLE);
        disableUserInteraction();

        String url = "https://free.currconv.com/api/v7/convert?apiKey=YOUR_API_KEY&q=" + currencyConvertCode() +"&compact=ultra";
        request = new NetworkRequest(this, url, this);
    }

    public void networkResponse(JSONObject jsonObject , VolleyError error) {
        enableUserInteraction();
        progressBar.setVisibility(View.GONE);

        try {
            conversionRate = jsonObject.getDouble(currencyConvertCode());

            TextView country1CurrencyTextView = findViewById(R.id.country1CurrencyTextView);
            TextView country2CurrencyTextView = findViewById(R.id.country2CurrencyTextView);

            country1CurrencyTextView.setText("1");
            country2CurrencyTextView.setText(String.format("%.2f", conversionRate));

        } catch (Exception exception) {
            Log.d(MainActivity.TAG, "parser error" + exception.getLocalizedMessage());
        }
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
}
