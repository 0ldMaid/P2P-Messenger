package co.intentservice.chatui.sample;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;


public class file_delete_picture {

    SharedPreferences settings;
    SharedPreferences.Editor editor;


    public void deletePicture(String file) {

        //Delete old image. But not the seed image.
        try {

            //These settings are set in the settings screen but we load a few of them here because the system needs them.
            settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
            editor = settings.edit();

            String old_base64x1 = settings.getString("seed_icon", "");
            String old_base64x2 = settings.getString("user_background", "");

            System.out.println("t " + file);
            System.out.println("1 " + old_base64x1);
            System.out.println("2 " + old_base64x2);

            File fdelete = new File(Main.directory, "/" + file + ".png");

            if (fdelete.exists()) {

                if (!file.equals(old_base64x1) && !file.equals(old_base64x2)) {

                    if (fdelete.delete()) {
                        System.out.println("file Deleted.");
                    }
                    else {
                        System.out.println("file not Deleted.");
                    }

                }
                else {
                    System.out.println("file still needed.");
                }

            }

        } catch (Exception e2) {
            System.out.println(e2.getMessage());
        }

    }

}//class
