package com.example.currencyconverterlocationbased;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Currency-Converter-TAG";
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
    }

    public void buttonClicked(View view) {
        Intent intent = new Intent(this, CountrySelectionView.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (100) : {
                if (resultCode == Activity.RESULT_OK) {
                    Country country = data.getParcelableExtra("Country");
                    String displayText = "Country name is : " + country.name;
                    Log.d(MainActivity.TAG, displayText);
                    textView.setText(displayText);
                }
                break;
            }
        }
    }
}
