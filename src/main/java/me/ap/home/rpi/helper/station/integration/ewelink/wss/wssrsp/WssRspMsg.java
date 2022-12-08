package me.ap.home.rpi.helper.station.integration.ewelink.wss.wssrsp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.ap.home.rpi.helper.station.integration.ewelink.devices.Params;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class WssRspMsg {

    private Integer error;
    private String action;
    private String deviceid;
    private String apikey;
    private String userAgent;
    private Params params;
    private String from;
    private Config config;
    private String seq;
    private String sequence;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
