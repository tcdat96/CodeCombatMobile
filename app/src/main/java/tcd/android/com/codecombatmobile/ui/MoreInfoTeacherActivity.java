package tcd.android.com.codecombatmobile.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import tcd.android.com.codecombatmobile.R;

public class MoreInfoTeacherActivity extends AppCompatActivity {

    private Spinner mCountriesSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info_teacher);

        setTitle(R.string.title_activity_more_info);

        initUiComponents();
    }

    private void initUiComponents() {
        // countries list spinner
        mCountriesSpinner = findViewById(R.id.spinner_country_list);
        List<String> countryList = retrieveCountryList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);
        mCountriesSpinner.setAdapter(adapter);
    }

    private List<String> retrieveCountryList() {
        Locale[] locales = Locale.getAvailableLocales();
        List<String> countries = new ArrayList<>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length()>0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        return countries;
    }
}
