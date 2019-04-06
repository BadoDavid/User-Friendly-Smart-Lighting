package hu.bme.mit.ufsmartlighting.Wifi;

public class WifiItem
{
    private String ssid;
    //private String rssi;

    public WifiItem(String ssid)//, String rssi)
    {
        this.ssid = ssid;
        //this.rssi = rssi;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        WifiItem itemCompare = (WifiItem) obj;
        if(itemCompare.getSsid().equals(this.getSsid()))
            return true;

        return false;
    }
}
