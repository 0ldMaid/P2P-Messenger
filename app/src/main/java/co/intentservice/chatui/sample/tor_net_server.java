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

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.junit.Assert;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;


public class tor_net_server{

    private static final int TOTAL_SECONDS_PER_TOR_STARTUP = 4 * 60;
    private static final int TOTAL_TRIES_PER_TOR_STARTUP = 5;
    private static final int WAIT_FOR_HIDDEN_SERVICE_MINUTES = 3;
    static final String hiddenServiceManagerDirectoryName = "hiddenservicemanager";
    public static OnionProxyManager hiddenServiceManager;
    static String serverOnionAddress = "Server is not active!";

    static SharedPreferences settings;
    SharedPreferences.Editor editor;

    static JSONParser parser = new JSONParser();

    static tools_encryption aes = new tools_encryption();
    static database_messages messagesx = new database_messages();
    static database_accounts accountx = new database_accounts();

    static int localPort = 9343;
    static int hiddenServicePort = 9344;

    static boolean start_once = true;

	//This is the server it can handel requests from other nodes and also from TOR browsers.
	//The TOR system is started and run from the client class. We just connect to the port from this class.

	tor_net_server(){//*********************************************************

	    try {

            //Build the server.

            //These settings are set in the settings screen but we load a few of them here because the system needs them.
            settings = PreferenceManager.getDefaultSharedPreferences(Main.context2);
            editor = settings.edit();

            ApplicationInfo appInfo = Main.context2.getApplicationInfo();
            //File installDir = new File(appInfo.nativeLibraryDir, "");
            File writeDir = new File(Main.context2.getFilesDir(), "");

            File installDir = new File(writeDir + "/" + hiddenServiceManagerDirectoryName);
            System.out.println("> " + installDir);
            TorConfig torConfig = AndroidTorConfig.createConfig(installDir, installDir, Main.context2);
            System.out.println("torConfig " + torConfig.toString());

            TorInstaller torInstaller = new AndroidTorInstaller(Main.context2, installDir) {
                @Override
                public InputStream openBridgesStream() throws IOException {
                    //return context.getResources().openRawResource(R.raw.bridges);
                    return null;
                }
            };

            hiddenServiceManager = new AndroidOnionProxyManager(Main.context2, torConfig, torInstaller, new DefaultSettings(), null, null);
            // Note: Normally you never want to call anything like deleteTorWorkingDirectory since this
            // is where all the cached data about the Tor network is kept and it makes connectivity
            // must faster. We are deleting it here just to make sure we are running clean tests.
            //deleteTorWorkingDirectory(hiddenServiceManager.getContext().getConfig().getConfigDir());

            File torrc = new File(torConfig.getTorrcFile().getParentFile(), "/torrc");
            System.out.println("torrc " + torrc);
            System.out.println("torrc " + torrc.exists());

            if (!torrc.exists()) {

                hiddenServiceManager.setup();

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
                buildTorrc.append("SOCKSPort 9000").append("\n");
                buildTorrc.append("DNSPort 5400").append("\n");
                buildTorrc.append("TransPort 8000").append("\n");
                buildTorrc.append("CookieAuthentication 1").append("\n");
                buildTorrc.append("DisableNetwork 1").append("\n");

                FileOutputStream stream = new FileOutputStream(torrc);
                try {
                    stream.write(buildTorrc.toString().getBytes());
                } finally {
                    stream.close();
                }

            }

            Assert.assertTrue(hiddenServiceManager.startWithRepeat(TOTAL_SECONDS_PER_TOR_STARTUP, TOTAL_TRIES_PER_TOR_STARTUP, true));
            System.out.println("Hidden Service Manager is running.");

            int localPort = 9343;
            int hiddenServicePort = 9344;
            serverOnionAddress = hiddenServiceManager.publishHiddenService(hiddenServicePort, localPort);
            System.out.println("onionAddress for test hidden service is:  " + serverOnionAddress);
            //System.out.println("clientManager.getIPv4LocalHostSocksPort() " + clientManager.getIPv4LocalHostSocksPort());

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("server_onion_address", serverOnionAddress);
            editor.commit();

            System.out.println("onionAddress for test hidden service is:  " + settings.getString("server_onion_address", ""));

            if (start_once) {

                Timer xtimerx = new Timer();
                xtimerx.schedule(new RemindTask_server(), 0);

                //cannot use this again.
                start_once = false;

            }//***************

        }  catch (Exception e){

	        e.printStackTrace();

        }

	}//*****************************************************************************



	//This is the server port manager to handel multiple requests.
    //Thanks to people on stack overflow for this.

	static class RemindTask_server extends TimerTask{

        Runtime rxrunti = Runtime.getRuntime();

        public void run(){//************************************************************************************

            //always on
            while (true) {

                ServerSocket serverSocket = null;

                try {

                    serverSocket = new ServerSocket(localPort);
                    //serverSocket.setSoTimeout(60000);

                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(30000);
                    //Delegate to new thread
                    new Thread(new serverInput(clientSocket)).start();

                    serverSocket.close();

                } catch (SocketTimeoutException e) {

                    System.out.println("Server timeout 60 seconds try again...");

                } catch (Exception e) {

                    //e.printStackTrace();

                    //try {serverSocket.close();} catch (Exception ex){}

                    try {Thread.sleep(1000);} catch (InterruptedException ex){}

                }//*****************


                System.out.println("Server is off...");
                try {Thread.sleep(1000);} catch (InterruptedException e){}

            }//*****while

        }//runx***************************************************************************************************

    }//remindtask



    //Server thread that allows multiple incoming connections.

	static class serverInput implements Runnable {

	   private final Socket clientSocket; //initialize in const'r


		public serverInput(Socket clientSocketx){

			clientSocket = clientSocketx;

		}//**************************************


	    public void run() {

            try {

                //String sign_message = "";
                String image_send = "";
                String image_iv = "";
                String image_key = "";
                String remote_read_time = "";
                String client_sentence = "";
                String jsonText = "";
                String statex = "";
                String responsex = "";
                String friend_key = "";
                String account_type = "";
                String version_id = "";

                boolean send_html = false;
                boolean send_image = false;

                if (clientSocket != null) {System.out.println("Connected");}

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String inputLine;
                while (!(inputLine = in.readLine()).equals("")) {

                    System.out.println(inputLine);
                    client_sentence = client_sentence + inputLine;

                    if (inputLine.equals("\r") || inputLine.equals("\n")) {System.out.println("BREAK>>>"); break;}
                    else {System.out.println("xx");}

                }//**********************************************


                client_sentence = aes.decryptMessage(client_sentence, settings.getString("prv_key_id", ""));
                System.out.println(client_sentence);

                //in.close();

                System.out.println(">>>");

                //Make sure the client is using tor and does not have an IP address.
                String client_ip = clientSocket.getRemoteSocketAddress().toString();
                System.out.println("CLIENT ADDRESS " + clientSocket.getRemoteSocketAddress().toString());


                try {

                    statex = "0";

                    Object obj = null;

                    //This sometimes throws an error if we get a response that is corrupted.
                    //This will shutdown the app.
                    //java.lang.Error: Error: could not match input
                    //JSONParser parser = new JSONParser();
                    try {

                         obj = parser.parse(client_sentence);

                    } catch (Error e) {System.out.println("Response is unreadable...");}

                    JSONObject jsonObject = (JSONObject) obj;

                    String request = (String) jsonObject.get("request");
                    System.out.println("[CONNECTION] " + request);

                    String new_messages = null;

                    //Populate the info.
                    try {friend_key =         (String) jsonObject.get("pub_key").toString();}          catch (Exception e) {}
                    try {version_id =         (String) jsonObject.get("version").toString();}       catch (Exception e) {}
                    try {new_messages =       (String) jsonObject.get("new_messages").toString();}     catch (Exception e) {}
                    try {image_send =         (String) jsonObject.get("image_id").toString();}         catch (Exception e) {}
                    try {image_iv =           (String) jsonObject.get("image_iv").toString();}         catch (Exception e) {}
                    try {image_key =          (String) jsonObject.get("image_key").toString();}        catch (Exception e) {}
                    try {remote_read_time =   (String) jsonObject.get("messages_read").toString();}    catch (Exception e) {}
                    try {account_type =       (String) jsonObject.get("account_type").toString();}     catch (Exception e) {}

                    int friend_status = 0;

                    //It's OK to be friends with ourselves.
                    System.out.println("pub_key      " + friend_key);
                    System.out.println("my_key       " + settings.getString("pub_key_id", ""));
                    System.out.println("account_type " + account_type);

                    //if the user is a friend we check
                    if (account_type.equals(Integer.toString(Account.SET_DB_ADMIN_INT)) &&
                                          friend_key.equals(settings.getString("pub_key_id", ""))) {

                        friend_status = Account.SET_DB_STATUS_FRIEND_CONFIRMED;

                    }
                    else if(account_type.equals(Integer.toString(Account.SET_DB_FRIEND_INT))) {

                        friend_status = accountx.getFriendStatusFromKey(friend_key);

                    }
                    //if we are allowing followers we can show them our stats.
                    else if (settings.getBoolean("following_allowed", false)) {

                        friend_status = 1;

                    }

                    System.out.println("friend_status >>> " + friend_status);

                    try {

                        //These are the possible server requests we can get from the user.
                        //If it's not a node and just a browser then we send them HTML.
                        //These are only for nodes to use.

                        //We need 2 variables from each method so we have to package them into an array before we return them.
                        String[] test_statex_json = new String[2];

                        if (request.equals("status") && friend_status > Account.SET_DB_STATUS_BLOCKED) {

                            //The user is requesting our status.

                            responsex = getStatus(friend_key, remote_read_time, new_messages);
                            statex = "1";

                        }
                        else if(request.equals("receive_message") && friend_status == Account.SET_DB_STATUS_FRIEND_CONFIRMED) {

                            //Get a toke listings using it's HASH

                            test_statex_json = receiveMessage("");
                            statex = test_statex_json[0];
                            responsex = test_statex_json[1];

                        }
                        else if(request.equals("get_image") && friend_status > Account.SET_DB_STATUS_BLOCKED) {

                            //Get a toke listings using it's HASH

                            if (image_send.length() > 0) {

                                send_image = true;

                            }
                            else {

                                send_image = false;
                                statex = "0";

                            }

                        }
                        else {

                            //The user is requesting something we don't provide.
                            statex = "0";
                            responsex = "Request Error!";

                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        statex = "0";
                        responsex = "Server Error!";

                    }


                } catch (Exception e) {

                    e.printStackTrace();
                    System.out.println("JSON ERORR SEND HTML");

                    statex = "0";
                    responsex = "Connection Error!";

                }


                //If the client using the TOR browser is requesting an image or the HTML code.
                if (!send_image) {

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

                    JSONObject obj = new JSONObject();
                    obj.put("response", statex);

                    try {

                        obj.put("message", responsex);

                    } catch (Exception e) {e.printStackTrace();}

                    StringWriter outs = new StringWriter();
                    obj.writeJSONString(outs);
                    jsonText = outs.toString();

                    if (friend_key.length() > 0) {

                        jsonText = aes.encryptMessage(jsonText, friend_key);
                        System.out.println(jsonText);

                    }

                    if (!send_html) {out.print(jsonText);}
                    else {out.print(responsex);}

                    out.flush();
                    out.close();

                }//**************
                else {

                    System.out.println("Image test: " + image_send);

                    OutputStream outputStream = clientSocket.getOutputStream();

                    //We are only going to allow our picture from this server.
                    File file = new File(Main.directory, "/" + image_send + ".png");//Or any other format supported
                    FileInputStream streamIn = new FileInputStream(file);
                    //InputStream is = MainActivity.context2.getAssets().open("hex100.jpg");
                    Bitmap bitmap = BitmapFactory.decodeStream(streamIn);

                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArray);

                    byte[] buffer = aes.encryptImage(byteArray.toByteArray(),image_key,image_iv);

                    System.out.println(buffer.length);
                    outputStream.write(buffer);


                    outputStream.flush();
                    outputStream.close();

                }//**

                clientSocket.close();
                //serverSocket.close();


            }//try
            catch (Exception e) {

                e.printStackTrace();

                System.err.println("Could not listen on port: " + Main.p2p_port);

                //try {Thread.sleep(1000);} catch (InterruptedException ex) {System.out.println("Cannot sleep!");}

            }//******************


            //try {Thread.sleep(1000);} catch (InterruptedException e){}


	    }//run

	}//****************************************************



    static String[] receiveMessage(String id){

        System.out.println("Get token id");

        String test_statex = "0";
        String jsonText = "";

        try {


            System.out.println("GET TOKEN FROM ID...");
            String[] token_array = null;


            JSONObject obj = new JSONObject();
            int xxp1 = 0;
            for (int loop = 0; loop < token_array.length; loop++) {//***********

                obj.put("l" + Integer.toString(xxp1), token_array[loop]);
                System.out.println("l" + token_array[loop]);
                xxp1++;

            }//*****************************************************************


            if (token_array[0].length() > 3){test_statex = "1";}
            else {test_statex = "0";}

            System.out.println(JSONValue.toJSONString(obj));

            jsonText = JSONValue.toJSONString(obj);


        } catch (Exception e) {e.printStackTrace(); test_statex = "0"; jsonText = "error";}


        String[] jsonText2 = new String[2];

        jsonText2[0] = test_statex;
        jsonText2[1] = jsonText;

        return jsonText2;

    }//******************************



    static String getStatus(String friend_key, String remote_read_time, String new_messages){

        System.out.println("Get status");

        String friend_status = "0";
        String friend_id = "";
		String jsonText = "";
		String messages_received = "";
        String messages_failed = "";

        try {

            //database_accounts getx = new database_accounts();
            String[][] friendsx = accountx.getFriendsList();

            for (int xloop1 = 0; xloop1 < friendsx[0].length; xloop1++) {//***********

                if (friendsx[4][xloop1].equals(friend_key)) {

                    friend_id = friendsx[0][xloop1];
                    friend_status = friendsx[11][xloop1];

                    System.out.println("friend_id     " + friend_id);
                    System.out.println("friend_status " + friend_status);

                }//********************************************************

            }//************************************************************************************

            Object obj1 = null;

            //This sometimes throws an error if we get a response that is corrupted.
            //This will shutdown the app.
            //java.lang.Error: Error: could not match input
            //JSONParser parser = new JSONParser();
            try {

                obj1 = parser.parse(new_messages);

            } catch (Error e) {System.out.println("Response is unreadable..");}

            //Object obj2 = parser.parse(message);
            JSONObject jsonObject = (JSONObject) obj1;

            System.out.println("jsonObject.size() " + jsonObject.size());

            JSONObject obj_added = new JSONObject();
            JSONObject obj_failed = new JSONObject();

            boolean new_message_added = false;
            boolean new_message_failed = false;
            boolean image_message_added = false;

            int message_added_count = 0;
            int message_failed_count = 0;

            for (int xloop1 = 0; xloop1 < jsonObject.size(); xloop1++) {//***********

                //Get strings from the JSON string.
                String json_message = (String) jsonObject.get(Integer.toString(xloop1));
                System.out.println("json_message " + json_message);

                Object obj2 = parser.parse(json_message);
                JSONObject jsonObject2 = (JSONObject) obj2;

                String message_text = (String) jsonObject2.get("message");
                String message_date = (String) jsonObject2.get("date_id");
                String message_type = (String) jsonObject2.get("type");

                System.out.println("message_text " + message_text);
                System.out.println("message_date " + message_date);
                System.out.println("message_type " + message_type);

                boolean addedx = false;

                if (message_type.equals(Integer.toString(Chat.MESSAGE_TYPE_TEXT_RECEIVED))) {

                    addedx = messagesx.addMessageRemote(
                                                 friend_id,
                                                 Long.parseLong(message_date),
                                                 message_text,
                                                 Integer.parseInt(message_type),
                                                 Chat.SET_MESSAGE_STATUS_SENT);

                }
                else if (message_type.equals(Integer.toString(Chat.MESSAGE_TYPE_IMAGE_RECEIVED))) {

                    addedx = messagesx.addMessageRemote(
                                                 friend_id,
                                                 Long.parseLong(message_date),
                                                 message_text,
                                                 Integer.parseInt(message_type),
                                                 Chat.SET_MESSAGE_STATUS_GET_IMAGE);

                    if (addedx) {

                        image_message_added = true;

                    }

                }

                if (addedx) {

                    System.out.println("New message added...");

                    obj_added.put(Integer.toString(message_added_count), message_date);

                    new_message_added = true;
                    message_added_count++;

                }//*********
                else {

                    System.out.println("New message failed...");

                    obj_failed.put(Integer.toString(message_failed_count), message_date);

                    new_message_failed = true;
                    message_failed_count++;

                }


            }//**********************************************************************

            //The list of messages added correctly.
            if (new_message_added) {

                //Alert chat we have new message.
                Chat.bv_update.setUpdate(true);
                Main.bv_update.setUpdate(true);

                messages_received = JSONValue.toJSONString(obj_added);

                //We have to notify the client we need an update.
                if (image_message_added) {

                    accountx.setFriendNeedsUpdate(Integer.parseInt(friend_id), 1);

                    //Inform connection we have work to send.
                    //Main.work_ready = true;

                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("work_ready", true);
                    editor.commit();

                }

            }//*********************

            //The list of messages that failed.
            if (new_message_failed) {

                messages_failed = JSONValue.toJSONString(obj_failed);

            }

            //We get what messages the client has read.
            if (Long.parseLong(remote_read_time) < System.currentTimeMillis()) {

                messagesx.setMessagesRead(friend_id, Long.parseLong(remote_read_time));
                //Chat.bv_read.setRead(true);

            }

        } catch (Exception e) {e.printStackTrace();}

		try {

			JSONObject obj = new JSONObject();

			obj.put(Integer.toString(tor_net_client.SERVER_SET_ID_ACTIVE),"1");
            obj.put("version", Main.program_version);
            obj.put("user_name", settings.getString("user_name", "John Smith"));
            obj.put("friend_status", friend_status);
            obj.put("user_birthday", settings.getString("user_birthday", ""));
            obj.put("user_relationship_status", settings.getString("user_relationship_status", ""));
            obj.put("user_job_status", settings.getString("user_job_status", ""));
            obj.put("account_type", Integer.toString(accountx.getAccountType(friend_key)));
            obj.put("user_status", settings.getString("user_status", ""));
            obj.put("messages_received", messages_received);
            obj.put("messages_failed", messages_failed);
            obj.put("messages_read", Long.toString(messagesx.getRead(friend_id)));
            obj.put("user_avatar", settings.getString("user_avatar", ""));
            obj.put("user_background", settings.getString("user_background", ""));
            obj.put("new_dapps_id", Main.last_dapp_id);
            obj.put("new_product_id", Main.last_listing_id);
            obj.put("new_product_listing", Main.last_listing_json);
            obj.put("last_picture_id", Main.last_picture_id);
            obj.put("last_picture_message_id", Main.last_picture_message_id);

            System.out.println(JSONValue.toJSONString(obj));

			jsonText = JSONValue.toJSONString(obj);

		} catch (Exception e) {e.printStackTrace();}

		return jsonText;

    }//status****************



}//last
