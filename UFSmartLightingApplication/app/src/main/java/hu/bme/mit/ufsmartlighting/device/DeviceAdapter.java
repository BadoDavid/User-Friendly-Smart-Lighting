package hu.bme.mit.ufsmartlighting.device;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.ufsmartlighting.R;

public class DeviceAdapter extends RecyclerView.Adapter implements DeviceViewHolder.OnItemChangedListener {

    private final List<DeviceItem> devices;

    //DeviceViewHolder.OnItemChangedListener dvhListener;

    private DeviceViewHolder.OnItemChangedListener dvhListener;

    public DeviceAdapter(DeviceViewHolder.OnItemChangedListener listener) {
        this.dvhListener = listener;
        devices = new ArrayList<>();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        DeviceViewHolder holder = (DeviceViewHolder) viewHolder;

        DeviceItem item = devices.get(position);
        holder.nameTextView.setText(devices.get(position).getName());
        holder.typeTextView.setText("RGB bulb");
        holder.item = item;
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public void addDevice(DeviceItem newDevice) {
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

    @Override
    public void onDeviceSelected(DeviceItem item) {
        dvhListener.onDeviceSelected(item);
    }

    @Override
    public void onDeviceDeleted(DeviceItem item) {

        dvhListener.onDeviceDeleted(item);

        removeDevice(devices.indexOf(item));
    }
}