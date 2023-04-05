package co.intentservice.chatui.sample;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;


public class file_save_picture {

    public void savePicture(String file, Bitmap bitmap) {

        //Delete old image. But not the seed image.
        try {

            FileOutputStream fout = new FileOutputStream(new File(Main.directory, "/" + file + ".png"));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
            fout.flush();
            fout.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public void saveOutputStream(String file, ByteArrayOutputStream out) {

        //Delete old image. But not the seed image.
        try {

            FileOutputStream fout = new FileOutputStream(new File(Main.directory, "/" + file + ".png"));

            fout.write(out.toByteArray());

            fout.flush();
            fout.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}//class
