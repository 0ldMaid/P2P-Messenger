package co.intentservice.chatui.sample;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import com.msopentech.thali.android.installer.AndroidTorInstaller;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.android.toronionproxy.AndroidTorConfig;
import com.msopentech.thali.toronionproxy.DefaultSettings;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import com.msopentech.thali.toronionproxy.TorConfig;
import com.msopentech.thali.toronionproxy.TorInstaller;
import com.msopentech.thali.toronionproxy.Utilities;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


public class tor_net_client{

    Timer xtimerx;//class loop.

    int break_time_seconds = 60;

    private static final int TOTAL_SECONDS_PER_TOR_STARTUP = 4 * 60;
    private static final int TOTAL_TRIES_PER_TOR_STARTUP = 5;
    private static final int WAIT_FOR_HIDDEN_SERVICE_MINUTES = 3;
    static final String clientManagerDirectoryName = "clientmanager";
    public static OnionProxyManager onionProxyManager;

    JSONParser parser = new JSONParser();

    tools_encryption aes = new tools_encryption();
    BitmapFactory.Options options = new BitmapFactory.Options();
    database_update_display_status statusx = new database_update_display_status();
    static database_accounts accountsx = new database_accounts();
    database_messages messagesx = new database_messages();
    database_feed feedx = new database_feed();

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    public static final int SERVER_SET_ID_ACTIVE = 0;
    public static final int SERVER_SET_ID_VERSION = 1;
    public static final int SERVER_SET_ID_USER_NAME = 2;
    public static final int SERVER_SET_ID_FRIEND_STATUS = 3;
    public static final int SERVER_SET_ID_USER_BIRTHDAY = 4;
    public static final int SERVER_SET_ID_USER_RELATIONSHIP_STATUS = 5;
    public static final int SERVER_SET_ID_USER_JOB_STATUS = 6;
    public static final int SERVER_SET_ID_ACCOUNT_TYPE = 7;
    public static final int SERVER_SET_ID_USER_STATUS = 8;
    public static final int SERVER_SET_ID_MESSAGES_RECEIVED = 9;
    public static final int SERVER_SET_ID_MESSAGES_FAILED = 10;
    public static final int SERVER_SET_ID_MESSAGES_READ = 11;
    public static final int SERVER_SET_ID_USER_AVATAR = 12;
    public static final int SERVER_SET_ID_USER_BACKGROUND = 13;
    public static final int SERVER_SET_ID_NEW_DAPPS_ID = 14;
    public static final int SERVER_SET_ID_NEW_PRODUCT_ID = 15;
    public static final int SERVER_SET_ID_LAST_PICTURE_ID = 16;
    public static final int SERVER_SET_ID_LAST_PICTURE_MESSAGE_ID = 17;

    static final String testAddress = "yahoo.com";//Test address to make sure Tor is working even if one of our clients is not.
    static final String local_host_connect = "127.0.0.1";
    static String connection_status = "Starting...";

    static String friend_connection = "";

    static int no_server_time = 0;
    static int localPort = 9343;
    static int hiddenServicePort = 9344;
    static int tor_sock4_port = 0;
    static int tor_tries = 0;//3 times and we restart.
    static int no_internet_time = 0;

    static long client_loop_time = (long) 0;
    long threadId;//There were errors with different threads being called this was to test that.

    static boolean[] active_list_b;//Which threads do we already have running.
    static boolean tor_server_connected = false;
    static boolean tor_peer_connected = false;
    static boolean tor_connected = false;
    static boolean tor_active = false;
    static boolean tor_starting = false;


    tor_net_client() {//*****************************************************************

        tor_server_connected = false;
        tor_peer_connected = false;
        tor_connected = false;
        tor_active = false;
        tor_starting = true;

        //These settings are set in the settings screen but we load a few of them here because the system needs them.
        settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
        editor = settings.edit();

        //start hidden service if requested.
        //int hiddenServicePort = 80;
        //int localPort = onionProxyManager.getIPv4LocalHostSocksPort();
        //int localPort = Main.p2p_port;

        client_loop_time = System.currentTimeMillis();
        start_tor();

        //We listen for updates and make them seen.
        Main.sv_status.setListener3(new string_variable_status.ChangeListener() {
            @Override
            public void onChange() {

                System.out.println("test_status()");
                test_status();

            }
        });

        //xtimerx = new Timer();
        //xtimerx.schedule(new RemindTask_backup(), 0);

        //This is the loop to test if tor is working.
        //xtimerx = new Timer();
        //xtimerx.schedule(new RemindTask_start_tor(), 0);

        xtimerx = new Timer();
        xtimerx.schedule(new RemindTask_connection(), 0);

    }//*************************************************************************************



    class RemindTask_start_tor extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************

            threadId = Thread.currentThread().getId();
            System.out.println("threadId " + threadId);

            start_tor();

        }//runx*************************************************************************************************

    }//remindtask




    public static void start_tor() {

        tor_server_connected = false;
        tor_peer_connected = false;
        tor_connected = false;
        tor_active = false;
        tor_starting = true;

        //Start tor.
        try {

            ApplicationInfo appInfo = Main.context2.getApplicationInfo();
            //File installDir = new File(appInfo.nativeLibraryDir,"");
            File writeDir = new File(Main.context2.getFilesDir(),"");

            File installDir = new File(writeDir + "/" + clientManagerDirectoryName);
            System.out.println("> " + installDir);
            TorConfig torConfig = AndroidTorConfig.createConfig(installDir, installDir, Main.context2);
            System.out.println("torConfig " + torConfig.toString());

            TorInstaller torInstaller = new AndroidTorInstaller(Main.context2,installDir) {
                @Override
                public InputStream openBridgesStream() throws IOException {
                    //return context.getResources().openRawResource(R.raw.bridges);
                    return null;
                }
            };

            onionProxyManager = new AndroidOnionProxyManager(Main.context2, torConfig, torInstaller, new DefaultSettings(), null, null);
            //deleteTorWorkingDirectory(clientManager.getContext().getConfig().getConfigDir());

            File torrc = new File(torConfig.getTorrcFile().getParentFile(), "/torrc");
            System.out.println("torrc " + torrc);
            System.out.println("torrc " + torrc.exists());

            if (!torrc.exists()) {

                onionProxyManager.setup();

                File controltxt = new File(torConfig.getControlPortFile().getParentFile(), "/control.txt");
                System.out.println("controltxt " + controltxt);

                File cookietxt = new File(torConfig.getCookieAuthFile().getParentFile(), "/control_auth_cookie");
                System.out.println("cookietxt " + cookietxt);

                StringBuilder buildTorrc = new StringBuilder();
                buildTorrc.append("AutomapHostsOnResolve 1").append("\n");
                buildTorrc.append("ControlPortWriteToFile " + controltxt).append("\n");
                buildTorrc.append("CookieAuthFile " + cookietxt).append("\n");
                buildTorrc.append("RunAsDaemon 1").append("\n");
                buildTorrc.append("AvoidDiskWrites 0").append("\n");
                buildTorrc.append("ControlPort auto").append("\n");
                buildTorrc.append("SOCKSPort 9001").append("\n");
                buildTorrc.append("DNSPort 5400").append("\n");
                buildTorrc.append("TransPort 8001").append("\n");
                buildTorrc.append("CookieAuthentication 1").append("\n");
                buildTorrc.append("DisableNetwork 1").append("\n");

                FileOutputStream stream = new FileOutputStream(torrc);
                try {
                    stream.write(buildTorrc.toString().getBytes());
                } finally {
                    stream.close();
                }

            }

            Assert.assertTrue(onionProxyManager.startWithRepeat(TOTAL_SECONDS_PER_TOR_STARTUP, TOTAL_TRIES_PER_TOR_STARTUP, true));
            System.out.println("Client Manager is running.");

            tor_sock4_port = onionProxyManager.getIPv4LocalHostSocksPort();
            System.out.println("tor_sock4_port " + tor_sock4_port + " Main.p2p_port " + Main.p2p_port);
            Main.p2p_port = tor_sock4_port;

        }
        catch (Exception e) {e.printStackTrace();}
        catch (Error e) {e.printStackTrace();}

    }//*********************



    //The system that connects to facebook and the system that connects to other nodes is not the same.
    //If we cannot connect to facebook then we restart Tor, if we cannot connect to a node we change nodes.
    //We assume that the facebook onion address will be up more of the time then any single one of our peers.

	class RemindTask_backup extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			threadId = Thread.currentThread().getId();
			System.out.println("threadId " + threadId);

			while (true) {

				try {

                    no_server_time++;

                    System.out.println("no_server_time " + no_server_time);

                    if (no_server_time > 10) {

                        start_tor();

                        no_server_time = 0;

                    }//****************************************


					System.out.println("threadId " + threadId + " " + Thread.currentThread().getId());

					//boolean torreadyx = onionProxyManager.torReady();

					//System.out.println("isBootstrapped()     " + Main.onionProxyManager.isBootstrapped());
                    //System.out.println("bootstrappedStatus() " + Main.onionProxyManager.bootstrappedStatus());
                    //System.out.println("isNetworkEnabled()   " + Main.onionProxyManager.isNetworkEnabled());
                    System.out.println("isRunning()          " + onionProxyManager.isRunning());
                    System.out.println("status()             " + onionProxyManager.getStartUpStatus());

                    //if (onionProxyManager.bootstrappedStatus().contains("PROGRESS=100")) {}

					if (onionProxyManager.isRunning()) {

					    System.out.println("Tor is ready...");
					    tor_active = true;
                        Main.torSystemReady = true;

					}
					else {

					    System.out.println("Tor is not ready...");
					    tor_active = false;

					}

				} catch (Exception e) {

				    e.printStackTrace();

                    tor_server_connected = false;
                    tor_peer_connected = false;
                    tor_connected = false;
                    tor_active = false;
                    tor_starting = false;

				}
				catch (Error e) {

                    e.printStackTrace();

                    tor_server_connected = false;
                    tor_peer_connected = false;
                    tor_connected = false;
                    tor_active = false;
                    tor_starting = false;

                }

				try {Thread.sleep(30000);} catch(InterruptedException e) {e.printStackTrace();}

				//System.out.println("Loop 32");

			}//while****

		}//runx*************************************************************************************************

	}//remindtask




    //If one of our connection threads cannot connect to a node we try a different address from our node list.
    //We save the ones that are working and keep using those for as long as they are active.

	public class RemindTask_connection extends TimerTask{

	    Runtime rxrunti = Runtime.getRuntime();

		public void run(){//************************************************************************************

			long thisTick = (long) System.currentTimeMillis();
            String connection_test_status = "Starting...";

			while (true) {

                //connect to peers
				try {

					thisTick = System.currentTimeMillis();

					System.out.println("Testing...");
                    //System.out.println("Conn isBootstrapped()     " + Main.onionProxyManager.isBootstrapped());
                    //System.out.println("Conn bootstrappedStatus() " + Main.onionProxyManager.bootstrappedStatus());
                    //System.out.println("Conn isNetworkEnabled()   " + Main.onionProxyManager.isNetworkEnabled());
                    System.out.println("status()                  " + onionProxyManager.getStartUpStatus());
                    System.out.println("Conn isRunning()          " + onionProxyManager.isRunning());

                    if (onionProxyManager.isRunning()) {//************************************************

                        //Test if we have internet access.
                        if (tor_connected) {xx4();}
                        else {xx3();}

                        if (tor_connected) {//**********************************************************************************************

                            //See if there is anything new.
                            statusx.getStatusAll();

                            //If we have no work to do then we update our list to see new work.
                            accountsx.getAccountsList();

                            //If there is no work to do then it is safe to update our work list.
                            active_list_b = new boolean[accountsx.getAccountCount()];

                            System.out.println("getx.getAccountCount() " + accountsx.getAccountCount());

                            for (int xloop1 = 0; xloop1 < accountsx.getAccountCount(); xloop1++) {//*******

                                System.out.println("[WORKER] START WORKER");

                                System.out.println("Start worker for " + accountsx.getAccountTorAddress(xloop1));
                                System.out.println("Worker active?   " + active_list_b[xloop1]);

                                runx(xloop1);

                                /**
                                if (!active_list_b[xloop1]) {

                                    active_list_b[xloop1] = true;

                                    Timer xtimerx = new Timer();
                                    xtimerx.schedule(new RemindTask_worker(xloop1), 0);

                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }//***************************
                                **/

                            }//********************************************************************************

                            //Main.work_ready = false;

                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("work_ready", false);
                            editor.commit();

                        }//************************************************************************************************************************************

					}//*****************************************************************************
                    else {

                        tor_server_connected = false;
                        tor_peer_connected = false;
                        tor_connected = false;
                        tor_active = true;
                        tor_starting = false;

                    }

                    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

                    for (int xloop1 = 0; xloop1 < break_time_seconds; xloop1++) {//******

                        boolean work_ready = settings.getBoolean("work_ready", false);

                        System.out.println("Break time... " + Integer.toString(xloop1) + " " + work_ready + " " + tor_server_connected);

                        //show connection status
                        test_status();

                        if (work_ready) {break;}
                        if (!tor_server_connected) {break;}

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }//****************************************************
					//System.out.println("Loop 32");


				} catch (Exception e) {

                    //Update friends list.
                    accountsx.getFriendsList();

                    no_internet_time++;
                    System.out.println("no_internet_time " + no_internet_time);

                    //if (no_internet_time > Main.no_internet_timeout) {System.exit(0);}

                    e.printStackTrace();

				}//******************

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

			}//while****


	    }//runx*************************************************************************************************

	}//remindtask



    //This is the system that will restart TOR if it cannot connect to facebook for a test.
    //This happens a lot and will probably be called at least a few times per day.
    //If the internet is bad it could be called constantly.

    public void xx3() {

        try {

            String jsonText = "";

            System.out.println("X3 testAddress " + testAddress);

            Socket socket = Utilities.socks4aSocketConnection(testAddress, 80, local_host_connect, tor_sock4_port);//127.0.0.1
            socket.setSoTimeout(10000);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();

            System.out.println("socketw");

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            String modifiedSentence = "";

            String line;
            while ((line = in.readLine()) != null) {

                //System.out.println(line);
                modifiedSentence = modifiedSentence + line;

            }//*************************************

            System.out.println("Connection test: " + modifiedSentence.length());

            tor_tries = 0;
            no_internet_time = 0;

            tor_connected = true;
            tor_active = true;
            tor_starting = false;

            outputStream.close();
            outx.close();
            in.close();
            //socket.close();

        } catch (Exception e) {

            e.printStackTrace();
            tor_active = false;
            tor_connected = false;

        }//******************

    }//***************



    //This is the system that will restart TOR if it cannot connect to facebook for a test.
    //This happens a lot and will probably be called at least a few times per day.
    //If the internet is bad it could be called constantly.

    public void xx4() {

        try {

            String jsonText = "";

            System.out.println("X4 testAddress " + tor_net_server.serverOnionAddress);

            try {

                JSONObject obj = new JSONObject();
                obj.put("request","test");
                obj.put("account_type", Integer.toString(Account.SET_DB_ADMIN_INT));
                obj.put("pub_key", settings.getString("pub_key_id", ""));
                obj.put("nonce", Long.toString(System.currentTimeMillis()));
                //obj.put("messages_read", Long.toString(System.currentTimeMillis()));
                //obj.put("new_messages", "{}");//unsent.getMessages(-1)

                StringWriter out = new StringWriter();
                obj.writeJSONString(out);
                jsonText = out.toString();
                System.out.println(jsonText);

                jsonText = aes.encryptMessage(jsonText, settings.getString("pub_key_id", ""));
                System.out.println(jsonText);

            } catch (Exception e) {System.out.println("JSON ERROR");}

            Socket socket = Utilities.socks4aSocketConnection(tor_net_server.serverOnionAddress, hiddenServicePort, local_host_connect, tor_sock4_port);//127.0.0.1
            socket.setSoTimeout(30000);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();

            System.out.println("socketw");

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            String modifiedSentence = "";

            String line;
            while ((line = in.readLine()) != null) {

                //System.out.println(line);
                modifiedSentence = modifiedSentence + line;

            }//*************************************

            System.out.println("Connection test: " + modifiedSentence.length());

            tor_tries = 0;
            no_internet_time = 0;

            tor_server_connected = true;
            tor_peer_connected = true;
            tor_connected = true;
            tor_active = true;
            tor_starting = false;
            no_server_time = 0;

            outputStream.close();
            outx.close();
            in.close();
            //socket.close();

        } catch (Exception e) {

            e.printStackTrace();
            tor_server_connected = false;
            tor_peer_connected = false;
            tor_connected = false;

        }//******************

    }//***************




    public void test_status() {

        //show connection status
        try {

            //if the class is not active this won't run.
            //Main.onionProxyManager.isBootstrapped();
            //onionProxyManager.isRunning();

                 if (tor_server_connected)   {connection_status = Main.context2.getResources().getString(R.string.tor_bootstrapped);}
            else if (tor_peer_connected)     {connection_status = Main.context2.getResources().getString(R.string.tor_peer_active);}
            else if (tor_connected)          {connection_status = Main.context2.getResources().getString(R.string.tor_connection_active);}
            else if (tor_active)             {connection_status = Main.context2.getResources().getString(R.string.tor_active);}
            else if (tor_starting)           {connection_status = Main.context2.getResources().getString(R.string.tor_restarting);}
            else                             {connection_status = Main.context2.getResources().getString(R.string.tor_no_connection);}

            //show all activities what connection we have.
            if (!Main.sv_status.getStatus().equals(connection_status)) {

                Main.sv_status.setStatus(connection_status);
                System.out.println("connection_status " + connection_status);

            }

            //connection_test_status = connection_status;

        } catch (Exception e) {e.printStackTrace();}

    }



	//This is the main connection loop which keeps the program connected to peers.
	//There is only one connection to one peer.

	public class RemindTask_worker extends TimerTask{

		Runtime rxrunti = Runtime.getRuntime();

        private final int worker;

        RemindTask_worker (int workerx) {

            worker = workerx;

        }//******************************

		public void run(){//************************************************************************************

            try {


                if (tor_active) {

                    //See if there is anything new.
                    //statusx.getStatusAll();

                    System.out.println("Tor loop " + worker);

                    //Everyone gets one peer.
                    runx(worker);

                }//****************************
                else {

                    System.out.println("Tor is not active... loop >>>> " + worker);

                }//**


            } catch (Exception e) {e.printStackTrace();}//******************

            //try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}

            active_list_b[worker] = false;

		}//runx*************************************************************************************************

	}//remindtask



    //This is set up in case we add more peers in the future right now there is only one.

    public void runx (int run_number) {//************************************************************************************

        try {

            System.out.println("[WORKER] " + run_number);

            //Get the user peer if requested.
            String onion_address = accountsx.getAccountTorAddress(run_number);
            String friend_name = accountsx.getAccountNameID(run_number);

            int unsent_messages = 0;
            try {

                unsent_messages = Integer.parseInt(accountsx.getAccountUnsentMessages(run_number));

            } catch (Exception e) {}

            int friend_needs_update = 0;
            try {

                friend_needs_update = Integer.parseInt(accountsx.getAccountUpdateRequired(run_number));

            } catch (Exception e) {}


            System.out.println("Unsent Messages " + unsent_messages);
            System.out.println("Friend Update   " + friend_needs_update);

            if (friend_needs_update == 0) {

                System.out.println("No work to do!");

            }
            else if (!onion_address.contains(".onion")) {

                System.out.println("Not a .onion address!");

            }
            else if (!tor_active) {

                System.out.println("TOR IS NOT READY...");

            }
            else if (tor_starting) {

                System.out.println("TOR IS NOT READY...");

            }
            else {

                System.out.println("Other node work...");
                System.out.println("onionAddress " + run_number + " " + onion_address);

                //Show the feed who we are connected too.
                friend_connection = friend_name;
                Feed.feed_connection.setConnected(true);

                //Test for a connection.
                String status = request_status(run_number);

                //Show the status.
                System.out.println("status: " + status);

                //If the node is active.
                if (status.equals(Integer.toString(tor_net_client.SERVER_SET_ID_ACTIVE))) {

                    //MainActivity.no_connection_time = 0;

                    //pause
                    //try {Thread.sleep(3000);} catch (InterruptedException e){e.printStackTrace();}

                }//if**********************

            }//else

        } catch (Exception e) {e.printStackTrace();}

    }//runx*************************************************************************************************



	//Main stats request asks the network for the current status happens every 10 seconds.

	public String request_status(int run_number){//*****************************************************************

		System.out.println("Request Status " + run_number);

        String onion_address = accountsx.getAccountTorAddress(run_number);
        String friend_pub_key = accountsx.getAccountPubKey(run_number);

        int account_id = -1;
        try {

            account_id = Integer.parseInt(accountsx.getAccountXD(run_number));

        } catch (Exception e) {}

        String friend_name = "";
        try {

            friend_name = accountsx.getAccountNameID(run_number);

        } catch (Exception e) {}

        String status_message = "";
        try {

            status_message = accountsx.getAccountStatusMessage(run_number);

        } catch (Exception e) {}

        int new_messages = 0;
        try {

            new_messages = Integer.parseInt(accountsx.getAccountNewMessages(run_number));

        } catch (Exception e) {}

        int request_count = 0;
        try {

            request_count = Integer.parseInt(accountsx.getAccountUpdateRequired(run_number));

        } catch (Exception e) {}

        int friend_status = 0;
        try {

            friend_status = Integer.parseInt(accountsx.getAccountStatus(run_number));

        } catch (Exception e) {}

        int account_type = 0;
        try {

            account_type = Integer.parseInt(accountsx.getAccountType(run_number));

        } catch (Exception e) {}

        String friend_avatar = "";
        try {

            friend_avatar = accountsx.getAccountAvatarImage(run_number);

        } catch (Exception e) {}

        String friend_background = "";
        try {

            friend_background = accountsx.getAccountBackgroundImage(run_number);

        } catch (Exception e) {}

        String last_picture_id = "";
        try {

            last_picture_id = accountsx.getAccountLastPicId(run_number);

        } catch (Exception e) {}

        String last_product_id = "";
        try {

            last_product_id = accountsx.getAccountLastProductId(run_number);

        } catch (Exception e) {}

        String friend_version = "";
        try {

            friend_version = accountsx.getFriendVersion(run_number);

        } catch (Exception e) {}


        String jsonText = "";
		String jsonSentence = "";

		try {

			JSONObject obj = new JSONObject();
			obj.put("request","status");
            obj.put("pub_key", settings.getString("pub_key_id", ""));
            obj.put("account_type", Integer.toString(account_type));
            obj.put("tor_address", tor_net_server.serverOnionAddress);
            obj.put("messages_read", Long.toString(messagesx.getRead(Integer.toString(account_id))));
            //obj.put("messages_received", messages_received);
            obj.put("user_avatar", settings.getString("user_avatar", ""));
            obj.put("user_background", settings.getString("user_background", ""));
            obj.put("nonce", Long.toString(System.currentTimeMillis()));
            obj.put("version", Main.versionx);

            if (friend_status == Account.SET_DB_STATUS_FRIEND_CONFIRMED) {

                obj.put("new_messages", messagesx.getUnsentMessages(Integer.toString(account_id)));

            }
            else {

                obj.put("new_messages", "{}");

            }

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			System.out.println(jsonText);

            jsonText = aes.encryptMessage(jsonText, friend_pub_key);
            System.out.println(jsonText);

		} catch (Exception e) {System.out.println("JSON ERROR");}


		try {

            System.out.println("address:       " + tor_sock4_port);
            System.out.println("onion address: " + onion_address);

            Socket socket = Utilities.socks4aSocketConnection(onion_address, hiddenServicePort, local_host_connect, tor_sock4_port);//127.0.0.1
            socket.setSoTimeout(30000);

            System.out.println("socketg");

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);

            System.out.println("socketw");

            String modifiedSentence = "";

            String line;
            while ((line = in.readLine()) != null) {

                System.out.println(line);
                modifiedSentence = modifiedSentence + line;

            }//*************************************

            outputStream.close();
            outx.close();
            in.close();
            //socket.close();


            Object obj = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            try {

                modifiedSentence = aes.decryptMessage(modifiedSentence, settings.getString("prv_key_id", ""));
                System.out.println(modifiedSentence);

                obj = parser.parse(modifiedSentence);

            }
            catch (Error e) {e.printStackTrace();}
            catch (Exception e) {e.printStackTrace();}

			JSONObject jsonObject = (JSONObject) obj;

			String response = (String) jsonObject.get("response");
			String message = (String) jsonObject.get("message");
			System.out.println("JSON " + response);

			if (response.equals("1")) {

                tor_peer_connected = true;

				jsonSentence = "active";

                Object obj2 = null;

                //This sometimes throws an error if we get a response that is corrupted.
                //This will shutdown the app.
                //java.lang.Error: Error: could not match input
                try {

                    obj2 = parser.parse(message);

                } catch (Error e) {System.out.println("Response is unreadable..");}

				//Object obj2 = parser.parse(message);
				JSONObject jsonObject2 = (JSONObject) obj2;

                //Get strings from the JSON string.
                String remote_user_name = (String) jsonObject2.get("user_name");
				String remote_friend_status = (String) jsonObject2.get("friend_status");
                String remote_user_status = (String) jsonObject2.get("user_status");
                String remote_message_list = (String) jsonObject2.get("messages_received");
                String remote_message_read = (String) jsonObject2.get("messages_read");
                String remote_user_avatar = (String) jsonObject2.get("user_avatar");
                String remote_user_background = (String) jsonObject2.get("user_background");
                String remote_account_type = (String) jsonObject2.get("account_type");
                String remote_picture_id = (String) jsonObject2.get("last_picture_id");
                String remote_picture_message_id = (String) jsonObject2.get("last_picture_message_id");
                String remote_last_product_id = (String) jsonObject2.get("new_product_id");
                String remote_last_product_json = (String) jsonObject2.get("new_product_listing");
                String remote_version_json = (String) jsonObject2.get("version");

                System.out.println("messages_received         " + remote_user_name);
                System.out.println("friend_status incoming    " + remote_friend_status);
                System.out.println("remote_account_type       " + remote_account_type);
                System.out.println("new_messages              " + new_messages);
                System.out.println("messages_read             " + remote_message_read);
                System.out.println("user_avatar               " + remote_user_avatar);
                System.out.println("user_background           " + remote_user_background);
                System.out.println("remote_user_status        " + remote_user_status);
                System.out.println("remote_picture_id         " + remote_picture_id);
                System.out.println("remote_picture_message_id " + remote_picture_message_id);
                System.out.println("remote_last_product_id    " + remote_last_product_id);
                System.out.println("remote_version_id         " + remote_version_json);

                if (Integer.parseInt(remote_account_type) == Account.SET_DB_FRIEND_INT
                                          && account_type == Account.SET_DB_FRIEND_INT
                                          && friend_status != Account.SET_DB_STATUS_FRIEND_CONFIRMED) {

                    accountsx.confirmFriend(account_id,remote_version_json);
                    //Chat.bv_update.setUpdate(true);
                    //Main.bv_update.setUpdate(true);

                }

                if (remote_message_list.length() > 2) {

                    messagesx.confirmSent(account_id, remote_message_list);
                    //Chat.bv_sent.setSent(true);
                    //Main.bv_update.setUpdate(true);

                }

                if (Long.parseLong(remote_message_read) < System.currentTimeMillis()) {

                    messagesx.setMessagesRead(Integer.toString(account_id), Long.parseLong(remote_message_read));
                    //Chat.bv_read.setRead(true);

                }

                if (!friend_name.equals(remote_user_name)) {

                    accountsx.updateFriendName(account_id, remote_user_name);
                    //Main.bv_update.setUpdate(true);

                }

                if (!friend_version.equals(remote_version_json)) {

                    accountsx.updateFriendVersion(account_id, remote_version_json);
                    //Main.bv_update.setUpdate(true);

                }

                if (!status_message.equals(remote_user_status)) {

                    accountsx.updateFriendStatus(account_id, remote_user_status);

                    if (settings.getBoolean("show_friend_status", true)) {

                        feedx.addStatusItem(
                                Integer.toString(account_id),
                                "",
                                remote_user_name,
                                remote_user_avatar,
                                Long.toString(System.currentTimeMillis()),
                                Integer.toString(Feed.FEED_ID_STATUS),
                                remote_user_status
                        );

                    }

                }

                boolean picture_success = true;

                if (!last_product_id.equals(remote_last_product_id)) {

                    //accountsx.updateFriendLastProduct(account_id, remote_last_product_id);

                    if (settings.getBoolean("show_friend_products", true)) {

                        System.out.println("last_product_id        " + last_product_id);
                        System.out.println("remote_last_product_id " + remote_last_product_id);

                        Object obj3 = null;

                        //This sometimes throws an error if we get a response that is corrupted.
                        //This will shutdown the app.
                        //java.lang.Error: Error: could not match input
                        try {

                            obj3 = parser.parse(remote_last_product_json);

                        } catch (Error e) {System.out.println("Response is unreadable..");}

                        //Object obj2 = parser.parse(message);
                        JSONObject jsonObject3 = (JSONObject) obj3;

                        //obj.put("date_id",  cursor.getString(cursor.getColumnIndex("date_id")) );
                        //obj.put("item_title",  cursor.getString(cursor.getColumnIndex("item_title")) );
                        //obj.put("item_part_number",  cursor.getString(cursor.getColumnIndex("item_part_number")) );
                        //obj.put("item_picture_1",  cursor.getString(cursor.getColumnIndex("item_picture_1")) );
                        //obj.put("currency",  cursor.getString(cursor.getColumnIndex("currency")) );
                        //obj.put("seller_name",  cursor.getString(cursor.getColumnIndex("seller_name")) );
                        //obj.put("item_price",  cursor.getString(cursor.getColumnIndex("item_price")) );
                        //obj.put("item_total_on_hand",  cursor.getString(cursor.getColumnIndex("item_total_on_hand")) );
                        //obj.put("item_weight",  cursor.getString(cursor.getColumnIndex("item_weight")) );
                        //obj.put("item_package_d",  cursor.getString(cursor.getColumnIndex("item_package_d")) );
                        //obj.put("item_package_l",  cursor.getString(cursor.getColumnIndex("item_package_l")) );
                        //obj.put("item_package_w",  cursor.getString(cursor.getColumnIndex("item_package_w")) );
                        //obj.put("item_description",  cursor.getString(cursor.getColumnIndex("item_description")) );
                        //obj.put("item_notes",  cursor.getString(cursor.getColumnIndex("item_notes")) );
                        //obj.put("item_search_1",  cursor.getString(cursor.getColumnIndex("item_search_1")) );
                        //obj.put("item_search_2",  cursor.getString(cursor.getColumnIndex("item_search_2")) );
                        //obj.put("item_search_3",  cursor.getString(cursor.getColumnIndex("item_search_3")) );
                        //obj.put("custom_1",  cursor.getString(cursor.getColumnIndex("custom_1")) );
                        //obj.put("custom_2",  cursor.getString(cursor.getColumnIndex("custom_2")) );
                        //obj.put("custom_3",  cursor.getString(cursor.getColumnIndex("custom_3")) );

                        String product_date = (String) jsonObject3.get("date_id");
                        String product_item_title = (String) jsonObject3.get("item_title");
                        String product_part_number = (String) jsonObject3.get("item_part_number");
                        String product_picture_1 = (String) jsonObject3.get("item_picture_1");
                        String product_currency = (String) jsonObject3.get("currency");
                        String product_seller_name = (String) jsonObject3.get("seller_name");
                        String product_price = (String) jsonObject3.get("item_price");
                        String product_total_on_hand = (String) jsonObject3.get("item_total_on_hand");
                        String product_weight = (String) jsonObject3.get("item_weight");
                        String product_package_d = (String) jsonObject3.get("item_package_d");
                        String product_package_l = (String) jsonObject3.get("item_package_l");
                        String product_package_w = (String) jsonObject3.get("item_package_w");
                        String product_description = (String) jsonObject3.get("item_description");
                        String product_notes = (String) jsonObject3.get("item_notes");
                        String product_search_1 = (String) jsonObject3.get("item_search_1");
                        String product_search_2 = (String) jsonObject3.get("item_search_2");
                        String product_search_3 = (String) jsonObject3.get("item_search_3");
                        String product_custom_1 = (String) jsonObject3.get("custom_1");
                        String product_custom_2 = (String) jsonObject3.get("custom_2");
                        String product_custom_3 = (String) jsonObject3.get("custom_3");

                        String image_id = request_image_save(onion_address, friend_pub_key, product_picture_1, account_id, account_type);

                        if (image_id != null) {

                            feedx.addProductItem(
                                    Integer.toString(account_id),
                                    "",
                                    product_item_title,
                                    remote_user_name,
                                    remote_user_avatar,
                                    Long.toString(System.currentTimeMillis()),
                                    product_picture_1,
                                    product_description,
                                    product_price,
                                    product_currency
                            );

                        }
                        else {
                            picture_success = false;
                        }

                    }

                }

                if (settings.getBoolean("show_friend_avatar", true) && !remote_user_avatar.equals(friend_avatar)) {

                    String image_id = request_image_save(onion_address, friend_pub_key, remote_user_avatar, account_id, account_type);

                    if (image_id != null) {

                        accountsx.updateFriendAvatar(Integer.toString(account_id), image_id);

                        feedx.addAvatarItem(
                                Integer.toString(account_id),
                                "",
                                remote_user_name,
                                remote_user_avatar,
                                Long.toString(System.currentTimeMillis()),
                                Integer.toString(Feed.FEED_ID_AVATAR),
                                remote_user_avatar
                        );

                    }
                    else {
                        picture_success = false;
                    }

                }

                if (settings.getBoolean("download_friend_backgrounds", true) && !remote_user_background.equals(friend_background)) {

                    String image_id = request_image_save(onion_address, friend_pub_key, remote_user_background, account_id, account_type);

                    if (image_id != null) {

                        accountsx.updateFriendBackground(Integer.toString(account_id), image_id);

                    }
                    else {
                        picture_success = false;
                    }

                }

                if (settings.getBoolean("show_friend_pictures", true) && !remote_picture_id.equals(last_picture_id) && remote_picture_id.length() > 0) {

                    String image_id = request_image_save(onion_address, friend_pub_key, remote_picture_id, account_id, account_type);

                    if (image_id != null) {

                        accountsx.updateFriendAlbum(Integer.toString(account_id), image_id);

                        feedx.addPictureItem(
                                Integer.toString(account_id),
                                "",
                                remote_user_name,
                                remote_user_avatar,
                                Long.toString(System.currentTimeMillis()),
                                Integer.toString(Feed.FEED_ID_PICTURE),
                                remote_picture_id,
                                remote_picture_message_id
                        );

                    }
                    else {
                        picture_success = false;
                    }

                }

                String[] chatix = messagesx.getImageMessageList(Integer.toString(account_id));
                System.out.println("chatix.length " + chatix.length);
                if (chatix.length > 0) {

                    for (int loop = 0; loop < chatix.length; loop++ ) {

                        System.out.println("Getting new chat pictures: " + chatix[loop]);

                        String image_id = request_image_save(onion_address, friend_pub_key, chatix[loop], account_id, account_type);

                        if (image_id != null) {

                            System.out.println("Confirm I got: " + chatix[loop]);

                            messagesx.setImageMessagesReceived(Integer.toString(account_id),image_id);
                            //Chat.bv_image_update.setUpdate(true);

                        }
                        else {
                            picture_success = false;
                            break;
                        }

                    }

                }

                //Set the update time so we know when we connected last.
                accountsx.updateConnectionTime(Integer.toString(account_id), System.currentTimeMillis());

                if (picture_success) {

                    //We got the latest info, our work is done.
                    accountsx.setFriendNeedsUpdate(account_id, 0);

                }

            }//***********************
			else{jsonSentence = "error";}

			System.out.println("Set Friend Update DONE " + account_id);

		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("Cannot find node!");
			jsonSentence = "not active!";

            tor_peer_connected = false;

            if (request_count > 0) {request_count = request_count - 1;}

            System.out.println("requset_count " + request_count);

            //We failed to get the info so we set our attempts -1;
            accountsx.setFriendNeedsUpdate(account_id, request_count);

		}//******************

        Feed.feed_connection.setConnected(false);

		return jsonSentence;

	}//*******************************************************************************************************




    //Main stats request asks the network for the current status happens every 10 seconds.

    public String request_image_save(String onion_address, String friend_pub_key, String image_id, int friend_id, int account_type){//*****************************************************************

        System.out.println("image_id " + image_id);

        String iv_string = null;
        String key_string = null;
        String image = null;
        String jsonText = "";


        try {

            if (image_id.length() == 0) {

                throw new IllegalArgumentException("Image request name cannot be null!");

            }

            long timex = System.currentTimeMillis();

            iv_string = tools_encryption.getIV();
            key_string = tools_encryption.getAESKey();

            System.out.println((System.currentTimeMillis() - timex));

            JSONObject obj = new JSONObject();
            obj.put("request","get_image");
            obj.put("pub_key", settings.getString("pub_key_id", ""));
            obj.put("image_iv", iv_string);
            obj.put("image_key", key_string);
            obj.put("image_id", image_id);
            obj.put("account_type", Integer.toString(account_type));

            StringWriter out = new StringWriter();
            obj.writeJSONString(out);
            jsonText = out.toString();
            System.out.println(jsonText);

            jsonText = aes.encryptMessage(jsonText, friend_pub_key);
            System.out.println(jsonText);

        } catch (Exception e) {System.out.println("JSON ERROR");}


        try {

            System.out.println("address:       " + tor_sock4_port);
            System.out.println("onion address: " + onion_address);

            Socket socket = Utilities.socks4aSocketConnection(onion_address, hiddenServicePort, local_host_connect, tor_sock4_port);//127.0.0.1
            //socket.setSoTimeout(60000);

            System.out.println("socketg");

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter outx = new PrintWriter(outputStream);
            outx.print(jsonText + "\r\n\r\n");
            outx.flush();
            InputStream inputStream = socket.getInputStream();
            //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            //BufferedReader in = new BufferedReader(inputStreamReader);

            System.out.println("socketw");

            // this dynamically extends to take the bytes you read
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

            // this is storage overwritten on each iteration with bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            // we need to know how may bytes were read to write them to the byteBuffer
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            //and then we can return your byte array.

            try {

                byte[] buffer2  = aes.decryptImage(byteBuffer.toByteArray(),key_string,iv_string);

                options.inMutable = true;
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer2, 0, buffer2.length, options);
                //Canvas canvas = new Canvas(bmp); // now it should work ok

                //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                outputStream.close();
                outx.close();
                //in.close();
                //socket.close();

                System.out.println("bitmap.getHeight() " + bitmap.getHeight());

                file_save_picture picturex = new file_save_picture();
                picturex.savePicture(image_id,bitmap);

                image = image_id;

            } catch (Exception e) {

                e.printStackTrace();

                //Show connection which friend needs to update.
                accountsx.setFriendNeedsUpdate(friend_id, 10);

                //Inform connection we have work to send.
                //Main.work_ready = true;

                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("work_ready", true);
                editor.commit();

            }//******************


        } catch (Exception e) {

            e.printStackTrace();
            System.out.println("Cannot find node!");
            tor_peer_connected = false;

        }//******************


        return image;

    }//*******************************************************************************************************





}//last
