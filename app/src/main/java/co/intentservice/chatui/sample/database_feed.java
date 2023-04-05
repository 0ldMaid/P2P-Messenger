package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database_feed extends SQLiteOpenHelper {



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

    database_feed() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************






    public boolean addItem(
            String link_id,
            String hash_id,
            String name_id,
            String avatar_image,
            String date_id,
            String view_type,
            String status,
            String title,
            String description,
            String image1,
            String price,
            String currency,
            String tor_address,
            String pub_key,
            String dapp_id
    ){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Add to feed...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("hash_id", hash_id);//
                values.put("name_id", name_id);//
                values.put("avatar_image", avatar_image);//
                values.put("date_id", date_id);//
                values.put("view_type", view_type);//
                values.put("status", status);//
                values.put("title", title);//
                values.put("description", description);//
                values.put("image1", image1);//
                values.put("price", price);//
                values.put("currency", currency);//
                values.put("tor_address", tor_address);//
                values.put("pub_key", pub_key);//
                values.put("dapp_id", dapp_id);//

                //Inserting Row.
                db.insert("feed_db", null, values);

                System.out.println("Committed the transaction");

                Feed.feed_added.setAdded(true);

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }





    public boolean addStatusItem(
                                String link_id,
                                String hash_id,
                                String name_id,
                                String avatar_image,
                                String date_id,
                                String view_type,
                                String description
                                ){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Add to feed...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("hash_id", hash_id);//
                values.put("name_id", name_id);//
                values.put("avatar_image", avatar_image);//
                values.put("date_id", date_id);//
                values.put("view_type", view_type);//
                values.put("description", description);//

                //Inserting Row.
                db.insert("feed_db", null, values);

                Feed.feed_added.setAdded(true);

                System.out.println("Committed the transaction");

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean addAvatarItem(
                                String link_id,
                                String hash_id,
                                String name_id,
                                String avatar_image,
                                String date_id,
                                String view_type,
                                String image1
                                ){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Add to feed...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("hash_id", hash_id);//
                values.put("name_id", name_id);//
                values.put("avatar_image", avatar_image);//
                values.put("date_id", date_id);//
                values.put("view_type", view_type);//
                values.put("image1", image1);//

                String query2 = ("SELECT image1 FROM feed_db WHERE image1=?");// ORDER BY id ASC
                Cursor cursor2 = db.rawQuery(query2, new String[]{image1});

                if (cursor2.getCount() == 0 && image1.length() > 0) {

                    //Inserting Row.
                    db.insert("feed_db", null, values);

                    //ContentValues values2 = new ContentValues();
                    //values2.put("link_id", link_id);//
                    //int update = db.update("feed_db", values2, "xd=" + item_id, null);

                    Feed.feed_added.setAdded(true);
                    Main.bv_rebuild.setRebuild(true);
                    Chat.bv_read.setRead(true);

                    added = true;

                    System.out.println("Added...");

                }

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean addPictureItem(
                                String link_id,
                                String hash_id,
                                String name_id,
                                String avatar_image,
                                String date_id,
                                String view_type,
                                String image1,
                                String description
                                ){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Add to feed...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("hash_id", hash_id);//
                values.put("name_id", name_id);//
                values.put("avatar_image", avatar_image);//
                values.put("date_id", date_id);//
                values.put("view_type", view_type);//
                values.put("image1", image1);//
                values.put("description", description);//

                String query2 = ("SELECT image1 FROM feed_db WHERE image1=?");// ORDER BY id ASC
                Cursor cursor2 = db.rawQuery(query2, new String[]{image1});

                if (cursor2.getCount() == 0 && image1.length() > 0) {

                    //Inserting Row.
                    db.insert("feed_db", null, values);

                    Feed.feed_added.setAdded(true);
                    Main.bv_rebuild.setRebuild(true);

                    added = true;

                    System.out.println("Added...");

                }

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean addProductItem(
            String link_id,
            String hash_id,
            String title,
            String name_id,
            String avatar_image,
            String date_id,
            String image1,
            String description,
            String price,
            String currency
    ){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Add to feed...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("title", title);//
                values.put("hash_id", hash_id);//
                values.put("name_id", name_id);//
                values.put("avatar_image", avatar_image);//
                values.put("date_id", date_id);//
                values.put("view_type", Feed.FEED_ID_PRODUCT);//
                values.put("image1", image1);//
                values.put("description", description);//
                values.put("price", price);//
                values.put("currency", currency);//
                values.put("status", 0);//

                String query2 = ("SELECT image1 FROM feed_db WHERE image1=?");// ORDER BY id ASC
                Cursor cursor2 = db.rawQuery(query2, new String[]{image1});

                if (cursor2.getCount() == 0 && image1.length() > 0) {

                    //Inserting Row.
                    db.insert("feed_db", null, values);

                    Feed.feed_added.setAdded(true);
                    Main.bv_rebuild.setRebuild(true);

                    added = true;

                    System.out.println("Added...");

                }

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public String[][] getItems() {

        String items[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Messages...");

                String query = ("SELECT * FROM feed_db ORDER BY date_id DESC LIMIT 1000");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{});

                cursor.moveToFirst();

                items = new String[16][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    //String t4 = "CREATE TABLE feed_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, name_id TEXT, avatar_image TEXT, date_id LONG, view_type INTEGER, status INTEGER, title TEXT, description TEXT, image1 TEXT, price TEXT, currency TEXT, tor_address TEXT, pub_key TEXT, dapp_id TEXT)";

                    items[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    items[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    items[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("hash_id"));
                    items[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("name_id"));
                    items[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("avatar_image"));
                    items[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    items[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("view_type"));
                    items[7][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    items[8][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    items[9][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    items[10][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image1"));
                    items[11][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("price"));
                    items[12][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
                    items[13][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("tor_address"));
                    items[14][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("pub_key"));
                    items[15][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("dapp_id"));

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return items;

    }





    public boolean testItemsForImage(String image_id) {

        boolean found = true;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Test images...");

                String query = ("SELECT image1 FROM feed_db WHERE image1='" + image_id + "'");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{});

                if (cursor.getCount() > 0) {
                    found = true;
                }

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return found;

    }





}//class
