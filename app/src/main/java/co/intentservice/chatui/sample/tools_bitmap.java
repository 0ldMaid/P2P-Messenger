package co.intentservice.chatui.sample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;

public class tools_bitmap {


    public static BitmapDrawable getFittedBitmap(Bitmap bitmap, int width, int height) {

        System.out.println("Canvas  Width " + width + " Height " + height);
        System.out.println("Picture Width " + bitmap.getWidth() + " Height " + bitmap.getHeight());

        float resized_perc = (width * 100) / bitmap.getWidth();
        resized_perc = resized_perc / 100;

        int resized_width = (int) (bitmap.getWidth() * resized_perc);
        int resized_height = (int) (bitmap.getHeight() * resized_perc);

        System.out.println("Resized Width " + resized_width + " Height " + resized_height);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        //final int color1 = 0xff424242;
        final int color2 = 0x80FFFFFF;
        final int color3 = 0xff154766;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, resized_width, resized_height);
        //final RectF rectF = new RectF(rect);
        //final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color3);
        //canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(0, 0, width, height, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setColor(color2);
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);

        //Set image
        BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), output);

        return bdrawable;

    }


    public static Bitmap getRoundedRec(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 25;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        //canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getWidth() / 2, bitmap.getWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }


    public static Bitmap getRoundedCircle(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        //final RectF rectF = new RectF(rect);
        //final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getWidth() / 2, bitmap.getWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }


}//class
