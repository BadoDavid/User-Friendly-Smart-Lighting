package hu.bme.mit.ufsmartlighting.device;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.QuickContactBadge;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import hu.bme.mit.ufsmartlighting.R;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    DeviceItem item;

    TextView nameTextView;
    TextView typeTextView;

    ImageButton btnConfig;

    ImageButton resetButton;

    OnItemChangedListener itemChangedListener;

    public DeviceViewHolder(final View view, OnItemChangedListener listener) {
        super(view);
        itemChangedListener = listener;

        nameTextView = itemView.findViewById(R.id.DeviceItemNameTextView);
        typeTextView = itemView.findViewById(R.id.DeviceType);

        btnConfig = itemView.findViewById(R.id.btnConfig);

        resetButton = itemView.findViewById(R.id.DeviceItemResetButton);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (itemChangedListener != null) {
                    itemChangedListener.onDeviceNameChanged(item);
                }

                return false;
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemChangedListener != null) {
                    itemChangedListener.onDeviceSelected(item);
                }
            }
        });

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
                if(item.getTurnedOn()) {

                    item.setTurnedOn(false);
                    view.setBackgroundColor(Color.rgb(0, 0, 0));
                }
                else
                {
                    item.setTurnedOn(true);

                    Integer ledValue = item.getState();

                    int redValue = (int) ((ledValue & 0xFF0000) >> 16);
                    int greenValue = (int) ((ledValue & 0x00FF00) >> 8);
                    int blueValue = (int) (ledValue & 0x0000FF);

                    view.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                }

                if (itemChangedListener != null) {
                    itemChangedListener.onDeviceSwitched(item);
                }
            }
        });
    }

    public interface OnItemChangedListener {

        void onDeviceSelected(DeviceItem item);
        void onDeviceDeleted(DeviceItem item);
        void onDeviceNameChanged(DeviceItem item);
        void onDeviceSwitched(final DeviceItem item);
    }
}
