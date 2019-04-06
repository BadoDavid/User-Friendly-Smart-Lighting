package hu.bme.mit.ufsmartlighting.Wifi;

public class SelectableWifiItem extends WifiItem
{
    private boolean isSelected = false;

    public SelectableWifiItem(WifiItem item,boolean isSelected) {
        super(item.getSsid());
        this.isSelected = isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}