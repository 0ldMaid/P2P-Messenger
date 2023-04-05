package co.intentservice.chatui.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class recycler_view_store extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    file_load_bitmap bitmapx = new file_load_bitmap();

    private List<ArrayList<String>> mData1;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Bitmap avatarx;

    // data is passed into the constructor
    recycler_view_store(Context context, List<ArrayList<String>> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData1 = data;

    }



    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = mInflater.inflate(R.layout.store_product, parent, false);
            return new ViewHolderProduct(view);

    }

    @Override
    public int getItemViewType(int position) {

        //return Integer.parseInt(mData1.get(position).get(0));
        return 0;

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //int viewType = holder.getItemViewType();

        ViewHolderProduct viewHolder1 = (ViewHolderProduct) holder;

        //Set image
        BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), avatarx);
        viewHolder1.imageView.setBackground(bdrawable);

        viewHolder1.myTextTitle.setText(mData1.get(position).get(1));
        viewHolder1.myTextDate.setText(mData1.get(position).get(2));

        try {

            Bitmap imagex = bitmapx.getBitmap(mData1.get(position).get(3));

            int width = imagex.getWidth();
            int height = imagex.getHeight();

            System.out.println("width  " + width);
            System.out.println("height " + height);

            DisplayMetrics displayMetrics = Main.context2.getResources().getDisplayMetrics();

            int device_width = displayMetrics.widthPixels;
            //int device_height = 500;

            float scale = ((float)width / device_width);
            int new_height = (int) (height / scale);

            BitmapDrawable bdrawable2 = new BitmapDrawable(Main.context2.getResources(), imagex);
            viewHolder1.pictureView.setBackground(bdrawable2);
            viewHolder1.pictureView.getLayoutParams().height = new_height;

        } catch (Exception e) {

            e.printStackTrace();

            viewHolder1.pictureView.setBackground(null);
            viewHolder1.pictureView.getLayoutParams().height = 200;

        }

        viewHolder1.myTextDescription.setText(mData1.get(position).get(4));
        viewHolder1.myTextPrice.setText(mData1.get(position).get(5));
        viewHolder1.activex.setChecked(Boolean.parseBoolean(mData1.get(position).get(6)));

    }


    public class ViewHolderProduct extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageButton myEditButton;
        ImageView imageView;
        TextView myTextTitle;
        TextView myTextDate;
        ImageView pictureView;
        TextView myTextDescription;
        TextView myTextPrice;
        CheckBox activex;

        ViewHolderProduct(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_profile);
            myTextTitle = itemView.findViewById(R.id.text_title);
            myTextDate = itemView.findViewById(R.id.text_date);
            myTextDescription = itemView.findViewById(R.id.text_description);
            pictureView = itemView.findViewById(R.id.image_product1);
            myTextPrice = itemView.findViewById(R.id.text_price);

            myEditButton = itemView.findViewById(R.id.image_button_edit);
            myEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onImageItemClick(view, getLayoutPosition());
                }
            });

            activex = itemView.findViewById(R.id.item_active);
            activex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mClickListener.onCheckItemClick(activex.isChecked(), getLayoutPosition());
                }
            });

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {mClickListener.onItemClick(view, getLayoutPosition());}
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) {mClickListener.onItemLongPress(view, getLayoutPosition());}
            return true;
        }

    }




    // total number of rows
    @Override
    public int getItemCount() {
        return mData1.size();
    }

    String getID(int id) {
        return mData1.get(id).get(0);
    }

    public void setAvatarImage(Bitmap imagex) {

        avatarx = imagex;

    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onImageItemClick(View view, int position);
        void onCheckItemClick(boolean checked, int position);
        void onItemLongPress(View v, int position);
    }

}