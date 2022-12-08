package me.ap.home.rpi.helper.station.integration.ewelink.login;

public class StatusChange {

    private String deviceid;
    private String version;
    private String appid;
    private String ts;
    private Object params;


    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "StatusChange{" +
                "deviceid='" + deviceid + '\'' +
                ", version='" + version + '\'' +
                ", appid='" + appid + '\'' +
                ", ts='" + ts + '\'' +
                ", params='" + params + '\'' +
                '}';
    }
}
