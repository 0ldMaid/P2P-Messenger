package co.intentservice.chatui.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class Settings_Chat extends PreferenceActivity {

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;

    database_messages messagesx = new database_messages();

    private final static String TAG = "SettingsActivity";

    public Settings_Chat() {}


    //This is the android settings screen were users can change their info and use some simple tools.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //MyLog.d(TAG, "onCreate");
        //getFragmentManager().beginTransaction().replace(android.R.id.content, new LocationFragment()).commit();

        addPreferencesFromResource(R.xml.content_settings_chat);

        String chatId = getIntent().getStringExtra("chat_id");

        //Set the version code.
        Preference buttonid = findPreference("programid");
        buttonid.setSummary(Main.program_version);

        Preference button = findPreference("empty_chat");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //code for what you want it to do

                System.out.println("Delete... " + chatId);
                messagesx.deleteMessages(chatId);

                Chat.bv_rebuild.setRebuild(true);

                return true;
            }
        });


    }//onCreate




}//last