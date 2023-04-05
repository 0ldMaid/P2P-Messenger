package co.intentservice.chatui.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class recycler_view_feed extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    file_load_bitmap loadbx = new file_load_bitmap();

    private List<ArrayList<String>> mData1;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    recycler_view_feed(Context context, List<ArrayList<String>> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData1 = data;

    }



    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {

            View view = mInflater.inflate(R.layout.feed_product, parent, false);
            return new ViewHolderProduct(view);

        }
        else if (viewType == 1) {

            View view = mInflater.inflate(R.layout.feed_status, parent, false);
            return new ViewHolderStatus(view);

        }
        else if (viewType == 2) {

            View view = mInflater.inflate(R.layout.feed_avatar, parent, false);
            return new ViewHolderAvatar(view);

        }
        else if (viewType == 3) {

            View view = mInflater.inflate(R.layout.feed_picture, parent, false);
            return new ViewHolderPicture(view);

        }
        else {

            View view = mInflater.inflate(R.layout.feed_status, parent, false);
            return new ViewHolderStatus(view);

        }

    }

    @Override
    public int getItemViewType(int position) {

        return Integer.parseInt(mData1.get(position).get(6));

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();

        //items[0][rowCount] = cursor.getString(cursor.getColumnIndex("xd"));
        //items[1][rowCount] = cursor.getString(cursor.getColumnIndex("link_id"));
        //items[2][rowCount] = cursor.getString(cursor.getColumnIndex("hash_id"));
        //items[3][rowCount] = cursor.getString(cursor.getColumnIndex("name_id"));
        //items[4][rowCount] = cursor.getString(cursor.getColumnIndex("avatar_image"));
        //items[5][rowCount] = cursor.getString(cursor.getColumnIndex("date_id"));
        //items[6][rowCount] = cursor.getString(cursor.getColumnIndex("view_type"));
        //items[7][rowCount] = cursor.getString(cursor.getColumnIndex("status"));
        //items[8][rowCount] = cursor.getString(cursor.getColumnIndex("title"));
        //items[9][rowCount] = cursor.getString(cursor.getColumnIndex("description"));
        //items[10][rowCount] = cursor.getString(cursor.getColumnIndex("image1"));
        //items[11][rowCount] = cursor.getString(cursor.getColumnIndex("price"));
        //items[12][rowCount] = cursor.getString(cursor.getColumnIndex("currency"));
        //items[13][rowCount] = cursor.getString(cursor.getColumnIndex("tor_address"));
        //items[14][rowCount] = cursor.getString(cursor.getColumnIndex("pub_key"));
        //items[15][rowCount] = cursor.getString(cursor.getColumnIndex("dapp_id"));

        if (viewType == 0) {

            ViewHolderProduct viewHolder0 = (ViewHolderProduct) holder;

            try {

                Bitmap avatarx = loadbx.getBitmap(mData1.get(position).get(4));

                //Set image
                BitmapDrawable bdrawable1 = new BitmapDrawable(Main.context2.getResources(), avatarx);
                viewHolder0.imageView.setBackground(bdrawable1);

            } catch (Exception e) {e.printStackTrace();}

            try {

                Bitmap imagex = loadbx.getBitmap(mData1.get(position).get(10));

                int width = imagex.getWidth();
                int height = imagex.getHeight();

                DisplayMetrics displayMetrics = Main.context2.getResources().getDisplayMetrics();

                int device_width = displayMetrics.widthPixels;
                //int device_height = displayMetrics.heightPixels;

                float scale = ((float)width / device_width);
                int new_height = (int) (height / scale);

                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), imagex);
                viewHolder0.pictureView.setBackground(bdrawable);
                viewHolder0.pictureView.getLayoutParams().height = new_height;

            } catch (Exception e) {

                e.printStackTrace();

                viewHolder0.pictureView.setBackground(null);
                viewHolder0.pictureView.getLayoutParams().height = 200;

            }

            viewHolder0.myTextTitle.setText(mData1.get(position).get(3) + " is selling: " + mData1.get(position).get(8));
            viewHolder0.myTextDate.setText(mData1.get(position).get(5));
            viewHolder0.myTextDescription.setText(mData1.get(position).get(9));
            viewHolder0.myTextPrice.setText(mData1.get(position).get(11) + mData1.get(position).get(12));

        }
        else if (viewType == 1) {

            ViewHolderStatus viewHolder1 = (ViewHolderStatus) holder;

            viewHolder1.myTextTitle.setText(mData1.get(position).get(3) + " has updated their status...");
            viewHolder1.myTextDate.setText(mData1.get(position).get(5));
            viewHolder1.myTextDescription.setText(mData1.get(position).get(9));

            try {

                Bitmap avatarx = loadbx.getBitmap(mData1.get(position).get(4));

                //Set image
                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), avatarx);
                viewHolder1.imageView.setBackground(bdrawable);

            } catch (Exception e) {e.printStackTrace();}

        }
        else if (viewType == 2) {

            ViewHolderAvatar viewHolder1 = (ViewHolderAvatar) holder;

            viewHolder1.myTextTitle.setText(mData1.get(position).get(3) + " has updated their icon...");
            viewHolder1.myTextDate.setText(mData1.get(position).get(5));

            try {

                Bitmap avatarx = loadbx.getBitmap(mData1.get(position).get(4));

                //Set image
                BitmapDrawable bdrawable1 = new BitmapDrawable(Main.context2.getResources(), avatarx);
                viewHolder1.imageView.setBackground(bdrawable1);

                BitmapDrawable bdrawable2 = new BitmapDrawable(Main.context2.getResources(), avatarx);
                viewHolder1.avatarView.setBackground(bdrawable2);

            } catch (Exception e) {e.printStackTrace();}

        }
        else if (viewType == 3) {

            ViewHolderPicture viewHolder1 = (ViewHolderPicture) holder;

            viewHolder1.myTextTitle.setText(mData1.get(position).get(3) + " has added a new picture...");
            viewHolder1.myTextDate.setText(mData1.get(position).get(5));

            try {

                Bitmap avatarx = loadbx.getBitmap(mData1.get(position).get(4));

                //Set image
                BitmapDrawable bdrawable1 = new BitmapDrawable(Main.context2.getResources(), avatarx);
                viewHolder1.imageView.setBackground(bdrawable1);

            } catch (Exception e) {e.printStackTrace();}

            try {

                Bitmap imagex = loadbx.getBitmap(mData1.get(position).get(10));

                int width = imagex.getWidth();
                int height = imagex.getHeight();

                DisplayMetrics displayMetrics = Main.context2.getResources().getDisplayMetrics();

                int device_width = displayMetrics.widthPixels;
                //int device_height = displayMetrics.heightPixels;

                float scale = ((float)width / device_width);
                int new_height = (int) (height / scale);

                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), imagex);
                viewHolder1.pictureView.setBackground(bdrawable);
                viewHolder1.pictureView.getLayoutParams().height = new_height;

            } catch (Exception e) {

                e.printStackTrace();

                viewHolder1.pictureView.setBackground(null);
                viewHolder1.pictureView.getLayoutParams().height = 200;

            }

            viewHolder1.descriptionView.setText(mData1.get(position).get(9));

        }
        else {

            ViewHolderStatus viewHolder1 = (ViewHolderStatus) holder;

            //Set image
            //BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), avatarx);
            //viewHolder1.imageView.setBackground(bdrawable);

            viewHolder1.myTextTitle.setText(mData1.get(position).get(1));
            viewHolder1.myTextDate.setText(mData1.get(position).get(2));
            viewHolder1.myTextDescription.setText(mData1.get(position).get(3));

        }


    }



    // total number of rows
    @Override
    public int getItemCount() {
        return mData1.size();
    }


    public class ViewHolderProduct extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView myTextTitle;
        TextView myTextDate;
        ImageView pictureView;
        TextView myTextDescription;
        TextView myTextPrice;
        ImageButton myEditButton;

        ViewHolderProduct(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_profile);
            myTextTitle = itemView.findViewById(R.id.text_title);
            myTextDate = itemView.findViewById(R.id.text_date);
            pictureView = itemView.findViewById(R.id.image_product1);
            myTextDescription = itemView.findViewById(R.id.text_description);
            myTextPrice = itemView.findViewById(R.id.text_price);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {mClickListener.onItemClick(view, getAdapterPosition());}
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {mClickListener.onItemLongPress(view, getAdapterPosition());}
            return true;
        }

    }

    public class ViewHolderStatus extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView myTextTitle;
        TextView myTextDate;
        TextView myTextDescription;

        ViewHolderStatus(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_profile);
            myTextTitle = itemView.findViewById(R.id.text_title);
            myTextDate = itemView.findViewById(R.id.text_date);
            myTextDescription = itemView.findViewById(R.id.text_description);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {mClickListener.onItemClick(view, getAdapterPosition());}
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {mClickListener.onItemLongPress(view, getAdapterPosition());}
            return true;
        }

    }

    public class ViewHolderAvatar extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView myTextTitle;
        TextView myTextDate;
        ImageView avatarView;

        ViewHolderAvatar(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_profile);
            myTextTitle = itemView.findViewById(R.id.text_title);
            myTextDate = itemView.findViewById(R.id.text_date);
            avatarView = itemView.findViewById(R.id.image_icon);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {mClickListener.onItemClick(view, getAdapterPosition());}
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {mClickListener.onItemLongPress(view, getAdapterPosition());}
            return true;
        }

    }

    public class ViewHolderPicture extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView myTextTitle;
        TextView myTextDate;
        ImageView pictureView;
        TextView descriptionView;

        ViewHolderPicture(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_profile);
            myTextTitle = itemView.findViewById(R.id.text_title);
            myTextDate = itemView.findViewById(R.id.text_date);
            pictureView = itemView.findViewById(R.id.album_image);
            descriptionView = itemView.findViewById(R.id.text_description);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {mClickListener.onItemClick(view, getAdapterPosition());}
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {mClickListener.onItemLongPress(view, getAdapterPosition());}
            return true;
        }

    }




    //get the text of the message
    String getText(int id) { return mData1.get(id).get(2); }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData1.get(id).get(0);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongPress(View v, int position);
    }

}