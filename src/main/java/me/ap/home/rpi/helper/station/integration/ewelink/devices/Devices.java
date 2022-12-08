
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
public class Devices extends ErrorContainer {

    private int error;
    private List<DeviceItem> devicelist = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
