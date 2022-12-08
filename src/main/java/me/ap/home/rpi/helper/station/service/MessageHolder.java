package me.ap.home.rpi.helper.station.service;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Getter
@Setter
public class MessageHolder {

    private String message;
    private ReplyKeyboard keyboard;
    byte[] image;

    public MessageHolder(String message, ReplyKeyboard keyboard) {
        this.message = message;
        this.keyboard = keyboard;
    }

    public MessageHolder(byte[] image, ReplyKeyboard keyboard) {
        this.keyboard = keyboard;
        this.image = image;
    }
}
