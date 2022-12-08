package me.ap.home.rpi.helper.station.config;

import me.ap.home.rpi.helper.station.ui.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class TelegramConfig {

    /*@Bean
    public TelegramBot telegramBot() {
        return new TelegramBot();
    }
*/


}
