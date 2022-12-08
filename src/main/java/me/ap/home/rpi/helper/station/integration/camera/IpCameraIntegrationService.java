package me.ap.home.rpi.helper.station.integration.camera;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.discovery.MDnsDiscoveryService;
import me.ap.home.rpi.helper.station.httpclient.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class IpCameraIntegrationService {

    @Autowired
    private MDnsDiscoveryService mDnsDiscoveryService;

    @Value("${accounts.ipcam.login}")
    private String ipCamLogin;

    @Value("${accounts.ipcam.password}")
    private String ipCamPassword;

    public byte[] getSnapshot(String camHostname) {
        String ipAddress = mDnsDiscoveryService.getIpAddressByDeviceName(camHostname);
        String url = String.format("http://%s/cgi-bin/snapshot.cgi", ipAddress);
        return HttpUtils.downloadFileWithDigitAuth(url, ipCamLogin, ipCamPassword);
    }
}
