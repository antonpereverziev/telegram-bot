package me.ap.home.rpi.helper.station.udpserver;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.discovery.MDnsDiscoveryService;
import me.ap.home.rpi.helper.station.integration.camera.IpCameraIntegrationService;
import me.ap.home.rpi.helper.station.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.*;

@Slf4j
@Component
public class UdpLogServerService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IpCameraIntegrationService ipCameraIntegrationService;

    @PostConstruct
    public void init() throws SocketException, UnknownHostException {
        log.info("UDP log server stared");
        Server server = new Server(this);
        server.run();
    }

    public void processMotionEvent() {
        notificationService.sendNotification("Human motion detected");
        notificationService.sendImage(ipCameraIntegrationService.getSnapshot("CAM1"));
    }
}
