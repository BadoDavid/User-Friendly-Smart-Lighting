package hu.bme.mit.ufsmartlighting;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.ufsmartlighting.Wifi.SelectableWifiAdapter;
import hu.bme.mit.ufsmartlighting.Wifi.SelectableWifiItem;
import hu.bme.mit.ufsmartlighting.Wifi.SelectableWifiViewHolder;
import hu.bme.mit.ufsmartlighting.Wifi.WiFiApAdapter;
import hu.bme.mit.ufsmartlighting.Wifi.WifiItem;

public class WiFiApDialogFragment extends AppCompatDialogFragment
        implements SelectableWifiViewHolder.OnItemSelectedListener, WiFiApAdapter.OnWiFiApSelectedListener
{
    public static final String TAG = "WiFiApDialogFragment";

    //RecyclerView rv;
    //WiFiApAdapter adapter;

    RecyclerView recyclerView;
    SelectableWifiAdapter adapter;

    WifiManager wifi;
    String wifiSSID;

    ScanResult selectedResult;

    setOnWiFiApSSIDListener mCallback;

    // Container Activity must implement this interface
    public interface setOnWiFiApSSIDListener {
        public void onWiFiApSSID(String wifiApSSID, String wifiApPassword);
    }

    public void setOnWiFiApSSIDListener(MainActivity activity) {
        mCallback = activity;
    }

    /* TODO: If it has interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            optionsFragmentInterface =
                    (OptionsFragmentInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OptionsFragmentInterface");
        }
    }
    */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().setTitle("Available WiFi Access Points");

        View rootView=inflater.inflate(R.layout.fraglayout,container);

        Button btn = rootView.findViewById(R.id.okBtn);

        final EditText etPassword = rootView.findViewById(R.id.etPassword);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String key = etPassword.getText().toString();

                if (key.isEmpty()) {
                    etPassword.requestFocus();
                    etPassword.setError("Please enter your password");

                    return;
                }
                else
                {
                    /* If the key is not empty, then connect to the selected WiFi AP */
                    WifiConfiguration wifiConfig = new WifiConfiguration();
                    wifiConfig.SSID = String.format("\"%s\"", wifiSSID);
                    wifiConfig.preSharedKey = String.format("\"%s\"", key);

                    WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    //remember id
                    int netId = wifiManager.addNetwork(wifiConfig);

                    if(!wifiManager.getConnectionInfo().getSSID().contains("Bado"))
                    {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(netId, true);
                        wifiManager.reconnect();
                    }

                    // Invoke MainActivity callback function to save SSID in SharedPreferences
                    mCallback.onWiFiApSSID(wifiSSID, key);

                    removeFragment();
                }
            }
        });

        wifi = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView = (RecyclerView) rootView.findViewById(R.id.selection_list);
        recyclerView.setLayoutManager(layoutManager);
        List<WifiItem> selectableItems = generateItems();
        adapter = new SelectableWifiAdapter(this, selectableItems,false);
        recyclerView.setAdapter(adapter);

        //RECYCER
        //rv = (RecyclerView) rootView.findViewById(R.id.mRecyerID);
        //rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        //ADAPTER
        //adapter = new WiFiApAdapter(this);
        //rv.setAdapter(adapter);

        return rootView;
    }

    private void removeFragment()
    {
        this.getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onWiFiApSelected(String wifiAP) {

        wifiSSID = wifiAP;

        /*
         * FIXME: not a better place to invoke the callback?s
         * mCallback.onWiFiApSSID(wifiSSID);
         */
    }

    /*
    private View getContentView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_new_city, null);
        editText = view.findViewById(R.id.NewCityDialogEditText);
        return view;
    }
    */

    public List<WifiItem> generateItems()
    {
        List<ScanResult> scanResultList = wifi.getScanResults();
        List<WifiItem> selectableItems = new ArrayList<>();

        for (ScanResult scanResults : scanResultList)
        {
            if (scanResults.SSID != null) {
                selectableItems.add(new WifiItem(scanResults.SSID));
                //adapter.addDevice(scanResults.SSID);
            }
        }

        return selectableItems;
    }

    @Override
    public void onItemSelected(SelectableWifiItem selectableItem)
    {
        wifiSSID = selectableItem.getSsid();

        List<WifiItem> selectedItems = adapter.getSelectedItems();
    }
}
