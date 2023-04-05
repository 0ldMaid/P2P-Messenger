package co.intentservice.chatui.sample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class Store_Products extends AppCompatActivity implements recycler_view_store.ItemClickListener{

    private static boolean activityVisible;
    private static boolean scrollBottom = true;

    static boolean_variable_main_update bv_update = new boolean_variable_main_update();

    database_products productsx = new database_products();
    file_load_bitmap loadb = new file_load_bitmap();

    RecyclerView recyclerView;
    recycler_view_store adapter;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

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
        setContentView(R.layout.activity_my_store_products);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        //chat_view_main = (LinearLayout) findViewById(R.id.chat_view_main);
        //chatView = (ChatView) findViewById(R.id.chat_view);
        Button addItem = (Button) findViewById(R.id.add_item);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                database_products product = new database_products();
                product.addProduct("1","0");

            }
        });


        try {

            db_products = productsx.getProducts(100);

            System.out.println("Products: " + db_products[0].length);

            // set up the RecyclerView
            recyclerView = (RecyclerView) findViewById(R.id.reyclerview_product_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            //layoutManager.setStackFromEnd(true);
            //layoutManager.setReverseLayout(true);
            adapter = new recycler_view_store(this, data_messages);
            adapter.setClickListener(this);
            adapter.setAvatarImage(loadb.getBitmap(settings.getString("user_avatar", "")));
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
            e.printStackTrace();
        }

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(settings.getString("user_name", "") + "'s Inventory");

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("Add products for your friends to buy");

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
        Store_Products.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("HIDE");
        Store_Products.activityPaused();
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

        System.out.println("CLICK 2");

        Intent i = new Intent(getBaseContext(), Edit.class);
        i.putExtra("ID", db_products[0][position]);
        startActivity(i);

    }

    @Override
    public void onCheckItemClick(boolean checked, int position) {

        System.out.println("CLICK BOX");

        System.out.println(checked + " " + db_products[0][position]);

        productsx.setActive(Integer.parseInt(db_products[0][position]), checked);

    }

    @Override
    public void onItemLongPress(View view, int position) {

        final String product_id = adapter.getID(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getWindow().getDecorView().getRootView().getContext());

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want delete this product?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //Do nothing but close the dialog

                System.out.println("Delete friend_id " + product_id);

                productsx.delete(product_id);

                //new Store_Products.rebuild_chain().execute();

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

                db_products = productsx.getProducts(100);

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

                        System.out.println("db_products[0].length " + db_products[0].length);

                        for (int loop1 = 0; loop1 < db_products[0].length; loop1++) {//********

                            ArrayList<String> data = new ArrayList<>();
                            data.add(db_products[0][loop1]);//id
                            data.add(settings.getString("user_name", "") + " is selling " + db_products[3][loop1]);//title
                            data.add(df2.format(new Date(Long.parseLong(db_products[2][loop1]))));//date
                            data.add(db_products[5][loop1]);//image
                            data.add(db_products[14][loop1]);//description
                            data.add(db_products[8][loop1] + db_products[6][loop1]);//price
                            data.add(db_products[22][loop1]);//active
                            data_messages.add(data);

                            System.out.println("db_products[22][loop1] " + db_products[22][loop1]);

                        }//******************************************************************

                        // set up the RecyclerView
                        //adapter.add(data_messages);

                        //recyclerView.addAll(0, data_messages);

                        recyclerView.getRecycledViewPool().clear();
                        adapter.notifyDataSetChanged();

                        adapter.notifyItemRangeInserted(0, data_messages.size());
                        adapter.notifyItemChanged(data_messages.size());

                        recyclerView.scrollToPosition(0);

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
