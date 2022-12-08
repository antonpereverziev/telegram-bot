package me.ap.home.rpi.helper.station.integration.ewelink.wss;

;import me.ap.home.rpi.helper.station.integration.ewelink.wss.wssrsp.WssRspMsg;

public interface WssResponse {

    void onMessage(String s);

    void onMessageParsed(WssRspMsg rsp);

    void onError(String error);
}
