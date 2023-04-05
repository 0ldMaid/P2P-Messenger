package co.intentservice.chatui.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;


public class file_extract_picture {

    tools_bitmap rcbitmap = new tools_bitmap();
    tools_encryption hex = new tools_encryption();

    public String printPicture(String image_name, boolean rounded) {

        String base64x = "";

        try {

            InputStream istr = Main.assetManager.open(image_name);
            Bitmap bitmap = BitmapFactory.decodeStream(istr);

            if (rounded) {

                bitmap = rcbitmap.getRoundedCircle(bitmap);

            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            byte[] sha256_1 = MessageDigest.getInstance("MD5").digest(byteArray);
            //base64x = Base64.toBase64String(sha256_1);
            base64x = Base64.encodeToString(sha256_1, Base64.DEFAULT);

            base64x = "PP" + hex.bytesToHex(base64x.getBytes());

            System.out.println("base64x " + base64x);

            //Save the image.
            try {

                FileOutputStream fout = new FileOutputStream(new File(Main.directory, "/" + base64x + ".png"));

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
                fout.flush();
                fout.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            bitmap.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return base64x;

    }


}//class
