package hu.bme.mit.ufsmartlighting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.service.carrier.CarrierService;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

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
        implements WiFiApDialogFragment.setOnWiFiApSSIDListener, DeviceViewHolder.OnItemChangedListener {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 125;

    static final int DEVICE_STATE_REQUEST = 1;

    TextView tv;

    private boolean networkFine = false;

    private RecyclerView recyclerView;
    private static DeviceAdapter adapter;

    private SwipeRefreshLayout pullToRefresh;

    private ProgressDialog progressDialog;

    private WifiManager.MulticastLock wifiLock;
    private WifiMonitoringReceiver wifiMonitoringReceiver;

    private String ipAddr;
    private int server_port;

    private String smartLightingState;

    private boolean wifiOK = false;

    TextView tvConnectedWifiName;

    private String wifiSSID;

    private String wifiPassword;

    public WifiManager wifi;

    List<ScanResult> wifiList;
    List<String> values = new ArrayList<String>();

    int netCount=0;

    private void createProgressDialog() {

        progressDialog.setTitle("WiFi connectivty");

        progressDialog.setMessage("Connect to another access point!");

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setCancelable(false);

        progressDialog.create();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "In progress...", Toast.LENGTH_SHORT).show();
                progressDialog.show();
            }
        });

    }

    /**
     * BroadcastReceiver which handle Wifi status changes
     */
    private final BroadcastReceiver mWifiStatusChenged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            //Toast.makeText(getApplicationContext(), "Wifi is state changed!!!!", Toast.LENGTH_LONG).show();

            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                final NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                if(!networkInfo.isConnected()) {
                    //do stuff
                    networkFine = false;
                    createProgressDialog();
                }
                else
                {
                    new AsyncTask<Void, Void, String>() {

                        @Override
                        protected void onPreExecute ()
                        {
                            progressDialog.setMessage("Connected!");
                            //Toast.makeText(getApplicationContext(), "Completed!4!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        protected String doInBackground(Void... voids)
                        {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return "Start multicast handling!";
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            progressDialog.dismiss();
                            tvConnectedWifiName.setText(wifi.getConnectionInfo().getSSID());
                            pullToRefresh.setRefreshing(true);

                            handleMulticastMsg();
                            super.onPostExecute(s);
                        }
                    }.execute();
                }

                //Other actions implementation
            }

            if(wifi.getConnectionInfo().getSSID().contains("SLD"))
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
                    if (wifiList.get(j).SSID.contains("SLD")) {
                        String networkSSID = wifiList.get(j).SSID;
                        String networkPass = "";

                        WifiConfiguration conf = new WifiConfiguration();
                        conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes

                        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                        wifi.addNetwork(conf);

                        List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                        for (WifiConfiguration configs : list) {
                            if (configs.SSID != null && configs.SSID.equals("\"" + networkSSID + "\""))
                            {
                                wifi.disconnect();
                                wifi.enableNetwork(configs.networkId, true);
                                wifi.reconnect();
                                wifiOK = true;

                                adapter.deleteAll();

                                break;
                            }
                        }

                        break;
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvConnectedWifiName = findViewById(R.id.tvConnectedWifi);

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

        progressDialog = new ProgressDialog(MainActivity.this);

        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        registerReceiver(mWifiStatusChenged,
                new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = ""; //wifi.getConnectionInfo().getSSID();
        wifiSSID = sharedPref.getString(getString(R.string.saved_wifi_ssid), defaultValue);
        wifiPassword = sharedPref.getString(getString(R.string.saved_wifi_password), defaultValue);

        tvConnectedWifiName.setText(wifiSSID);

        //Toast.makeText(getApplicationContext(), wifiSSID, Toast.LENGTH_SHORT).show();

        // If password is not saved or SSID not equals to the saved value
        if(wifiPassword.equals(defaultValue) || wifiSSID.equals(defaultValue)) //|| !wifiSSID.equals(defaultValue))
        {
            setWifiFragment();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkandAskPermission();
        }

        Intent i = new Intent(MainActivity.this, APDiscoveryService.class);
        i.putExtra(APDiscoveryService.EXTRA_SSID, wifiSSID);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            startForegroundService(i);
        } else {
            startService(i);
        }

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
                    socket = new MulticastSocket(17235);
                    socket.setNetworkInterface(byInetAddress);
                    socket.joinGroup(InetAddress.getByName("224.1.1.1"));
                    socket.setSoTimeout(10000);
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

                        final String slName = jsonObj.getString("name");

                        ipAddr = jsonObj.getString("ip_address");

                        server_port = jsonObj.getInt("port_num");

                        smartLightingState = jsonObj.getString("led_state");

                        // We're running on a worker thread here, but we need to update the list view from the main thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                //pullToRefresh.setRefreshing(false);

                                Toast.makeText(getApplicationContext(), recvMsg, Toast.LENGTH_SHORT).show();
                                //tv.setText(myString);

                                DeviceItem item;

                                if(smartLightingState.contains("NA"))
                                {
                                    item = new DeviceItem(slName, smartLightingState.substring(0, 3),
                                                    0, ipAddr, server_port, true);
                                }
                                else
                                {
                                    Integer ledValue = Integer.valueOf(smartLightingState.substring(4,10), 16);

                                    item = new DeviceItem(slName, smartLightingState.substring(0, 3),
                                                ledValue , ipAddr, server_port, true);
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

            Intent i = new Intent(MainActivity.this, APDiscoveryService.class);
            stopService(i);

            setWifiFragment();

            i = new Intent(MainActivity.this, APDiscoveryService.class);
            i.putExtra(APDiscoveryService.EXTRA_SSID, wifiSSID);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                startForegroundService(i);
            } else {
                startService(i);
            }

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

            //wifi.startScan();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setWifiFragment(){

        wifi.startScan();

        WiFiApDialogFragment wifiApFragment = new WiFiApDialogFragment();

        wifiApFragment.setOnWiFiApSSIDListener(this);

        //wifiApFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_AppCompat_Light_Dialog);

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

        final int devPos = adapter.getDevicePosition(device);

        if(wifi.getConnectionInfo().getSSID().contains(wifiSSID)){

            Integer ledValue = device.getState();

            int redValue = (int) ((ledValue & 0xFF0000) >> 16);
            int greenValue = (int) ((ledValue & 0x00FF00) >> 8);
            int blueValue = (int) (ledValue & 0x0000FF);

            switch (device.getType()){
                case "RGB":
                    ColorPickerDialogBuilder
                        .with(this)
                        .setTitle("Choose color")
                        .initialColor(Color.rgb(redValue, greenValue, blueValue))
                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                        .lightnessSliderOnly()
                        .density(10)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                                Toast.makeText(getApplicationContext(),
                                        "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();

                                onDeviceChanged(device, selectedColor & 0x00FFFFFF);
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                //changeBackgroundColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
                    break;
                case "PWR":
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    // Get the layout inflater
                    LayoutInflater inflater = getLayoutInflater();

                    View myV = (View) inflater.inflate(R.layout.fragseekbar, null);

                    SeekBar sbBright = myV.findViewById(R.id.seekBar);

                    sbBright.setProgress(redValue);

                    sbBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            int bValue = seekBar.getProgress();
                            int ledValue = (bValue << 16) | ((bValue) << 8) | bValue;
                            onDeviceChanged(device, ledValue & 0x00FFFFFF);
                            //onDeviceChanged('R', seekBar.getProgress());
                        }
                    });

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(myV)
                            // Add action buttons
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // sign in the user ...
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //LoginDialogFragment.this.getDialog().cancel();
                                }
                            });

                    builder.create().show();
                    break;
                default:
                    break;
            }
        }
        else
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

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

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        adapter.deleteAll();
                                                    }
                                                });


                                                //wifi.startScan(); fixme

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

                            Integer ledValue = device.getState();

                            int redValue = (int) ((ledValue & 0xFF0000) >> 16);
                            int greenValue = (int) ((ledValue & 0x00FF00) >> 8);
                            int blueValue = (int) (ledValue & 0x0000FF);

                            switch (device.getType()){
                                case "RGB":
                                    ColorPickerDialogBuilder
                                        .with(builder.getContext())
                                        .setTitle("Choose color")
                                        .initialColor(Color.WHITE)
                                        .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                                        .lightnessSliderOnly()
                                        .density(10)
                                        .setOnColorChangedListener(new OnColorChangedListener() {
                                            @Override
                                            public void onColorChanged(int selectedColor) {
                                                Toast.makeText(getApplicationContext(),
                                                        "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();

                                                onDeviceChanged(device, selectedColor & 0x00FFFFFF);
                                            }
                                        })
                                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                                            @Override
                                            public void onColorSelected(int selectedColor) {
                                                //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                                            }
                                        })
                                        .setPositiveButton("ok", new ColorPickerClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                                //changeBackgroundColor(selectedColor);
                                            }
                                        })
                                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .build()
                                        .show();
                                    break;
                                case "PWR":
                                    AlertDialog.Builder sbBuilder = new AlertDialog.Builder(builder.getContext());
                                    // Get the layout inflater
                                    LayoutInflater inflater = getLayoutInflater();

                                    View myV = (View) inflater.inflate(R.layout.fragseekbar, null);

                                    SeekBar sbBright = myV.findViewById(R.id.seekBar);

                                    sbBright.setProgress(255);

                                    sbBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {
                                            int bValue = seekBar.getProgress();
                                            int ledValue = (bValue << 16) | ((bValue) << 8) | bValue;
                                            onDeviceChanged(device, ledValue & 0x00FFFFFF);
                                            //onDeviceChanged('R', seekBar.getProgress());
                                        }
                                    });

                                    // Inflate and set the layout for the dialog
                                    // Pass null as the parent view because its going in the dialog layout
                                    sbBuilder.setView(myV)
                                            // Add action buttons
                                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    // sign in the user ...
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //LoginDialogFragment.this.getDialog().cancel();
                                                }
                                            });

                                    sbBuilder.create().show();
                                    break;
                                default:
                                    break;
                            }
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
    public void onDeviceNameChanged(final DeviceItem item) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_change_name, null);

        final EditText etName = v.findViewById(R.id.etDevName);

        etName.setText(item.getName());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // change name
                        item.setName(etName.getText().toString());
                        onDeviceChanged(item, item.getState());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        builder.create().show();
    }

    @Override
    public void onDeviceSwitched(final DeviceItem item) {

        new Thread() {
            public void run() {

                DatagramSocket udpSocket = null;
                InetAddress local = null;

                try
                {
                    //String messageStr = color + String.valueOf(number);

                    udpSocket = new DatagramSocket();
                    local = InetAddress.getByName(item.getAddress());

                    JSONObject object = new JSONObject();

                    String state;

                    if(item.getTurnedOn())
                    {
                        state = "ON";
                    }
                    else
                    {
                        state = "OFF";
                    }

                    object.put("type", "Switch");
                    object.put("state", state);

                    System.out.print(object.toString());

                    String stringMsg =  object.toString();

                    byte[] msg = stringMsg.getBytes();

                    System.out.println(msg.length);

                    //DatagramPacket p = new DatagramPacket(message, message.length,local,udpPort);

                    DatagramPacket p = new DatagramPacket(msg, msg.length, local, item.getPort());

                    udpSocket.send(p);
                }
                catch(IOException e) {
                    System.out.println(e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();

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

    public static void OnDeviceStateChanged(int pos, Integer number) {
        adapter.updateDeviceState(pos, number);
    }

    public void onDeviceChanged(final DeviceItem device, final int ledValue) {

        final int pos = adapter.getDevicePosition(device);

        final int redValue = (int) ((ledValue & 0xFF0000) >> 16);
        final int greenValue = (int) ((ledValue & 0x00FF00) >> 8);
        final int blueValue = (int) (ledValue & 0x0000FF);

        //ledValue = ((long)(redValue) << 16) | ((long)(greenValue) << 8) | (long)(blueValue);

        //Intent intent = new Intent();
        //intent.putExtra(EXTRA_DEVICE_POSITION, itemPos);
        //intent.putExtra(EXTRA_DEVICE_STATE, ledValue);
        //setResult(MainActivity.DEVICE_STATE_REQUEST, intent);
        //finish();//finishing activity
        //listener.OnDeviceStateChanged( itemPos,ledValue);

        adapter.updateDeviceState(pos, ledValue);

        //MainActivity.OnDeviceStateChanged(itemPos, ledValue);

        new Thread() {
            public void run() {

                DatagramSocket udpSocket = null;
                InetAddress local = null;

                try
                {
                    //String messageStr = color + String.valueOf(number);

                    udpSocket = new DatagramSocket();
                    local = InetAddress.getByName(device.getAddress());

                    JSONObject object = new JSONObject();

                    object.put("type", "Config");
                    object.put("device_name", device.getName());
                    object.put("red", redValue);
                    object.put("green", greenValue);
                    object.put("blue", blueValue);

                    System.out.print(object.toString());

                    String stringMsg =  object.toString();

                    byte[] msg = stringMsg.getBytes();

                    System.out.println(msg.length);

                    //DatagramPacket p = new DatagramPacket(message, message.length,local,udpPort);

                    DatagramPacket p = new DatagramPacket(msg, msg.length, local, device.getPort());

                    udpSocket.send(p);
                }
                catch(IOException e) {
                    System.out.println(e.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {

        Intent i = new Intent(MainActivity.this, APDiscoveryService.class);
        stopService(i);

        super.onDestroy();
    }
}
