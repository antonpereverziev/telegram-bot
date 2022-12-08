package me.ap.home.rpi.helper.station.ui;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.discovery.MDnsDiscoveryService;
import me.ap.home.rpi.helper.station.integration.ewelink.EwelinkIntegrationService;
import me.ap.home.rpi.helper.station.integration.camera.IpCameraIntegrationService;
import me.ap.home.rpi.helper.station.integration.cmd.CommandExecutionService;
import me.ap.home.rpi.helper.station.integration.shelly.Shelly1IntegrationService;
import me.ap.home.rpi.helper.station.pi.sensor.weather.TemperatureSensor;
import me.ap.home.rpi.helper.station.service.MessageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

import static me.ap.home.rpi.helper.station.ui.MenuProcessor.getKeyboard;

@Slf4j
@Component
public class MessageProcessor {

    public static final String GET_PICTURE = "Picture";
    public static final String GET_TEMPERATURE = "Temperature";
    public static final String UPGRADE_BOT = "Upgrade bot";
    public static final String LIGHT_ON = "Light ON";
    public static final String LIGHT_OFF = "Light OFF";
    public static final String SHELLY_DEVICES = "Shelly devices";
    public static final String EWELINK_DEVICES = "Ewelink devices";
    public static final String WEB_CAMERAS = "Web cameras";
    public static final String BACK_ARROW = "<-";
    public static final String DONE = "Done";

    @Value("${devices.ipcam.d-1}")
    private String ipCam1Name;

    @Autowired
    private TemperatureSensor temperatureSensor;

    @Autowired
    private CommandExecutionService commandExecutionService;

    @Autowired
    private Shelly1IntegrationService shelly1Integration;

    @Autowired
    private MDnsDiscoveryService mDnsDiscoveryService;

    @Autowired
    private EwelinkIntegrationService ewelinkIntegrationService;

    @Autowired
    private IpCameraIntegrationService ipCameraIntegrationService;

    @PostConstruct
    public void init() {
        MenuProcessor.init();
    }

    public MessageHolder processMessage(String text, String telegramChatId) throws TelegramApiException {
        if (MenuProcessor.TEXT_MENU_MAPPING.containsKey(text) || BACK_ARROW.equals(text)) {
            return MenuProcessor.processMenuNavigation(text, telegramChatId);
        }
        switch (text) {
            case GET_TEMPERATURE:
                return new MessageHolder(String.format("%.2f\u2103", temperatureSensor.getTemperature()), getKeyboard(telegramChatId));
            case GET_PICTURE:
                return new MessageHolder(ipCameraIntegrationService.getSnapshot(ipCam1Name), getKeyboard(telegramChatId));
            case LIGHT_ON:
                ewelinkIntegrationService.enable4CHRelay();
                return new MessageHolder(DONE, getKeyboard(telegramChatId));
            case LIGHT_OFF:
                ewelinkIntegrationService.disable4CHRelay();
                return new MessageHolder(DONE, getKeyboard(telegramChatId));
            case UPGRADE_BOT:
                String[] args = {"sh", "/home/pi/home-telegram-bot/misc/upgrade.sh"};
                return new MessageHolder(commandExecutionService.executeCommandAndGetResultResult(args), getKeyboard(telegramChatId));
            case SHELLY_DEVICES:
                return new MessageHolder(mDnsDiscoveryService.getDiscoveredShellyDevices(), getKeyboard(telegramChatId));
            case EWELINK_DEVICES:
                return new MessageHolder(mDnsDiscoveryService.getDiscoveredEwelinkDevices(), getKeyboard(telegramChatId));
            case WEB_CAMERAS:
                return new MessageHolder(mDnsDiscoveryService.getDiscoveredCamDevices(), getKeyboard(telegramChatId));
            default:
                return new MessageHolder("¯\\_(ツ)_/¯", getKeyboard(telegramChatId));
        }
    }


}
