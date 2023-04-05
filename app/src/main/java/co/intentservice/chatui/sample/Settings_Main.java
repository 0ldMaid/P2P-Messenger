package co.intentservice.chatui.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;


public class Settings_Main extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    private final static String TAG = "SettingsActivity";

    public Settings_Main() {}


    //This is the android settings screen were users can change their info and use some simple tools.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //MyLog.d(TAG, "onCreate");
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new LocationFragment()).commit();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }//onCreate

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // handle the preference change here

        if (key.equals("show_user_ads")) {

            System.out.println("show_user_ads " );

            Main.bv_rebuild.setRebuild(true);

        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.content_settings_main);

            //Set the version code.
            Preference button_id = findPreference("programid");
            button_id.setSummary(Main.program_version);

            Preference ads_id = findPreference("show_user_adds");




        }
    }



}//last