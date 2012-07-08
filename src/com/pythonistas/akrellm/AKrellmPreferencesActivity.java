package com.pythonistas.akrellm;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;


public class AKrellmPreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        // Preference circlePreference = getPreferenceScreen().findPreference("numberOfCircles");
        // circlePreference.setOnPreferenceChangeListener(numberCheckListener);
    }
    
    // Preference.OnPreferenceChangeListener numberCheckListener = new OnPreferenceChangeListener() {
    //         @Override
    //         public boolean onPreferenceChange(Preference preference, Object newValue) {
    //             if (newValue != null && newValue.toString().length() > 0 
    //                 && newValue.toString().matches("\\d*")) {
    //                 return true;
    //             }
    //             Toast.makeText(AKrellmPreferencesActivity.this, "Invalid Input",
    //                            Toast.LENGTH_SHORT).show();
    //             return false;
    //         }
                
    //     };
}