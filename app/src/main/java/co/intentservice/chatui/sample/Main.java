package co.intentservice.chatui.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.leolin.shortcutbadger.ShortcutBadger;

public class Main extends AppCompatActivity implements recycler_view_main.ItemClickListener {

    static boolean_variable_main_update bv_update = new boolean_variable_main_update();
    static boolean_variable_main_rebuild bv_rebuild = new boolean_variable_main_rebuild();
    static string_variable_status sv_status = new string_variable_status();

    //database_test testdb = null;
    database_accounts accountsx = null;
    database_update_display_status statusx = null;
    tools_build_main_layout layout_tools = null;

    public static boolean torSystemReady = false;
    public static boolean activityIdle = false;
    public static boolean databaseUpdated = false;
    public static boolean databaseInstalled = false;
    private static boolean activityVisible;
    private static boolean scrollBottom;

    static boolean account_ads_open = true;
    private static boolean account_list_open = true;
    private static boolean apps_list_open = true;
    private static boolean dapps_list_open = true;
    private static boolean friends_list_open = true;
    private static boolean follow_list_open = true;

    //static boolean work_ready = false;

    static File directory;

    static Context context1;
    static Context context2;
    static AssetManager assetManager;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    SwipeRefreshLayout layout;
    TextView textView1;
    TextView textView2;
    View adContainer;

    Timer xtimerx;//class loop.

    static String versionx = "1";
    static String program_version = "1.0.6";//Program version name.
    static String idx = "P2PMessenger";//Program version name.
    static String path = "";//The path to our save folder.
    //static String pub_key_id = "";//Public RSA key.
    //static String prv_key_id = "";//Private RSA key.
    static String last_dapp_id = "";//the last dapp we created
    static String last_listing_id = "";//the last listing we created
    static String last_listing_json = "";//listing in JSON format
    static String last_picture_id = "";//the last picture we added
    static String last_picture_message_id = "";//the last picture message we added

    long last_exit_click = 0l;//We want to double click to exit in case the user makes a mistake.

    static int picture_size_px = 1000;
    static int image_size_px = 200;
    static int p2p_port = 37337;
    static int new_messages = 0;
    static int unsent_messages = 0;
    static int store_items = 0;
    static int feed_items_new = 0;
    static int album_items = 0;
    static int chat_image_size_dp = 150;
    //static int no_internet_timeout = 3000;

    //ArrayList<ArrayList<String>> data_messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);

        //Get static app context for the rest of the program to use.
        context1 = this.getBaseContext();
        context2 = this.getApplicationContext();

        //MobileAds.initialize(context2, new OnInitializationCompleteListener() {
            //@Override
            //public void onInitializationComplete(InitializationStatus initializationStatus) {


            //}
        //});

        //MobileAds.initialize(context2, "ca-app-pub-1455267053898698~5142013483");//YOUR_ADMOB_APP_ID

        //Get static asset manager for the program to use.
        assetManager = context2.getAssets();

        //Get static directory for app to use.
        directory = getApplication().getCacheDir();

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(context2);
        editor = settings.edit();

        //Load the database.
        database_driver driver = new database_driver(context2.getApplicationContext());
        if (databaseInstalled || databaseUpdated) {installImages();}

        //Get the drop down menu settings.
        account_ads_open = settings.getBoolean("show_user_adds", true);
        account_list_open = settings.getBoolean("account_list_open", true);
        apps_list_open = settings.getBoolean("apps_list_open", true);
        dapps_list_open = settings.getBoolean("dapps_list_open", true);
        friends_list_open = settings.getBoolean("friends_list_open", true);
        follow_list_open = settings.getBoolean("follow_list_open", true);

        //Get the database.
        accountsx = new database_accounts();

        //layout tools
        RecyclerView view = (RecyclerView) findViewById(R.id.reyclerview_main_list);
        layout_tools = new tools_build_main_layout(this, context1, view);

        //Get friends status.
        statusx = new database_update_display_status();

        //MyApplication.context = getApplicationContext();
        path = getApplicationInfo().dataDir;

        //ad container
        adContainer = (View) findViewById(R.id.adMobView);

        //Textview top left.
        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.home_title));

        //Textview top left.
        textView2 = (TextView) findViewById(R.id.textView2);

        new rebuildChain().execute();

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

                Toast.makeText(context2, "Reloading...", Toast.LENGTH_LONG).show();

                //Show.
                new rebuildChain().execute();

            }

        });


        //Settings button gear top right.
        Button settings_gear = (Button) findViewById(R.id.user_settings);
        settings_gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), Settings_Main.class);
                //i.putExtra("Transfer Token", "Transfer");
                startActivity(i);

            }
        });


        custom_text_view searchx;

        //Search button on the bottom of the screen.
        searchx = (custom_text_view)findViewById(R.id.searchx);
        searchx.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);

                return false;

            }
        });
        searchx.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    //Check if no view has focus:

                    //if(!network.full_node){remote_db_search();}
                    //else{local_db_search();}

                }

                return false;

            }
        });

        //We listen for updates and make them seen.
        bv_update.setListener(new boolean_variable_main_update.ChangeListener() {
            @Override
            public void onChange() {

                System.out.println("new Main.update_chain().execute();");
                new updateChain().execute();

            }
        });

        //We listen for updates and make them seen.
        bv_rebuild.setListener(new boolean_variable_main_rebuild.ChangeListener() {
            @Override
            public void onChange() {

                System.out.println("new Main.rebuild_chain().execute();");
                new rebuildChain().execute();

            }
        });

        //We listen for updates and make them seen.
        sv_status.setListener1(new string_variable_status.ChangeListener() {
            @Override
            public void onChange() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("Changed... 65x");
                        textView2.setText(sv_status.getStatus());

                    }//@Override

                });

            }
        });

        //Set the status top of the screen.
        textView2.setText(Main.sv_status.getStatus());

        //Background network service.
        //startService(new Intent(Main.this,MyJobServiceNetwork.class));

        //Start the network.
        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_NetworkFeedTask(), 0);

        //new Main.NetworkFeedTask().execute();

    }



    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("STOP");

        adContainer.setEnabled(false);

        activityIdle = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");

        adContainer.setEnabled(false);

        activityIdle = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SHOW");

        activityIdle = false;

        if (torSystemReady) {

            Main.sv_status.setStatus("No connection...");

        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (account_ads_open) {

                    adContainer.setEnabled(true);
                    adContainer.setVisibility(View.VISIBLE);

                }

                textView2.setText(sv_status.getStatus());

            }//@Override

        });

    }


    @Override
    public void onItemClick(View view, int position) {

        layout_tools.itemClick(position);

        if (layout_tools.isClickAccountSettings(position)) {

            Intent i = new Intent(getBaseContext(), Account.class);
            startActivity(i);

        }


        if (layout_tools.isClickAccountStore(position)) {

            Toast.makeText(context2, "Coming Soon...", Toast.LENGTH_LONG).show();

        }


        if (layout_tools.isClickAccountAlbum(position)) {

            Intent i = new Intent(getBaseContext(), Album.class);
            startActivity(i);

        }


        if (layout_tools.isClickAppsFeed(position)) {

            Intent i = new Intent(getBaseContext(), Feed.class);
            startActivity(i);

        }


        if (layout_tools.isClickAppsLove(position)) {

            Toast.makeText(context2, "Coming Soon...", Toast.LENGTH_LONG).show();

        }


        if (layout_tools.isClickFriends(position)) {

            System.out.println("Friend");

            int friend_id = layout_tools.getClickFriendsID(position);

            JSONObject obj = new JSONObject();

            obj.put("name_id", accountsx.getFriendNameID(friend_id));
            obj.put("link_id", accountsx.getFriendXD(friend_id));
            obj.put("pub_key", accountsx.getFriendPubKey(friend_id));
            obj.put("tor_address", accountsx.getFriendTorAddress(friend_id));
            obj.put("user_avatar", accountsx.getFriendAvatarImage(friend_id));
            obj.put("user_background", accountsx.getFriendBackgroundImage(friend_id));
            obj.put("user_version", accountsx.getFriendVersion(friend_id));

            final String jsonx = JSONValue.toJSONString(obj);

            Intent i = new Intent(getBaseContext(), Chat.class);
            i.putExtra("friend_json", jsonx);
            startActivity(i);

        }


    }




    @Override
    public void onItemLongPress(View view, int position) {

        if (layout_tools.isLongPressFriend(position)) {

            System.out.println("Friend");

            String friend_id = Integer.toString(layout_tools.getClickFriendsID(position));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want delete your friend?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing but close the dialog

                    System.out.println("Delete friend_id " + friend_id);

                    int delete_friend_id = Integer.parseInt(friend_id);

                    database_accounts unfriend = new database_accounts();
                    unfriend.delete(unfriend.getFriendXD(delete_friend_id));

                    //Show.
                    //new Main.rebuild_chain().execute();
                    layout_tools.rebuildDataMessages();

                    dialog.dismiss();

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }


        if (layout_tools.isLongPressFollowing(position)) {

            System.out.println("Following");

            final String follow_id = Integer.toString(layout_tools.getClickFollowingID(position));

            AlertDialog.Builder builder = new AlertDialog.Builder(this);//getWindow().getDecorView().getRootView().getContext()

            builder.setTitle("Confirm");
            builder.setMessage("Are you sure you want un-follow this account?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    //Do nothing but close the dialog

                    System.out.println("Delete friend_id " + follow_id);

                    int delete_friend_id = Integer.parseInt(follow_id);

                    database_accounts unfriend = new database_accounts();
                    unfriend.delete(unfriend.getFollowingXD(delete_friend_id));

                    //Show.
                    //new Main.rebuild_chain().execute();
                    layout_tools.rebuildDataMessages();

                    dialog.dismiss();

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        }



    }



    public void setShortcutBadger() {

        int badgeCount = new_messages;

        if (badgeCount > 0) {

            ShortcutBadger.applyCount(context2, badgeCount); //for 1.1.4+
            //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3

        }
        else {

            ShortcutBadger.removeCount(context2); //for 1.1.4+
            //ShortcutBadger.with(getApplicationContext()).remove();  //for 1.1.3

        }

    }



    private void installImages() {

        SharedPreferences.Editor editor = settings.edit();

        file_extract_picture printp = new file_extract_picture();

        String base64x1 = printp.printPicture("seed.png", true);
        editor.putString("user_avatar", base64x1);
        editor.putString("seed_icon", base64x1);

        String base64x2 = printp.printPicture("icons8_shop_96.png", false);
        editor.putString("shop_icon", base64x2);

        String base64x3 = printp.printPicture("icons8_photo_album_96.png", false);
        editor.putString("album_icon", base64x3);

        String base64x4 = printp.printPicture("icons8_activity_feed_96.png", false);
        editor.putString("feed_icon", base64x4);

        String base64x5 = printp.printPicture("background.png", false);
        editor.putString("user_background", base64x5);

        String base64x6 = printp.printPicture("icons8_dating_website_100.png", false);
        editor.putString("love_icon", base64x6);

        editor.commit();

    }




    public void onBackPressed() {

        System.out.println("Exit!");

        System.out.println("System.currentTimeMillis() " + System.currentTimeMillis());
        System.out.println("last_exit_click            " + last_exit_click);

        System.out.println("difference                 " + (System.currentTimeMillis() - last_exit_click));

        if ((System.currentTimeMillis() - last_exit_click) < 1000) {

            stopService(new Intent(Main.context2,MyJobServiceNetwork.class));
            System.exit(0);

        }
        else {

            Toast.makeText(getApplicationContext(), (String) "Double click to exit", Toast.LENGTH_LONG).show();

        }

        last_exit_click = System.currentTimeMillis();


    }//***************************






    private class NetworkFeedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Update Chain...");

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {

                //start client
                tor_net_client client = new tor_net_client();

                //start server
                tor_net_server server = new tor_net_server();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return null;

        }//do in background()

    }//build_chain2**************************************************







    class RemindTask_NetworkFeedTask extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//********************************

            //start client
            tor_net_client client = new tor_net_client();

            //start server
            tor_net_server server = new tor_net_server();

        }//runx*********************************************

    }//remindtask




    private class updateChain extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Update Chain...");

        }


        @Override
        protected Void doInBackground(Void... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    layout_tools.updateDataMessages();
                    layout.setRefreshing(false);

                }
            });

            return null;

        }//do in background()

    }//build_chain2**************************************************




    private class rebuildChain extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Rebuild Chain...");

        }


        @Override
        protected Void doInBackground(Void... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    layout_tools.rebuildDataMessages();
                    layout.setRefreshing(false);

                }
            });

            return null;

        }//do in background()

    }//build_chain2**************************************************






}
