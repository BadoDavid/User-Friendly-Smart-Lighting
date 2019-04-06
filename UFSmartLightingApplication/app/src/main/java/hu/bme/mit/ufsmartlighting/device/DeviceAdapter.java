package hu.bme.mit.ufsmartlighting.device;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.ufsmartlighting.R;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private final List<String> devices;

    private OnDeviceSelectedListener listener;

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(final String city);

        void onDeviceDeleted(String item);
    }

    public DeviceAdapter(OnDeviceSelectedListener listener) {
        this.listener = listener;
        devices = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
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

    class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;

        Button resetButton;

        String item;

        DeviceViewHolder(View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.DeviceItemNameTextView);
            resetButton = itemView.findViewById(R.id.DeviceItemResetButton);

            resetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDeviceDeleted(item);
                    }                    
                    removeDevice(devices.indexOf(item));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onDeviceSelected(item);
                    }
                }
            });
        }
    }
}