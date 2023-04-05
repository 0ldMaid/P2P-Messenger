package co.intentservice.chatui.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class recycler_view_chat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    file_load_bitmap file = new file_load_bitmap();
    tools_bitmap rcbitmap = new tools_bitmap();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    private List<ArrayList<String>> mData1;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Bitmap avatarx;
    private Bitmap systemx;

    // data is passed into the constructor
    recycler_view_chat(Context context, List<ArrayList<String>> data) {

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        try {

            systemx = file.getBitmap(settings.getString("seed_icon", "seed_icon"));

        } catch (Exception e) {e.printStackTrace();}

        this.mInflater = LayoutInflater.from(context);
        this.mData1 = data;

    }



    // inflates the row layout from xml when needed
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        if (viewType == Chat.MESSAGE_TYPE_SYSTEM) {

            View view = mInflater.inflate(R.layout.message_system_text, parent, false);
            return new ViewHolderSystem(view);

        }
        else if (viewType == Chat.MESSAGE_TYPE_TEXT_SENT) {

            View view = mInflater.inflate(R.layout.message_send_text, parent, false);
            return new ViewHolderSend(view);

        }
        else if (viewType == Chat.MESSAGE_TYPE_TEXT_RECEIVED) {

            View view = mInflater.inflate(R.layout.message_received_text, parent, false);
            return new ViewHolderRecv(view);

        }
        else if (viewType == Chat.MESSAGE_TYPE_IMAGE_SENT) {

            View view = mInflater.inflate(R.layout.message_send_image, parent, false);
            return new ViewHolderImageSend(view);

        }
        else if (viewType == Chat.MESSAGE_TYPE_IMAGE_RECEIVED) {

            View view = mInflater.inflate(R.layout.message_received_image, parent, false);
            return new ViewHolderImageRecv(view);

        }
        else {

            return null;

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

        if (viewType == Chat.MESSAGE_TYPE_SYSTEM)  {

            ViewHolderSystem viewHolder = (ViewHolderSystem) holder;

            //Set image
            BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), systemx);

            viewHolder.imageView.setBackground(bdrawable);
            //viewHolder.recvTextView.setText(mData1.get(position).get(1));
            viewHolder.myTextView.setText(mData1.get(position).get(2));
            viewHolder.timeTextView.setText(mData1.get(position).get(4));

        }
        else if (viewType == Chat.MESSAGE_TYPE_TEXT_SENT) {

            ViewHolderSend viewHolder = (ViewHolderSend) holder;

            viewHolder.myTextView.setText(mData1.get(position).get(2));
            viewHolder.timeTextView.setText(mData1.get(position).get(4));

            String status_string = "";
            if (mData1.get(position).get(3).equals("0")) {status_string = "";}
            else if (mData1.get(position).get(3).equals("1")) {status_string = "Sent";}
            else if (mData1.get(position).get(3).equals("2")) {status_string = "Read";}
            else if (mData1.get(position).get(3).equals("3")) {status_string = "Failed";}

            viewHolder.statusTextView.setText(status_string);

        }
        else if (viewType == Chat.MESSAGE_TYPE_TEXT_RECEIVED)  {

            ViewHolderRecv viewHolder = (ViewHolderRecv) holder;

            //Set image
            BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), avatarx);

            viewHolder.imageView.setBackground(bdrawable);
            viewHolder.recvTextView.setText(mData1.get(position).get(1));
            viewHolder.myTextView.setText(mData1.get(position).get(2));
            viewHolder.timeTextView.setText(mData1.get(position).get(4));

        }
        else if (viewType == Chat.MESSAGE_TYPE_IMAGE_SENT)  {

            ViewHolderImageSend viewHolder = (ViewHolderImageSend) holder;

            try {

                Bitmap picture = rcbitmap.getRoundedRec(file.getBitmap(mData1.get(position).get(2)));

                int width = picture.getWidth();
                int height = picture.getHeight();
                int width_dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

                float scale_dp = (float) (150.0 / width_dp);
                int new_height = (int) (scale_dp * height);

                System.out.println("scale_dp   " + scale_dp);
                System.out.println("width_dp   " + width_dp);
                System.out.println("width      " + width);
                System.out.println("height     " + height);
                System.out.println("new_height " + new_height);

                //Set image
                BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), picture);
                viewHolder.myPictureView.setBackground(bdrawable);
                viewHolder.myPictureView.getLayoutParams().height = new_height;

            } catch (Exception e) {

                e.printStackTrace();
                viewHolder.myPictureView.getLayoutParams().height = (int) (Main.chat_image_size_dp * Resources.getSystem().getDisplayMetrics().density);

            }

            viewHolder.timeTextView.setText(mData1.get(position).get(4));

            String status_string = "";
            if (mData1.get(position).get(3).equals("0")) {status_string = "";}
            else if (mData1.get(position).get(3).equals("1")) {status_string = Main.context2.getResources().getString(R.string.chat_system_message_sent);}
            else if (mData1.get(position).get(3).equals("2")) {status_string = Main.context2.getResources().getString(R.string.chat_system_message_read);}
            else if (mData1.get(position).get(3).equals("3")) {status_string = Main.context2.getResources().getString(R.string.chat_system_message_failed);}

            viewHolder.statusTextView.setText(status_string);

        }
        else if (viewType == Chat.MESSAGE_TYPE_IMAGE_RECEIVED)  {

            ViewHolderImageRecv viewHolder = (ViewHolderImageRecv) holder;

            //Set image
            BitmapDrawable bdrawable = new BitmapDrawable(Main.context2.getResources(), avatarx);

            viewHolder.imageView.setBackground(bdrawable);
            viewHolder.recvTextView.setText(mData1.get(position).get(1));

            try {

                Bitmap picture = rcbitmap.getRoundedRec(file.getBitmap(mData1.get(position).get(2)));

                int width = picture.getWidth();
                int height = picture.getHeight();
                int width_dp = (int) (width / Resources.getSystem().getDisplayMetrics().density);

                float scale_dp = (float) (150.0 / width_dp);
                int new_height = (int) (scale_dp * height);

                System.out.println("scale_dp   " + scale_dp);
                System.out.println("width_dp   " + width_dp);
                System.out.println("width      " + width);
                System.out.println("height     " + height);
                System.out.println("new_height " + new_height);

                //Set image
                BitmapDrawable bdrawable2 = new BitmapDrawable(Main.context2.getResources(), picture);
                viewHolder.myPictureView.setBackground(bdrawable2);
                viewHolder.myPictureView.getLayoutParams().height = new_height;

            } catch (Exception e) {

                e.printStackTrace();
                System.out.println(e.getMessage());
                viewHolder.myPictureView.getLayoutParams().height = (int) (Main.chat_image_size_dp * Resources.getSystem().getDisplayMetrics().density);

            }

            viewHolder.timeTextView.setText(mData1.get(position).get(4));

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


    public class ViewHolderSystem extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView recvTextView;
        TextView myTextView;
        TextView timeTextView;

        ViewHolderSystem(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_message_profile);
            recvTextView = itemView.findViewById(R.id.text_message_name);
            myTextView = itemView.findViewById(R.id.text_message_body);
            timeTextView = itemView.findViewById(R.id.text_message_time);
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

    public class ViewHolderSend extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView myTextView;
        TextView timeTextView;
        TextView statusTextView;

        ViewHolderSend(View itemView) {

            super(itemView);

            myTextView = itemView.findViewById(R.id.text_message_body);
            timeTextView = itemView.findViewById(R.id.text_message_time);
            statusTextView = itemView.findViewById(R.id.text_message_status);
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

    public class ViewHolderRecv extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView recvTextView;
        TextView myTextView;
        TextView timeTextView;

        ViewHolderRecv(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_message_profile);
            recvTextView = itemView.findViewById(R.id.text_message_name);
            myTextView = itemView.findViewById(R.id.text_message_body);
            timeTextView = itemView.findViewById(R.id.text_message_time);
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

    public class ViewHolderImageSend extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView myPictureView;
        TextView timeTextView;
        TextView statusTextView;

        ViewHolderImageSend(View itemView) {

            super(itemView);

            myPictureView = itemView.findViewById(R.id.image_message_body);
            timeTextView = itemView.findViewById(R.id.text_message_time);
            statusTextView = itemView.findViewById(R.id.text_message_status);
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

    public class ViewHolderImageRecv extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView imageView;
        TextView recvTextView;
        ImageView myPictureView;
        TextView timeTextView;

        ViewHolderImageRecv(View itemView) {

            super(itemView);

            imageView = itemView.findViewById(R.id.image_message_profile);
            recvTextView = itemView.findViewById(R.id.text_message_name);
            myPictureView = itemView.findViewById(R.id.image_message_body);
            timeTextView = itemView.findViewById(R.id.text_message_time);
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