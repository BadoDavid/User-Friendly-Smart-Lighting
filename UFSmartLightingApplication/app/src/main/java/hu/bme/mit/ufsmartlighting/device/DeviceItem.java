package hu.bme.mit.ufsmartlighting.device;

public class DeviceItem
{
    private String name;
    private String type;
    private Integer state;
    private String address;
    private Integer port;
    private Boolean turnedOn;

    public DeviceItem(String name, String type, Integer state, String address, Integer port, Boolean turnedOn) {
        this.name = name;
        this.type = type;
        this.state = state;
        this.address = address;
        this.port = port;
        this.turnedOn = turnedOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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

    public Boolean getTurnedOn() {
        return turnedOn;
    }

    public void setTurnedOn(Boolean turnedOn) {
        this.turnedOn = turnedOn;
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
