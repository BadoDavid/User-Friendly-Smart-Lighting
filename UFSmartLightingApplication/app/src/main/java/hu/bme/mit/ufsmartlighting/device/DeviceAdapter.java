package hu.bme.mit.ufsmartlighting.device;

import android.graphics.Color;
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
        holder.nameTextView.setText(item.getName());
        holder.typeTextView.setText(item.getType());

        if(item.getTurnedOn()) {
            Integer ledValue = item.getState();

            int redValue = (int) ((ledValue & 0xFF0000) >> 16);
            int greenValue = (int) ((ledValue & 0x00FF00) >> 8);
            int blueValue = (int) (ledValue & 0x0000FF);

            holder.itemView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
            //holder.itemView.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
        }

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

    public DeviceItem getDevice (int position)
    {
        return devices.get(position);
    }

    public int getDevicePosition(DeviceItem item)
    {
        return devices.indexOf(item);
    }

    public void updateDeviceState(int position, Integer state) {

        devices.get(position).setState(state);

        notifyItemChanged(position);

        /*
        if (position < devices.size()) {
            notifyItemRangeChanged(position, devices.size() - position);
        }
        */
    }

    public void deleteAll()
    {
        devices.clear();

        notifyDataSetChanged();
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

    @Override
    public void onDeviceNameChanged(DeviceItem item) {
        dvhListener.onDeviceNameChanged(item);
    }

    @Override
    public void onDeviceSwitched(DeviceItem item) {
        dvhListener.onDeviceSwitched(item);
    }
}