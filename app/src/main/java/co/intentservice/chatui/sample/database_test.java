package co.intentservice.chatui.sample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database_test extends SQLiteOpenHelper {



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

    database_test() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************




    public boolean getDatabaseStatus() {

        boolean active = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {


                System.out.println("Get database status..."  );

                String query = ("SELECT xd FROM accounts_db");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{});

                active = true;

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                active = false;
                db.close();
                System.out.println("finally block executed");

            }


        } catch (Exception e) {

            e.printStackTrace();
            System.exit(0);

        }

        return active;

    }


}//class
