package me.ap.home.rpi.helper.station.integration.ewelink.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Param {

    @JsonProperty("switch")
    private String switchValue;

    public String getSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(String switchValue) {
        this.switchValue = switchValue;
    }

    @Override
    public String toString() {
        return "Param{" +
                "switchValue='" + switchValue + '\'' +
                '}';
    }
}
