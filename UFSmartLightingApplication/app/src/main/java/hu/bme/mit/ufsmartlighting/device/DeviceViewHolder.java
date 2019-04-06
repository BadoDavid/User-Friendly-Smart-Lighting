package hu.bme.mit.ufsmartlighting.device;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import hu.bme.mit.ufsmartlighting.R;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    DeviceItem item;

    TextView nameTextView;
    TextView typeTextView;
    Button resetButton;

    OnItemChangedListener itemChangedListener;

    public DeviceViewHolder(View view, OnItemChangedListener listener) {
        super(view);
        itemChangedListener = listener;

        nameTextView = itemView.findViewById(R.id.DeviceItemNameTextView);
        typeTextView = itemView.findViewById(R.id.DeviceType);
        resetButton = itemView.findViewById(R.id.DeviceItemResetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemChangedListener != null) {
                    itemChangedListener.onDeviceDeleted(item);
                }
                //removeDevice(devices.indexOf(item));
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemChangedListener != null) {
                    itemChangedListener.onDeviceSelected(item);
                }
            }
        });
    }

    public interface OnItemChangedListener {

        void onDeviceSelected(DeviceItem item);
        void onDeviceDeleted(DeviceItem item);
    }
}
