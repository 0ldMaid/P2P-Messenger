package co.intentservice.chatui.sample;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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


public class Album extends AppCompatActivity implements recycler_view_album.ItemClickListener{

    private static boolean activityVisible;
    private static boolean scrollBottom = true;

    private static int RESULT_LOAD_IMAGE_PIC = 1;
    private static int RESULT_LOAD_IMAGE_BACKGROUND = 2;

    // Storage Permissions variables
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    static boolean_variable_main_update bv_update = new boolean_variable_main_update();

    tools_encryption hex = new tools_encryption();
    file_save_picture savex = new file_save_picture();
    //database_add_album_image image = new database_add_album_image();
    database_album albumx = new database_album();
    file_load_bitmap loadb = new file_load_bitmap();

    RecyclerView recyclerView;
    recycler_view_album adapter;

    DateFormat df1 = new SimpleDateFormat("HH:mm", Locale.getDefault());
    DateFormat df2 = new SimpleDateFormat("dd/MM", Locale.getDefault());

    String[][] db_images;

    SwipeRefreshLayout layout;
    ArrayList<ArrayList<String>> data_messages = new ArrayList<>();

    TextView textView1;
    TextView textView2;

    private long one_day = 86400000l;

    static boolean acitivty_paused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_my_album);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        //editor = settings.edit();

        //chat_view_main = (LinearLayout) findViewById(R.id.chat_view_main);
        //chatView = (ChatView) findViewById(R.id.chat_view);
        Button addItem = (Button) findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String t6 = "CREATE TABLE image_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";

                verifyStoragePermissions(Album.this);

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_PIC);

            }
        });


        try {

            db_images = albumx.getAlbumImages();

            System.out.println("Products: " + db_images[0].length);

            // set up the RecyclerView
            recyclerView = (RecyclerView) findViewById(R.id.reyclerview_product_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            layoutManager.setStackFromEnd(true);
            layoutManager.setReverseLayout(true);
            adapter = new recycler_view_album(this, data_messages);
            adapter.setClickListener(this);
            adapter.setAvatarImage(loadb.getBitmap(settings.getString("user_avatar", "")));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

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
            e.printStackTrace();
        }

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(settings.getString("user_name", "") + "'s Photo Album");

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("Add photos for your friends to see!");

        new rebuild_chain().execute();


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

                //Show.
                new rebuild_chain().execute();

            }

        });

        //We listen for updates and make them seen.
        bv_update.setListener(new boolean_variable_main_update.ChangeListener() {
            @Override
            public void onChange() {

                new rebuild_chain().execute();

            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SHOW");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");
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



    @Override
    public void onItemClick(View view, int position) {

        System.out.println("CLICK 1");

    }

    @Override
    public void onImageItemClick(View view, int position) {

        System.out.println("CLICK 2 " + position);

        final int postionx = position;

        final EditText taskEditText = new EditText(getWindow().getDecorView().getRootView().getContext());
        AlertDialog dialog = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext())
                .setTitle("Change display description")
                .setMessage("Enter new description")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String description = String.valueOf(taskEditText.getText());

                        albumx.updateDescription(db_images[0][postionx],description);

                        new rebuild_chain().execute();

                        InputMethodManager imm = (InputMethodManager)getSystemService(Main.context2.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(taskEditText.getWindowToken(), 0);

                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

    @Override
    public void onCheckItemClick(boolean checked, int position) {

        System.out.println("CLICK BOX");

        //System.out.println(checked + " " + db_products[0][position]);

        //updatep.setActive(Integer.parseInt(db_products[0][position]), checked);

    }

    @Override
    public void onItemLongPress(View view, int position) {

        System.out.println("CLICK 3");

        final int positionx = position;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want delete your picture?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //Do nothing but close the dialog

                albumx.deleteImage(db_images[0][positionx],db_images[4][positionx]);

                new rebuild_chain().execute();

                Main.bv_rebuild.setRebuild(true);

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

        android.app.AlertDialog alert = builder.create();
        alert.show();


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

                Bitmap myBitmap = BitmapFactory.decodeFile(picturePath);

                final int maxSize = (int) Main.picture_size_px;

                int inWidth = myBitmap.getWidth();
                int inHeight = myBitmap.getHeight();

                System.out.println("inWidth  " + inWidth);
                System.out.println("inHeight " + inHeight);

                float scalex = (float) 1.0f;

                if (inWidth > maxSize) {

                    scalex = (float) maxSize / inWidth;

                }
                else if (inHeight > maxSize) {

                    scalex = (float) maxSize / inHeight;

                }

                int outWidth = (int) (inWidth * scalex);
                int outHeight = (int) (inHeight * scalex);

                System.out.println("scale     " + scalex);
                System.out.println("outWidth  " + outWidth);
                System.out.println("outHeight " + outHeight);

                //Set image
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(myBitmap, outWidth, outHeight, false);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                //bitmap.recycle();

                byte[] md5 = MessageDigest.getInstance("MD5").digest(byteArray);
                //String base64x = Base64.toBase64String(md5);
                String base64x = Base64.encodeToString(md5, Base64.DEFAULT);

                base64x = hex.bytesToHex(base64x.getBytes());

                System.out.println("base64x " + base64x);

                savex.savePicture(base64x,resizedBitmap);

                albumx.addImage("",Long.toString(System.currentTimeMillis()),"Add description here...",base64x,"1","1");

                new rebuild_chain().execute();

                Main.bv_rebuild.setRebuild(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


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

                db_images = albumx.getAlbumImages();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //rebuild everything.
                        int size = data_messages.size();
                        if (size > 0) {
                            for (int loop = 0; loop < size; loop++) {
                                data_messages.remove(0);
                            }

                            adapter.notifyItemRangeRemoved(0, size);
                        }

                        System.out.println("db_images.length " + db_images[0].length);

                        long time_now = System.currentTimeMillis();

                        for (int loop1 = 0; loop1 < db_images[0].length; loop1++) {//********

                            System.out.println(db_images[0][loop1]);
                            System.out.println(db_images[1][loop1]);
                            System.out.println(db_images[2][loop1]);
                            System.out.println(db_images[3][loop1]);

                            ArrayList<String> data = new ArrayList<>();
                            data.add(settings.getString("user_name", "") + " has added a new picture...");//title

                            if ((time_now - Long.parseLong(db_images[2][loop1])) > one_day) {

                                data.add(df2.format(new Date(Long.parseLong(db_images[2][loop1]))));//date

                            }
                            else {

                                data.add(df1.format(new Date(Long.parseLong(db_images[2][loop1]))));//date

                            }

                            data.add(db_images[4][loop1]);//image
                            data.add(db_images[3][loop1]);//image
                            data_messages.add(data);

                            //System.out.println("db_products[22][loop1] " + db_images[22][loop1]);

                        }//******************************************************************

                        // set up the RecyclerView
                        //adapter.add(data_messages);

                        //recyclerView.addAll(0, data_messages);

                        recyclerView.getRecycledViewPool().clear();
                        adapter.notifyDataSetChanged();

                        adapter.notifyItemRangeInserted(0, data_messages.size());
                        adapter.notifyItemChanged(data_messages.size());

                        layout.setRefreshing(false);


                    }//@Override

                });

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            //setImage();

            return null;

        }//do in background()

    }//build_chain2**************************************************



}
