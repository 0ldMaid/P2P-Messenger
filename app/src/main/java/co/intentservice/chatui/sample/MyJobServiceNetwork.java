package co.intentservice.chatui.sample;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class MyJobServiceNetwork extends Service {

    Timer xtimerx;//class loop.

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();

        //Start the network.
        //xtimerx = new Timer();
        //xtimerx.schedule(new RemindTask_NetworkFeedTask(), 0);

        new NetworkFeedTask().execute();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Service destroyed by user.", Toast.LENGTH_LONG).show();
    }




    class RemindTask_NetworkFeedTask extends TimerTask {

        Runtime rxrunti = Runtime.getRuntime();

        public void run() {//********************************

            //start client
            tor_net_client client = new tor_net_client();

            //start server
            tor_net_server server = new tor_net_server();

        }//runx*********************************************

    }//remindtask





    private class NetworkFeedTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Showing progress dialog.

            System.out.println("Update Chain...");

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {

                //start client
                tor_net_client client = new tor_net_client();

                //start server
                tor_net_server server = new tor_net_server();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return null;

        }//do in background()

    }//build_chain2**************************************************






}