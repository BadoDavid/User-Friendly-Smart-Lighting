package hu.bme.mit.ufsmartlighting;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.bme.mit.ufsmartlighting.device.DeviceAdapter;
import hu.bme.mit.ufsmartlighting.device.DeviceItem;
import hu.bme.mit.ufsmartlighting.device.DeviceViewHolder;

public class MainActivity extends AppCompatActivity
        implements WiFiApDialogFragment.setOnWiFiApSSIDListener, DeviceViewHolder.OnItemChangedListener, DetailsActivity.OnDeviceStateChangedListener {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;

    TextView tv;

    private boolean networkFine = false;

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;

    private SwipeRefreshLayout pullToRefresh;

    private WifiManager.MulticastLock wifiLock;
    private WifiMonitoringReceiver wifiMonitoringReceiver;

    private String ipAddr;
    private int server_port;

    private String smartLightingState;

    private boolean wifiOK = false;

    private String wifiSSID;

    private String wifiPassword;

    public WifiManager wifi;

    List<ScanResult> wifiList;
    List<String> values = new ArrayList<String>();

    int netCount=0;

    /**
     * BroadcastReceiver which handle Wifi status changes
     */
    private final BroadcastReceiver mWifiStatusChenged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            Toast.makeText(getApplicationContext(), "Wifi is state changed!!!!", Toast.LENGTH_LONG).show();

            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if(!networkInfo.isConnected()) {
                    //do stuff
                    networkFine = false;
                }

                //Other actions implementation
            }

            if(wifi.getConnectionInfo().getSSID().contains("Bado"))
            {
                //handleMulticastMsg(); TODO: somewhere else
                wifiOK = true;
            }
            else
            {
                wifiOK = false;
            }
        }
    };

    /**
     * BroadcastReceiver which handles WifiScan results
     */
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            wifiList = wifi.getScanResults();

            netCount = wifiList.size();
            // wifiScanAdapter.notifyDataSetChanged();
            Log.d("Wifi","Total Wifi Network"+netCount);

            // add your logic here
            if(!wifiOK) {
                for (int j = 0; j < wifiList.size(); j++) {
                    if (wifiList.get(j).SSID.contains("Bado")) {
                        String networkSSID = wifiList.get(j).SSID;
                        String networkPass = "";

                        WifiConfiguration conf = new WifiConfiguration();
                        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes

                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                        wifi.addNetwork(conf);

                        List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                        for (WifiConfiguration configs : list) {
                            if (configs.SSID != null && configs.SSID.equals("\"" + networkSSID + "\"")) {
                                wifi.disconnect();
                                wifi.enableNetwork(configs.networkId, true);
                                wifi.reconnect();
                                wifiOK = true;

                                break;
                            }
                        }

                        break;
                    }
                }
            }

            /*
            if(wifiList.size() == 0)
            {
                wifi.startScan();
            }
            */
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initRecyclerView();

        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled() == false)
        {
            Toast.makeText(this, "Wifi is disabled enabling...", Toast.LENGTH_LONG).show();
            wifi.setWifiEnabled(true);
        }

        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("MulticastUdp");
            lock.acquire();
        }

        setWifiMonitorRegistered(true);

        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        registerReceiver(mWifiStatusChenged,
                new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = ""; //wifi.getConnectionInfo().getSSID();
        wifiSSID = sharedPref.getString(getString(R.string.saved_wifi_ssid), defaultValue);
        wifiPassword = sharedPref.getString(getString(R.string.saved_wifi_password), defaultValue);

        Toast.makeText(getApplicationContext(), wifiSSID, Toast.LENGTH_SHORT).show();

        // If password is not saved or SSID not equals to the saved value
        if(wifiPassword.equals(defaultValue) || wifiSSID.equals(defaultValue)) //|| !wifiSSID.equals(defaultValue))
        {
            setWifiFragment();
        }

        /*
            int defaultValue = getResources().getInteger(R.integer.saved_high_score_default_key);
            int highScore = sharedPref.getInt(getString(R.string.saved_high_score_key), defaultValue);
         */

        //wifi.disconnect();


        //register Broadcast receiver
        /*
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiList=wifi.getScanResults();
                netCount=wifiList.size();
                //adapter.notifyDataSetChanged();
                Log.d("Wifi","Total Wifi Network"+netCount);
            }
        },new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkandAskPermission();
        }

        wifi.startScan();

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleMulticastMsg();
            }
        });

        //pullToRefresh.setRefreshing(true);

        //handleMulticastMsg();
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.MainRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Receiving UDP Multicast messages = save the device IP address and port number
     */
    private void handleMulticastMsg() {

        final List<String> messages = new ArrayList<>();

        new Thread() {
            public void run() {

                if (wifi != null){
                    WifiManager.MulticastLock lock = wifi.createMulticastLock("MulticastUdp");
                    lock.acquire();
                }

                MulticastSocket socket = null;
                InetAddress group = null;

                try {
                    int ipAddress = wifi.getConnectionInfo().getIpAddress();
                    InetAddress inetAddress = InetAddress.getByAddress(new byte[]{(byte) (ipAddress & 255), (byte) ((ipAddress >> 8) & 255), (byte) ((ipAddress >> 16) & 255), (byte) ((ipAddress >> 24) & 255)});
                    NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(inetAddress);
                    socket = new MulticastSocket(7235);
                    socket.setNetworkInterface(byInetAddress);
                    socket.joinGroup(InetAddress.getByName("224.1.1.1"));
                    socket.setSoTimeout(5000);
                    socket.setTimeToLive(20);

                    //group = InetAddress.getByName("224.1.1.1");
                    //socket = new MulticastSocket(7235);
                    //socket.joinGroup(group);

                    DatagramPacket packet = new DatagramPacket(new byte[512], 512);
                    while (true) {
                        byte[] buf = new byte[512];
                        packet = new DatagramPacket(buf, buf.length);

                        if (socket == null) {
                            break;
                        }

                        socket.receive(packet);

                        final String recvMsg = new String(packet.getData(), 0, packet.getLength());

                        System.out.println(recvMsg);

                        JSONObject jsonObj = new JSONObject(recvMsg);

                        ipAddr = jsonObj.getString("ip_address");

                        server_port = jsonObj.getInt("port_num");

                        smartLightingState = jsonObj.getString("led_state");

                        /*
                        ipAddr = "";

                        // Java byte values are signed. Convert to an int so we don't have to deal with negative values for bytes >= 0x7f (unsigned).
                        int[] valueBuf = new int[4];
                        int ii;
                        for (ii = 0; ii < (valueBuf.length-1); ii++) {
                            valueBuf[ii] = (buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256;

                            //final int value = (valueBuf[0] << 8) | valueBuf[1];

                            ipAddr = ipAddr + String.valueOf(valueBuf[ii]) + ".";

                        }

                        valueBuf[ii] = (buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256;

                        ipAddr = ipAddr + String.valueOf(valueBuf[ii]);

                        ii++;

                        server_port = (buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256;

                        ii++;

                        server_port += ((buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256) << 8;

                        ii++;

                        System.out.println(ipAddr);

                        Log.d("MainActivity", ipAddr);

                        final String myString = ipAddr + ":" + String.valueOf(server_port);

                        String alreadyConnected = String.valueOf((buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256);

                        System.out.println(alreadyConnected);

                        if(alreadyConnected.equals("58")) {
                            // save led state!
                            for (; ii < 20; ii++) {
                                smartLightingState = smartLightingState + String.valueOf((buf[ii] >= 0) ? (int) buf[ii] : (int) buf[ii] + 256);
                            }
                        }
                        else
                        {
                            smartLightingState = myString;
                        }

                        System.out.println(smartLightingState);
                        */

                        // We're running on a worker thread here, but we need to update the list view from the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //pullToRefresh.setRefreshing(false);

                                Toast.makeText(getApplicationContext(), recvMsg, Toast.LENGTH_SHORT).show();
                                //tv.setText(myString);

                                DeviceItem item;

                                if("NA".equals(smartLightingState))
                                {
                                    item = new DeviceItem("Smart Bulb", "NA",
                                                    new Long(0), ipAddr, server_port);
                                }
                                else
                                {
                                    Long ledValue = Long.valueOf(smartLightingState.substring(4,10), 16);

                                    item = new DeviceItem("Smart Bulb", smartLightingState.substring(0, 3),
                                                ledValue , ipAddr, server_port);
                                }

                                adapter.addDevice(item);

                                adapter.notifyDataSetChanged();
                            }
                        });

                        // TODO: Needed a sopisticated solution like count addDevice errors
                        if(messages.contains(ipAddr))
                        {
                            break;
                        }
                        else
                        {
                            messages.add(ipAddr);
                        }
                        //break;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            pullToRefresh.setRefreshing(false);
                        }
                    });
                }
                catch(IOException | JSONException e) {
                    System.out.println(e.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            pullToRefresh.setRefreshing(false);

                            Toast.makeText(getApplicationContext(), "No device found!", Toast.LENGTH_LONG);
                            //tv.setText(myString);
                            //adapter.addDevice(myString);
                        }
                    });
                }
                finally {
                    if (socket != null) {
                        try {
                            if (group != null) {
                                socket.leaveGroup(group);
                            }
                            socket.close();
                        }
                        catch(IOException e) {

                        }
                    }
                }
            }
        }.start();
    }

    private void setWifiMonitorRegistered(boolean z) {
        if (z) {
            if (this.wifiMonitoringReceiver != null) {
                unregisterReceiver(this.wifiMonitoringReceiver);
            }
            this.wifiMonitoringReceiver = new WifiMonitoringReceiver(this);
            registerReceiver(this.wifiMonitoringReceiver, new IntentFilter("android.net.wifi.STATE_CHANGE"));
        } else if (this.wifiMonitoringReceiver != null) {
            unregisterReceiver(this.wifiMonitoringReceiver);
            this.wifiMonitoringReceiver = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            setWifiFragment();

            return true;
        }

        if (id == R.id.action_whoamI) {

            String mySSID, myPass;

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            String defaultValue = ""; //wifi.getConnectionInfo().getSSID();
            mySSID = sharedPref.getString(getString(R.string.saved_wifi_ssid), defaultValue);
            myPass = sharedPref.getString(getString(R.string.saved_wifi_password), defaultValue);

            Toast.makeText(this, mySSID+"::"+myPass, Toast.LENGTH_SHORT)
                    .show();

            wifi.startScan();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setWifiFragment(){

        wifi.startScan();

        WiFiApDialogFragment wifiApFragment = new WiFiApDialogFragment();

        wifiApFragment.setOnWiFiApSSIDListener(this);

        wifiApFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);

        wifiApFragment.show(getSupportFragmentManager(), wifiApFragment.TAG);
                //show(getFragmentManager(), wifiApFragment.TAG);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    wifi.startScan();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    private void checkandAskPermission() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Network");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 0; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }

            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        // initVideo();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onDeviceSelected(final DeviceItem device) {

        if(wifi.getConnectionInfo().getSSID().contains(wifiSSID)){
//        if(networkFine) {
            Intent showDetailsIntent = new Intent();
            showDetailsIntent.setClass(MainActivity.this, DetailsActivity.class);
            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_NAME, device.getName());
            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_STATE, device.getState());
            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_IPADDR, device.getAddress());
            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_PORT, device.getPort());
            startActivity(showDetailsIntent);
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to connect your own network? ")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            new Thread() {
                                public void run() {

                                    DatagramSocket udpSocket = null;
                                    InetAddress local = null;

                                    try
                                    {
                                        //String messageStr = color + String.valueOf(number);

                                        udpSocket = new DatagramSocket();
                                        local = InetAddress.getByName(ipAddr);

                                        int ssidLength = wifiSSID.length();
                                        int passwordLength = wifiPassword.length();

                                        byte[] message = new byte[ssidLength+passwordLength+3];

                                        message[0] = (byte) 'N';

                                        /*                                      /*
                                        message[1] = (byte) 'S';
                                        message[2] = (byte) '_';
                                        message[3] = (byte) 'H';
                                        message[4] = (byte) 'o';
                                        message[5] = (byte) 'm';
                                        message[6] = (byte) 'e';
                                        message[7] = (byte) ':';
                                        */
                                        for (int i = 0; i < ssidLength; i++)
                                        {
                                            message[i+1] = (byte) wifiSSID.charAt(i);
                                        }

                                        message[ssidLength+1] = (byte) ':';

                                        /*
                                        message[8] = (byte) 's';
                                        message[9] = (byte) 'a';
                                        message[10] = (byte) 'g';
                                        message[11] = (byte) 'o';
                                        message[12] = (byte) 'd';
                                        message[13] = (byte) 'i';
                                        message[14] = (byte) 'e';
                                        message[15] = (byte) 'k';
                                        */
                                        for (int i = 0; i < passwordLength; i++)
                                        {
                                            message[ssidLength+i+2] = (byte) wifiPassword.charAt(i);
                                        }

                                        message[ssidLength+passwordLength+2] = (byte) ':';

                                        System.out.println(wifiSSID+":"+wifiPassword);

                                        JSONObject object = new JSONObject();

                                        object.put("type", "Network");
                                        object.put("ssid", wifiSSID);
                                        object.put("password", wifiPassword);

                                        System.out.print(object.toString());

                                        String stringMsg =  object.toString();

                                        byte[] msg = stringMsg.getBytes();

                                        System.out.println(msg.length);

                                        DatagramPacket p = new DatagramPacket(msg, msg.length, local, server_port);

                                        //DatagramPacket p = new DatagramPacket(message, message.length,local,server_port);
                                        udpSocket.send(p);

                                        /*
                                        String networkSSID = "S_Home";
                                        //String networkPass = "";
                                        */

                                        List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                                        for (WifiConfiguration configs : list) {
                                            if (configs.SSID != null && configs.SSID.equals("\"" + wifiSSID + "\"")) {
                                                wifi.disconnect();
                                                wifi.enableNetwork(configs.networkId, true);
                                                wifi.reconnect();
                                                //wifiOK = true;

                                                wifi.startScan();

                                                //handleMulticastMsg();
                                                break;
                                            }
                                        }
                                    }
                                    catch(IOException | JSONException e) {
                                        System.out.println(e.toString());
                                    }
                                }
                            }.start();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent showDetailsIntent = new Intent();
                            showDetailsIntent.setClass(MainActivity.this, DetailsActivity.class);
                            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_NAME, device.getName());
                            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_STATE, device.getState());
                            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_IPADDR, device.getAddress());
                            showDetailsIntent.putExtra(DetailsActivity.EXTRA_DEVICE_PORT, device.getPort());
                            startActivity(showDetailsIntent);
                        }
                    });
            builder.create().show();
        }
    }

    @Override
    public void onDeviceDeleted(DeviceItem item) {

        // If device deleted start multicast handling
        handleMulticastMsg();
    }

    @Override
    public void onWiFiApSSID(String wifiApSSID, String wifiApPassword) {

        wifiSSID = wifiApSSID;
        wifiPassword = wifiApPassword;

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_wifi_ssid), wifiApSSID);
        editor.putString(getString(R.string.saved_wifi_password), wifiApPassword);
        editor.commit();

        Toast.makeText(getApplicationContext(), wifiApSSID, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnDeviceStateChanged(Long number) {

    }
}
