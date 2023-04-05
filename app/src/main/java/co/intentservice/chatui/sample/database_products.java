package co.intentservice.chatui.sample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class database_products extends SQLiteOpenHelper {



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

    database_products() {//*************

        super(Main.context2, database_driver.DATABASE_NAME, null, database_driver.DATABASE_VERSION);

    }//**************************************************




    public boolean addProduct(String link_id, String typex){

        System.out.println("link_id      " + link_id);
        System.out.println("type         " + typex);

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //"CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                System.out.println("Add product...");

                ContentValues values = new ContentValues();
                values.put("link_id", link_id);//
                values.put("date_id", System.currentTimeMillis());//
                values.put("item_title", "New Item");//
                values.put("item_description", "Buy my stuff...");//
                values.put("item_price", "1.00");//
                values.put("currency", "BTC");//
                values.put("item_active", "0");//

                //Inserting Row.
                db.insert("listings_db", null, values);

                System.out.println("Committed the transaction");

                Store_Products.bv_update.setUpdate(true);
                Main.bv_rebuild.setRebuild(true);

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public boolean updateProduct(
                                String item_id,
                                String title,
                                String item_part_number,
                                String picture_1,
                                String currency,
                                //seller name taken from account
                                String sale_price,
                                String item_total_on_hand,
                                String weight,
                                String item_package_d,
                                String item_package_l,
                                String item_package_w,
                                String item_description,
                                String item_notes,
                                String item_search_1,
                                String item_search_2,
                                String item_search_3,
                                String custom_1,
                                String custom_2,
                                String custom_3
                        ) {

        boolean added = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                //"CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, type INTEGER, status INTEGER)";

                System.out.println("Update product...");

                ContentValues values = new ContentValues();
                values.put("item_title", title);//
                values.put("item_part_number", item_part_number);//
                values.put("item_picture_1", picture_1);//
                values.put("currency", currency);//
                values.put("item_price", sale_price);//
                values.put("item_total_on_hand", item_total_on_hand);//
                values.put("item_weight", weight);//
                values.put("item_package_d", item_package_d);//
                values.put("item_package_l", item_package_l);//
                values.put("item_package_w", item_package_w);//
                values.put("item_description", item_description);//
                values.put("item_notes", item_notes);//
                values.put("item_search_1", item_search_1);//
                values.put("item_search_2", item_search_2);//
                values.put("item_search_3", item_search_3);//
                values.put("custom_1", custom_1);//
                values.put("custom_2", custom_2);//
                values.put("custom_3", custom_3);//

                //Inserting Row.
                int update = db.update("listings_db", values, "xd=" + item_id, null);

                System.out.println("Committed the transaction " + update);

                Store_Products.bv_update.setUpdate(true);

                added = true;

            } catch(Exception e) {e.printStackTrace(); added = false;}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return added;

    }



    public void setActive(int item_id, boolean active) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                ContentValues values = new ContentValues();

                values.put("item_active", active);//
                int updated = db.update("listings_db", values, "xd=" + item_id, null);

                System.out.println("Committed the transaction " + updated + " " + active);

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

    }



    public String[] getProduct(String id){

        String product[] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {


                System.out.println("Get products...");

                String query = ("SELECT * FROM listings_db WHERE xd=? LIMIT 1");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{id});

                cursor.moveToFirst();

                product = new String[23];

                while (!cursor.isAfterLast()) {

                    //String t4 = "CREATE TABLE listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id LONG, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_date_listed LONG, item_hits TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_out TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_name TEXT, seller_ip TEXT, seller_email TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT)";

                    product[0] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    product[1] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    product[2] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    product[3] = cursor.getString(cursor.getColumnIndexOrThrow("item_title"));
                    product[4] = cursor.getString(cursor.getColumnIndexOrThrow("item_part_number"));
                    product[5] = cursor.getString(cursor.getColumnIndexOrThrow("item_picture_1"));
                    product[6] = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
                    product[7] = cursor.getString(cursor.getColumnIndexOrThrow("seller_name"));
                    product[8] = cursor.getString(cursor.getColumnIndexOrThrow("item_price"));
                    product[9] = cursor.getString(cursor.getColumnIndexOrThrow("item_total_on_hand"));
                    product[10] = cursor.getString(cursor.getColumnIndexOrThrow("item_weight"));
                    product[11] = cursor.getString(cursor.getColumnIndexOrThrow("item_package_d"));
                    product[12] = cursor.getString(cursor.getColumnIndexOrThrow("item_package_l"));
                    product[13] = cursor.getString(cursor.getColumnIndexOrThrow("item_package_w"));
                    product[14] = cursor.getString(cursor.getColumnIndexOrThrow("item_description"));
                    product[15] = cursor.getString(cursor.getColumnIndexOrThrow("item_notes"));
                    product[16] = cursor.getString(cursor.getColumnIndexOrThrow("item_search_1"));
                    product[17] = cursor.getString(cursor.getColumnIndexOrThrow("item_search_2"));
                    product[18] = cursor.getString(cursor.getColumnIndexOrThrow("item_search_3"));
                    product[19] = cursor.getString(cursor.getColumnIndexOrThrow("custom_1"));
                    product[20] = cursor.getString(cursor.getColumnIndexOrThrow("custom_2"));
                    product[21] = cursor.getString(cursor.getColumnIndexOrThrow("custom_3"));

                    String changeb = cursor.getString(cursor.getColumnIndexOrThrow("item_active"));

                    if (changeb.equals("1")) {

                        product[22] = "true";

                    }
                    else {

                        product[22] = "false";

                    }

                    cursor.moveToNext();

                }//while

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return product;

    }



    public String getNewProduct() {

        String product = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {


                System.out.println("Get new product...");

                String query = ("SELECT * FROM listings_db WHERE item_active=1 ORDER BY xd DESC LIMIT 1");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{});

                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    //String t4 = "CREATE TABLE listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id LONG, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_date_listed LONG, item_hits TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_out TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_name TEXT, seller_ip TEXT, seller_email TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT)";

                    JSONObject obj = new JSONObject();

                    //obj.put("xd","1");
                    //obj.put("link_id", Main.program_version);
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

                    //product[0] = cursor.getString(cursor.getColumnIndex("xd"));
                    //product[1] = cursor.getString(cursor.getColumnIndex("link_id"));
                    //product[2] = cursor.getString(cursor.getColumnIndex("date_id"));
                    //product[3] = cursor.getString(cursor.getColumnIndex("item_title"));
                    //product[4] = cursor.getString(cursor.getColumnIndex("item_part_number"));
                    //product[5] = cursor.getString(cursor.getColumnIndex("item_picture_1"));
                    //product[6] = cursor.getString(cursor.getColumnIndex("currency"));
                    //product[7] = cursor.getString(cursor.getColumnIndex("seller_name"));
                    //product[8] = cursor.getString(cursor.getColumnIndex("item_price"));
                    //product[9] = cursor.getString(cursor.getColumnIndex("item_total_on_hand"));
                    //product[10] = cursor.getString(cursor.getColumnIndex("item_weight"));
                    //product[11] = cursor.getString(cursor.getColumnIndex("item_package_d"));
                    //product[12] = cursor.getString(cursor.getColumnIndex("item_package_l"));
                    //product[13] = cursor.getString(cursor.getColumnIndex("item_package_w"));
                    //product[14] = cursor.getString(cursor.getColumnIndex("item_description"));
                    //product[15] = cursor.getString(cursor.getColumnIndex("item_notes"));
                    //product[16] = cursor.getString(cursor.getColumnIndex("item_search_1"));
                    //product[17] = cursor.getString(cursor.getColumnIndex("item_search_2"));
                    //product[18] = cursor.getString(cursor.getColumnIndex("item_search_3"));
                    //product[19] = cursor.getString(cursor.getColumnIndex("custom_1"));
                    //product[20] = cursor.getString(cursor.getColumnIndex("custom_2"));
                    //product[21] = cursor.getString(cursor.getColumnIndex("custom_3"));

                    product = JSONValue.toJSONString(obj);

                    cursor.moveToNext();

                }//while

                cursor.close();

            } catch(Exception e) {e.printStackTrace();}
            finally {

                db.close();
                System.out.println("finally block executed");

            }

        } catch (Exception e) {e.printStackTrace();}

        return product;

    }



    public String[][] getProducts(int limit){

        String products[][] = null;

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                System.out.println("Get products...");

                String query = ("SELECT * FROM listings_db ORDER BY date_id DESC LIMIT ?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{Integer.toString(limit)});

                cursor.moveToFirst();

                products = new String[23][cursor.getCount()];

                int rowCount = 0;
                while (!cursor.isAfterLast()) {

                    //String t4 = "CREATE TABLE listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, hash_id TEXT, sig_id TEXT, date_id LONG, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_date_listed LONG, item_hits TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_out TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_name TEXT, seller_ip TEXT, seller_email TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT)";

                    products[0][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("xd"));
                    products[1][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("link_id"));
                    products[2][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("date_id"));
                    products[3][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_title"));
                    products[4][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_part_number"));
                    products[5][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_picture_1"));
                    products[6][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
                    products[7][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("seller_name"));
                    products[8][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_price"));
                    products[9][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_total_on_hand"));
                    products[10][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_weight"));
                    products[11][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_package_d"));
                    products[12][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_package_l"));
                    products[13][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_package_w"));
                    products[14][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_description"));
                    products[15][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_notes"));
                    products[16][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_search_1"));
                    products[17][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_search_2"));
                    products[18][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("item_search_3"));
                    products[19][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("custom_1"));
                    products[20][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("custom_2"));
                    products[21][rowCount] = cursor.getString(cursor.getColumnIndexOrThrow("custom_3"));

                    String changeb = cursor.getString(cursor.getColumnIndexOrThrow("item_active"));

                    if (changeb.equals("1")) {

                        products[22][rowCount] = "true";

                    }
                    else {

                        products[22][rowCount] = "false";

                    }

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

        return products;

    }



    public void delete(String account_xd) {

        try {

            SQLiteDatabase db = this.getWritableDatabase();

            try {

                String query = ("SELECT * FROM listings_db WHERE xd=?");// ORDER BY id ASC
                Cursor cursor = db.rawQuery(query, new String[]{account_xd});

                if (cursor.getCount() > 0) {

                    cursor.moveToFirst();

                    String delete1 = cursor.getString(cursor.getColumnIndexOrThrow("item_picture_1"));

                    file_delete_picture deletep = new file_delete_picture();
                    deletep.deletePicture(delete1);

                }

                db.execSQL("DELETE FROM listings_db WHERE xd=" + account_xd);

                Store_Products.bv_update.setUpdate(true);
                Main.bv_update.setUpdate(true);

            } catch (Exception e) {e.printStackTrace();}
            finally {

                db.close();
                //System.out.println("finally block executed");

            }//*****

        } catch (Exception e) {e.printStackTrace();}

    }



}//class
