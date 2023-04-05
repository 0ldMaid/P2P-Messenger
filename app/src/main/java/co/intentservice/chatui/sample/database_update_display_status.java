package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.security.MessageDigest;

public class database_update_display_status extends SQLiteOpenHelper {

    static tools_encryption hex = new tools_encryption();

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

    database_update_display_status() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************




    public void getStatusAll(){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            //Get the number of store items we have.
            try {

                String query = ("SELECT xd,hash_id FROM listings_db");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("Listings cursor.getCount() " + cursor.getCount());

                Main.store_items = cursor.getCount();

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}

            try {

                System.out.println("Get new product...");

                String query = ("SELECT * FROM listings_db WHERE item_active=1 ORDER BY xd DESC LIMIT 1");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{});

                cursor.moveToFirst();

                JSONObject obj = new JSONObject();

                obj.put("date_id",  cursor.getString(cursor.getColumnIndexOrThrow("date_id")) );
                obj.put("item_title",  cursor.getString(cursor.getColumnIndexOrThrow("item_title")) );
                obj.put("item_part_number",  cursor.getString(cursor.getColumnIndexOrThrow("item_part_number")) );
                obj.put("item_picture_1",  cursor.getString(cursor.getColumnIndexOrThrow("item_picture_1")) );
                obj.put("currency",  cursor.getString(cursor.getColumnIndexOrThrow("currency")) );
                obj.put("seller_name",  cursor.getString(cursor.getColumnIndexOrThrow("seller_name")) );
                obj.put("item_price",  cursor.getString(cursor.getColumnIndexOrThrow("item_price")) );
                obj.put("item_total_on_hand",  cursor.getString(cursor.getColumnIndexOrThrow("item_total_on_hand")) );
                obj.put("item_weight",  cursor.getString(cursor.getColumnIndexOrThrow("item_weight")) );
                obj.put("item_package_d",  cursor.getString(cursor.getColumnIndexOrThrow("item_package_d")) );
                obj.put("item_package_l",  cursor.getString(cursor.getColumnIndexOrThrow("item_package_l")) );
                obj.put("item_package_w",  cursor.getString(cursor.getColumnIndexOrThrow("item_package_w")) );
                obj.put("item_description",  cursor.getString(cursor.getColumnIndexOrThrow("item_description")) );
                obj.put("item_notes",  cursor.getString(cursor.getColumnIndexOrThrow("item_notes")) );
                obj.put("item_search_1",  cursor.getString(cursor.getColumnIndexOrThrow("item_search_1")) );
                obj.put("item_search_2",  cursor.getString(cursor.getColumnIndexOrThrow("item_search_2")) );
                obj.put("item_search_3",  cursor.getString(cursor.getColumnIndexOrThrow("item_search_3")) );
                obj.put("custom_1",  cursor.getString(cursor.getColumnIndexOrThrow("custom_1")) );
                obj.put("custom_2",  cursor.getString(cursor.getColumnIndexOrThrow("custom_2")) );
                obj.put("custom_3",  cursor.getString(cursor.getColumnIndexOrThrow("custom_3")) );

                Main.last_listing_json = JSONValue.toJSONString(obj);

                byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(Main.last_listing_json.getBytes());
                Main.last_listing_id = hex.bytesToHex(sha256_1);

                cursor.moveToNext();

                cursor.close();

            } catch(Exception e) {}

            //Get the feed count number
            try {

                String query = ("SELECT xd FROM feed_db");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("Feed cursor.getCount() " + cursor.getCount());

                Main.feed_items_new = cursor.getCount();

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}

            //Get details about the last album picture we added.
            try {

                String query = ("SELECT image,message FROM image_db ORDER BY xd DESC");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                System.out.println("Images cursor.getCount() " + cursor.getCount());

                Main.album_items = cursor.getCount();

                if (cursor.getCount() > 0) {

                    cursor.moveToFirst();
                    Main.last_picture_id = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                    Main.last_picture_message_id = cursor.getString(cursor.getColumnIndexOrThrow("message"));

                }

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}

            try {

                System.out.println("Get Status All...");

                //String query1 = ("SELECT * FROM accounts_db WHERE account_type=1");// ORDER BY id ASC
                String query1 = ("SELECT * FROM accounts_db WHERE account_type=?");// ORDER BY id ASC
                Cursor cursor1 = db.rawQuery(query1,  new String[]{Integer.toString(Account.SET_DB_FRIEND_INT)});

                cursor1.moveToFirst();

                int rowCount1 = 0;
                int rowCount2 = 0;
                while (!cursor1.isAfterLast()) {

                    String friend_id = cursor1.getString(cursor1.getColumnIndexOrThrow(cursor1.getColumnName(0)));

                    //String query2 = ("SELECT xd FROM chat_db WHERE type=1 AND status=1 AND link_id = ?");// ORDER BY id ASC
                    String query2 = ("SELECT xd FROM chat_db WHERE link_id=? AND type=? AND status=?");// ORDER BY id ASC
                    Cursor cursor2 = db.rawQuery(query2,
                            new String[]{
                                    friend_id,
                                    Integer.toString(Chat.MESSAGE_TYPE_TEXT_RECEIVED),
                                    Integer.toString(Chat.SET_MESSAGE_STATUS_SENT)
                            });

                    int new_messages = cursor2.getCount();

                    System.out.println("new_messages " + new_messages);

                    if (new_messages > 0) {rowCount1++;}

                    cursor2.close();

                    String last_message = "";

                    try {

                        //String query3 = ("SELECT message FROM chat_db WHERE type=1 AND link_id=" + friend_id + " ORDER BY xd DESC LIMIT 1");// ORDER BY id ASC
                        String query3 = ("SELECT message,type FROM chat_db WHERE link_id=? AND (type=? OR type=? OR type=?) ORDER BY xd DESC LIMIT 1");// ORDER BY id ASC
                        Cursor cursor3 = db.rawQuery(query3,
                                new String[]{
                                        friend_id,
                                        Integer.toString(Chat.MESSAGE_TYPE_SYSTEM),
                                        Integer.toString(Chat.MESSAGE_TYPE_TEXT_RECEIVED),
                                        Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED)
                                });

                        System.out.println("cursor3.getCount() " + cursor3.getCount());

                        if (cursor3.getCount() > 0) {

                            cursor3.moveToFirst();

                            String typex = cursor3.getString(cursor3.getColumnIndexOrThrow("type"));

                            if (typex.equals(Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED))) {

                                last_message = "IMAGE Received";
                                System.out.println("message " + last_message);

                            }
                            else {

                                last_message = cursor3.getString(cursor3.getColumnIndexOrThrow("message"));
                                System.out.println("message " + last_message);

                            }

                        }

                        cursor3.close();

                    } catch(Exception e) {e.printStackTrace();}


                    //String query4 = ("SELECT xd FROM chat_db WHERE type=0 AND status=0 AND link_id=" + friend_id + " ORDER BY xd DESC LIMIT 1");// ORDER BY id ASC
                    String query4 = ("SELECT xd FROM chat_db WHERE link_id=? AND (type=? OR type=?) AND status=? ORDER BY xd DESC LIMIT 1");// ORDER BY id ASC
                    Cursor cursor4 = db.rawQuery(query4,
                            new String[]{
                                    friend_id,
                                    Integer.toString(Chat.MESSAGE_TYPE_TEXT_SENT),
                                    Integer.toString(Chat.MESSAGE_TYPE_IMAGE_SENT),
                                    Integer.toString(Chat.SET_MESSAGE_STATUS_CREATED)
                            });

                    int unsent_messages = cursor4.getCount();
                    System.out.println("unsent_messages " + unsent_messages);

                    if (unsent_messages > 0) {rowCount2++;}

                    cursor4.close();

                    ContentValues cv = new ContentValues();
                    cv.put("unsent_messages", unsent_messages);
                    cv.put("new_messages", new_messages);
                    cv.put("last_message", last_message);
                    if (unsent_messages > 0) {
                        cv.put("update_required", 1);
                    }
                    db.update("accounts_db", cv, "xd=" + friend_id, null);

                    cursor1.moveToNext();

                }//while

                cursor1.close();

                Main.new_messages = rowCount1;
                Main.unsent_messages = rowCount2;

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}


    }//get status





}//class
