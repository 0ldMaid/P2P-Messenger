package co.intentservice.chatui.sample;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class Chat extends AppCompatActivity implements recycler_view_chat.ItemClickListener {

    private static int RESULT_LOAD_IMAGE_PIC = 1;

    public static final int SET_MESSAGE_STATUS_CREATED = 0;
    public static final int SET_MESSAGE_STATUS_SENT = 1;
    public static final int SET_MESSAGE_STATUS_READ = 2;
    public static final int SET_MESSAGE_STATUS_GET_IMAGE = 3;
    public static final int SET_MESSAGE_STATUS_FAILED = 4;

    public static final int MESSAGE_TYPE_SYSTEM = 0;
    public static final int MESSAGE_TYPE_TEXT_SENT = 1;
    public static final int MESSAGE_TYPE_TEXT_RECEIVED = 2;
    public static final int MESSAGE_TYPE_IMAGE_SENT = 3;
    public static final int MESSAGE_TYPE_IMAGE_RECEIVED = 4;

    //Storage Permissions variables
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static boolean activityVisible;
    private static boolean scrollBottom = true;

    RecyclerView recyclerView;
    recycler_view_chat adapter;

    static SharedPreferences settings;
    SharedPreferences.Editor editor;

    String[] friend_info;

    DateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    DateFormat df2 = new SimpleDateFormat("dd/MM", Locale.getDefault());

    JSONParser parser = new JSONParser();

    //static database_test testdb = new database_test();
    static database_messages messagesx = new database_messages();
    static database_accounts accountsx = new database_accounts();

    static boolean_variable_chat_read bv_read = new boolean_variable_chat_read();
    static boolean_variable_chat_sent bv_sent = new boolean_variable_chat_sent();
    static boolean_variable_chat_update bv_update = new boolean_variable_chat_update();
    static boolean_variable_chat_rebuild bv_rebuild = new boolean_variable_chat_rebuild();
    static tools_encryption hex = new tools_encryption();

    ArrayList<ArrayList<String>> data_messages = new ArrayList<>();
    //String db_messages[][];

    SwipeRefreshLayout layout;

    TextView textView1;
    TextView textView2;
    View adContainer;

    EditText chatBox;
    Button sendx;
    Button attachx;

    static boolean acitivty_paused = false;

    private int db_load_number = 100;
    private int scrollDy = 0;
    static private int link_id3 = 0;

    static String namex2;
    static String link_id2;
    static String versionx;
    static String image_background;

    private long last_message_time = 0l;
    private long one_day = 86400000l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);

        String sessionId = getIntent().getStringExtra("friend_json");

        System.out.println("sessionId " + sessionId);

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        //chat_view_main = (LinearLayout) findViewById(R.id.chat_view_main);
        //chatView = (ChatView) findViewById(R.id.chat_view);
        chatBox = (EditText) findViewById(R.id.edittext_chatbox);

        try {

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(sessionId);

            } catch (Error e) {e.printStackTrace();}

            JSONObject jsonObject = (JSONObject) obj;

            final String namex = (String) jsonObject.get("name_id");
            final String link_id = (String) jsonObject.get("link_id");
            final String pub_keyx = (String) jsonObject.get("pub_key");
            final String addressx = (String) jsonObject.get("tor_address");
            final String imagex = (String) jsonObject.get("user_avatar");
            image_background = (String) jsonObject.get("user_background");
            versionx = (String) jsonObject.get("user_version");

            System.out.println("link_id " + link_id);

            namex2 = namex;
            link_id2 = link_id;
            link_id3 = Integer.parseInt(link_id);

            //Update database this friend's messages are read.
            int updatedx = messagesx.confirmRead(link_id3);
            System.out.println("[TEST] " + updatedx);

            if (updatedx > 0) {

                //Show connection which friend needs to update.
                accountsx.setFriendNeedsUpdate(link_id3, 1);

                //Inform connection we have work to send.
                //Main.work_ready = true;

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("work_ready", true);
                editor.commit();

            }//****************

            //Inform main screen to update.
            Main.bv_update.setUpdate(true);

            String[][] db_messages = messagesx.getMessages(link_id2);

            boolean found_last_date = false;

            long time_now = System.currentTimeMillis();

            for (int loop1 = 0; loop1 < db_messages[0].length; loop1++) {//********

                ArrayList<String> data = new ArrayList<>();
                data.add(db_messages[4][loop1]);//type
                data.add(namex2);//name
                data.add(db_messages[3][loop1]);//message
                data.add(db_messages[5][loop1]);//status

                if ((time_now - Long.parseLong(db_messages[2][loop1])) > one_day) {

                    data.add(df2.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }
                else {

                    data.add(df1.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }

                //data.add(db_messages[2][loop1]);//date
                data.add(db_messages[6][loop1]);//image
                data_messages.add(data);

                //System.out.println("status " + db_messages[5][loop1]);

                if (!found_last_date &&
                        (db_messages[4][loop1].equals(Integer.toString(MESSAGE_TYPE_TEXT_RECEIVED)) ||
                                db_messages[4][loop1].equals(Integer.toString(MESSAGE_TYPE_IMAGE_RECEIVED))) ) {

                    last_message_time = Long.parseLong(db_messages[2][loop1]);

                    System.out.println("last_message_time " + last_message_time);
                    found_last_date = true;

                }

            }//********************************************************************


            Bitmap bitmap = null;

            try {

                System.out.println("Load user_avatar " + imagex);
                file_load_bitmap file = new file_load_bitmap();
                bitmap = file.getBitmap(imagex);

            } catch (Exception e) {

                System.out.println(e.getMessage());

            }

            // set up the RecyclerView
            recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            //layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            adapter = new recycler_view_chat(this, data_messages);
            adapter.setClickListener(this);
            adapter.setAvatarImage(bitmap);
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
        textView1.setText(getResources().getString(R.string.chat_title) + " - " + namex2 + " (" + versionx + ")");

        textView2 = (TextView) findViewById(R.id.textView2);

        attachx = (Button) findViewById(R.id.button_chatbox_attach);
        attachx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //Toast.makeText(Main.context2, "Coming soon...", Toast.LENGTH_LONG).show();

                verifyStoragePermissions(Chat.this);

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_PIC);

            }

        });

        sendx = (Button) findViewById(R.id.button_chatbox_send);
        sendx.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                System.out.println("Send " + chatBox.getText().toString());

                if (chatBox.getText().toString().length() > 0) {

                    long timex = System.currentTimeMillis();

                    messagesx.addMessage(link_id2, timex, chatBox.getText().toString(), MESSAGE_TYPE_TEXT_SENT, SET_MESSAGE_STATUS_CREATED);

                    new buildChainSendText(chatBox.getText().toString(), timex).execute();

                    chatBox.setText("");

                    //Show connection which friend needs to update.
                    accountsx.setFriendNeedsUpdate(link_id3, 10);

                    //Inform connection we have work to send.
                    //Main.work_ready = true;

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("work_ready", true);
                    editor.commit();

                }//*********************************************

            }

        });

        layout = (SwipeRefreshLayout) findViewById(R.id.contentView);
        layout.addOnLayoutChangeListener( new View.OnLayoutChangeListener() {

            public void onLayoutChange( View v, int left, int top, int right, int bottom, int leftWas, int topWas, int rightWas, int bottomWas ) {

                int widthWas = rightWas - leftWas; // Right exclusive, left inclusive
                int heightWas = bottomWas - topWas; // Bottom exclusive, top inclusive
                if( v.getHeight() != heightWas || v.getWidth() != widthWas ) {

                    setImage(image_background);

                }

            }

        });
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                //Show.
                new rebuildChain().execute();

            }

        });

        //Settings button gear top right.
        Button settings_gear = (Button) findViewById(R.id.user_settings);
        settings_gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getBaseContext(), Settings_Chat.class);
                i.putExtra("chat_id", link_id2);
                startActivity(i);

            }
        });

        chatBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                System.out.println("Keyboard");
                adapter.notifyDataSetChanged();
                adapter.notifyItemInserted(0);
                recyclerView.scrollToPosition(0);

            }

        });

        bv_sent.setListener(new boolean_variable_chat_sent.ChangeListener() {
            @Override
            public void onChange() {

                //this can show SENT AND READ
                new buildChainSendSent().execute();

            }
        });

        bv_read.setListener(new boolean_variable_chat_read.ChangeListener() {
            @Override
            public void onChange() {

                //this can show SENT AND READ
                new buildChainSendRead().execute();

            }
        });

        bv_update.setListener(new boolean_variable_chat_update.ChangeListener() {
            @Override
            public void onChange() {

                new buildChainRecv().execute();

            }
        });

        bv_rebuild.setListener(new boolean_variable_chat_rebuild.ChangeListener() {
            @Override
            public void onChange() {

                //Show.
                new rebuildChain2().execute();

            }
        });

        Main.sv_status.setListener2(new string_variable_status.ChangeListener() {
            @Override
            public void onChange() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        System.out.println("Changed... 55");
                        textView2.setText(Main.sv_status.getStatus());

                    }//@Override

                });

            }
        });

        textView2.setText(Main.sv_status.getStatus());

    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");

        //adContainer.setEnabled(false);
        //adContainer.setVisibility(View.GONE);

        Chat.activityPaused();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SHOW");

        //adContainer.setEnabled(true);
        //adContainer.setVisibility(View.VISIBLE);

        if (Main.torSystemReady) {

            Main.sv_status.setStatus("No connection...");
            //Main.work_ready = true;

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("work_ready", true);
            editor.commit();

        }

        try {

            Chat.activityResumed();

        } catch (Error e) {e.printStackTrace();}

    }


    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {

        activityVisible = true;

        if (acitivty_paused) {

            //Update database this friend's messages are read.
            messagesx.confirmRead(Integer.parseInt(link_id2));

            //Show connection which friend needs to update.
            accountsx.setFriendNeedsUpdate(link_id3, 1);

            //Inform connection we have work to send.
            //Main.work_ready = true;

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("work_ready", true);
            editor.commit();

            Main.bv_update.setUpdate(true);

            acitivty_paused = false;

        }

    }

    public static void activityPaused() {
        activityVisible = false;
        acitivty_paused = true;
    }




    @Override
    public void onItemClick(View view, int position) {



        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onItemLongPress(View view, int position) {

        //Toast.makeText(this, "You long clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();

        final int postion_final = position;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());
        builderSingle.setIcon(R.drawable.ic_launcher);
        //builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getWindow().getDecorView().getRootView().getContext(), android.R.layout.select_dialog_item);
        arrayAdapter.add("Copy");
        //arrayAdapter.add("Edit");
        //arrayAdapter.add("Delete");
        //arrayAdapter.add("Resend");
        //arrayAdapter.add("Save");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String strName = arrayAdapter.getItem(which);

                if (strName.equals("Copy")) {

                    String message_data = data_messages.get(postion_final).get(2);

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Message", message_data);
                    clipboard.setPrimaryClip(clip);

                }


            }
        });
        builderSingle.show();

    }




    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(Main.context2, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);

        }

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE_PIC && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            // String picturePath contains the path of selected Image

            try {

                //Set image
                Bitmap bitmapx = BitmapFactory.decodeFile(picturePath);

                int image_size_width = bitmapx.getWidth();
                int image_size_height = bitmapx.getHeight();

                System.out.println("CC width  " + image_size_width);
                System.out.println("CC height " + image_size_height);

                if (bitmapx.getWidth() > Main.picture_size_px) {

                    float scalex = ((float) Main.picture_size_px) / bitmapx.getWidth();

                    System.out.println("scalex " + scalex);

                    image_size_width = (int) (bitmapx.getWidth() * scalex);
                    image_size_height = (int) (bitmapx.getHeight() * scalex);

                }
                else if (bitmapx.getHeight() > Main.picture_size_px) {

                    float scalex = ((float) Main.picture_size_px) / bitmapx.getHeight();

                    System.out.println("scalex " + scalex);

                    image_size_width = (int) (bitmapx.getWidth() * scalex);
                    image_size_height = (int) (bitmapx.getHeight() * scalex);

                }

                System.out.println("CC width  " + image_size_width);
                System.out.println("CC height " + image_size_height);

                Bitmap bitmap = Bitmap.createScaledBitmap(bitmapx, image_size_width, image_size_height, false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                //bitmap.recycle();

                byte[] md5 = MessageDigest.getInstance("MD5").digest(byteArray);
                //String base64x = Base64.toBase64String(md5);
                String base64x = Base64.encodeToString(md5, Base64.DEFAULT);

                base64x = hex.bytesToHex(base64x.getBytes());

                System.out.println("base64x " + base64x);

                file_save_picture savex = new file_save_picture();
                savex.savePicture(base64x,bitmap);

                long timex = System.currentTimeMillis();

                messagesx.addMessage(link_id2, timex, base64x, MESSAGE_TYPE_IMAGE_SENT, SET_MESSAGE_STATUS_CREATED);
                new buildChainSendImage(base64x, timex).execute();

                //Show connection which friend needs to update.
                accountsx.setFriendNeedsUpdate(link_id3, 10);

                //Inform connection we have work to send.
                //Main.work_ready = true;

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("work_ready", true);
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }




    public void setImage(String image_background) {

        //Set the image.
        try {

            System.out.println("Load user_background " + image_background);
            file_load_bitmap file = new file_load_bitmap();
            Bitmap bitmap = file.getBitmap(image_background);

            int width  = layout.getMeasuredWidth();
            int height = layout.getMeasuredHeight();

            tools_bitmap bitmapt = new tools_bitmap();
            BitmapDrawable bdrawable = bitmapt.getFittedBitmap(bitmap, width, height);

            layout.setBackground(bdrawable);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }




    public class rebuildChain extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {

            System.out.println("messages " + data_messages.size());

            db_load_number = db_load_number + 100;

            String[][] db_messages = messagesx.getMessagesDESC(link_id2, Integer.toString(db_load_number));

            System.out.println("messages " + db_messages[0].length);

            boolean found_last_date = false;

            long time_now = System.currentTimeMillis();

            //rebuild everything.
            int size = data_messages.size();
            if (size > 0) {
                for (int loop = 0; loop < size; loop++) {
                    data_messages.remove(0);
                }

                //adapter.notifyItemRangeRemoved(0, size);
            }

            for (int loop1 = 0; loop1 < db_messages[0].length; loop1++) {//********

                final ArrayList<String> data = new ArrayList<>();
                data.add(db_messages[4][loop1]);//type
                data.add(namex2);//name
                data.add(db_messages[3][loop1]);//message
                data.add(db_messages[5][loop1]);//status

                if ((time_now - Long.parseLong(db_messages[2][loop1])) > one_day) {

                    data.add(df2.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }
                else {

                    data.add(df1.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }

                //data.add(db_messages[2][loop1]);//date
                data.add(db_messages[6][loop1]);//image
                data_messages.add(data);

                //System.out.println("status " + db_messages[5][loop1]);

                if (!found_last_date &&
                        (db_messages[4][loop1].equals(Integer.toString(MESSAGE_TYPE_TEXT_RECEIVED)) ||
                                db_messages[4][loop1].equals(Integer.toString(MESSAGE_TYPE_IMAGE_RECEIVED))) ) {

                    last_message_time = Long.parseLong(db_messages[2][loop1]);

                    System.out.println("last_message_time " + last_message_time);
                    found_last_date = true;

                }

                final int loopx1 = loop1;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            data_messages.set(loopx1, data);
                            adapter.notifyItemChanged(loopx1);

                        } catch (Error e) {
                            e.printStackTrace();
                        }


                    }//@Override

                });

            }//********************************************************************

            if (activityVisible) {

                //Update database this friend's messages are read.
                int updatedx = messagesx.confirmRead(link_id3);
                System.out.println("[TEST] " + updatedx);

                if (updatedx > 0) {

                    //Show connection which friend needs to update.
                    accountsx.setFriendNeedsUpdate(link_id3, 1);

                    //Inform connection we have work to send.
                    //Main.work_ready = true;

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("work_ready", true);
                    editor.commit();

                }//****************

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    layout.setRefreshing(false);

                }//@Override

            });

            return null;

        }//do in background()

    }//build_chain2**************************************************





    public class rebuildChain2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {

            System.out.println("messages " + data_messages.size());

            db_load_number = db_load_number + 100;

            String[][] db_messages = messagesx.getMessagesDESC(link_id2, Integer.toString(db_load_number));

            System.out.println("messages " + db_messages[0].length);

            boolean found_last_date = false;

            long time_now = System.currentTimeMillis();

            //rebuild everything.
            int size = data_messages.size();
            if (size > 0) {
                for (int loop = 0; loop < size; loop++) {
                    data_messages.remove(0);
                }

                //adapter.notifyItemRangeRemoved(0, size);
            }

            for (int loop1 = 0; loop1 < db_messages[0].length; loop1++) {//********

                final ArrayList<String> data = new ArrayList<>();
                data.add(db_messages[4][loop1]);//type
                data.add(namex2);//name
                data.add(db_messages[3][loop1]);//message
                data.add(db_messages[5][loop1]);//status

                if ((time_now - Long.parseLong(db_messages[2][loop1])) > one_day) {

                    data.add(df2.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }
                else {

                    data.add(df1.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }

                //data.add(db_messages[2][loop1]);//date
                data.add(db_messages[6][loop1]);//image
                data_messages.add(data);

                //System.out.println("status " + db_messages[5][loop1]);

                if (!found_last_date &&
                        (db_messages[4][loop1].equals(Integer.toString(MESSAGE_TYPE_TEXT_RECEIVED)) ||
                                db_messages[4][loop1].equals(Integer.toString(MESSAGE_TYPE_IMAGE_RECEIVED))) ) {

                    last_message_time = Long.parseLong(db_messages[2][loop1]);

                    System.out.println("last_message_time " + last_message_time);
                    found_last_date = true;

                }

                final int loopx1 = loop1;



            }//********************************************************************


            return null;

        }//do in background()

    }//build_chain2**************************************************





    public class buildChainSendText extends AsyncTask<Void, Void, Void> {

        String messagex;
        long timex;

        public buildChainSendText(String message, long time) {

            super();

            messagex = message;
            timex = time;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {


            final ArrayList<String> data = new ArrayList<>();
            data.add(Integer.toString(MESSAGE_TYPE_TEXT_SENT));//type
            data.add(namex2);//name
            data.add(messagex);//message
            data.add(Integer.toString(SET_MESSAGE_STATUS_CREATED));//status
            data.add(df1.format(new Date(timex)));//date
            data.add("");//image


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        data_messages.add(0, data);
                        adapter.notifyItemInserted(0);

                        recyclerView.scrollToPosition(0);

                    } catch (Error e) {System.out.println("Scrolling...");}

                }//@Override

            });

            return null;

        }//do in background()

    }//build_chain2**************************************************




    public class buildChainSendImage extends AsyncTask<Void, Void, Void> {

        String imagex;
        long timex;

        public buildChainSendImage(String image, long time) {

            super();

            imagex = image;
            timex = time;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {


            final ArrayList<String> data = new ArrayList<>();
            data.add(Integer.toString(MESSAGE_TYPE_IMAGE_SENT));//type
            data.add(namex2);//name
            data.add(imagex);//message
            data.add(Integer.toString(SET_MESSAGE_STATUS_CREATED));//status
            data.add(df1.format(new Date(timex)));//date
            data.add(imagex);//image


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {

                        data_messages.add(0, data);
                        adapter.notifyItemInserted(0);

                        recyclerView.scrollToPosition(0);

                    } catch (Error e) {System.out.println("Scrolling...");}

                }//@Override

            });

            return null;

        }//do in background()

    }//build_chain2**************************************************




    public class buildChainRecv extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {

            String[][] db_messages = messagesx.getNewMessages(Long.toString(last_message_time));

            System.out.println("new_messages " + db_messages[0].length);

            long time_now = System.currentTimeMillis();

            for (int loop1 = 0; loop1 < db_messages[0].length; loop1++) {//********

                final ArrayList<String> data = new ArrayList<>();
                data.add(db_messages[4][loop1]);//type
                data.add(namex2);//name
                data.add(db_messages[3][loop1]);//message
                data.add(Integer.toString(SET_MESSAGE_STATUS_CREATED));//status

                if (time_now - Long.parseLong(db_messages[2][loop1]) > one_day) {

                    data.add(df2.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }
                else {

                    data.add(df1.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }

                //data.add(df1.format(new Date(Long.parseLong(db_messages2[2][loop1]))));//date
                data.add(db_messages[6][loop1]);//image

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //scrolling while updating will cause error.
                        try {

                            data_messages.add(0, data);
                            adapter.notifyItemInserted(0);

                            if (scrollBottom) {

                                scrollDy = 0;
                                recyclerView.scrollToPosition(0);

                            }

                        } catch (Error e) {e.printStackTrace();}

                    }//@Override

                });

                    last_message_time = Long.parseLong(db_messages[2][loop1]);

                    System.out.println("last_message_time " + last_message_time);

            }//for

            if (activityVisible) {

                //Update database this friend's messages are read.
                int updatedx = messagesx.confirmRead(link_id3);
                System.out.println("[TEST] " + updatedx);

                if (updatedx > 0) {

                    //Show connection which friend needs to update.
                    accountsx.setFriendNeedsUpdate(link_id3, 1);

                    //Inform connection we have work to send.
                    //Main.work_ready = true;

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("work_ready", true);
                    editor.commit();

                }//****************

            }

            //new Chat.build_chain_send_sent().execute();

            return null;

        }//do in background()

    }//build_chain2**************************************************




    public class buildChainRecvImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {

            String[][] db_messages = messagesx.getImageMessages(link_id2);

            System.out.println("image_messages " + db_messages[0].length);

            for (int loop1 = 0; loop1 < db_messages[0].length; loop1++) {//********

                final ArrayList<String> data = new ArrayList<>();
                data.add(db_messages[4][loop1]);//type
                data.add(namex2);//name
                data.add(db_messages[3][loop1]);//message
                data.add(Integer.toString(SET_MESSAGE_STATUS_CREATED));//status

                if (Long.parseLong(db_messages[2][loop1]) > one_day) {

                    data.add(df2.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }
                else {

                    data.add(df1.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date

                }

                //data.add(df1.format(new Date(Long.parseLong(db_messages[2][loop1]))));//date
                data.add(db_messages[6][loop1]);//image

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //scrolling while updating will cause error.
                        try {

                            data_messages.add(0, data);
                            adapter.notifyItemInserted(0);

                        } catch (Error e) {e.printStackTrace();}

                    }//@Override

                });

            }//for

            return null;

        }//do in background()

    }//build_chain2**************************************************




    public class buildChainSendSent extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {

            String[][] db_messages = messagesx.getMessagesDESC(link_id2, Integer.toString(data_messages.size()));

            System.out.println("messages " + data_messages.size());
            System.out.println("messages " + db_messages[0].length + " " + data_messages.size());

            if (db_messages[0].length == data_messages.size()) {

                //get items to test
                for (int loop1 = 0; loop1 < data_messages.size(); loop1++) {//********

                    if (data_messages.get(loop1).get(0).equals(Integer.toString(MESSAGE_TYPE_TEXT_SENT)) ||
                            data_messages.get(loop1).get(0).equals(Integer.toString(MESSAGE_TYPE_IMAGE_SENT))) {

                        //System.out.println(data_messages.get(loop1).get(3) + " " + db_messages[5][loop1]);

                        if (data_messages.get(loop1).get(3).equals(Integer.toString(SET_MESSAGE_STATUS_CREATED)) &&
                                              db_messages[5][loop1].equals(Integer.toString(SET_MESSAGE_STATUS_SENT))) {

                            System.out.println(data_messages.get(loop1).get(2) + " " + db_messages[3][loop1]);

                            final ArrayList<String> data = new ArrayList<>();
                            data.add(data_messages.get(loop1).get(0));//type
                            data.add(data_messages.get(loop1).get(1));//name
                            data.add(data_messages.get(loop1).get(2));//message
                            data.add(Integer.toString(SET_MESSAGE_STATUS_SENT));//status
                            data.add(data_messages.get(loop1).get(4));//date
                            data.add(data_messages.get(loop1).get(5));//image
                            //data_messages.add(data);

                            final int loopx1 = loop1;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        data_messages.set(loopx1, data);
                                        adapter.notifyItemChanged(loopx1);

                                    } catch (Error e) {
                                        e.printStackTrace();
                                    }


                                }//@Override

                            });

                        }//if


                        if (data_messages.get(loop1).get(3).equals(Integer.toString(SET_MESSAGE_STATUS_CREATED)) &&
                                                 db_messages[5][loop1].equals(Integer.toString(SET_MESSAGE_STATUS_READ))) {

                            System.out.println(data_messages.get(loop1).get(2) + " " + db_messages[3][loop1]);

                            final ArrayList<String> data = new ArrayList<>();
                            data.add(data_messages.get(loop1).get(0));//type
                            data.add(data_messages.get(loop1).get(1));//name
                            data.add(data_messages.get(loop1).get(2));//message
                            data.add(Integer.toString(SET_MESSAGE_STATUS_READ));//status
                            data.add(data_messages.get(loop1).get(4));//date
                            data.add(data_messages.get(loop1).get(5));//image
                            //data_messages.add(data);

                            final int loopx1 = loop1;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        data_messages.set(loopx1, data);
                                        adapter.notifyItemChanged(loopx1);

                                    } catch (Error e) {
                                        e.printStackTrace();
                                    }


                                }//@Override

                            });

                        }//if


                    }//if

                }//for

            }//if

            return null;

        }//do in background()

    }//build_chain2**************************************************




    public class buildChainSendRead extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

        }


        @Override
        protected Void doInBackground(Void... params) {

            String[][] db_messages = messagesx.getMessagesDESC(link_id2, Integer.toString(data_messages.size()));

            System.out.println("messages " + data_messages.size());
            System.out.println("messages " + db_messages[0].length);

            if (db_messages[0].length == data_messages.size()) {

                //get items to test
                for (int loop1 = 0; loop1 < data_messages.size(); loop1++) {//********

                    if (data_messages.get(loop1).get(0).equals(Integer.toString(MESSAGE_TYPE_TEXT_SENT)) ||
                            data_messages.get(loop1).get(0).equals(Integer.toString(MESSAGE_TYPE_IMAGE_SENT))) {

                        //System.out.println(data_messages.get(loop1).get(3) + " " + db_messages[5][loop1]);

                        if (data_messages.get(loop1).get(3).equals(Integer.toString(SET_MESSAGE_STATUS_SENT)) &&
                                             db_messages[5][loop1].equals(Integer.toString(SET_MESSAGE_STATUS_READ))) {

                            System.out.println(data_messages.get(loop1).get(2) + " " + db_messages[3][loop1]);

                            final ArrayList<String> data = new ArrayList<>();
                            data.add(data_messages.get(loop1).get(0));//type
                            data.add(data_messages.get(loop1).get(1));//name
                            data.add(data_messages.get(loop1).get(2));//message
                            data.add(Integer.toString(SET_MESSAGE_STATUS_READ));//status
                            data.add(data_messages.get(loop1).get(4));//date
                            data.add(data_messages.get(loop1).get(5));//image
                            //data_messages.add(data);

                            final int loopx1 = loop1;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        data_messages.set(loopx1, data);
                                        adapter.notifyItemChanged(loopx1);

                                    } catch (Error e) {
                                        e.printStackTrace();
                                    }


                                }//@Override

                            });

                        }//if

                    }//if

                }//for

            }//if

            return null;

        }//do in background()

    }//build_chain2**************************************************




}//last
