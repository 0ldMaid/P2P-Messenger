package co.intentservice.chatui.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import static java.sql.Types.NULL;

public class tools_build_main_layout extends AppCompatActivity{

    database_update_display_status statusx = new database_update_display_status();
    static database_accounts accountsx = new database_accounts();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    ArrayList<ArrayList<String>> data_messages = new ArrayList<>();
    RecyclerView recyclerView;
    recycler_view_main adapter;

    Context context;

    private static boolean scrollBottom;

    static String[][] friendsx = new String[12][0];
    static String[][] following = new String[12][0];

    public static final int DISPLAY_ID_ADS = 0;
    public static final int DISPLAY_ID_HEADER = 1;
    public static final int DISPLAY_ID_ACCOUNT = 2;

    public static final int DISPLAY_ID_POSITION_ACCOUNT = 0;//accounts
    public static final int DISPLAY_ID_POSITION_STORE = 1;//accounts
    public static final int DISPLAY_ID_POSITION_ALBUM = 2;//accounts
    public static final int DISPLAY_ID_POSITION_FEED = 0;//apps
    public static final int DISPLAY_ID_POSITION_MATCH = 1;//apps

    public static final int SET_HEADER_ACCOUNT_ID = 100;
    public static final int SET_HEADER_APP_ID = 101;
    public static final int SET_HEADER_DAPP_ID = 102;
    public static final int SET_HEADER_FRIEND_ID = 103;
    public static final int SET_HEADER_FOLLOW_ID = 104;

    public static final int SET_ACCOUNT_ID = 200;
    public static final int SET_APP_ID = 201;
    public static final int SET_DAPP_ID = 202;
    public static final int SET_FRIEND_ID = 203;
    public static final int SET_FOLLOW_ID = 204;



    tools_build_main_layout(recycler_view_main.ItemClickListener click, Context context2, RecyclerView view) {

        System.out.println("Build main layout dropdown list.");

        //setContentView(R.layout.activity_main);

        context = context2;

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();

        try {

            // set up the RecyclerView
            recyclerView = view;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            //layoutManager.setStackFromEnd(true);
            //layoutManager.setReverseLayout(true);
            adapter = new recycler_view_main(context, data_messages);
            adapter.setClickListener(click);
            //adapter.setAvatarImage(loadImage(iconx));
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(null);
            //recyclerView.scrollToPosition(0);

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    super.onScrollStateChanged(recyclerView, newState);

                    if (!recyclerView.canScrollVertically(1)) {

                        scrollBottom = true;
                        //Toast.makeText(Chat.this, "Last", Toast.LENGTH_LONG).show();

                    }
                    else {

                        scrollBottom = false;

                    }

                }

            });

            //Show.
            //new Main.rebuild_chain().execute();
            //rebuildDataMessages();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }



    }




    public Boolean isClickAccountSettings(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_ACCOUNT_ID))) {

            if (adapter.getNumber(position).equals(Integer.toString(DISPLAY_ID_POSITION_ACCOUNT))) {

                return true;

            }

        }

        return false;

    }



    public Boolean isClickAccountStore(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_ACCOUNT_ID))) {

            if (adapter.getNumber(position).equals(Integer.toString(DISPLAY_ID_POSITION_STORE))) {

                return true;

            }

        }

        return false;

    }



    public Boolean isClickAccountAlbum(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_ACCOUNT_ID))) {

            if (adapter.getNumber(position).equals(Integer.toString(DISPLAY_ID_POSITION_ALBUM))) {

                return true;

            }

        }

        return false;

    }



    public Boolean isClickAppsFeed(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_APP_ID))) {

            if (adapter.getNumber(position).equals(Integer.toString(DISPLAY_ID_POSITION_FEED))) {

                return true;

            }

        }

        return false;

    }



    public Boolean isClickAppsLove(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_APP_ID))) {

            if (adapter.getNumber(position).equals(Integer.toString(DISPLAY_ID_POSITION_MATCH))) {

                return true;

            }

        }

        return false;

    }



    public Boolean isClickFriends(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_FRIEND_ID))) {

            return true;

        }

        return false;

    }



    public int getClickFriendsID(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_FRIEND_ID))) {

            return Integer.parseInt(adapter.getNumber(position));

        }

        return NULL;

    }



    public Boolean isClickFollowing(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_FOLLOW_ID))) {

            if (adapter.getNumber(position).equals(Integer.toString(DISPLAY_ID_POSITION_ACCOUNT))) {

                return true;

            }

        }

        return false;

    }



    public int getClickFollowingID(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_FOLLOW_ID))) {

            return Integer.parseInt(adapter.getNumber(position));

        }

        return NULL;

    }



    public Boolean isLongPressFriend(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_FRIEND_ID))) {

            return true;

        }

        return false;

    }



    public Boolean isLongPressFollowing(int position) {

        if (adapter.getItemType2(position).equals(Integer.toString(SET_FOLLOW_ID))) {

            return true;

        }

        return false;

    }




    public ArrayList<String> buildAccountHeader() {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_HEADER));//type
            data.add(Integer.toString(SET_HEADER_ACCOUNT_ID));//type 2
            data.add(Main.context2.getResources().getString(R.string.list_account) + "");//name

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildAppsHeader() {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_HEADER));//type
            data.add(Integer.toString(SET_HEADER_APP_ID));//type 2
            data.add(Main.context2.getResources().getString(R.string.list_apps) + "");//name

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildDappsHeader() {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_HEADER));//type
            data.add(Integer.toString(SET_HEADER_DAPP_ID));//type 2

            if (1 == 1) {

                data.add(Main.context2.getResources().getString(R.string.list_dpps) + "");//name

            }
            else {

                data.add(Main.context2.getResources().getString(R.string.list_dpps) + "");//name

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildFriendsHeader(int friends_int) {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_HEADER));//type
            data.add(Integer.toString(SET_HEADER_FRIEND_ID));//type 2

            if (friends_int == 0) {

                data.add(Main.context2.getResources().getString(R.string.list_friends) + "");//name

            }
            else {

                data.add(Main.context2.getResources().getString(R.string.list_friends) + " " + Integer.toString(friends_int));//name

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildFollowHeader(int following_int) {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_HEADER));//type
            data.add(Integer.toString(SET_HEADER_FOLLOW_ID));//type 2

            if (following_int == 0) {

                data.add(Main.context2.getResources().getString(R.string.list_following) + "");//name Html.fromHtml("&#709;")

            }
            else {

                data.add(Main.context2.getResources().getString(R.string.list_following) + " " + Integer.toString(following_int));//name Html.fromHtml("&#709;")

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildAccountButton() {

        ArrayList<String> data = new ArrayList<>();

        final String icon_avatar = settings.getString("user_avatar", "");
        final String namex =  settings.getString("user_name", "");
        final String status =  settings.getString("user_status", "");

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_ACCOUNT_ID));//type
            data.add(namex + "'s Account");//name
            data.add(status);//message
            data.add("");//status
            data.add(icon_avatar);//status
            data.add("0");//status
            data.add("0");//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildStoreButton() {

        ArrayList<String> data = new ArrayList<>();

        final String icon_store =  settings.getString("shop_icon", "");
        final String namex =  settings.getString("user_name", "");

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_ACCOUNT_ID));//type
            data.add(namex + "'s Store");//name
            data.add(Main.store_items + " Item(s) for sale");//message
            data.add("");//status
            data.add(icon_store);//status
            data.add("0");//status
            data.add("1");//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildAlbumButton() {

        ArrayList<String> data = new ArrayList<>();

        final String icon_album =  settings.getString("album_icon", "");
        final String namex =  settings.getString("user_name", "");

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_ACCOUNT_ID));//type
            data.add(namex + "'s Photo Album");//name
            data.add(Main.album_items + " Images(s)");//message
            data.add("");//status
            data.add(icon_album);//status
            data.add("0");//status
            data.add("2");//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildFeedButton() {

        ArrayList<String> data = new ArrayList<>();

        final String icon_feed =  settings.getString("feed_icon", "");

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_APP_ID));//type
            data.add("News Feed");//name
            data.add(Main.feed_items_new + " items(s)");//message
            data.add("");//status
            data.add(icon_feed);//status
            data.add("0");//status
            data.add("0");//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildLoveButton() {

        ArrayList<String> data = new ArrayList<>();

        final String icon_love =  settings.getString("love_icon", "");

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_APP_ID));//type
            data.add("MatchMaker 1.0");//name
            data.add("0 items(s)");//message
            data.add("");//status
            data.add(icon_love);//status
            data.add("0");//status
            data.add("1");//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildFriendButton(String[][] friendsx, int friend_id) {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_FRIEND_ID));//type
            data.add(friendsx[2][friend_id]);//name
            data.add(friendsx[6][friend_id]);//message

            String new_message_count = "";

            try {

                new_message_count = friendsx[8][friend_id];

                if (new_message_count.equals("0")) {
                    new_message_count = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            data.add(new_message_count);//new
            data.add(friendsx[12][friend_id]);//image
            data.add("0");//status
            data.add(Integer.toString(friend_id));//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }



    public ArrayList<String> buildFollowingButton(String[][] following, int following_id) {

        ArrayList<String> data = new ArrayList<>();

        try {

            data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
            data.add(Integer.toString(SET_FOLLOW_ID));//type
            data.add(following[2][following_id]);//name
            data.add(following[6][following_id]);//message

            String new_message_count = "";

            try {

                new_message_count = following[8][following_id];

                if (new_message_count.equals("0")) {
                    new_message_count = "";
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            data.add(new_message_count);//new
            data.add(following[12][following_id]);//image
            data.add("0");//status
            data.add(Integer.toString(following_id));//list

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return data;

    }




    public void itemClick(int position) {

        boolean account_ads_open = settings.getBoolean("show_user_adds", true);
        boolean account_list_open = settings.getBoolean("account_list_open", true);
        boolean apps_list_open = settings.getBoolean("apps_list_open", true);
        boolean dapps_list_open = settings.getBoolean("dapps_list_open", true);
        boolean friends_list_open = settings.getBoolean("friends_list_open", true);
        boolean follow_list_open = settings.getBoolean("follow_list_open", true);

        if (adapter.getItemType1(position).equals(Integer.toString(DISPLAY_ID_HEADER)) &&
                adapter.getID(position).equals(Integer.toString(SET_HEADER_ACCOUNT_ID))) {

            if (account_list_open) {

                account_list_open = false;

                //removeRange(Integer.toString(SET_ACCOUNT_ID));
                removeRange(Integer.toString(SET_ACCOUNT_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("account_list_open", account_list_open);
                editor.commit();

            }
            else {

                account_list_open = true;

                //insertRange(Integer.toString(SET_ACCOUNT_ID));
                insertRange(Integer.toString(SET_ACCOUNT_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("account_list_open", account_list_open);
                editor.commit();

            }

        }

        if (adapter.getItemType1(position).equals(Integer.toString(DISPLAY_ID_HEADER)) &&
                adapter.getID(position).equals(Integer.toString(SET_HEADER_APP_ID))) {

            if (apps_list_open) {

                apps_list_open = false;

                //removeRange(Integer.toString(SET_APP_ID));
                removeRange(Integer.toString(SET_APP_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("apps_list_open", apps_list_open);
                editor.commit();

            }
            else {

                apps_list_open = true;

                //insertRange(Integer.toString(SET_APP_ID));
                insertRange(Integer.toString(SET_APP_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("apps_list_open", apps_list_open);
                editor.commit();

            }

        }

        if (adapter.getItemType1(position).equals(Integer.toString(DISPLAY_ID_HEADER)) &&
                adapter.getID(position).equals(Integer.toString(SET_HEADER_DAPP_ID))) {

            if (dapps_list_open) {

                dapps_list_open = false;

                //removeRange(Integer.toString(SET_DAPP_ID));
                removeRange(Integer.toString(SET_DAPP_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("dapps_list_open", dapps_list_open);
                editor.commit();

            }
            else {

                dapps_list_open = true;

                //insertRange(Integer.toString(SET_DAPP_ID));
                insertRange(Integer.toString(SET_DAPP_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("dapps_list_open", dapps_list_open);
                editor.commit();

            }

        }

        if (adapter.getItemType1(position).equals(Integer.toString(DISPLAY_ID_HEADER)) &&
                adapter.getID(position).equals(Integer.toString(SET_HEADER_FRIEND_ID))) {

            if (friends_list_open) {

                friends_list_open = false;

                //removeRange(Integer.toString(SET_FRIEND_ID));
                removeRange(Integer.toString(SET_FRIEND_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("friends_list_open", friends_list_open);
                editor.commit();

            }
            else {

                friends_list_open = true;

                //insertRange(Integer.toString(SET_FRIEND_ID));
                insertRange(Integer.toString(SET_FRIEND_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("friends_list_open", friends_list_open);
                editor.commit();

            }

        }

        if (adapter.getItemType1(position).equals(Integer.toString(DISPLAY_ID_HEADER)) &&
                adapter.getID(position).equals(Integer.toString(SET_HEADER_FOLLOW_ID))) {

            if (follow_list_open) {

                follow_list_open = false;

                //removeRange(Integer.toString(SET_FOLLOW_ID));
                removeRange(Integer.toString(SET_FOLLOW_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("follow_list_open", follow_list_open);
                editor.commit();

            }
            else {

                follow_list_open = true;

                //insertRange(Integer.toString(SET_FOLLOW_ID));
                insertRange(Integer.toString(SET_FOLLOW_ID), data_messages, adapter, friendsx, following);

                //SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("follow_list_open", follow_list_open);
                editor.commit();

            }

        }

    }



    public void removeRange(String range, ArrayList<ArrayList<String>> data_messages, recycler_view_main adapter, String[][] friendsx, String[][] following) {

        if (range.equals(Integer.toString(SET_ACCOUNT_ID))) {

            System.out.println("SET_ACCOUNT_ID " + SET_ACCOUNT_ID);

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(range)) {

                    data_messages.remove(loop);
                    data_messages.remove(loop);
                    data_messages.remove(loop);
                    adapter.notifyItemRangeRemoved(loop, loop + 2);
                    adapter.notifyDataSetChanged();

                    break;

                }

            }

        }

        if (range.equals(Integer.toString(SET_APP_ID))) {

            System.out.println("SET_APP_ID " + SET_APP_ID);

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(range)) {

                    data_messages.remove(loop);
                    data_messages.remove(loop);
                    adapter.notifyItemRangeRemoved(loop, loop + 1);
                    adapter.notifyDataSetChanged();

                    break;

                }

            }

        }

        if (range.equals(Integer.toString(SET_FRIEND_ID))) {

            System.out.println("SET_FRIEND_ID " + SET_FRIEND_ID);

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(range)) {

                    for (int loop1 = 0; loop1 < friendsx[0].length; loop1++) {//********

                        data_messages.remove(loop);

                    }

                    adapter.notifyItemRangeRemoved(loop, loop + friendsx[0].length);
                    adapter.notifyDataSetChanged();

                    break;

                }

            }

        }

        if (range.equals(Integer.toString(SET_FOLLOW_ID))) {

            System.out.println("SET_FOLLOW_ID " + SET_FOLLOW_ID);

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(range)) {

                    for (int loop1 = 0; loop1 < following[0].length; loop1++) {//********

                        data_messages.remove(loop);

                    }

                    adapter.notifyItemRangeRemoved(loop, loop + following[0].length);
                    adapter.notifyDataSetChanged();

                    break;

                }

            }

        }

    }



    public void insertRange(String range, ArrayList<ArrayList<String>> data_messages, recycler_view_main adapter, String[][] friendsx, String[][] following) {

        if (range.equals(Integer.toString(SET_ACCOUNT_ID))) {

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(Integer.toString(SET_HEADER_ACCOUNT_ID))) {

                    data_messages.add(loop + 1, buildAccountButton());
                    data_messages.add(loop + 2, buildStoreButton());
                    data_messages.add(loop + 3, buildAlbumButton());

                    //recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeInserted(loop + 1,loop + 3);

                    break;

                }

            }

        }

        if (range.equals(Integer.toString(SET_APP_ID))) {

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(Integer.toString(SET_HEADER_APP_ID))) {

                    data_messages.add(loop + 1, buildFeedButton());
                    data_messages.add(loop + 2, buildLoveButton());

                    //recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeInserted(loop + 1,loop + 2);

                    break;

                }

            }

        }

        if (range.equals(Integer.toString(SET_FRIEND_ID))) {

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(Integer.toString(SET_HEADER_FRIEND_ID))) {

                    for (int loop1 = 0; loop1 < accountsx.getFriendCount(); loop1++) {//********

                        final int loopx = loop + loop1 + 1;
                        System.out.println("loopx " + loopx);

                        data_messages.add(loopx,buildFriendButton(friendsx, loop1));

                    }//******************************************************************

                    //recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeInserted(loop + 1,loop + accountsx.getFriendCount());

                    break;

                }

            }

        }

        if (range.equals(Integer.toString(SET_FOLLOW_ID))) {

            for (int loop = 0; loop < data_messages.size(); loop++) {

                if (data_messages.get(loop).get(1).equals(Integer.toString(SET_HEADER_FOLLOW_ID))) {

                    for (int loop1 = 0; loop1 < accountsx.getFollowingCount(); loop1++) {//********

                        final int loopx = loop + loop1 + 1;
                        System.out.println("loopx " + loopx);

                        data_messages.add(loopx, buildFollowingButton(following, loop1));

                    }//******************************************************************

                    //recyclerView.getRecycledViewPool().clear();
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemRangeInserted(loop + 1, loop + accountsx.getFollowingCount());

                    break;

                }

            }

        }

    }//last





    public void rebuildDataMessages() {

        boolean account_ads_open = settings.getBoolean("show_user_adds", true);
        boolean account_list_open = settings.getBoolean("account_list_open", true);
        boolean apps_list_open = settings.getBoolean("apps_list_open", true);
        boolean dapps_list_open = settings.getBoolean("dapps_list_open", true);
        boolean friends_list_open = settings.getBoolean("friends_list_open", true);
        boolean follow_list_open = settings.getBoolean("follow_list_open", true);

        statusx.getStatusAll();

        friendsx = accountsx.getFriendsList();
        following = accountsx.getFollowingList();

        //rebuild everything.
        int size = data_messages.size();
        if (size > 0) {
            for (int loop = 0; loop < size; loop++) {
                data_messages.remove(0);
            }

            //adapter.notifyItemRangeRemoved(0, size);
        }

        //build the drop down list.
        data_messages.add(buildAccountHeader());

        if (account_list_open) {

            data_messages.add(buildAccountButton());
            data_messages.add(buildStoreButton());
            data_messages.add(buildAlbumButton());

        }

        data_messages.add(buildAppsHeader());

        if (apps_list_open) {

            data_messages.add(buildFeedButton());
            data_messages.add(buildLoveButton());

        }

        //data_messages.add(layout_tools.build_dapps_header());

        data_messages.add(buildFriendsHeader(accountsx.getFriendCount()));

        if (friends_list_open) {

            for (int loop1 = 0; loop1 < accountsx.getFriendCount(); loop1++) {//********

                data_messages.add(buildFriendButton(friendsx, loop1));

            }//*********************************************************************

        }

        data_messages.add(buildFollowHeader(accountsx.getFollowingCount()));

        if (follow_list_open) {

            for (int loop1 = 0; loop1 < accountsx.getFollowingCount(); loop1++) {//********

                data_messages.add(buildFollowingButton(following, loop1));

            }//*********************************************************************

        }

        recyclerView.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();

        adapter.notifyItemRangeInserted(0, data_messages.size());
        adapter.notifyItemChanged(data_messages.size());

        recyclerView.scrollToPosition(0);

    }




    public void updateDataMessages() {

        //Get the drop down menu settings.
        boolean account_ads_open = settings.getBoolean("show_user_adds", true);
        boolean account_list_open = settings.getBoolean("account_list_open", true);
        boolean apps_list_open = settings.getBoolean("apps_list_open", true);
        boolean dapps_list_open = settings.getBoolean("dapps_list_open", true);
        boolean friends_list_open = settings.getBoolean("friends_list_open", true);
        boolean follow_list_open = settings.getBoolean("follow_list_open", true);

        statusx.getStatusAll();

        friendsx = accountsx.getFriendsList();
        following = accountsx.getFollowingList();

        if (account_list_open) {

            //get items to test
            for (int loop1 = 0; loop1 < data_messages.size(); loop1++) {//********

                if (data_messages.get(loop1).get(1).equals(Integer.toString(SET_ACCOUNT_ID))) {

                    data_messages.set(loop1, buildAccountButton());
                    adapter.notifyItemChanged(loop1);

                    data_messages.set(loop1 + 1, buildStoreButton());
                    adapter.notifyItemChanged(loop1 + 1);

                    break;

                }

            }

        }

        if (apps_list_open) {

            //get items to test
            for (int loop1 = 0; loop1 < data_messages.size(); loop1++) {//********

                if (data_messages.get(loop1).get(1).equals(Integer.toString(SET_APP_ID))) {

                    data_messages.set(loop1, buildFeedButton());
                    adapter.notifyItemChanged(loop1);

                    break;

                }

            }

        }

        if (friends_list_open) {

            //get items to test
            for (int loop1 = 0; loop1 < data_messages.size(); loop1++) {//********

                if (data_messages.get(loop1).get(1).equals(Integer.toString(SET_FRIEND_ID))) {

                    int friend_id = Integer.parseInt(data_messages.get(loop1).get(7));
                    int testx = 0;

                    System.out.println("Friend 6  " + accountsx.getFriendLastMessage(friend_id));
                    System.out.println("friend_id " + friend_id);

                    if (data_messages.get(loop1).get(2).equals(friendsx[2][friend_id])) {
                        testx++;
                    }
                    if (data_messages.get(loop1).get(3).equals(friendsx[6][friend_id])) {
                        testx++;
                    }
                    if (data_messages.get(loop1).get(4).equals(friendsx[8][friend_id])) {
                        testx++;
                    }

                    System.out.println("testx " + testx);

                    if (testx != 3) {

                        System.out.println(data_messages.get(loop1).get(2) + " " + friendsx[6][friend_id]);

                        final ArrayList<String> data = new ArrayList<>();
                        data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
                        data.add(Integer.toString(SET_FRIEND_ID));//type
                        data.add(friendsx[2][friend_id]);//name
                        data.add(friendsx[6][friend_id]);//message

                        String new_message_count = "";

                        try {

                            new_message_count = friendsx[8][friend_id];

                            if (new_message_count.equals("0")) {
                                new_message_count = "";
                            }

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        data.add(new_message_count);//new
                        data.add(friendsx[12][friend_id]);//image
                        data.add("0");//status
                        data.add(Integer.toString(friend_id));//list
                        //data_messages.add(data);

                        try {

                            data_messages.set(loop1, data);
                            adapter.notifyItemChanged(loop1);

                        } catch (Error e) {
                            e.printStackTrace();
                        }

                    }//if

                }//if

            }//for

        }//if

        if (follow_list_open) {

            //get items to test
            for (int loop1 = 0; loop1 < data_messages.size(); loop1++) {//********

                if (data_messages.get(loop1).get(1).equals(Integer.toString(SET_FOLLOW_ID))) {

                    int friend_id = Integer.parseInt(data_messages.get(loop1).get(7));
                    int testx = 0;

                    //System.out.println("Friend 6 " + getx.getFollowingLastMessage(friend_id));

                    if (data_messages.get(loop1).get(2).equals(following[2][friend_id])) {
                        testx++;
                    }
                    if (data_messages.get(loop1).get(3).equals(following[6][friend_id])) {
                        testx++;
                    }
                    if (data_messages.get(loop1).get(4).equals(following[8][friend_id])) {
                        testx++;
                    }

                    System.out.println("testx" + testx);

                    if (testx != 3) {

                        System.out.println(data_messages.get(loop1).get(2) + " " + following[6][friend_id]);

                        final ArrayList<String> data = new ArrayList<>();
                        data.add(Integer.toString(DISPLAY_ID_ACCOUNT));//type
                        data.add(Integer.toString(SET_FOLLOW_ID));//type
                        data.add(following[2][friend_id]);//name
                        data.add(following[6][friend_id]);//message

                        String new_message_count = "";

                        try {

                            new_message_count = following[8][friend_id];

                            if (new_message_count.equals("0")) {
                                new_message_count = "";
                            }

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        data.add(new_message_count);//new
                        data.add(following[12][friend_id]);//image
                        data.add("0");//status
                        data.add(Integer.toString(friend_id));//list
                        //data_messages.add(data);

                        try {

                            data_messages.set(loop1, data);
                            adapter.notifyItemChanged(loop1);

                        } catch (Error e) {
                            e.printStackTrace();
                        }

                    }//if

                }//if

            }//for

        }//if

    }



}//class
