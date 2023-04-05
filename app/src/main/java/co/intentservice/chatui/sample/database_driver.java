package co.intentservice.chatui.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class database_driver extends SQLiteOpenHelper{

    KeyPair keyPair;

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    //All Static variables
    //Database Version
    static final int DATABASE_VERSION = 4;

    //Database Name
    static final String DATABASE_NAME = Main.idx;

    //This is the database drive start up class. Also if the app is just installed it will install the database tables.

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        //This is the friends databaseg. status 0 = rejected, 1 = request sent, 2 = accepted
        String t1 = "CREATE TABLE accounts_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, prv_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, update_required INTEGER, status INTEGER, status_message TEXT, relationship_status TEXT, birthday_status TEXT, job_status TEXT, last_product_id TEXT, last_dapps_id TEXT, account_type INTEGER, last_picture_id TEXT, version_id TEXT)";
        db.execSQL(t1);

        //This is the chat database. status: 0 = created 1 = sent 2 = read
        String t2 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";
        db.execSQL(t2);

        //This is the listings database that stores the info for each listing.
        String t3 = "CREATE TABLE listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, sig_id TEXT, date_id LONG, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_active BOOLEAN, item_date_listed LONG, item_hits TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_out TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_name TEXT, seller_ip TEXT, seller_email TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT)";
        db.execSQL(t3);

        //This is the feed database.
        String t4 = "CREATE TABLE feed_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, name_id TEXT, avatar_image TEXT, date_id LONG, view_type INTERGER, status INTEGER, title TEXT, description TEXT, image1 TEXT, price TEXT, currency TEXT, tor_address TEXT, pub_key TEXT, dapp_id TEXT)";
        db.execSQL(t4);

        //This is the dapps database.
        String t5 = "CREATE TABLE dapps_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, name_id TEXT, avatar_image TEXT, date_id LONG, view_type INTERGER, status INTEGER, title TEXT, description TEXT, image1 TEXT, price TEXT, currency TEXT, tor_address TEXT, pub_key TEXT, dapp_id TEXT)";
        db.execSQL(t5);

        //This is the chat database. status: 0 = created 1 = sent 2 = read
        String t6 = "CREATE TABLE image_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";
        db.execSQL(t6);

        DateFormat dateFormatx = new SimpleDateFormat("yyyyMMddHHmmss");
        Date datex = new Date();
        System.out.println(dateFormatx.format(datex));

        tools_encryption hex = new tools_encryption();

        //Here we get a new set of keys for the user. If they have their own they can enter them later.
        try {

            //RSA keys are easy for web developers to use.
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //after non intensive search 2048 seems to be ok for now.
            kpg.initialize(2048);
            keyPair = kpg.genKeyPair();

            //System.out.println("privateKey Base 64: " + Base64.toBase64String(keyPair.getPrivate().getEncoded()));
            //System.out.println("Public Base 64:     " + Base64.toBase64String(keyPair.getPublic().getEncoded()));

            System.out.println("privateKey Base 64: " + Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT));
            System.out.println("Public Base 64:     " + Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT));

            //byte[] clear = Base64.decode(Base64.toBase64String(keyPair.getPrivate().getEncoded()));
            byte[] clear = keyPair.getPrivate().getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec priv = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            RSAPublicKeySpec keySpecx = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));
            PublicKey publicKey = kf.generatePublic(keySpecx);

            //String base64 = Base64.toBase64String(keyPair.getPublic().getEncoded());
            //byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(base64.getBytes());
            //String base64x = Base64.toBase64String(sha256_1);

            //String base64 = Base64.toBase64String(keyPair.getPublic().getEncoded());
            byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(keyPair.getPublic().getEncoded());
            //String base = Base64.encodeToString(sha256_1, Base64.DEFAULT);
            String base = hex.bytesToHex(sha256_1);

            //MainActivity.prv_key_id = Base64.toBase64String(keyPair.getPrivate().getEncoded());
            //MainActivity.pub_key_id = Base64.toBase64String(keyPair.getPublic().getEncoded());
            //Main.prv_key_id = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);
            //Main.pub_key_id = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);

            //Main.prv_key_id = hex.bytesToHex(keyPair.getPrivate().getEncoded());
            //Main.pub_key_id = hex.bytesToHex(keyPair.getPublic().getEncoded());

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("prv_key_id", hex.bytesToHex(keyPair.getPrivate().getEncoded()));
            editor.putString("pub_key_id", hex.bytesToHex(keyPair.getPublic().getEncoded()));
            editor.putString("user_id", base);
            editor.putString("user_name", "John Smith");
            editor.putString("user_status", "Click here to edit your account and add new friends");
            editor.commit();

        } catch (Exception e) {e.printStackTrace();}

        Main.databaseInstalled = true;

        System.out.println("DBsx");

    }//***************************************






    //Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        db.execSQL("DROP TABLE IF EXISTS accounts_db");
        db.execSQL("DROP TABLE IF EXISTS chat_db");
        db.execSQL("DROP TABLE IF EXISTS listings_db");
        db.execSQL("DROP TABLE IF EXISTS feed_db");
        db.execSQL("DROP TABLE IF EXISTS dapps_db");
        db.execSQL("DROP TABLE IF EXISTS image_db");

        //This is the friends databaseg. status 0 = rejected, 1 = request sent, 2 = accepted
        String t1 = "CREATE TABLE accounts_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, hash_id TEXT, name_id TEXT, avatar_image TEXT, background_image TEXT, date_id LONG, pub_key TEXT, prv_key TEXT, tor_address TEXT, last_message TEXT, last_message_time LONG, new_messages INTEGER, unsent_messages INTEGER, update_required INTEGER, status INTEGER, status_message TEXT, relationship_status TEXT, birthday_status TEXT, orientation_status TEXT, last_product_id TEXT, last_dapps_id TEXT, account_type INTEGER, last_picture_id TEXT, version_id TEXT)";
        db.execSQL(t1);

        //This is the chat database. status: 0 = created 1 = sent 2 = read
        String t2 = "CREATE TABLE chat_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";
        db.execSQL(t2);

        //This is the listings database that stores the info for each listing.
        String t3 = "CREATE TABLE listings_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, sig_id TEXT, date_id LONG, owner_id TEXT, owner_rating TEXT, currency TEXT, custom_template TEXT, custom_1 TEXT, custom_2 TEXT, custom_3 TEXT, item_active BOOLEAN, item_date_listed LONG, item_hits TEXT, item_cost TEXT, item_description TEXT, item_id TEXT, item_price TEXT, item_weight TEXT, item_notes TEXT, item_package_d TEXT, item_package_l TEXT, item_package_w TEXT, item_part_number TEXT, item_title TEXT, item_type TEXT, item_search_1 TEXT, item_search_2 TEXT, item_search_3 TEXT, item_picture_1 TEXT, item_total_on_hand TEXT, sale_payment_address TEXT, sale_payment_type TEXT, sale_tax TEXT, sale_shipping_company TEXT, sale_shipping_out TEXT, seller_address_1 TEXT, seller_address_2 TEXT, seller_address_city TEXT, seller_address_state TEXT, seller_address_zip TEXT, seller_address_country TEXT, seller_id TEXT, seller_name TEXT, seller_ip TEXT, seller_email TEXT, seller_notes TEXT, seller_phone TEXT, seller_logo TEXT)";
        db.execSQL(t3);

        //This is the feed database.
        String t4 = "CREATE TABLE feed_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, name_id TEXT, avatar_image TEXT, date_id LONG, view_type INTERGER, status INTEGER, title TEXT, description TEXT, image1 TEXT, price TEXT, currency TEXT, tor_address TEXT, pub_key TEXT, dapp_id TEXT)";
        db.execSQL(t4);

        //This is the dapps database.
        String t5 = "CREATE TABLE dapps_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, hash_id TEXT, name_id TEXT, avatar_image TEXT, date_id LONG, view_type INTERGER, status INTEGER, title TEXT, description TEXT, image1 TEXT, price TEXT, currency TEXT, tor_address TEXT, pub_key TEXT, dapp_id TEXT)";
        db.execSQL(t5);

        //This is the chat database. status: 0 = created 1 = sent 2 = read
        String t6 = "CREATE TABLE image_db (xd INTEGER PRIMARY KEY AUTOINCREMENT, link_id TEXT, date_id LONG, message TEXT, image TEXT, type INTEGER, status INTEGER)";
        db.execSQL(t6);

        DateFormat dateFormatx = new SimpleDateFormat("yyyyMMddHHmmss");
        Date datex = new Date();
        System.out.println(dateFormatx.format(datex));

        tools_encryption hex = new tools_encryption();

        //Here we get a new set of keys for the user. If they have their own they can enter them later.
        try {

            //RSA keys are easy for web developers to use.
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //after non intensive search 2048 seems to be ok for now.
            kpg.initialize(2048);
            keyPair = kpg.genKeyPair();

            //System.out.println("privateKey Base 64: " + Base64.toBase64String(keyPair.getPrivate().getEncoded()));
            //System.out.println("Public Base 64:     " + Base64.toBase64String(keyPair.getPublic().getEncoded()));

            System.out.println("privateKey Base 64: " + Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT));
            System.out.println("Public Base 64:     " + Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT));

            //byte[] clear = Base64.decode(Base64.toBase64String(keyPair.getPrivate().getEncoded()));
            byte[] clear = keyPair.getPrivate().getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec priv = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            RSAPublicKeySpec keySpecx = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));
            PublicKey publicKey = kf.generatePublic(keySpecx);

            //String base64 = Base64.toBase64String(keyPair.getPublic().getEncoded());
            //byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(base64.getBytes());
            //String base64x = Base64.toBase64String(sha256_1);

            //String base64 = Base64.toBase64String(keyPair.getPublic().getEncoded());
            byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(keyPair.getPublic().getEncoded());
            //String base = Base64.encodeToString(sha256_1, Base64.DEFAULT);
            String base = hex.bytesToHex(sha256_1);

            //MainActivity.prv_key_id = Base64.toBase64String(keyPair.getPrivate().getEncoded());
            //MainActivity.pub_key_id = Base64.toBase64String(keyPair.getPublic().getEncoded());
            //Main.prv_key_id = Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);
            //Main.pub_key_id = Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);

            //Main.prv_key_id = hex.bytesToHex(keyPair.getPrivate().getEncoded());
            //Main.pub_key_id = hex.bytesToHex(keyPair.getPublic().getEncoded());

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("prv_key_id", hex.bytesToHex(keyPair.getPrivate().getEncoded()));
            editor.putString("pub_key_id", hex.bytesToHex(keyPair.getPublic().getEncoded()));
            editor.putString("user_id", base);
            editor.putString("user_name", "John Smith");
            editor.putString("user_status", "Click here to edit your account and add new friends");
            editor.commit();

        } catch (Exception e) {e.printStackTrace();}

        Main.databaseUpdated = true;

        System.out.println("DBsx");

    }//***********************************************************************





    public database_driver(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        SQLiteDatabase db = this.getWritableDatabase();
        db.close();

    }//Database driver





}//Database driver
