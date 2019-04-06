package hu.bme.mit.ufsmartlighting.device;

public class DeviceItem
{
    private String name;
    private String address;
    private Integer port;

    public DeviceItem(String name, String address, Integer port) {
        this.name = name;
        this.address = address;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;

        DeviceItem itemCompare = (DeviceItem) obj;
        if(itemCompare.getName().equals(this.getName()))
            return true;

        return false;
    }

}
