package me.ap.home.rpi.helper.station.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.jmdns.*;

@Slf4j
@Service
public class MDnsDiscoveryService {

    private static final String EWELINK_SERVICE = "_ewelink._tcp.local.";
    private static final String HTTP_SERVICE = "_http._tcp.local.";

    private static Map<String, String> DISCOVERED_DEVICES = new HashMap();

    @PostConstruct
    public void init() {
        // temporary hardcoded ip addresses until mDNS is slow
        DISCOVERED_DEVICES.put("eWeLink_10006fdd1c","192.168.1.100");
        DISCOVERED_DEVICES.put("eWeLink_10004af447","192.168.1.103");
        DISCOVERED_DEVICES.put("shelly1-59D625","192.168.1.104");
        DISCOVERED_DEVICES.put("CAM1","192.168.1.105");
        //-----------------------------------------------------
        try {
            log.info("Discovery service started.");
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());
            // Add a service listener
            jmdns.addServiceTypeListener(new TypeListener());
            jmdns.addServiceListener(EWELINK_SERVICE, new CommonListener());
            jmdns.addServiceListener(HTTP_SERVICE, new CommonListener());
        } catch (UnknownHostException e) {
            log.error(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public String getDiscoveredEwelinkDevices() {
        return DISCOVERED_DEVICES.entrySet().stream()
                .filter(f -> f.getKey().startsWith("eWeLink"))
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n", "", ""));
    }

    public String getDiscoveredShellyDevices() {
        return DISCOVERED_DEVICES.entrySet().stream()
                .filter(f -> f.getKey().startsWith("shelly"))
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n", "", ""));
    }

    public String getDiscoveredCamDevices() {
        return DISCOVERED_DEVICES.entrySet().stream()
                .filter(f -> f.getKey().startsWith("CAM"))
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n", "", ""));
    }

    private static class CommonListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            ServiceInfo eventInfo = event.getInfo();
            String localAddress = eventInfo.getInet4Addresses()[0].getHostAddress();
            log.info("Lan event received from {} with payload {}", localAddress, eventInfo.getName());
            DISCOVERED_DEVICES.put(eventInfo.getName(), localAddress);
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            log.info("Service removed: " + event.getInfo().getHostAddresses()[0]);
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            ServiceInfo eventInfo = event.getInfo();
            String localAddress = eventInfo.getInet4Addresses()[0].getHostAddress();
            log.info("Lan event received from {} with payload {}", localAddress, eventInfo.getName());
            DISCOVERED_DEVICES.put(eventInfo.getName(), localAddress);
        }
    }

    static class TypeListener implements ServiceTypeListener {

        @Override
        public void serviceTypeAdded(ServiceEvent event) {
            log.info("Service type added: {}", event.getType());
        }

        @Override
        public void subTypeForServiceTypeAdded(ServiceEvent event) {
            log.info("SubType for service type added: {}", event.getType());
        }
    }

    public String getIpAddressByDeviceName(String deviceId) {
        return DISCOVERED_DEVICES.get(deviceId);
    }
}
