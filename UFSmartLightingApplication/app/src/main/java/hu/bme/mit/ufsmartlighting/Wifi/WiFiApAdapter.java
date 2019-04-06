package hu.bme.mit.ufsmartlighting.Wifi;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.ufsmartlighting.R;

public class WiFiApAdapter extends RecyclerView.Adapter<WiFiApAdapter.WiFiApViewHolder> {

    private final List<String> devices;

    private OnWiFiApSelectedListener listener;

    public interface OnWiFiApSelectedListener {
        void onWiFiApSelected(String wifiAP);
    }

    public WiFiApAdapter(OnWiFiApSelectedListener listener) {
        this.listener = listener;
        devices = new ArrayList<>();
    }

    @NonNull
    @Override
    public WiFiApViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifiap, parent, false);
        return new WiFiApViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WiFiApViewHolder holder, int position) {
        String item = devices.get(position);
        holder.nameTextView.setText(devices.get(position));
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(String newDevice) {
        if (!devices.contains(newDevice)) {
            devices.add(newDevice);
            notifyItemInserted(devices.size() - 1);
        }
    }

    public void removeDevice(int position) {
        devices.remove(position);
        notifyItemRemoved(position);

        if (position < devices.size()) {
            notifyItemRangeChanged(position, devices.size() - position);
        }
    }

    class WiFiApViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        boolean stateClicked = false;

        String item;

        WiFiApViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.WiFiApItemNameTextView);
            //resetButton = itemView.findViewById(R.id.DeviceItemResetButton);

            /*
            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDeviceDeleted(item);
                    }
                    removeDevice(devices.indexOf(item));
                }
            });
            */

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(stateClicked)
                    {
                        nameTextView.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorWhite));
                        stateClicked = false;
                    }
                    else
                    {
                        nameTextView.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorGrey));
                        stateClicked = true;
                    }
                    if (listener != null) {

                        String ssid = nameTextView.getText().toString();

                        listener.onWiFiApSelected(ssid);
                    }
                }
            });
        }
    }
}
