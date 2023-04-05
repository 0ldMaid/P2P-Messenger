package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database_album extends SQLiteOpenHelper {



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

    database_album() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************




    public String[][] getAlbumImages() {

        String images[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {


                System.out.println("Get album images..."  );

                String query = ("SELECT * FROM image_db WHERE type=1 ORDER BY xd DESC");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{});

                cursor.moveToLast();

                images = new String[23][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isBeforeFirst()) {

                    //String t6 = "CREATE TABLE image_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";

                    images[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    images[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    images[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    images[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    images[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                    images[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    images[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                    rowCount++;
                    cursor.moveToPrevious();

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



    public int addImage(
                        String link_id,
                        String date_id,
                        String message,
                        String image,
                        String type,
                        String status
                ){

        int added = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //String t6 = "CREATE TABLE image_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";

                System.out.println("Add to images...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("date_id", date_id);//
                values.put("message", message);//
                values.put("image", image);//
                values.put("type", type);//
                values.put("status", status);//

                //Inserting Row.
                db.insert("image_db", null, values);

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public int updateDescription(
                                String account_xd,
                                String message
                        ){

        int updated = 0;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //String t6 = "CREATE TABLE image_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";

                System.out.println("Add to images...");

                ContentValues values = new ContentValues();
                values.put("message", message);//

                //Inserting Row.
                updated = db.update("image_db", values, "xd=" + account_xd, null);

                System.out.println("Committed the transaction");

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return updated;

    }



    public void deleteImage(String account_xd, String image_id){

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                file_delete_picture deletep = new file_delete_picture();
                deletep.deletePicture(image_id);

                db.execSQL("DELETE FROM image_db WHERE xd=" + account_xd);

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }//*****


        } catch (Exception e) {e.printStackTrace();}


    }



}//class
