package me.ap.home.rpi.helper.station.ui;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

@Getter
@Setter
class MenuItem {
    private String key;
    private String text;
    private List<MenuItem> subMenuItems;
    private MenuItem parent;
    private ReplyKeyboard keyboard;
}