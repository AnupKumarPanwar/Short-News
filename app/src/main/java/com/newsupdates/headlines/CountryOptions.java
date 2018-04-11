package com.newsupdates.headlines;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CountryOptions extends AppCompatActivity {

    String[] countries={"United Arab Emirates", "Argentina", "Austria", "Australia", "Belgium", "Bulgaria", "Brazil", "Canada",  "China", "Colombia", "Cuba", "Czechia", "Germany", "Egypt", "France", "Great Britain", "Greece", "Hong Kong", "Hungary", "Indonesia", "Ireland", "Israel", "India", "Italy", "Japan", "Korea", "Lithuania", "Latvia", "Morocco", "Mexico", "Malaysia", "Nigeria", "Netherlands", "Norway", "New Zealand", "Philippines", "Poland", "Portugal", "Romania", "Serbia", "Russia", "Saudi Arabia", "Sweden", "Singapore", "Slovenia", "Slovakia", "Switzerland", "Thailand", "Turkey", "Taiwan", "Ukraine", "United States of America", "Venezuela", "South Africa"};

    String[] countryCodes={"ae", "ar", "at", "au", "be", "bg", "br", "ca", "cn", "co", "cu", "cz", "de", "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in", "it", "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl", "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "ch", "th", "tr", "tw", "ua", "us", "ve", "za"};


    RadioGroup radioGroup;

    FloatingActionButton done;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_options);

        done=(FloatingActionButton)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor=sharedPreferences.edit();

        radioGroup=(RadioGroup)findViewById(R.id.radio_group);

        country=sharedPreferences.getString("country", null);
        if (country==null)
        {
            country="us";
        }

        for (int i=0; i<countries.length; i++)
        {

            RadioButton radioButton=new RadioButton(getApplicationContext());
            radioButton.setText(countries[i] +  " ("+ countryCodes[i]+")");
            radioButton.setTextColor(Color.parseColor("#000000"));
            radioButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FF009587")));

            final int finalI = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editor.putString("country", countryCodes[finalI]);
                    editor.apply();
                }
            });


            if (countryCodes[i].equals(country))
            {
                radioButton.setChecked(true);
            }

            radioGroup.addView(radioButton);


        }

    }
}
