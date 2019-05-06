package hu.bme.mit.ufsmartlighting;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class APDiscoveryService extends Service {

    public static final String EXTRA_SSID = "extra.ssid";
    private boolean enabled = false;

    private MyTimeThread myTimeThread = null;

    public WifiManager wifi;
    public ConnectivityManager cm;

    List<ScanResult> wifiList;
    List<String> values = new ArrayList<String>();

    int netCount=0;
    private String mySSID;

    /**
     * BroadcastReceiver which handles WifiScan results
     */
    /*
    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            wifiList = wifi.getScanResults();

            netCount = wifiList.size();
            // wifiScanAdapter.notifyDataSetChanged();
            Log.d("Wifi","Total Wifi Network"+netCount);

            // add your logic here
            //if(!wifiOK) {
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
                                //wifiOK = true;

                                break;
                            }
                        }

                        break;
                    }
                }
            //}
        }
    };
    */

    private class MyTimeThread extends Thread {
        public void run() {
            Handler h = new Handler(APDiscoveryService.this.getMainLooper());

            while (enabled) {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(APDiscoveryService.this,
                                new Date(System.currentTimeMillis()).toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

                if(isConnected) {

                    wifi.startScan();

                    String currentSSID = wifi.getConnectionInfo().getSSID();

                    System.out.println(currentSSID);

                    if(! ((currentSSID.equals("\""+mySSID+"\"")) || (currentSSID.contains("Bado"))))
                    {
                        List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                        for (WifiConfiguration configs : list) {
                            if (configs.SSID != null && configs.SSID.equals("\"" + mySSID + "\"")) {
                                wifi.disconnect();
                                wifi.enableNetwork(configs.networkId, true);
                                wifi.reconnect();
                                //wifiOK = true;

                                break;
                            }
                        }
                    }
                }

                try {
                    sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public APDiscoveryService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startForeground(NOTIF_FOREGROUND_ID, getMyNotification("starting..."));

        mySSID = intent.getStringExtra(EXTRA_SSID);

        wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        /*
        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                */

        enabled = true;
        if (myTimeThread == null) {
            myTimeThread = new MyTimeThread();
            myTimeThread.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //stopForeground(true);
        enabled = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
