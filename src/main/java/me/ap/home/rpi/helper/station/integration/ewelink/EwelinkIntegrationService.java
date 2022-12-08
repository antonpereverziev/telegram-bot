package me.ap.home.rpi.helper.station.integration.ewelink;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.integration.ewelink.login.Status;
import me.ap.home.rpi.helper.station.integration.ewelink.devices.DeviceItem;
import me.ap.home.rpi.helper.station.integration.ewelink.devices.Switches;
import me.ap.home.rpi.helper.station.integration.ubidots.UbidotsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class EwelinkIntegrationService {

    @Value("${accounts.ewelink.email}")
    private String email;

    @Value("${accounts.ewelink.password}")
    private String password;

    @Value("${devices.sonoff.d-1}")
    private String th16DeviceId;

    @Value("${devices.sonoff.d-2}")
    private String switch4chDeviceId;

    private EweLink eweLink;

    @Autowired
    private UbidotsService ubidotsService;

    @PostConstruct
    public void init() throws Exception {
        log.info("Starting ewelink client");
        eweLink = new EweLink("eu", email, password);
        eweLink.login();
        log.info("Ewelink logged in");
        startTemperatureCollection();
    }

    public void enableTH16() {
        try {
            Status on = eweLink.setDeviceStatus(th16DeviceId, "{\"switch\":\"on\"}");
            log.info("Ewelink device enabled: {}", on);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void disableTH16() {
        try {
            Status off = eweLink.setDeviceStatus(th16DeviceId, "{\"switch\":\"off\"}");
            log.info("Ewelink device enabled: {}", off);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void enable4CHRelay() {
        try {
            Status on = eweLink.setDeviceStatus(switch4chDeviceId, Switches.createSwitches(true, true, true, false));
            log.info("Ewelink device {} enabled: {}", switch4chDeviceId, on);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void disable4CHRelay() {
        try {
            Status on = eweLink.setDeviceStatus(switch4chDeviceId, Switches.createSwitches(false, false, false, false));
            log.info("Ewelink device {} enabled : {}", switch4chDeviceId, on);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void startTemperatureCollection() {
        new Thread(() -> {
            while (true) {
                log.info("Sending data to Ubidots");
                try {
                    DeviceItem thermostat = eweLink.getDevice(th16DeviceId);
                    String temperature = thermostat.getParams().getCurrentTemperature();
                    log.debug(" {} Temperature: {}", th16DeviceId, temperature);
                    ubidotsService.sendData(Double.valueOf(temperature));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                try {
                    Thread.sleep(600000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        }).start();
    }
}