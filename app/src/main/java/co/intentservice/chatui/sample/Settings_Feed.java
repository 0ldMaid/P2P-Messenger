package co.intentservice.chatui.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class Settings_Feed extends PreferenceActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private final static String TAG = "SettingsActivity";

    public Settings_Feed() {}


    //This is the android settings screen were users can change their info and use some simple tools.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //MyLog.d(TAG, "onCreate");
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new LocationFragment()).commit();

        addPreferencesFromResource(R.xml.content_settings_feed);


        //Set the version code.
        Preference buttonid = findPreference("programid");
        buttonid.setSummary(Main.program_version);



    }//onCreate




}//last