package co.intentservice.chatui.sample;

import android.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class tools_encryption {

    private static final String ALGO = "AES/GCM/NoPadding";
    private static byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
    final protected static char[] hexArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    JSONParser parser = new JSONParser();

    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    tools_encryption() {


    }






    public String encryptMessage(String message, String pub_key) {

        String response = "";

        try {

            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256); // for example
            SecretKey secretKey = keyGen.generateKey();

            //String encodedKey = Base64.toBase64String(secretKey.getEncoded());
            String encodedKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

            byte[] iv = new byte[12];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            String iv_string = Base64.encodeToString(iv, Base64.DEFAULT);
            System.out.println("IV " + iv_string);
            byte[] iv2 = Base64.decode(iv_string, Base64.DEFAULT);

            GCMParameterSpec spec = new GCMParameterSpec(128,iv2);

            Cipher cipher1 = Cipher.getInstance(ALGO);//AES/ECB/PKCS5Padding
            cipher1.init(Cipher.ENCRYPT_MODE, secretKey, spec);
            //response = Base64.toBase64String(cipher1.doFinal(message.getBytes("UTF-8")));
            String message_base64 = Base64.encodeToString(cipher1.doFinal(message.getBytes("UTF-8")), Base64.DEFAULT);

            //X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(pub_key.getBytes()));
            //X509EncodedKeySpec keySpec = new X509EncodedKeySpec( Base64.decode(pub_key, Base64.DEFAULT) );
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec( hexToBytes(pub_key) );
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");//RSA/ECB/PKCS1Padding
            cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
            String message_key_base64 = bytesToHex(cipher2.doFinal(encodedKey.getBytes()));

            Cipher cipher3 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher3.init(Cipher.ENCRYPT_MODE, publicKey);
            String message_iv_base64 = bytesToHex(cipher3.doFinal(iv_string.getBytes()));

            System.out.println("message_iv_base64 " + message_iv_base64);

            JSONObject obj = new JSONObject();
            obj.put("1", message_base64);
            obj.put("2", message_key_base64);
            obj.put("3", message_iv_base64);
            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            response = out.toString();

        } catch (Exception e) {

            e.printStackTrace();

        }//*****************

        return response;

    }



    public String decryptMessage(String message, String prv_key) {

        String response = "";

        try {

            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                obj = parser.parse(message);

            } catch (Error e) {System.out.println("Response is unreadable...");}

            JSONObject jsonObject = (JSONObject) obj;

            String message1 = "";
            String key1 = "";
            String iv1 = "";

            //Populate the info.
            try {message1 = (String) jsonObject.get("1").toString();}  catch (Exception e) {}
            try {key1 = (String) jsonObject.get("2").toString();}      catch (Exception e) {}
            try {iv1 = (String) jsonObject.get("3").toString();}       catch (Exception e) {}

            System.out.println("message1 " + message1);
            System.out.println("key1     " + key1);
            System.out.println("iv1      " + iv1);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec( hexToBytes(prv_key) );
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");//RSA/ECB/PKCS1Padding
            cipher2.init(Cipher.DECRYPT_MODE, privateKey);
            String key2 = new String(cipher2.doFinal(hexToBytes(key1)));

            Cipher cipher3 = Cipher.getInstance("RSA/ECB/PKCS1Padding");//RSA/ECB/PKCS1Padding
            cipher3.init(Cipher.DECRYPT_MODE, privateKey);
            String iv_string = new String(cipher3.doFinal(hexToBytes(iv1)));
            byte[] IV2 =  Base64.decode(iv_string, Base64.DEFAULT);

            System.out.println("message_iv_base64 " + new String(cipher3.doFinal(hexToBytes(iv1))));

            // decode the base64 encoded string
            byte[] decodedKey = Base64.decode(key2, Base64.DEFAULT);
            // rebuild key using SecretKeySpec
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            GCMParameterSpec spec = new GCMParameterSpec(128,IV2);

            Cipher cipher1 = Cipher.getInstance(ALGO);//AES/ECB/PKCS5Padding
            cipher1.init(Cipher.DECRYPT_MODE, secretKey, spec);
            //response = new String(cipher1.doFinal(Base64.decode(message1)));
            response = new String(cipher1.doFinal( Base64.decode(message1, Base64.DEFAULT) ));

        } catch (Exception e) {

            e.printStackTrace();

        }//*****************

        return response;

    }



    public static String getAESKey() throws Exception  {

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // for example
        SecretKey secretKey = keyGen.generateKey();

        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);

    }



    public static String getIV() {

        byte[] iv = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        return Base64.encodeToString(iv, Base64.DEFAULT);

    }



    public static void getRSAKey() {

        try {

            //RSA keys are easy for web developers to use.
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            //after non intensive search 2048 seems to be ok for now.
            kpg.initialize(2048);
            KeyPair keyPair = kpg.genKeyPair();

            //System.out.println("privateKey Base 64: " + Base64.toBase64String(keyPair.getPrivate().getEncoded()));
            //System.out.println("Public Base 64:     " + Base64.toBase64String(keyPair.getPublic().getEncoded()));

            System.out.println("privateKey Base 64: " + Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT));
            System.out.println("Public Base 64:     " + Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT));

            //byte[] clear = Base64.decode(Base64.toBase64String(keyPair.getPrivate().getEncoded()));
            byte[] clear = keyPair.getPrivate().getEncoded();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            privateKey = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec priv = kf.getKeySpec(privateKey, RSAPrivateKeySpec.class);
            RSAPublicKeySpec keySpecx = new RSAPublicKeySpec(priv.getModulus(), BigInteger.valueOf(65537));
            publicKey = kf.generatePublic(keySpecx);

            //String base64 = Base64.toBase64String(keyPair.getPublic().getEncoded());
            //byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(base64.getBytes());
            //String base64x = Base64.toBase64String(sha256_1);

            //String base64 = Base64.toBase64String(keyPair.getPublic().getEncoded());
            byte[] sha256_1 = MessageDigest.getInstance("SHA-256").digest(keyPair.getPublic().getEncoded());
            //String base = Base64.encodeToString(sha256_1, Base64.DEFAULT);
            String base = bytesToHex(sha256_1);

        } catch (Exception e) {e.printStackTrace();}


    }



    public static byte[] encryptImage(byte[] data, String privateKey, String iv) throws Exception {

        System.out.println("encryptImage1 " + data.length);
        System.out.println("encryptImage2 " + privateKey);
        System.out.println("encryptImage3 " + iv);

        // decode the base64 encoded string
        byte[] decodedKey = Base64.decode(privateKey, Base64.DEFAULT);
        // rebuild key using SecretKeySpec
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        byte[] iv2 = Base64.decode(iv, Base64.DEFAULT);

        GCMParameterSpec spec = new GCMParameterSpec(128,iv2);

        Cipher cipher1 = Cipher.getInstance(ALGO);//AES/ECB/PKCS5Padding
        cipher1.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        //response = Base64.toBase64String(cipher1.doFinal(message.getBytes("UTF-8")));

        return cipher1.doFinal(data);

    }



    public static byte[] decryptImage(byte[] encryptedData, String privateKey, String iv) throws Exception {

        System.out.println("encryptImage1 " + encryptedData.length);
        System.out.println("encryptImage2 " + privateKey);
        System.out.println("encryptImage3 " + iv);

        // decode the base64 encoded string
        byte[] decodedKey = Base64.decode(privateKey, Base64.DEFAULT);
        // rebuild key using SecretKeySpec
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        byte[] iv2 = Base64.decode(iv, Base64.DEFAULT);

        GCMParameterSpec spec = new GCMParameterSpec(128,iv2);

        Cipher cipher1 = Cipher.getInstance(ALGO);//AES/ECB/PKCS5Padding
        cipher1.init(Cipher.DECRYPT_MODE, secretKey, spec);
        //response = new String(cipher1.doFinal(Base64.decode(message1)));

        return cipher1.doFinal( encryptedData );

    }



    public static void setByteKey(byte[] key) {

        keyValue = key;

    }



    private static Key generateKey() throws Exception {

        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;

    }



    public static byte[] hexToBytes(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;

    }



    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int loop = 0; loop < bytes.length; loop++ ) {

            int v = bytes[loop] & 0xFF;
            hexChars[loop * 2] = hexArray[v >>> 4];
            hexChars[loop * 2 + 1] = hexArray[v & 0x0F];

        }//***************************************

        return new String(hexChars);

    }//********************************************




    private final static char base64Array [] = {

            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '0', '1', '2', '3',
            '4', '5', '6', '7', '8', '9', '+', '/'

    };



    private static String Base64Encode (String string)    {

        String encodedString = "";
        byte bytes [] = string.getBytes ();
        int i = 0;
        int pad = 0;

        while (i < bytes.length) {

            byte b1 = bytes [i++];
            byte b2;
            byte b3;

            if (i >= bytes.length) {

                b2 = 0;
                b3 = 0;
                pad = 2;

            }
            else {

                b2 = bytes [i++];

                if (i >= bytes.length) {

                    b3 = 0;
                    pad = 1;

                }
                else {
                    b3 = bytes[i++];
                }
            }
            byte c1 = (byte)(b1 >> 2);
            byte c2 = (byte)(((b1 & 0x3) << 4) | (b2 >> 4));
            byte c3 = (byte)(((b2 & 0xf) << 2) | (b3 >> 6));
            byte c4 = (byte)(b3 & 0x3f);

            encodedString += base64Array [c1];
            encodedString += base64Array [c2];

            switch (pad) {
                case 0:

                    encodedString += base64Array [c3];
                    encodedString += base64Array [c4];
                    break;

                case 1:
                    encodedString += base64Array [c3];
                    encodedString += "=";
                    break;
                case 2:
                    encodedString += "==";
                    break;
            }

        }

        return encodedString;

    }






}//last