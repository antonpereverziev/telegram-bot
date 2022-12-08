package me.ap.home.rpi.helper.station.service;

import lombok.extern.slf4j.Slf4j;
import me.ap.home.rpi.helper.station.ui.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class NotificationService {

    @Value("${telegram.bot.admin.chatId}")
    private String chatId;

    @Autowired
    private TelegramBot bot;

    @PostConstruct
    public void init() {
        this.sendNotification("Bot is up and running");
    }

    public void sendNotification(String text) {
        bot.sendText(this.chatId, text);
    }

    public void sendImage(byte[] image) {
        bot.sendImage(this.chatId, image);
    }
}
