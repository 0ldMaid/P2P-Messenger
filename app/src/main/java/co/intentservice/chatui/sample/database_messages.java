package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringWriter;

public class database_messages extends SQLiteOpenHelper {

    JSONParser parser = new JSONParser();


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

    database_messages() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************



    public void deleteMessages(String account_id) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //"CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                db.execSQL("DELETE FROM chat_db WHERE link_id=? AND type!=" + Chat.MESSAGE_TYPE_SYSTEM,
                        new String[]{
                                account_id
                        });// ORDER BY id ASC
                //Cursor cursor = db.rawQuery(query, new String[]{account_id});


                System.out.println("Messages deleted...");

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

    }



    public boolean addMessage(String link_id, long datex, String messagex, int typex, int statusx) {

        System.out.println("link_id      " + link_id);
        System.out.println("date         " + datex);
        System.out.println("message      " + messagex);
        System.out.println("type         " + typex);
        System.out.println("status       " + statusx);

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //"CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                String query = ("SELECT * FROM chat_db WHERE date_id=? AND message=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{Long.toString(datex),messagex});

                System.out.println("Add message... " + cursor.getCount());

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("date_id", datex);//
                values.put("message", messagex);//
                values.put("type", typex);//
                values.put("status", statusx);//

                if (cursor.getCount() == 0) {

                    //Inserting Row.
                    db.insert("chat_db", null, values);
                    added = true;

                }
                else {

                    added = false;

                }

                System.out.println("Committed the transaction " + added);

                //Chat.update_chat = true;



            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean addMessageRemote(String link_id, long datex, String messagex, int typex, int statusx){

        System.out.println("link_id      " + link_id);
        System.out.println("date         " + datex);
        System.out.println("message      " + messagex);
        System.out.println("type         " + typex);
        System.out.println("status       " + statusx);

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //"CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                String query = ("SELECT * FROM chat_db WHERE date_id=? AND message=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{Long.toString(datex),messagex});

                System.out.println("Add message... " + cursor.getCount());

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("date_id", datex);//
                values.put("message", messagex);//
                values.put("type", typex);//
                values.put("status", statusx);//

                if (cursor.getCount() == 0 &&
                        (typex == Chat.MESSAGE_TYPE_TEXT_RECEIVED ||
                                typex == Chat.MESSAGE_TYPE_IMAGE_RECEIVED)) {

                    //Inserting Row.
                    db.insert("chat_db", null, values);
                    added = true;

                }
                else {

                    added = false;

                }

                System.out.println("Committed the transaction " + added);

                //Chat.update_chat = true;



            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public String[][] getMessages(String account_id){

        String messages[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Messages...");

                String query = ("SELECT * FROM chat_db WHERE link_id=? ORDER BY date_id DESC LIMIT 100");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{account_id});

                cursor.moveToFirst();

                messages = new String[7][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    messages[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    messages[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    messages[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    messages[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    messages[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    messages[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    messages[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return messages;

    }



    public String[][] getMessagesDESC(String link_id, String size){

        String messages[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Messages...");

                String query = ("SELECT * FROM chat_db WHERE link_id=? ORDER BY date_id DESC LIMIT ?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{link_id,size});

                cursor.moveToFirst();

                messages = new String[7][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    messages[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    messages[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    messages[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    messages[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    messages[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    messages[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    messages[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image"));

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

        return messages;

    }



    public String[][] getMessagesASC(String link_id, String size){

        String messages[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Messages...");

                String query = ("SELECT * FROM chat_db WHERE link_id=? ORDER BY date_id DESC LIMIT ?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{link_id,size});

                cursor.moveToLast();

                messages = new String[7][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isBeforeFirst()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    messages[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    messages[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    messages[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    messages[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    messages[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    messages[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    messages[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                    rowCount++;
                    cursor.moveToPrevious();

                }//while

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return messages;

    }



    public String[][] getImageMessages(String account_id){

        String messages[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Image Messages...");

                String query = ("SELECT * FROM chat_db WHERE link_id=? AND type=? ORDER BY date_id DESC LIMIT 100");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{account_id,Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED)});

                cursor.moveToFirst();

                messages = new String[7][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    messages[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    messages[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    messages[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    messages[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    messages[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    messages[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    messages[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return messages;

    }



    public String[] getImageMessageList(String account_id){

        String images[] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Image Message List...");

                String query = ("SELECT * FROM chat_db WHERE link_id=? AND status=? ORDER BY date_id DESC LIMIT 100");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query,
                        new String[]{
                            account_id,
                            Integer.toString(Chat.SET_MESSAGE_STATUS_GET_IMAGE)
                        });

                cursor.moveToFirst();

                images = new String[cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    images[rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return images;

    }



    public int setImageMessagesReceived(String account_id, String image_id){

        int messages = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Set image messages received... ");
                System.out.println("account_id  " + account_id);
                System.out.println("image_id    " + image_id);

                ContentValues cv = new ContentValues();
                cv.put("status", Chat.SET_MESSAGE_STATUS_SENT);//2
                messages = db.update("chat_db", cv, "link_id=? AND type=? AND status=? AND message=?",
                        new String[]{
                                account_id,
                                Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED),
                                Integer.toString(Chat.SET_MESSAGE_STATUS_GET_IMAGE),
                                image_id
                        });

                System.out.println("messages " + messages);

                Chat.bv_rebuild.setRebuild(true);

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return messages;

    }



    public int setMessagesRead(String account_id, Long date_id){

        int messages_read = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Show friend got messages... " + account_id);

                Long date_id2 = date_id;
                date_id2 = date_id2 + 1;
                String date_id3 = Long.toString(date_id2);

                ContentValues cv = new ContentValues();
                cv.put("status", Chat.SET_MESSAGE_STATUS_READ);//2
                //messages_read = db.update("chat_db", cv, "link_id=? AND status=1 AND type=0 AND date_id < ?", new String[]{friend_id,date_id3});
                messages_read = db.update("chat_db", cv, "link_id=? AND (type=? OR type=?) AND status=? AND date_id<?",
                        new String[]{
                                account_id,
                                Integer.toString(Chat.MESSAGE_TYPE_TEXT_SENT),
                                Integer.toString(Chat.MESSAGE_TYPE_IMAGE_SENT),
                                Integer.toString(Chat.SET_MESSAGE_STATUS_SENT),
                                date_id3
                        });

                Chat.bv_read.setRead(true);

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return messages_read;

    }



    public String[][] getNewMessages(String last_message_time){

        String messages[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get New Messages..." + last_message_time);

                //String query = ("SELECT * FROM chat_db WHERE date_id>? AND type=1 ORDER BY date_id ASC LIMIT 100");// ORDER BY id ASC
                String query = ("SELECT * FROM chat_db WHERE date_id>? AND (type=? OR type=?) ORDER BY date_id ASC LIMIT 100");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query,
                        new String[]{
                                last_message_time,
                                Integer.toString(Chat.MESSAGE_TYPE_TEXT_RECEIVED),
                                Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED)
                        });

                cursor.moveToFirst();

                messages = new String[7][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    messages[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    messages[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    messages[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    messages[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    messages[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    messages[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    messages[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image"));

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

        return messages;

    }



    public String getUnsentMessages(String link_id){

        String jsonText = "";

        try {

            JSONObject obj0 = new JSONObject();
            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get Messages...");

                //String query = ("SELECT * FROM chat_db WHERE link_id=? AND status=0 AND type=0 ORDER BY date_id ASC LIMIT 100");// ORDER BY id ASC
                String query = ("SELECT * FROM chat_db WHERE link_id=? AND (type=? OR type=?) AND status=? ORDER BY date_id ASC LIMIT 100");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query,
                        new String[]{
                                link_id,
                                Integer.toString(Chat.MESSAGE_TYPE_TEXT_SENT),
                                Integer.toString(Chat.MESSAGE_TYPE_IMAGE_SENT),
                                Integer.toString(Chat.SET_MESSAGE_STATUS_CREATED)
                        });

                cursor.moveToFirst();

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    // String t1 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                    String xdx = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    String link_idx = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    String date_idx = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    String messagex = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    int typex = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
                    String statusx = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    String imagex = cursor.getString(cursor.getColumnIndexOrThrow("image"));

                    //Change type for friend
                    if (typex == Chat.MESSAGE_TYPE_TEXT_SENT)       {typex = Chat.MESSAGE_TYPE_TEXT_RECEIVED;}
                    else if (typex == Chat.MESSAGE_TYPE_IMAGE_SENT) {typex = Chat.MESSAGE_TYPE_IMAGE_RECEIVED;}

                    JSONObject obj1 = new JSONObject();
                    obj1.put("message", messagex);
                    obj1.put("date_id", date_idx);
                    obj1.put("type", Integer.toString(typex));

                    StringWriter out1 = new StringWriter();
                    obj1.writeJSONString(out1);
                    String messageText = out1.toString();

                    obj0.put(Integer.toString(rowCount), messageText);

                    rowCount++;
                    cursor.moveToNext();

                }//while

                cursor.close();

                StringWriter out0 = new StringWriter();
                obj0.writeJSONString(out0);
                jsonText = out0.toString();
                System.out.println(jsonText);


            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return jsonText;

    }



    public long getRead(String friend_id){//friend_id link_id are the same.

        long done = 0l;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get messages I read... " + friend_id);

                //String query = ("SELECT date_id FROM chat_db WHERE link_id=? AND type=1 AND status=2 ORDER BY date_id DESC LIMIT 1");// ORDER BY id ASC
                String query = ("SELECT date_id FROM chat_db WHERE link_id=? AND (type=? OR type=?) AND status=? ORDER BY date_id DESC LIMIT 1");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{
                        friend_id,
                        Integer.toString(Chat.MESSAGE_TYPE_TEXT_RECEIVED),
                        Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED),
                        Integer.toString(Chat.SET_MESSAGE_STATUS_READ)});

                cursor.moveToFirst();

                if (cursor.getCount() == 1) {

                    done = cursor.getLong(cursor.getColumnIndexOrThrow("date_id"));

                }

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }


        } catch (Exception e) {e.printStackTrace();}

        return done;

    }



    public boolean confirmSent(int account_id, String message_list){

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Confirm friend got messages... " + account_id);

                Object obj1 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj1 = parser.parse(message_list);

                } catch (Error e) {System.out.println("Response is unreadable..");}

                //Object obj2 = parser.parse(message);
                JSONObject jsonObject = (JSONObject) obj1;

                System.out.println("jsonObject.size() " + jsonObject.size());

                JSONObject obj = new JSONObject();

                for (int xloop1 = 0; xloop1 < jsonObject.size(); xloop1++) {//***********

                    //Get strings from the JSON string.
                    String json_date = (String) jsonObject.get(Integer.toString(xloop1));
                    System.out.println("json_message " + json_date);

                    ContentValues cv = new ContentValues();
                    cv.put("status", Chat.SET_MESSAGE_STATUS_SENT);
                    //db.update("chat_db", cv, "link_id=" + friend_id + " AND date_id = " + json_date + " AND type=0", null);
                    //db.update("chat_db", cv, "link_id=" + friend_id + " AND date_id = " + json_date + " AND type=0", new String[]{});
                    db.update("chat_db", cv, "link_id=? AND date_id=? AND (type=? OR type=?)",
                            new String[]{Integer.toString(account_id),
                                    json_date,
                                    Integer.toString(Chat.MESSAGE_TYPE_TEXT_SENT),
                                    Integer.toString(Chat.MESSAGE_TYPE_IMAGE_SENT)
                            });

                }

                System.out.println("Committed the transaction");

                Chat.bv_sent.setSent(true);
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



    public int confirmRead(int account_id){

        int done = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Confirm I got messages... " + account_id);

                ContentValues cv = new ContentValues();
                cv.put("status", Chat.SET_MESSAGE_STATUS_READ);
                done =  db.update("chat_db", cv, "link_id=? AND (type=? OR type=?) AND status=?",
                        new String[]{Integer.toString(account_id),
                                Integer.toString(Chat.MESSAGE_TYPE_TEXT_RECEIVED),
                                Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED),
                                Integer.toString(Chat.SET_MESSAGE_STATUS_SENT)
                        });

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return done;

    }




}//class
