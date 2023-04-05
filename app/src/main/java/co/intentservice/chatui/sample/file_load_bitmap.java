package co.intentservice.chatui.sample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class file_load_bitmap {

    public Bitmap getBitmap(String file_name) throws FileNotFoundException {

        Bitmap bitmap = null;

        try {

            System.out.println("Load Image " + file_name);
            File file = new File(Main.directory, "/" + file_name + ".png");//Or any other format supported
            FileInputStream streamIn = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(streamIn);//This gets the image

            //System.out.println("getWidth()  " + bitmap.getWidth());
            //System.out.println("getHeight() " + bitmap.getHeight());

        } catch (Error e) {e.printStackTrace();}

        return bitmap;

    }

}//class
