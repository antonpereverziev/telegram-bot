package me.ap.home.rpi.helper.station.integration.ewelink.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.integration.ewelink.wss.WssResponse;
import me.ap.home.rpi.helper.station.integration.ewelink.wss.wssrsp.WssRspMsg;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@Slf4j
public class EweLinkWebSocketClient extends WebSocketClient {

    private static ObjectMapper mapper = new ObjectMapper();

    private WssResponse wssResponse;
    private String wssLogin;

    public void setWssResponse(WssResponse wssResponse) {
        this.wssResponse = wssResponse;
    }

    public void setWssLogin(String wssLogin) {
        this.wssLogin = wssLogin;
    }

    public EweLinkWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send(wssLogin);
    }

    @Override
    public void onMessage(String s) {
        if (s!= null && s.equalsIgnoreCase("pong")){
            //swallow this as its just a ping/pong
            log.debug(s);
        }else {
            wssResponse.onMessage(s);
            try {
                wssResponse.onMessageParsed(mapper.readValue(s, WssRspMsg.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.warn("WS onCloseCalled, system will self-recover {} {} {}",i,s,b);
    }

    @Override
    public void onError(Exception e) {
        wssResponse.onError(e.getMessage());
    }
}
