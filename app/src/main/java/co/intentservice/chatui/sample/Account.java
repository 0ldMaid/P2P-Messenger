package co.intentservice.chatui.sample;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class Account extends AppCompatActivity {

    JSONParser parser = new JSONParser();

    public static final int SET_DB_ADMIN_INT = 0;
    public static final int SET_DB_FRIEND_INT = 1;
    public static final int SET_DB_FOLLOWING_INT = 2;

    public static final int SET_DB_STATUS_BLOCKED = 0;
    public static final int SET_DB_STATUS_ALLOWED = 1;
    public static final int SET_DB_STATUS_FRIEND_CONFIRMED = 2;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    tools_bitmap rcbitmap = new tools_bitmap();
    file_load_bitmap file = new file_load_bitmap();
    database_accounts getx = new database_accounts();
    tools_encryption hex = new tools_encryption();

    private static int RESULT_LOAD_IMAGE_PIC = 1;
    private static int RESULT_LOAD_IMAGE_BACKGROUND = 2;

    public static boolean activityIdle = false;
    private static boolean activityVisible = false;
    private static boolean scrollBottom = false;

    TextView status_info;
    ImageView qr_imagex;
    ImageView qr_imagef;
    TextView send_address;
    TextView send_pub_key;
    LinearLayout layout;

    Timer xtimerx;//class loop.

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_account);

        status_info = (TextView) findViewById(R.id.status_info);
        qr_imagex = (ImageView) findViewById(R.id.qr_image_friend);
        qr_imagef = (ImageView) findViewById(R.id.qr_image_follow);
        send_address = (TextView) findViewById(R.id.send_address);
        send_pub_key = (TextView) findViewById(R.id.send_pub_key);

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();
        status_info.setText(settings.getString("user_name", ""));

        //System.out.println("Address " + Main.settings.getString("server_onion_address", ""));

        //verifyStoragePermissionsx(Account.this);
        //verifyStoragePermissionsCamera(Account.this);

        Button getName = (Button) findViewById(R.id.get_name);
        getName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final EditText taskEditText = new EditText(getWindow().getDecorView().getRootView().getContext());
                AlertDialog dialog = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext())
                        .setTitle("Change display name")
                        .setMessage("Enter new name")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String name = String.valueOf(taskEditText.getText());

                                editor.putString("user_name", name);
                                editor.commit();

                                status_info.setText(name);

                                Main.bv_rebuild.setRebuild(true);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }

        });


        Button getStatus = (Button) findViewById(R.id.get_status);
        getStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                final EditText taskEditText = new EditText(getWindow().getDecorView().getRootView().getContext());
                AlertDialog dialog = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext())
                        .setTitle("Change display status")
                        .setMessage("Enter new status")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String name = String.valueOf(taskEditText.getText());

                                //SharedPreferences.Editor editor = Main.settings.edit();
                                editor.putString("user_status", name);
                                editor.commit();

                                Main.bv_rebuild.setRebuild(true);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }

        });


        Button getBirthday = (Button) findViewById(R.id.get_birthday);
        getBirthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int mYear, mMonth, mDay, mHour, mMinute;

                //final Calendar c = Calendar.getInstance();
                mYear = 1975;
                mMonth = 1;
                mDay = 1;

                DatePickerDialog datePickerDialog = new DatePickerDialog(getWindow().getDecorView().getRootView().getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        System.out.println(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        try {

                            JSONObject obj = new JSONObject();
                            obj.put("day", Integer.toString(dayOfMonth));
                            obj.put("month", Integer.toString((monthOfYear + 1)));
                            obj.put("year", Integer.toString(year));

                            StringWriter out = new StringWriter();
                            obj.writeJSONString(out);
                            String jsonText = out.toString();
                            System.out.println(jsonText);

                            editor.putString("user_birthday", jsonText);
                            editor.commit();

                        } catch (Exception e) {System.out.println("JSON ERROR");}

                    }

                }, mYear, mMonth, mDay);

                datePickerDialog.show();

            }

        });


        Button getRelationshipStatus = (Button) findViewById(R.id.get_relationship_status);
        getRelationshipStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());
                builderSingle.setIcon(R.drawable.ic_launcher);
                //builderSingle.setTitle("Select One Name:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getWindow().getDecorView().getRootView().getContext(), android.R.layout.select_dialog_item);
                arrayAdapter.add("Single");
                arrayAdapter.add("Dating");
                arrayAdapter.add("Married");
                //arrayAdapter.add("");
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

                        System.out.println("Relationship Status: " + strName);

                        editor.putString("user_relationship_status", strName);
                        editor.commit();


                    }
                });
                builderSingle.show();

            }

        });


        Button getJobStatus = (Button) findViewById(R.id.get_job_status);
        getJobStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());
                builderSingle.setIcon(R.drawable.ic_launcher);
                //builderSingle.setTitle("Select One Name:-");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getWindow().getDecorView().getRootView().getContext(), android.R.layout.select_dialog_item);
                arrayAdapter.add("Employed");
                arrayAdapter.add("Unemployed");
                arrayAdapter.add("Retired");
                arrayAdapter.add("Job Hunting");
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

                        System.out.println("Job Status: " + strName);

                        editor.putString("user_job_status", strName);
                        editor.commit();


                    }
                });
                builderSingle.show();

            }

        });


        layout = (LinearLayout) findViewById(R.id.set_background);
        layout.addOnLayoutChangeListener( new View.OnLayoutChangeListener() {

            public void onLayoutChange( View v, int left, int top, int right, int bottom, int leftWas, int topWas, int rightWas, int bottomWas ) {

                int widthWas = rightWas - leftWas; // Right exclusive, left inclusive
                int heightWas = bottomWas - topWas; // Bottom exclusive, top inclusive
                if( v.getHeight() != heightWas || v.getWidth() != widthWas ) {

                    System.out.println("layout.addOnLayoutChangeListener");
                    setImage();

                }

            }

        });

        setImage();


        ImageButton getPic = (ImageButton) findViewById(R.id.get_pic2);
        getPic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //verifyStoragePermissions(Account.this);
                if (verifyStoragePermissionsRead(Account.this)) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE_PIC);

                }

            }

        });


        ImageButton getBackground = (ImageButton) findViewById(R.id.get_background);
        getBackground.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //verifyStoragePermissions(Account.this);
                if (verifyStoragePermissionsRead(Account.this)) {

                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE_BACKGROUND);

                }

            }

        });


        Button getQR = (Button) findViewById(R.id.get_qr_code);
        getQR.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (verifyStoragePermissionsCamera(Account.this)) {

                    IntentIntegrator integrator = new IntentIntegrator(Account.this);
                    integrator.setPrompt("Scan a barcode");
                    //integrator.setCameraId(0);  // Use a specific camera of the device
                    integrator.setOrientationLocked(false);
                    //integrator.setBeepEnabled(true);
                    //integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
                    integrator.initiateScan();

                    //new IntentIntegrator(TransferActivity.this).initiateScan(); // `this` is the current Activity

                }

            }

        });

        Button getTextCode = (Button) findViewById(R.id.get_text_code);
        getTextCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Message", build_json_friend_string());
                clipboard.setPrimaryClip(clip);

                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            }

        });

        Button pasteTextCode = (Button) findViewById(R.id.add_text_code);
        pasteTextCode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String friend_id = (String) clipboard.getText();

                import_contact(friend_id);

            }

        });



        CheckBox followx = (CheckBox) findViewById(R.id.allow_following);

        if (settings.getString("server_onion_address", "").length() > 0) {

            rebuild_qr_image_friend();

            if (settings.getBoolean("following_allowed", false)) {

                followx.setChecked(true);
                rebuild_qr_image_follow();

            }
            else {

                followx.setChecked(false);

            }

        }

        followx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    rebuild_qr_image_follow();

                    editor.putBoolean("following_allowed", true);
                    editor.commit();

                }
                else {

                    qr_imagef.setBackground(ContextCompat.getDrawable(Main.context2, R.drawable.placeholder));

                    editor.putBoolean("following_allowed", false);
                    editor.commit();

                }

            }

        });



        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_QRTask(), 0);

    }//last



    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("STOP");
        //Main.sv_status.setStatus("Android activity idle timeout...");
        //stopService(new Intent(Main.this,MyJobServiceNetwork.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");
        activityIdle = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SHOW");
        activityIdle = false;
    }



    public void onDateSet(DatePicker view, int year, int month, int day) {



    }



    class RemindTask_QRTask extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run() {//********************************


            CheckBox followx = (CheckBox) findViewById(R.id.allow_following);

            //Wait for tor to load.
            while (settings.getString("server_onion_address", "").length() == 0) {

                if (activityIdle) {break;}

                System.out.println("Waiting for tor address...");
                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

            }

            if (settings.getString("server_onion_address", "").length() > 0) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    rebuild_qr_image_friend();

                }//@Override

                });

                if (settings.getBoolean("following_allowed", false)) {

                    followx.setChecked(true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            rebuild_qr_image_follow();

                        }//@Override

                    });

                }
                else {

                    followx.setChecked(false);

                }

            }



        }//runx*********************************************

    }//remindtask




    private String build_json_friend_string() {

        String json_info = "test";

        try {

            JSONObject obj = new JSONObject();

            obj.put("v", Main.versionx);
            obj.put("x", "1");//type
            obj.put("n", settings.getString("user_name", ""));
            obj.put("h", settings.getString("user_id", ""));
            obj.put("p", settings.getString("pub_key_id", ""));
            obj.put("t", settings.getString("server_onion_address", ""));

            //json_info = JSONValue.toJSONString(obj);
            json_info = JSONValue.toJSONString(obj);

            System.out.println("json_info " + json_info);

            byte[] md5_address = MessageDigest.getInstance("MD5").digest(settings.getString("server_onion_address", "").getBytes());
            //String base64x = Base64.toBase64String(md5);
            String base64x_address = Base64.encodeToString(md5_address, Base64.DEFAULT);

            byte[] md5_key = MessageDigest.getInstance("MD5").digest(settings.getString("pub_key_id", "").getBytes());
            //String base64x = Base64.toBase64String(md5);
            String base64x_key = Base64.encodeToString(md5_key, Base64.DEFAULT);

            send_address.setText(getResources().getString(R.string.account_tor_address) + " " + base64x_address);
            send_pub_key.setText(getResources().getString(R.string.account_public_key) + " " + base64x_key);

        } catch (Exception e) {e.printStackTrace();}

        return json_info;

    }





    private void rebuild_qr_image_friend() {

        System.out.println("Rebuild QR image friend...");

        try {

            String json_info = build_json_friend_string();

            System.out.println("json_info " + json_info);

            //Here we are creating the QR code picture for the app.
            ByteArrayOutputStream out = QRCode.from(json_info).to(ImageType.JPG).withSize(1000, 1000).stream();//

            //Save the image.
            try {

                FileOutputStream fout = new FileOutputStream(new File(Main.directory, "/qr_image_friend.png"));

                fout.write(out.toByteArray());

                fout.flush();
                fout.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            //Set the image.
            try {

                System.out.println("Load QR code qr_image.png");
                file_load_bitmap file = new file_load_bitmap();
                Bitmap bitmap = file.getBitmap("qr_image_friend");

                //Set image
                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), bitmap);
                qr_imagex.setBackground(bdrawable);

            } catch (Exception e) {

                System.out.println(e.getMessage());
                //rebuild_qr_image();

            }


        } catch (Exception e) {e.printStackTrace();}


    }




    private void rebuild_qr_image_follow() {

        System.out.println("Rebuild QR image follow...");

        try {

            String json_info = "test";

            JSONObject obj = new JSONObject();

            obj.put("v", Main.versionx);
            obj.put("x", "2");//type
            obj.put("n", settings.getString("user_name", ""));
            obj.put("h", settings.getString("user_id", ""));
            obj.put("p", settings.getString("pub_key_id", ""));
            obj.put("t", settings.getString("server_onion_address", ""));

            json_info = JSONValue.toJSONString(obj);

            System.out.println("json_info " + json_info);

            //Here we are creating the QR code picture for the app.
            ByteArrayOutputStream out = QRCode.from(json_info).to(ImageType.JPG).withSize(1000, 1000).stream();//

            //Save the image.
            try {

                FileOutputStream fout = new FileOutputStream(new File(Main.directory, "/qr_image_follow.png"));

                fout.write(out.toByteArray());

                fout.flush();
                fout.close();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


            //Set the image.
            try {

                System.out.println("Load QR code qr_image_follow.png");
                file_load_bitmap file = new file_load_bitmap();
                Bitmap bitmap = file.getBitmap("qr_image_follow");

                //Set image
                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), bitmap);
                qr_imagef.setBackground(bdrawable);

            } catch (Exception e) {

                System.out.println(e.getMessage());
                //rebuild_qr_image();

            }


        } catch (Exception e) {e.printStackTrace();}


    }




    //persmission method.
    public static boolean verifyStoragePermissionsRead(Activity activity) {

        boolean permission = false;

        // Check if we have read or write permission
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        System.out.println("readPermission   " + readPermission);

        String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }

        if (readPermission == PackageManager.PERMISSION_GRANTED) {permission = true;}

        return permission;

    }


    //persmission method.
    public static boolean verifyStoragePermissionsCamera(Activity activity) {

        boolean permission = false;

        // Check if we have read or write permission
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        System.out.println("cameraPermission " + cameraPermission);

        String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA};

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }

        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {permission = true;}

        return permission;

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
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath), Main.image_size_px, Main.image_size_px, false);

                //tools_get_rounded_corners_bitmap rcbitmap = new tools_get_rounded_corners_bitmap();
                Bitmap bitmap = rcbitmap.getRoundedCircle(resizedBitmap);
                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), bitmap);

                ImageButton button1 = (ImageButton) findViewById(R.id.get_pic1);
                button1.setBackground(bdrawable);

                ImageButton button2 = (ImageButton) findViewById(R.id.get_pic2);
                button2.setBackground(bdrawable);

                ImageButton button3 = (ImageButton) findViewById(R.id.get_pic3);
                button3.setBackground(bdrawable);

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

                file_delete_picture deletex = new file_delete_picture();
                deletex.deletePicture(settings.getString("user_avatar", ""));

                editor.putString("user_avatar", base64x);
                editor.commit();

                Main.bv_rebuild.setRebuild(true);

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }



        if (requestCode == RESULT_LOAD_IMAGE_BACKGROUND && resultCode == RESULT_OK && null != data) {

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

                System.out.println("A1 width  " + bitmapx.getWidth());
                System.out.println("A1 height " + bitmapx.getHeight());

                int image_size_width = bitmapx.getWidth();
                int image_size_height = bitmapx.getHeight();

                if (bitmapx.getWidth() > Main.picture_size_px) {

                    float scalex = ((float) Main.picture_size_px / bitmapx.getWidth());

                    image_size_width = (int) (bitmapx.getWidth() * scalex);
                    image_size_height = (int) (bitmapx.getHeight() * scalex);

                }
                else if (bitmapx.getHeight() > Main.picture_size_px) {

                    float scalex = ((float) Main.picture_size_px / bitmapx.getHeight());

                    image_size_width = (int) (bitmapx.getWidth() * scalex);
                    image_size_height = (int) (bitmapx.getHeight() * scalex);

                }

                System.out.println("AA width  " + image_size_width);
                System.out.println("AA height " + image_size_height);

                Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath), image_size_width, image_size_height, false);

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

                //Delete old image.
                try {

                    String old_base64x = settings.getString("user_background", "");

                    File fdelete = new File(Main.directory, "/" + old_base64x + ".png");

                    if (fdelete.exists()) {

                        if (fdelete.delete()) {
                            System.out.println("file Deleted");
                        } else {
                            System.out.println("file not Deleted");
                        }

                    }

                } catch (Exception e2) {
                    System.out.println(e2.getMessage());
                }

                editor.putString("user_background", base64x);
                editor.commit();

                Main.bv_rebuild.setRebuild(true);
                setImage();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            if (result.getContents() == null) {

                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();

            } else {

                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                import_contact(result.getContents());

            }
        }
        else {

            super.onActivityResult(requestCode, resultCode, data);

        }

    }





    public void setImage() {

        //Set the image.
        try {

            String base64x = settings.getString("user_background", "");

            System.out.println("Load user_background " + base64x);
            //file_load_bitmap file = new file_load_bitmap();
            Bitmap bitmap = file.getBitmap(base64x);//This gets the image

            int width  = layout.getMeasuredWidth();
            int height = layout.getMeasuredHeight();
            System.out.println("width       " + width);
            System.out.println("height      " + height);

            tools_bitmap bitmapt = new tools_bitmap();
            BitmapDrawable bdrawable = bitmapt.getFittedBitmap(bitmap, width, height);

            layout.setBackground(bdrawable);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Set the image.
        try {

            //file_load_bitmap file = new file_load_bitmap();
            Bitmap bitmap = file.getBitmap(settings.getString("user_avatar", ""));

            //Set image
            BitmapDrawable bdrawable1 = new BitmapDrawable(Main.context2.getResources(), bitmap);
            BitmapDrawable bdrawable2 = new BitmapDrawable(Main.context2.getResources(), bitmap);
            BitmapDrawable bdrawable3 = new BitmapDrawable(Main.context2.getResources(), bitmap);

            ImageButton button1 = (ImageButton) findViewById(R.id.get_pic1);
            button1.setBackground(bdrawable1);

            ImageButton button2 = (ImageButton) findViewById(R.id.get_pic2);
            button2.setBackground(bdrawable2);

            ImageButton button3 = (ImageButton) findViewById(R.id.get_pic3);
            button3.setBackground(bdrawable3);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }




    public void import_contact(String string_json) {

        System.out.println("Import Contact...");

        try {

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(string_json);

            } catch (Error e) {e.printStackTrace();}

            JSONObject jsonObject = (JSONObject) obj;

            String version = (String) jsonObject.get("v");//version
            String type = (String) jsonObject.get("x");//type
            String namex = (String) jsonObject.get("n");//name
            String hash_id = (String) jsonObject.get("h");//hash
            String pub_keyx = (String) jsonObject.get("p");//pub_key
            String addressx = (String) jsonObject.get("t");//tor

            System.out.println("version  " + version);
            System.out.println("type     " + type);
            System.out.println("namex    " + namex);
            System.out.println("hash_id  " + hash_id);
            System.out.println("pub_keyx " + pub_keyx);
            System.out.println("addressx " + addressx);

            boolean already_added = false;
            boolean added1 = false;

            String[][] friendsx =  getx.getFriendsList();
            String[][] following =  getx.getFollowingList();

            for (int xloop1 = 0; xloop1 < friendsx[0].length; xloop1++) {//*******

                if (friendsx[4][xloop1].equals(pub_keyx)) {

                    already_added = true;
                    Toast.makeText(getApplicationContext(),"Error! This account is already added as a friend!",Toast.LENGTH_SHORT).show();
                    break;

                }

            }//********************************************************************************

            for (int xloop1 = 0; xloop1 < following[0].length; xloop1++) {//*******

                if (following[4][xloop1].equals(pub_keyx)) {

                    already_added = true;
                    Toast.makeText(getApplicationContext(),"Error! You are already following this user!",Toast.LENGTH_SHORT).show();
                    break;

                }

            }//********************************************************************************

            if (already_added) {

                added1 = false;

            }
            else if (!addressx.contains(".onion")) {

                Toast.makeText(getApplicationContext(),"No Tor address!",Toast.LENGTH_SHORT).show();
                added1 = false;

            }
            else {

                if (type.equals("1")) {

                    //database_account addx = new database_add_account();
                    added1 = getx.addFriend(namex, hash_id, pub_keyx, addressx, type);
                    Toast.makeText(getApplicationContext(), "Friend added!", Toast.LENGTH_SHORT).show();

                }
                else if (type.equals("2")) {

                    //database_add_account addx = new database_add_account();
                    added1 = getx.addFollowing(namex, hash_id, pub_keyx, addressx, type);
                    Toast.makeText(getApplicationContext(), "Following added!", Toast.LENGTH_SHORT).show();

                }

            }

            System.out.println("added? " + added1);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
        }



    }






}//last
