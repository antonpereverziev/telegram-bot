
package me.ap.home.rpi.helper.station.integration.ewelink.devices;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.ap.home.rpi.helper.station.integration.ewelink.login.ErrorContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class DeviceItem extends ErrorContainer {

    private int error;
    private int msg;
    private Settings settings;
    private Family family;
    private String group;
    private Boolean online;
    private List<Object> shareUsersInfo = null;
    private List<Object> groups = null;
    private List<Object> devGroups = null;
    private String id;
    private String name;
    private String type;
    private String deviceid;
    private String apikey;
    private Extra extra;
    private Params params;
    private String createdAt;
    private Integer v;
    private String onlineTime;
    private String ip;
    private String location;
    private String offlineTime;
    private String deviceStatus;
    private Tags tags;
    private List<Object> sharedTo = null;
    private String devicekey;
    private String deviceUrl;
    private String brandName;
    private Boolean showBrand;
    private String brandLogoUrl;
    private String productModel;
    private DevConfig devConfig;
    private Integer uiid;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
