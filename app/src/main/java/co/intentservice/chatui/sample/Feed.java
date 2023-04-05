package co.intentservice.chatui.sample;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.JSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class Feed extends AppCompatActivity {

    private static boolean activityVisible;
    private static boolean scrollBottom = true;

    public static final int FEED_ID_PRODUCT = 0;
    public static final int FEED_ID_STATUS = 1;
    public static final int FEED_ID_AVATAR = 2;
    public static final int FEED_ID_PICTURE = 3;

    database_accounts updatex = new database_accounts();
    database_feed feed_db = new database_feed();

    static boolean_variable_feed_added feed_added = new boolean_variable_feed_added();
    static boolean_variable_feed_connection feed_connection = new boolean_variable_feed_connection();

    RecyclerView recyclerView;
    recycler_view_feed adapter;

    DateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    DateFormat df2 = new SimpleDateFormat("dd/MM", Locale.getDefault());

    //JSONParser parser = new JSONParser();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    ArrayList<ArrayList<String>> data_feed = new ArrayList<>();
    String[][] db_string_feed;

    TextView textView1;
    TextView textView2;
    View adContainer;

    //EditText chatBox;
    int scrollDy = 0;

    SwipeRefreshLayout layout;

    static boolean connected_peer_inform_read = false;
    static boolean acitivty_paused = false;

    static String namex2;
    static String link_id2;

    private long last_message_time = 0l;
    private long one_day = 86400000l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_news_feed);

        //ad container
        //adContainer = (View) findViewById(R.id.adMobView2);

        //Settings button gear top right.
        Button settings_gear = (Button) findViewById(R.id.user_settings);
        settings_gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), Settings_Feed.class);
                //i.putExtra("Transfer Token", "Transfer");
                startActivity(i);

            }
        });

        try {


            // set up the RecyclerView
            recyclerView = (RecyclerView) findViewById(R.id.reyclerview_feed_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setStackFromEnd(false);
            layoutManager.setReverseLayout(false);
            adapter = new recycler_view_feed(this, data_feed);
            //adapter.setClickListener(this);
            //adapter.setAvatarImage(loadImage(imagex));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.scrollToPosition(0);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1)) {

                        scrollBottom = true;
                        //Toast.makeText(Chat.this, "Last", Toast.LENGTH_LONG).show();

                    }
                    else {

                        scrollBottom = false;

                    }

                }

            });

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(getResources().getString(R.string.feed_title));

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(Integer.toString(updatex.getFriendCount()) + " Friends + " + updatex.getFollowingCount() + " Following");

        //Main layout with friends and apps.
        layout = (SwipeRefreshLayout) findViewById(R.id.contentView);
        layout.addOnLayoutChangeListener( new View.OnLayoutChangeListener() {

            public void onLayoutChange( View v, int left, int top, int right, int bottom, int leftWas, int topWas, int rightWas, int bottomWas ) {

                //int widthWas = rightWas - leftWas; // Right exclusive, left inclusive
                //if( v.getWidth() != widthWas ) {
                // Width has changed
                //}
                int heightWas = bottomWas - topWas; // Bottom exclusive, top inclusive
                if( v.getHeight() != heightWas ) {

                    //setImage();

                }

                //int width  = layout.getMeasuredWidth();
                //int height = layout.getMeasuredHeight();

            }

        });
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                Toast.makeText(Main.context2, "Reloading...", Toast.LENGTH_LONG).show();

                updatex.updateFriendFeed();
                //Main.work_ready = true;

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("work_ready", true);
                editor.commit();

                new rebuild_chain().execute();
                layout.setRefreshing(false);

            }

        });

        //We listen for updates and make them seen.
        feed_connection.setListener(new boolean_variable_feed_connection.ChangeListener() {
            @Override
            public void onChange() {

                if (feed_connection.isConnected()) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            System.out.println("Changed...");
                            textView2.setText("Connecting to: " + tor_net_client.friend_connection);

                        }//@Override

                    });

                }
                else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            System.out.println("Changed...");
                            textView2.setText(Integer.toString(updatex.getFriendCount()) + " Friends + " + updatex.getFollowingCount() + " Following");

                        }//@Override

                    });

                }

            }
        });

        feed_added.setListener(new boolean_variable_feed_added.ChangeListener() {
            @Override
            public void onChange() {

                new rebuild_chain().execute();

            }
        });

        new rebuild_chain().execute();

    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");
        //Feed.activityPaused();


    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SHOW");
        //Feed.activityResumed();


    }



    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {

        activityVisible = true;

        if (acitivty_paused) {


            acitivty_paused = false;

        }

    }

    public static void activityPaused() {
        activityVisible = false;
        acitivty_paused = true;
    }





    private class rebuild_chain extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Rebuild Chain...");

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {

                db_string_feed = feed_db.getItems();
                System.out.println("db_string_feed " + db_string_feed[0].length);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //rebuild everything.
                        int size = data_feed.size();
                        if (size > 0) {
                            for (int loop = 0; loop < size; loop++) {
                                data_feed.remove(0);
                            }

                            //adapter.notifyItemRangeRemoved(0, size);
                        }


                        //boolean found_last_date = false;
                        long time_now = System.currentTimeMillis();

                        for (int loop1 = 0; loop1 < db_string_feed[0].length; loop1++) {//********

                            //System.out.println("DB 4  " + db_string_feed[4][loop1]);
                            //System.out.println("DB 9  " + db_string_feed[9][loop1]);
                            //System.out.println("DB 10 " + db_string_feed[10][loop1]);

                            ArrayList<String> data = new ArrayList<>();
                            data.add(db_string_feed[0][loop1]);//xd
                            data.add(db_string_feed[1][loop1]);//link
                            data.add(db_string_feed[2][loop1]);//hash
                            data.add(db_string_feed[3][loop1]);//name
                            data.add(db_string_feed[4][loop1]);//icon

                            if ((time_now - Long.parseLong(db_string_feed[5][loop1])) > one_day) {

                                data.add(df2.format(new Date(Long.parseLong(db_string_feed[5][loop1]))));//date

                            }
                            else {

                                data.add(df1.format(new Date(Long.parseLong(db_string_feed[5][loop1]))));//date

                            }

                            data.add(db_string_feed[6][loop1]);//view
                            data.add(db_string_feed[7][loop1]);//status
                            data.add(db_string_feed[8][loop1]);//title
                            data.add(db_string_feed[9][loop1]);//description
                            data.add(db_string_feed[10][loop1]);//image
                            data.add(db_string_feed[11][loop1]);//price
                            data.add(db_string_feed[12][loop1]);//currency
                            data.add(db_string_feed[13][loop1]);//tor
                            data.add(db_string_feed[14][loop1]);//pub key
                            data.add(db_string_feed[15][loop1]);//dapps
                            data_feed.add(data);

                        }//********************************************************************

                        recyclerView.getRecycledViewPool().clear();
                        adapter.notifyDataSetChanged();

                        //adapter.notifyItemRangeInserted(0, data_messages.size());
                        //adapter.notifyItemChanged(data_messages.size());

                        recyclerView.scrollToPosition(0);

                        layout.setRefreshing(false);


                    }//@Override

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            //setImage();

            return null;

        }//do in background()

    }//build_chain2**************************************************







}
