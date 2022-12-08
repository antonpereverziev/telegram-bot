
package me.ap.home.rpi.helper.station.integration.ewelink.devices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Params {
    private BindInfos bindInfos;
    private String sledOnline;
    @JsonProperty("switch")
    private String _switch;
    private String power;
    private String voltage;
    private String current;
    private String fwVersion;
    private String staMac;
    private Integer rssi;
    private Integer init;
    private String alarmType;
    private List<Integer> alarmVValue = null;
    private List<Integer> alarmCValue = null;
    private List<Integer> alarmPValue = null;
    private String oneKwh;
    private Integer uiActive;
    private Integer timeZone;
    private Integer version;
    private String startup;
    private String pulse;
    private Integer pulseWidth;
    private List<Timer> timers = null;
    private String hundredDaysKwh;
    private OnlyDevice onlyDevice;
    private String ssid;
    private String bssid;
    private String endTime;
    private String startTime;
    private String subDevId;
    private String parentid;
    private String battery;
    private String trigTime;
    private String temperature;
    private String currentTemperature;
    private String humidity;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}
