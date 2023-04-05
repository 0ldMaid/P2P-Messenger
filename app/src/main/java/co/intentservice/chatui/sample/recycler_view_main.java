package co.intentservice.chatui.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import co.intentservice.chatui.sample.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class recycler_view_main extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    file_load_bitmap loadb = new file_load_bitmap();

    private List<ArrayList<String>> mData1;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Bitmap avatarx;

    // data is passed into the constructor
    recycler_view_main(Context context, List<ArrayList<String>> data) {

        this.mInflater = LayoutInflater.from(context);
        this.mData1 = data;

    }



    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //System.out.println("viewType " + viewType);

        if (viewType == 0) {

            View view = mInflater.inflate(R.layout.main_ad, parent, false);
            return new ViewHolderAd(view);

        }
        if (viewType == 1) {

            View view = mInflater.inflate(R.layout.main_title, parent, false);
            return new ViewHolderSend(view);

        }
        else {

            View view = mInflater.inflate(R.layout.main_accounts, parent, false);
            return new ViewHolderRecv(view);

        }

    }

    @Override
    public int getItemViewType(int position) {

        return Integer.parseInt(mData1.get(position).get(0));

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = holder.getItemViewType();

        //System.out.println("viewType " + viewType);

        if (viewType == 0) {

            ViewHolderAd viewHolder0 = (ViewHolderAd) holder;

            //final float scale = Main.context2.getResources().getDisplayMetrics().density;

            //int pixelsx = (int) (468 * scale + 0.5f);
            //int pixelsy = (int) (60 * scale + 0.5f);

            View adContainer = viewHolder0.layoutx;
            //AdSize customAdSize = new AdSize(pixelsx, pixelsy);
            AdView mAdView = new AdView(Main.context2);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            //mAdView.setAdSize(customAdSize);
            mAdView.setAdUnitId("ca-app-pub-1455267053898698/9036832945");
            ((RelativeLayout)adContainer).addView(mAdView);
            AdRequest.Builder builder = new AdRequest.Builder();
            if (BuildConfig.DEBUG) {
                builder.addTestDevice("8BE5AB22C1086A9599FDFB104A51551A");
            }
            AdRequest adRequest = builder.build();
            mAdView.loadAd(adRequest);

            //viewHolder0.layoutx.setText(mData1.get(position).get(2));

        }
        else if (viewType == 1) {

            ViewHolderSend viewHolder0 = (ViewHolderSend) holder;

            viewHolder0.myTextView.setText(mData1.get(position).get(2));

        }
        else {

            ViewHolderRecv viewHolder1 = (ViewHolderRecv) holder;

            viewHolder1.recvTextView.setText(mData1.get(position).get(2));
            viewHolder1.myTextView.setText(mData1.get(position).get(3));
            viewHolder1.newMessageTextView.setText(mData1.get(position).get(4));

            try {

                Bitmap bitmap = loadb.getBitmap(mData1.get(position).get(5).toString());
                //Set image
                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), bitmap);
                viewHolder1.imageView.setBackground(bdrawable);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }



    // total number of rows
    @Override
    public int getItemCount() {
        return mData1.size();
    }


    public void setAvatarImage(Bitmap imagex) {

        avatarx = imagex;

    }


    public class ViewHolderAd extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        RelativeLayout layoutx;

        ViewHolderAd(View itemView) {

            super(itemView);

            layoutx = itemView.findViewById(R.id.adMobView);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) mClickListener.onItemLongPress(view, getLayoutPosition());
            return true;
        }

    }


    public class ViewHolderRecv extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView recvTextView;
        TextView myTextView;
        TextView newMessageTextView;

        ViewHolderRecv(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_message_profile);
            recvTextView = itemView.findViewById(R.id.text_message_name);
            myTextView = itemView.findViewById(R.id.text_message_body);
            newMessageTextView = itemView.findViewById(R.id.text_message_new);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null) mClickListener.onItemLongPress(view, getLayoutPosition());
            return true;
        }

    }

    public class ViewHolderSend extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView myTextView;

        ViewHolderSend(View itemView) {

            super(itemView);

            myTextView = itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getLayoutPosition());
        }

    }


    //get the text of the message
    String getNumber(int id) { return mData1.get(id).get(7); }

    // convenience method for getting data at click position
    String getItemType1(int id) {
        return mData1.get(id).get(0);
    }

    // convenience method for getting data at click position
    String getItemType2(int id) {
        return mData1.get(id).get(1);
    }

    // convenience method for getting data at click position
    String getID(int id) {
        return mData1.get(id).get(1);
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