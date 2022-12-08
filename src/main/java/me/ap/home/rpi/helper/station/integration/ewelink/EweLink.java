package me.ap.home.rpi.helper.station.integration.ewelink;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.integration.ewelink.devices.DeviceItem;
import me.ap.home.rpi.helper.station.integration.ewelink.devices.Devices;
import me.ap.home.rpi.helper.station.integration.ewelink.login.*;
import me.ap.home.rpi.helper.station.integration.ewelink.wss.WssLogin;
import me.ap.home.rpi.helper.station.integration.ewelink.wss.WssResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.AbstractHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class EweLink {

    private static ObjectMapper mapper;

    private static String region;
    private String email;
    private String password;
    private int activityTimer = 9;
    private String baseUrl = "https://eu-api.coolkit.cc:8080/api/";
    public static final String APP_ID = "YzfeftUVcZ6twZw1OoVKPRFYTrGEg01Q";
    private static final String APP_SECRET = "4G91qSoboqYO4Y0XJ0LPPKIsq8reHdfa";
    private static boolean isLoggedIn = false;
    private static long lastActivity = 0L;
    private static final int TIMEOUT = 5000;

    private static String accessToken;
    private static String apiKey;
    private static WssResponse clientWssResponse;

    private static EweLinkWebSocketClient eweLinkWebSocketClient = null;
    private static Thread webSocketMonitorThread = null;

    public EweLink(String region, String email, String password) {
        this.region = region;
        this.email = email;
        this.password = password;
        if (region != null) {
            baseUrl = "https://" + region + "-api.coolkit.cc:8080/api/";
        }
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.info("EweLinkApi startup params : {} {}", region, email);
    }

    public void login() throws Exception {
        URL url = new URL(baseUrl + "user/login");
        HttpPost httpPost = new HttpPost(url.toURI());
        httpPost.addHeader("Content-Type", "application/json; utf-8");
        httpPost.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        String loginRequest = mapper.writeValueAsString(getLoginRequest());
        log.debug("Login Request:{}", loginRequest);
        httpPost.addHeader("Authorization", "Sign " + getAuthMac(loginRequest));
        httpPost.setEntity(new StringEntity(loginRequest));
        try (CloseableHttpResponse httpResponse = getHttpClient().execute(httpPost)){
            String json = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            log.debug("Login Response Raw:{}", json);
            LoginResponse loginResponse = mapper.readValue(json, LoginResponse.class);
            accessToken = loginResponse.getAt();
            apiKey = loginResponse.getUser().getApikey();
            isLoggedIn = true;
            lastActivity = new Date().getTime();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private LoginRequest getLoginRequest() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setAppid(APP_ID);
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        loginRequest.setTs(new Date().getTime() + "");
        loginRequest.setVersion("8");
        loginRequest.setNonce(Util.getNonce());
        return loginRequest;
    }

    public void getWebSocket(WssResponse wssResponse) throws Exception {
        if (!isLoggedIn) {
            throw new Exception("Not Logged In, please call login Method");
        }
        eweLinkWebSocketClient = new EweLinkWebSocketClient(new URI("wss://" + region + "-pconnect3.coolkit.cc:8080/api/ws"));
        clientWssResponse = wssResponse;
        eweLinkWebSocketClient.setWssResponse(clientWssResponse);
        eweLinkWebSocketClient.setWssLogin(mapper.writeValueAsString(new WssLogin(accessToken, apiKey, APP_ID, Util.getNonce())));
        eweLinkWebSocketClient.connect();
        if (webSocketMonitorThread == null) {
            webSocketMonitorThread = new Thread(new WebSocketMonitor());
            webSocketMonitorThread.start();
        }
    }

    public Devices getDevices() throws Exception {
        loginIfNeed();
        URL url = new URL(baseUrl + "user/device?lang=en&appid=" + APP_ID + "&ts=" + new Date().getTime() + "&version=8&getTags=1");
        return getEntity(url, new TypeReference<Devices>() {});
    }

    private CloseableHttpClient getHttpClient() {
        //conn.setConnectTimeout(TIMEOUT);
        //conn.setReadTimeout(TIMEOUT);
        return HttpClientBuilder.create()
                .setConnectionTimeToLive(TIMEOUT, TimeUnit.MILLISECONDS)
                .build();
    }

    private void loginIfNeed() {
        if (!isLoggedIn) {
            throw new RuntimeException("Not Logged In, please call login Method");
        }
        if (lastActivity + (activityTimer * 60 * 1000) < new Date().getTime()) {
            log.info("Longer than last Activity, perform login Again");
            try {
                login();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public DeviceItem getDevice(String deviceId) throws Exception {
        URL url = new URL(String.format( "%suser/device/%s?deviceid=%s&lang=en&appid=%s&ts=%s&version=8",
                baseUrl, deviceId, deviceId, APP_ID, String.valueOf(new Date().getTime())));
        return getEntity(url, new TypeReference<DeviceItem>() {});
    }

    public <T extends ErrorContainer> T getEntity(URL url, TypeReference<T> ref) throws Exception {
        loginIfNeed();
        CloseableHttpClient httpClient = getHttpClient();
        HttpGet httpGet = new HttpGet(url.toURI());
        setCommonHeaders(httpGet);
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            String json = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            log.debug("Response Raw:{}", json);
            T entity = mapper.readValue(json, ref);
            if (entity.getError() > 0) {
                throw new Exception("Error: " + entity.getError());
            } else {
                lastActivity = new Date().getTime();
                return entity;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public Status getDeviceStatus(String deviceId) throws Exception {
        URL url = new URL(String.format( "%suser/device/status?deviceid=%s&lang=en&appid=%s&ts=%s&version=8&params=switch|switches",
                baseUrl, deviceId, APP_ID, String.valueOf(new Date().getTime())));
        return getEntity(url, new TypeReference<Status>() {});
    }

    public Status setDeviceStatusByName(String name, String status) throws Exception {
        Devices devices = getDevices();
        String selectedDeviceId = null;
        for (DeviceItem deviceItem : devices.getDevicelist()) {
            if (deviceItem.getName().equalsIgnoreCase(name)) {
                selectedDeviceId = deviceItem.getDeviceid();
            }
        }
        if (selectedDeviceId == null) {
            throw new Exception("No Device id Found for Device Name:" + name);
        }
        return setDeviceStatus(selectedDeviceId, status);
    }

    public Status setDeviceStatus(String deviceId, Object status) throws Exception {
        loginIfNeed();
        URL url = new URL(baseUrl + "user/device/status");
        HttpPost httpPost = new HttpPost(url.toURI());
        setCommonHeaders(httpPost);
        String statusChangeRequest = mapper.writeValueAsString(getStatusChange(deviceId, status));
        log.debug("Login Request:{}", statusChangeRequest);
        httpPost.setEntity(new StringEntity(statusChangeRequest));
        try (CloseableHttpResponse httpResponse = getHttpClient().execute(httpPost)){
            String json = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            log.debug("Login Response Raw:{}", json);
            Status statusChangeResponse = mapper.readValue(json, Status.class);
            log.debug("StatusChange Response:{}", statusChangeResponse.toString());
            if (statusChangeResponse.getError() > 0) {
                throw new Exception(statusChangeResponse.getErrmsg());
            } else {
                isLoggedIn = true;
                lastActivity = new Date().getTime();
                return statusChangeResponse;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private StatusChange getStatusChange(String deviceId, Object status) {
        StatusChange statusChange = new StatusChange();
        statusChange.setAppid(APP_ID);
        statusChange.setDeviceid(deviceId);
        statusChange.setTs(new Date().getTime() + "");
        statusChange.setVersion("8");
        statusChange.setParams(status);
        return statusChange;
    }

    private void setCommonHeaders(AbstractHttpMessage httpMessage) {
        httpMessage.addHeader("Content-Type", "application/json; utf-8");
        httpMessage.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType());
        httpMessage.addHeader("Authorization", "Bearer " + accessToken);
    }

    private static String getAuthMac(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = null;
        byte[] byteKey = APP_SECRET.getBytes("UTF-8");
        final String HMAC_SHA256 = "HmacSHA256";
        sha256_HMAC = Mac.getInstance(HMAC_SHA256);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
        sha256_HMAC.init(keySpec);
        byte[] mac_data = sha256_HMAC.
                doFinal(data.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(mac_data);
    }

    public class WebSocketMonitor implements Runnable {
        Logger log = LoggerFactory.getLogger(WebSocketMonitor.class);
        @Override
        public void run() {
            log.info("Websocket Monitor Thread start");
            while (true) {
                try {
                    Thread.sleep(30000);
                    log.debug("send websocket ping");
                    eweLinkWebSocketClient.send("ping");

                } catch (Exception e) {
                    log.error("Error in sening websocket ping:", e);
                    log.info("Try reconnect to websocket");
                    try {
                        eweLinkWebSocketClient = new EweLinkWebSocketClient(new URI("wss://" + region + "-pconnect3.coolkit.cc:8080/api/ws"));
                        eweLinkWebSocketClient.setWssResponse(clientWssResponse);
                        eweLinkWebSocketClient.setWssLogin(mapper.writeValueAsString(new WssLogin(accessToken, apiKey, APP_ID, Util.getNonce())));
                        eweLinkWebSocketClient.connect();

                    } catch (Exception c) {
                        log.error("Error trying to reconnect:", c);
                    }
                }
            }
        }
    }
}
