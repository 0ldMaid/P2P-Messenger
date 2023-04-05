package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class database_test_key extends SQLiteOpenHelper {



    @Override
    public void onCreate(SQLiteDatabase db) {
        //This will ensure that all tables are created


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This will upgrade tables, adding columns and new tables.
        //Note that existing columns will not be converted


    }


    //Here we add a new .onion address node to our network.

    database_test_key() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************




    public boolean test(){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                String pub_keyx1 = new String("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq4nUfgVrQ+Le2tKcQruW\\/QgCXLRfWoYk\n3OPDWr5C01S67j1j3ntAt99WLiF52Sm+j\\/DRRBCBwS5OzBVUK0OjC9Z2I2qQzN6RkLqPI0fEw84u\ntOQXggc9eSvHdOsDPY3oJkJ+Uw2LggO7jG9j9Jsyo\\/EqLHArZyxaUnbIdMJex1HwDuRA8aIvFTAy\nuQ35yokc0UGt4srflc0oHZfX0VzgNktMq9uK7jWSzTRTsPkXGuHVsHlt2bWG0YsqAj4sekPMucGs\n0z5nIpzxaHBwxyOXnUUf43YCaT1MTSWvAfHNVDpYMglEjZqwBmJSCewtlU07LWkz0oG6i2TWjz6O\nZgGDxwIDAQAB\n    ");
                //String put_keyx2 = new String("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvxt3XJLx72MH+ngfyKYg607y+Bw41Glt\nz4AS1AtMwzewy731h4t0sdbe+oVHyN8PIeOZpdyBrfyozWPSJn9NJ12CJD1SDD75jTXw\/dscQoTk\n4fqrr5IAjs\/W9POLrDyc9h30ZAftn8ZWWyukcOkTmxM3iP0OxNVXqE7\\/vEkm2JNz\\/ljbVsvGVWj9\niDkQFIg5QJSHD+Q0larDnNvhGTZwg2JPfMFgTIDsIATzx5RmZZcP0sei9QfwAxLvO4MXTXtZEeCi\nj9iriDpPEq2t8QZBBgOkxUf5KNAQ86aweMK5v7dj4DI8+xYI1zd1CVT30QQEvdujohIxjmS+11yP\n7f0u6QIDAQAB\n    ");
                //String pub_keyx = Main.settings.getString("pub_key_id", "");

                for (int loop = 0; loop < 100; loop++ ) {

                    try {

                        System.out.println("Add friend...");

                        ContentValues values1 = new ContentValues();
                        values1.put("pub_key", pub_keyx1);//
                        //long id = db.insert("accounts_db", null, values1);

                        System.out.println("Committed the transaction");

                    } catch(Exception e) {e.printStackTrace();}

                    try {

                        String query = ("SELECT status FROM accounts_db WHERE pub_key=?");// ORDER BY id ASC
                        //String query = ("SELECT status FROM accounts_db WHERE account_type=" + Main.SET_DB_FRIEND_INT + " AND pub_key=?");// ORDER BY id ASC
                        Cursor cursor = db.rawQuery(query, new String[]{pub_keyx1});

                        System.out.println("cursor " + cursor.getCount());

                    } catch(Exception e) {e.printStackTrace();}

                }//loop

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return added;

    }




}//class
