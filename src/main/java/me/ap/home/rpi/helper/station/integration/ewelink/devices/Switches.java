package me.ap.home.rpi.helper.station.integration.ewelink.devices;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Switches {
    public ArrayList<Switch> switches = new ArrayList<>();
    public static Switches createSwitches(boolean ... switches) {
        Switches result = new Switches();
        int outletCounter = 0;
        for (boolean s: switches) {
            result.getSwitches().add(new Switch((s?"on":"off"), outletCounter++));
        }
        return result;
    }
}

@AllArgsConstructor
class Switch{
    @JsonProperty("switch")
    public String sonoffSwitch;
    public int outlet;
}
