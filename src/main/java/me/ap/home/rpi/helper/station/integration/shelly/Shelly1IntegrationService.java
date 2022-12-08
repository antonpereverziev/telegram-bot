package me.ap.home.rpi.helper.station.integration.shelly;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.discovery.MDnsDiscoveryService;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class Shelly1IntegrationService {

    @Autowired
    private MDnsDiscoveryService mDnsDiscoveryService;

    @Value("${devices.shelly.d-1}")
    private String shellyDeviceName;

    public boolean getState() {
        String ipAddress = mDnsDiscoveryService.getIpAddressByDeviceName(shellyDeviceName);
        String url = String.format("http://%s/relay/0",ipAddress);
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            String json = IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8);
            log.info("Shelly device returned JSON: {}", json);
            JSONObject object = new JSONObject(json);
            //{"ison":true,"has_timer":false,"timer_started":0,"timer_duration":0,"timer_remaining":0,"source":"cloud"}
            return (Boolean)object.get("ison");
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return false;
    }
}
