package co.intentservice.chatui.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

;


public class Edit extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE_PIC = 1;

    //Storage Permissions variables
    private static final int MY_CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    database_products productsx = new database_products();
    tools_encryption hex = new tools_encryption();

    String showid = "";

    Button back;
    Button next;
    Button load;
    Button delete;
    Button save;

    //This is just for testing it won't show.
    ImageView test_image = new ImageView(Main.context2);
    TextView test_load_text_view;

    TextView id;

    EditText currency;
    EditText custom_template;
    EditText custom_1;
    EditText custom_2;
    EditText custom_3;
    EditText item_errors;
    EditText item_date_listed;
    EditText item_date_listed_day;
    EditText item_date_listed_int;
    EditText hits;
    EditText item_confirm_code;
    EditText item_confirmed;
    EditText cost;
    EditText item_description;
    EditText item_id;
    EditText sale_price;
    EditText weight;
    EditText item_listing_id;
    EditText item_notes;
    EditText item_package_d;
    EditText item_package_l;
    EditText item_package_w;
    EditText item_part_number;
    EditText title;
    EditText item_title_url;
    EditText item_type;
    EditText item_search_1;
    EditText item_search_2;
    EditText item_search_3;
    EditText item_seller_id;
    EditText picture_1;
    EditText item_total_on_hand;

    //This is the android Edit screen were the user can edit their tokens.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_item);

        //Here we get all the textfields from the XML file.

        id = (TextView) findViewById(R.id.editTextID);

        title = (EditText) findViewById(R.id.editTextTitle);
        item_part_number = (EditText) findViewById(R.id.editTextPart);
        picture_1 = (EditText) findViewById(R.id.editTextPicURL);
        currency = (EditText) findViewById(R.id.editTextCurrency);
        sale_price = (EditText) findViewById(R.id.editTextPrice);
        item_total_on_hand = (EditText) findViewById(R.id.editTextQuantity);
        weight = (EditText) findViewById(R.id.editTextWeight);
        item_package_d = (EditText) findViewById(R.id.editTextHeight);
        item_package_l = (EditText) findViewById(R.id.editTextLength);
        item_package_w = (EditText) findViewById(R.id.editTextWidth);
        item_description = (EditText) findViewById(R.id.editTextDescription);
        item_notes = (EditText) findViewById(R.id.editTextNotes);
        item_search_1 = (EditText) findViewById(R.id.editTextSearch1);
        item_search_2 = (EditText) findViewById(R.id.editTextSearch2);
        item_search_3 = (EditText) findViewById(R.id.editTextSearch3);
        custom_1 = (EditText) findViewById(R.id.editTextCustom1);
        custom_2 = (EditText) findViewById(R.id.editTextCustom2);
        custom_3 = (EditText) findViewById(R.id.editTextCustom3);

        test_load_text_view = (TextView) findViewById(R.id.textViewTestPicture);

        String sessionId = getIntent().getStringExtra("ID");
        System.out.println("sessionId " + sessionId);
        showid = sessionId;
        id.setText(sessionId);

        back = (Button) findViewById(R.id.buttonPrev);
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                System.out.println("showid " + showid);


            }

        });

        next = (Button) findViewById(R.id.buttonNext);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                System.out.println("showid " + showid);


            }

        });


        load = (Button) findViewById(R.id.buttonLoadPicture);
        load.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                verifyStoragePermissions(Edit.this);

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE_PIC);

            }

        });


        save = (Button) findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                savex();

            }

        });

        new set_fields().execute();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle action bar item clicks here. The action bar will.
        //Automatically handle clicks on the Home/Up button, so long.
        //As you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);

    }



    private void savex() {

        System.out.println("showid " + showid);
        System.out.println("title  " + title.getText());

        productsx.updateProduct(
                showid,
                title.getText().toString(),
                item_part_number.getText().toString(),
                picture_1.getText().toString(),
                currency.getText().toString(),
                //seller name taken from account
                sale_price.getText().toString(),
                item_total_on_hand.getText().toString(),
                weight.getText().toString(),
                item_package_d.getText().toString(),
                item_package_l.getText().toString(),
                item_package_w.getText().toString(),
                item_description.getText().toString(),
                item_notes.getText().toString(),
                item_search_1.getText().toString(),
                item_search_2.getText().toString(),
                item_search_3.getText().toString(),
                custom_1.getText().toString(),
                custom_2.getText().toString(),
                custom_3.getText().toString()
        );

    }



    private class set_fields extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Rebuild Chain...");

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        final String[] db_product = productsx.getProduct(id.getText().toString());

                        title.setText(db_product[3]);
                        item_part_number.setText(db_product[4]);
                        picture_1.setText(db_product[5]);
                        currency.setText(db_product[6]);
                        //seller name taken from account
                        sale_price.setText(db_product[8]);
                        item_total_on_hand.setText(db_product[9]);
                        weight.setText(db_product[10]);
                        item_package_d.setText(db_product[11]);
                        item_package_l.setText(db_product[12]);
                        item_package_w.setText(db_product[13]);
                        item_description.setText(db_product[14]);
                        item_notes.setText(db_product[15]);
                        item_search_1.setText(db_product[16]);
                        item_search_2.setText(db_product[17]);
                        item_search_3.setText(db_product[18]);
                        custom_1.setText(db_product[19]);
                        custom_2.setText(db_product[20]);
                        custom_3.setText(db_product[21]);

                    }//@Override

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            //setImage();

            return null;

        }//do in background()

    }//build_chain2**************************************************






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

                //long timex = System.currentTimeMillis();

                test_load_text_view.setText("Image " + Integer.toString(image_size_width) + " x " + Integer.toString(image_size_height));

                picture_1.setText(base64x);
                savex();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }






}//last
