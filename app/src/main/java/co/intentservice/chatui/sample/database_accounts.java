package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class database_accounts extends SQLiteOpenHelper {

    private static String[][] accounts = null;
    private static String[][] friendsx = null;
    private static String[][] following = null;

    static int GET_FRIEND_XD = 0;
    static int GET_FRIEND_HASH_ID = 1;
    static int GET_FRIEND_NAME_ID = 2;
    static int GET_FRIEND_DATE_ID = 3;
    static int GET_FRIEND_PUB_KEY = 4;
    static int GET_FRIEND_TOR_ADDRESS = 5;
    static int GET_FRIEND_LAST_MESSAGE = 6;
    static int GET_FRIEND_LAST_MESSAGE_TIME = 7;
    static int GET_FRIEND_NEW_MESSAGES = 8;
    static int GET_FRIEND_UNSENT_MESSAGES = 9;
    static int GET_FRIEND_UPDATE_REQUIRED = 10;
    static int GET_FRIEND_STATUS = 11;
    static int GET_FRIEND_AVATAR_IMAGE = 12;
    static int GET_FRIEND_BACKGROUND_IMAGE = 13;
    static int GET_FRIEND_CONNECTION_ATTEMPTS = 14;

    SharedPreferences settings;

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

    database_accounts() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************





    public String getFriendXD(int friend_id) {

        return friendsx[0][friend_id];

    }

    public String getFriendHashID(int friend_id) {

        return friendsx[1][friend_id];

    }

    public String getFriendNameID(int friend_id) {

        return friendsx[2][friend_id];

    }

    public String getFriendDateID(int friend_id) {

        return friendsx[3][friend_id];

    }

    public String getFriendPubKey(int friend_id) {

        return friendsx[4][friend_id];

    }

    public String getFriendTorAddress(int friend_id) {

        return friendsx[5][friend_id];

    }

    public String getFriendLastMessage(int friend_id) {

        return friendsx[6][friend_id];

    }

    public String getFriendLastMessageTime(int friend_id) {

        return friendsx[7][friend_id];

    }

    public String getFriendNewMessages(int friend_id) {

        return friendsx[8][friend_id];

    }

    public String getFriendUnsentMessages(int friend_id) {

        return friendsx[9][friend_id];

    }

    public String getFriendUpdateRequired(int friend_id) {

        return friendsx[10][friend_id];

    }

    public String getFriendStatus(int friend_id) {

        return friendsx[11][friend_id];

    }

    public String getFriendAvatarImage(int friend_id) {

        return friendsx[12][friend_id];

    }

    public String getFriendBackgroundImage(int friend_id) {

        return friendsx[13][friend_id];

    }

    public int getFriendCount() {

        return friendsx[0].length;

    }

    public String getFriendVersion(int friend_id) {

        return friendsx[17][friend_id];

    }

    public String[][] getFriendsList() {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //String query2 = ("DELETE FROM friends_db WHERE 1=1");
                //db.execSQL(query2);

                //String query = ("SELECT * FROM accounts_db WHERE account_type=1 ORDER BY new_messages DESC");// ORDER BY id ASC
                String query = ("SELECT * FROM accounts_db WHERE account_type=" + Account.SET_DB_FRIEND_INT + " ORDER BY new_messages DESC");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                friendsx = new String[18][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    //String t2 = "CREATE TABLE friends_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, undelivered_messages INTEGER, status INTEGER)";

                    friendsx[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    friendsx[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("hash_id"));
                    friendsx[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("name_id"));
                    friendsx[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    friendsx[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("pub_key"));
                    friendsx[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("tor_address"));
                    friendsx[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_message"));
                    friendsx[7][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_message_time"));
                    friendsx[8][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("new_messages"));
                    friendsx[9][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("unsent_messages"));
                    friendsx[10][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("update_required"));
                    friendsx[11][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    friendsx[12][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("avatar_image"));
                    friendsx[13][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("background_image"));
                    friendsx[14][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
                    friendsx[15][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status_message"));
                    friendsx[16][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_picture_id"));
                    friendsx[17][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("version_id"));

                    System.out.println("Friend ID    " + friendsx[0][rowCount]);
                    System.out.println("Status       " + friendsx[11][rowCount]);
                    System.out.println("account_type " + friendsx[14][rowCount]);
                    System.out.println("key          " + friendsx[4][rowCount]);
                    System.out.println("size         " + friendsx[4][rowCount].length());
                    System.out.println("Version ID   " + friendsx[17][rowCount]);
                    //System.out.println("pub_key      " + base64x);

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();

                //tor_net_client.active_list_b = new boolean[rowCount];

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }//*****


        } catch (Exception e) {e.printStackTrace();}

        return friendsx;

    }



    public String getFollowingXD(int friend_id) {

        return following[0][friend_id];

    }

    public String getFollowingHashID(int friend_id) {

        return following[1][friend_id];

    }

    public String getFollowingNameID(int friend_id) {

        return following[2][friend_id];

    }

    public String getFollowingDateID(int friend_id) {

        return following[3][friend_id];

    }

    public String getFollowingPubKey(int friend_id) {

        return following[4][friend_id];

    }

    public String getFollowingTorAddress(int friend_id) {

        return following[5][friend_id];

    }

    public String getFollowingLastMessage(int friend_id) {

        return following[6][friend_id];

    }

    public String getFollowingLastMessageTime(int friend_id) {

        return following[7][friend_id];

    }

    public String getFollowingNewMessages(int friend_id) {

        return following[8][friend_id];

    }

    public String getFollowingUnsentMessages(int friend_id) {

        return following[9][friend_id];

    }

    public String getFollowingUpdateRequired(int friend_id) {

        return following[10][friend_id];

    }

    public String getFollowingStatus(int friend_id) {

        return following[11][friend_id];

    }

    public String getFollowingAvatarImage(int friend_id) {

        return following[12][friend_id];

    }

    public String getFollowingBackgroundImage(int friend_id) {

        return following[13][friend_id];

    }

    public int getFollowingCount() {

        return following[0].length;

    }

    public String[][] getFollowingList(){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("getFollowingList");

                //String query2 = ("DELETE FROM friends_db WHERE 1=1");
                //db.execSQL(query2);

                String query = ("SELECT * FROM accounts_db WHERE account_type=" + Account.SET_DB_FOLLOWING_INT + " ORDER BY new_messages DESC");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                following = new String[17][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    //String t2 = "CREATE TABLE friends_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, undelivered_messages INTEGER, status INTEGER)";

                    following[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    following[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("hash_id"));
                    following[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("name_id"));
                    following[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    following[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("pub_key"));
                    following[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("tor_address"));
                    following[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_message"));
                    following[7][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_message_time"));
                    following[8][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("new_messages"));
                    following[9][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("unsent_messages"));
                    following[10][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("update_required"));
                    following[11][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    following[12][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("avatar_image"));
                    following[13][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("background_image"));
                    following[14][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
                    following[15][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status_message"));
                    following[16][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_picture_id"));
                    following[17][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("version_id"));

                    System.out.println("Following ID " + following[0][rowCount]);
                    System.out.println("Status       " + following[11][rowCount]);

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();

                //tor_net_client.active_list_b = new boolean[rowCount];

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }//*****


        } catch (Exception e) {e.printStackTrace();}

        return following;

    }



    public String getAccountXD(int friend_id) {

        return accounts[0][friend_id];

    }

    public String getAccountHashID(int friend_id) {

        return accounts[1][friend_id];

    }

    public String getAccountNameID(int friend_id) {

        return accounts[2][friend_id];

    }

    public String getAccountDateID(int friend_id) {

        return accounts[3][friend_id];

    }

    public String getAccountPubKey(int friend_id) {

        return accounts[4][friend_id];

    }

    public String getAccountTorAddress(int friend_id) {

        return accounts[5][friend_id];

    }

    public String getAccountLastMessage(int friend_id) {

        return accounts[6][friend_id];

    }

    public String getAccountLastMessageTime(int friend_id) {

        return accounts[7][friend_id];

    }

    public String getAccountNewMessages(int friend_id) {

        return accounts[8][friend_id];

    }

    public String getAccountUnsentMessages(int friend_id) {

        return accounts[9][friend_id];

    }

    public String getAccountUpdateRequired(int friend_id) {

        return accounts[10][friend_id];

    }

    public String getAccountStatus(int friend_id) {

        return accounts[11][friend_id];

    }

    public String getAccountAvatarImage(int friend_id) {

        return accounts[12][friend_id];

    }

    public String getAccountBackgroundImage(int friend_id) {

        return accounts[13][friend_id];

    }

    public String getAccountType(int friend_id) {

        return accounts[14][friend_id];

    }

    public String getAccountStatusMessage(int friend_id) {

        return accounts[15][friend_id];

    }

    public String getAccountLastPicId(int friend_id) {

        return accounts[16][friend_id];

    }

    public String getAccountLastProductId(int friend_id) {

        return accounts[17][friend_id];

    }

    public int getAccountCount() {

        return accounts[0].length;

    }


    public String[][] getAccountsList() {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //String query2 = ("DELETE FROM friends_db WHERE 1=1");
                //db.execSQL(query2);

                String query = ("SELECT * FROM accounts_db ORDER BY new_messages DESC");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, null);

                cursor.moveToFirst();

                accounts = new String[18][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    //String t2 = "CREATE TABLE friends_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, undelivered_messages INTEGER, status INTEGER)";

                    accounts[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    accounts[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("hash_id"));
                    accounts[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("name_id"));
                    accounts[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    accounts[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("pub_key"));
                    accounts[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("tor_address"));
                    accounts[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_message"));
                    accounts[7][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_message_time"));
                    accounts[8][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("new_messages"));
                    accounts[9][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("unsent_messages"));
                    accounts[10][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("update_required"));
                    accounts[11][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    accounts[12][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("avatar_image"));
                    accounts[13][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("background_image"));
                    accounts[14][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("account_type"));
                    accounts[15][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status_message"));
                    accounts[16][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_picture_id"));
                    accounts[17][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("last_product_id"));

                    System.out.println("Account ID " + accounts[0][rowCount]);
                    System.out.println("Status     " + accounts[11][rowCount]);

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();

                //tor_net_client.active_list_b = new boolean[rowCount];

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }//*****


        } catch (Exception e) {e.printStackTrace();}

        return accounts;

    }



    public int getFriendStatusFromKey(String pub_key){

        int friend_status = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Confirm friend status... " + pub_key);

                String query = ("SELECT status FROM accounts_db WHERE pub_key=?");// ORDER BY id ASC
                //String query = ("SELECT status FROM accounts_db WHERE account_type=" + Main.SET_DB_FRIEND_INT + " AND pub_key=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{pub_key});

                cursor.moveToFirst();

                //String t2 = "CREATE TABLE friends_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, undelivered_messages INTEGER, status INTEGER)";

                int num_status = 0;

                try {

                    num_status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

                } catch (Exception e) {

                    e.printStackTrace();

                }

                System.out.println("status " + num_status);

                friend_status = num_status;

                cursor.close();

                System.out.println("Committed the transaction");

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch(Exception e){
            e.printStackTrace();
        }

        return friend_status;

    }



    public int getAccountType(String pub_key) {

        int account_type = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Confirm friend account... " + pub_key);

                String query = ("SELECT account_type FROM accounts_db WHERE pub_key=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{pub_key});

                cursor.moveToFirst();

                //String t2 = "CREATE TABLE friends_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, undelivered_messages INTEGER, status INTEGER)";

                int num_status = 0;

                try {

                    num_status = cursor.getInt(cursor.getColumnIndexOrThrow("account_type"));

                } catch (Exception e) {

                    e.printStackTrace();

                }

                System.out.println("account type " + num_status);

                account_type = num_status;

                cursor.close();

                System.out.println("Committed the transaction");

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch(Exception e){
            e.printStackTrace();
        }

        return account_type;

    }



    public boolean addFriend(String namex, String idx, String pub_keyx, String addressx, String type){

        System.out.println("namex    " + namex);
        System.out.println("idx      " + idx);
        System.out.println("pub_keyx " + pub_keyx.length());
        System.out.println("addressx " + addressx);
        System.out.println("type     " + type);

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER)";

                System.out.println("Add friend...");

                settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
                String seed_image = settings.getString("seed_icon", "");

                ContentValues values1 = new ContentValues();
                values1.put("name_id", namex);//
                values1.put("avatar_image", seed_image);//
                values1.put("last_message", Main.context2.getResources().getString(R.string.chat_system_message_waiting));//
                values1.put("hash_id", idx);//
                values1.put("pub_key", pub_keyx);//
                values1.put("tor_address", addressx);//
                values1.put("update_required", "100");//
                values1.put("status_message", "");//
                values1.put("status", Account.SET_DB_STATUS_ALLOWED);//
                values1.put("account_type", Account.SET_DB_FRIEND_INT);//
                values1.put("last_product_id", "");//
                values1.put("last_dapps_id", "");//
                values1.put("last_picture_id", "");//last_picture_id

                //Inserting Row.
                long id = db.insert("accounts_db", null, values1);

                System.out.println("Add friend first message...");

                ContentValues values2 = new ContentValues();
                values2.put("link_id", id);//
                values2.put("date_id", System.currentTimeMillis());//
                values2.put("message", Main.context2.getResources().getString(R.string.chat_system_message_waiting));//
                values2.put("type", Chat.MESSAGE_TYPE_SYSTEM);//System message *
                values2.put("status", Chat.SET_MESSAGE_STATUS_READ);//We don't need to send this to our friend.

                //Inserting Row.
                db.insert("chat_db", null, values2);

                System.out.println("Committed the transaction");

                added = true;

                //MainActivity.build_display = true;
                Main.bv_rebuild.setRebuild(true);

                //Inform connection we have work to send.
                //Main.work_ready = true;

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("work_ready", true);
                editor.commit();

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean addFollowing(String namex, String idx, String pub_keyx, String addressx, String type){

        System.out.println("namex    " + namex);
        System.out.println("idx      " + idx);
        System.out.println("pub_keyx " + pub_keyx.length());
        System.out.println("addressx " + addressx);
        System.out.println("type     " + type);

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, date_id LONG, pub_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER)";

                System.out.println("Add following...");

                settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
                String seed_image = settings.getString("seed_icon", "");
                String background_image = settings.getString("user_background", "");

                ContentValues values1 = new ContentValues();
                values1.put("name_id", namex);//
                values1.put("avatar_image", seed_image);//
                values1.put("background_image", background_image);//
                values1.put("last_message", "Waiting for first connection...");//
                values1.put("hash_id", idx);//
                values1.put("pub_key", pub_keyx);//
                values1.put("tor_address", addressx);//
                values1.put("update_required", "100");//
                values1.put("status_message", "");//
                values1.put("status", Account.SET_DB_STATUS_ALLOWED);//
                values1.put("account_type", Account.SET_DB_FOLLOWING_INT);//

                //Inserting Row.
                long id = db.insert("accounts_db", null, values1);

                System.out.println("Committed the transaction");

                added = true;

                //MainActivity.build_display = true;
                Main.bv_rebuild.setRebuild(true);

                //Inform connection we have work to send.
                //Main.work_ready = true;

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("work_ready", true);
                editor.commit();

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean updateFriendName(int account_id, String friend_name){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend name... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("name_id", friend_name);
                db.update("accounts_db", cv,"xd=" + account_id, null);

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                Main.bv_update.setUpdate(true);

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean updateFriendVersion(int account_id, String friend_version){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend version... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("version_id", friend_version);
                db.update("accounts_db", cv,"xd=" + account_id, null);

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                //Main.bv_update.setUpdate(true);

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean updateFriendStatus(int account_id, String friend_status){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend name... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("status_message", friend_status);
                db.update("accounts_db", cv,"xd=" + account_id, null);

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

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



    public boolean updateFriendLastProduct(int account_id, String product_id){

        boolean updated = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend product... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("last_product_id", product_id);
                db.update("accounts_db", cv,"xd=" + account_id, null);

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                updated = true;

            } catch(Exception e) {e.printStackTrace(); updated = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public boolean updateFriendAvatar(String account_id, String friend_avatar){

        boolean updated = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend avatar... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("avatar_image", friend_avatar);
                db.update("accounts_db", cv,"xd=?", new String[]{account_id});

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                updated = true;

            } catch(Exception e) {e.printStackTrace(); updated = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public boolean updateFriendBackground(String account_id, String friend_background){

        boolean updated = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend avatar... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("background_image", friend_background);
                db.update("accounts_db", cv,"xd=?", new String[]{account_id});

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                updated = true;

            } catch(Exception e) {e.printStackTrace(); updated = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public boolean updateFriendAlbum(String account_id, String friend_picture){

        boolean updated = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend avatar... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("last_picture_id", friend_picture);
                db.update("accounts_db", cv,"xd=?", new String[]{account_id});

                //database_accounts getx = new database_accounts();
                //Main.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                updated = true;

            } catch(Exception e) {e.printStackTrace(); updated = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public int updateFriendFeed() {

        int updated = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                ContentValues cv = new ContentValues();
                cv.put("update_required", 1);
                updated = db.update("accounts_db", cv,"1 = 1", new String[]{});

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public int setFriendNeedsUpdate(int account_id, int needs_update){

        int updated = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("update_required", needs_update);

                updated = db.update("accounts_db", cv, "xd=" + account_id, null);

                System.out.println("Committed the transaction");

                //Main.work_ready = true;

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public int updateConnectionTime(String account_id, long date_id){

        int added = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Update friend connection time... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("date_id", date_id);
                added = db.update("accounts_db", cv, "xd=" + account_id, null);

                //If someone we are following, update the status as date.
                //String query = ("SELECT date_id FROM accounts_db WHERE account_type=2 AND xd = ?");// ORDER BY id ASC
                String query = ("SELECT date_id FROM accounts_db WHERE account_type=" + Account.SET_DB_FOLLOWING_INT + " AND xd=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{account_id});

                System.out.println("cursor.getCount() " + cursor.getCount());

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    System.out.println("Update Time >>> " + date_id);

                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    cv.put("last_message", "Last Connection Time: " + df1.format(new Date(date_id)));
                    added = db.update("accounts_db", cv, "xd=" + account_id, null);

                    cursor.moveToNext();

                }

                cursor.close();

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public void delete(String account_xd) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                String query = ("SELECT * FROM accounts_db WHERE xd=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{account_xd});

                cursor.moveToFirst();

                String delete1 = cursor.getString(cursor.getColumnIndexOrThrow("avatar_image"));
                String delete2 = cursor.getString(cursor.getColumnIndexOrThrow("background_image"));

                file_delete_picture deletep = new file_delete_picture();
                deletep.deletePicture(delete1);
                deletep.deletePicture(delete2);

                db.execSQL("DELETE FROM accounts_db WHERE xd=" + account_xd);
                db.execSQL("DELETE FROM chat_db WHERE link_id=" + account_xd);
                db.execSQL("DELETE FROM listings_db WHERE link_id=" + account_xd);
                db.execSQL("DELETE FROM feed_db WHERE link_id=" + account_xd);

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }//*****

        } catch (Exception e) {e.printStackTrace();}

    }



    public boolean confirmFriend(int account_id, String version){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Confirm friend... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("last_message", Main.context2.getResources().getString(R.string.chat_system_message_confirmed2));
                cv.put("date_id", System.currentTimeMillis());//
                cv.put("status", Account.SET_DB_STATUS_FRIEND_CONFIRMED);//friend status
                cv.put("version_id", version);//friend version
                db.update("accounts_db", cv, "xd=?", new String[]{Integer.toString(account_id)});

                System.out.println("Add friend first message...");

                Long date_entered = System.currentTimeMillis();

                ContentValues values = new ContentValues();
                values.put("link_id", account_id);//
                values.put("date_id", date_entered);//
                values.put("message", Main.context2.getResources().getString(R.string.chat_system_message_confirmed));//
                values.put("type", Chat.MESSAGE_TYPE_SYSTEM);//System message *
                values.put("status", Chat.SET_MESSAGE_STATUS_READ);//We don't need to send this to our friend.
                db.insert("chat_db", null, values);

                //database_get_friends_list getx = new database_get_friends_list();
                //MainActivity.friendsx = getx.getFriendsList();

                System.out.println("Committed the transaction");

                Chat.bv_update.setUpdate(true);
                Main.bv_update.setUpdate(true);

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }




}//class
