package hu.bme.mit.ufsmartlighting.Wifi;

import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hu.bme.mit.ufsmartlighting.R;

public class SelectableWifiAdapter extends RecyclerView.Adapter
        implements SelectableWifiViewHolder.OnItemSelectedListener {

    private final List<SelectableWifiItem> mValues;
    private boolean isMultiSelectionEnabled = false;
    SelectableWifiViewHolder.OnItemSelectedListener listener;


    public SelectableWifiAdapter( SelectableWifiViewHolder.OnItemSelectedListener listener,
                              List<WifiItem> items, boolean isMultiSelectionEnabled) {
        this.listener = listener;
        this.isMultiSelectionEnabled = isMultiSelectionEnabled;

        mValues = new ArrayList<>();

        for (WifiItem item : items) {
            mValues.add(new SelectableWifiItem(item, false));
        }
    }

    @Override
    public SelectableWifiViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checked_wifi_item, parent, false);

        return new SelectableWifiViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        SelectableWifiViewHolder holder = (SelectableWifiViewHolder) viewHolder;
        SelectableWifiItem selectableItem = mValues.get(position);

        String name = selectableItem.getSsid();
        holder.textView.setText(name);

        if (isMultiSelectionEnabled)
        {
            TypedValue value = new TypedValue();
            holder.textView.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
            int checkMarkDrawableResId = value.resourceId;
            holder.textView.setCheckMarkDrawable(checkMarkDrawableResId);
        }
        else
        {
            TypedValue value = new TypedValue();
            holder.textView.getContext().getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorSingle, value, true);
            int checkMarkDrawableResId = value.resourceId;
            holder.textView.setCheckMarkDrawable(checkMarkDrawableResId);
        }

        holder.mItem = selectableItem;
        holder.setChecked(holder.mItem.isSelected());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public List<WifiItem> getSelectedItems() {

        List<WifiItem> selectedItems = new ArrayList<>();
        for (SelectableWifiItem item : mValues) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if(isMultiSelectionEnabled){
            return SelectableWifiViewHolder.MULTI_SELECTION;
        }
        else{
            return SelectableWifiViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public void onItemSelected(SelectableWifiItem item) {
        if (!isMultiSelectionEnabled) {

            for (SelectableWifiItem selectableItem : mValues)
            {
                if (!selectableItem.equals(item) && selectableItem.isSelected())
                {
                    selectableItem.setSelected(false);
                }
                else if (selectableItem.equals(item) && item.isSelected())
                {
                    selectableItem.setSelected(true);
                }
            }
            notifyDataSetChanged();
        }
        listener.onItemSelected(item);
    }
}
