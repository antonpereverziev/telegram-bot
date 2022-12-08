package me.ap.home.rpi.helper.station.integration.ewelink.login;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Status extends ErrorContainer {

    private int error;
    private Param params;
    private String deviceid;
    private String errmsg;

}
