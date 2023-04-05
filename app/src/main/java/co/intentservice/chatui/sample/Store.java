package co.intentservice.chatui.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.simple.parser.JSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class Store extends AppCompatActivity {

    private static boolean activityVisible;
    private static boolean scrollBottom = true;

    static boolean_variable_main_update bv_update = new boolean_variable_main_update();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    database_products productsx = new database_products();
    file_load_bitmap loadb = new file_load_bitmap();

    RecyclerView recyclerView;
    recycler_view_store adapter;

    DateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    DateFormat df2 = new SimpleDateFormat("dd/MM", Locale.getDefault());

    String[][] db_products;

    SwipeRefreshLayout layout;

    JSONParser parser = new JSONParser();

    ArrayList<ArrayList<String>> data_messages = new ArrayList<>();

    TextView textView1;
    TextView textView2;

    static boolean connected_peer_inform_read = false;
    static boolean acitivty_paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_store);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(settings.getString("user_name", "") + "'s Store");

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("Inventory and sales management");

        Button getName = (Button) findViewById(R.id.get_products);
        getName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getBaseContext(), Store_Products.class);
                startActivity(i);

            }

        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SHOW");
        Store.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");
        Store.activityPaused();
    }

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {

        activityVisible = true;

        if (acitivty_paused) {


        }

    }

    public static void activityPaused() {
        activityVisible = false;
        acitivty_paused = true;
    }






}
