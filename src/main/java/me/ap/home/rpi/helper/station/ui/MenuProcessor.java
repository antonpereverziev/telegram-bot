package me.ap.home.rpi.helper.station.ui;

import me.ap.home.rpi.helper.station.service.MessageHolder;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuProcessor {
    public static MenuItem MENU_ROOT = new MenuItem();
    public static Map<String, MenuItem> TEXT_MENU_MAPPING = new HashMap<>();

    // Don't use '-' in menu keys
    public static String MENU_STRUCTURE = """
               +-GET_TEMPERATURE            =Temperature
               +-GET_PICTURE                =Picture
               +-LIGHT_ON                   =Light ON
               +-LIGHT_OFF                  =Light OFF
               +-ADVANCED_MENU              =->               
                 +-GET_DISCOVERED_DEVICES   =Get devices
                 | +-EWELINK_DEVICES        =Ewelink devices
                 | +-SHELLY_DEVICES         =Shelly devices
                 | +-WEB_CAMERAS            =Web cameras
                 +-ADMINISTRATION           =Administration         @admin
                   +-UPGRADE_BOT            =Upgrade bot
                   +-GET_LOGS               =Get logs
            """;

    private static Map<String, MenuItem> CHAT_ID_MENU_MAP = new HashMap<>();

    public static void init() {
        String[] menuItems = MENU_STRUCTURE.split("\n");
        MENU_ROOT.setSubMenuItems(new ArrayList<>());
        List<MenuItem> currentMenu = MENU_ROOT.getSubMenuItems();
        int indent = 0;
        int previousIndent = 0;
        MenuItem parent = MENU_ROOT;
        for (String item : menuItems) {
            String[] keyValue = item.split("=");
            indent = keyValue[0].lastIndexOf('-');
            if (previousIndent == 0) {
                previousIndent = indent;
            }
            String key = keyValue[0].replaceAll("\\W", "");
            String text = keyValue[1].trim();
            MenuItem menuItem = new MenuItem();
            menuItem.setKey(key);
            menuItem.setText(text);
            if (indent == previousIndent) {
                menuItem.setParent(parent);
                currentMenu.add(menuItem);
            } else if (indent > previousIndent) {
                parent = currentMenu.get(currentMenu.size() - 1);
                menuItem.setParent(parent);
                currentMenu = new ArrayList<>();
                parent.setSubMenuItems(currentMenu);
                currentMenu.add(menuItem);
                previousIndent = indent;
            } else {
                parent = parent.getParent();
                currentMenu = parent.getSubMenuItems();
                menuItem.setParent(parent);
                currentMenu.add(menuItem);
                previousIndent = indent;
            }
        }
        processKeyboards(MENU_ROOT, true);
    }

    private static void processKeyboards(MenuItem menu, boolean isMain) {
        List<String> stringItems = new ArrayList<>();
        TEXT_MENU_MAPPING.put(menu.getText(), menu);
        for (MenuItem item : menu.getSubMenuItems()) {
            stringItems.add(item.getText());
            if(item.getSubMenuItems() != null) {
                processKeyboards(item, false);
            }
        }
        if(!isMain) {
            stringItems.add(MessageProcessor.BACK_ARROW);
        }
        menu.setKeyboard(getKeyboard(stringItems));
    }

    public static MessageHolder processMenuNavigation(String text, String chatId) {
        MenuItem menuItem = null;
        if (MessageProcessor.BACK_ARROW.equals(text)) {
            menuItem = CHAT_ID_MENU_MAP.get(chatId);
            menuItem = menuItem.getParent();
        } else {
            menuItem = TEXT_MENU_MAPPING.get(text);
        }
        CHAT_ID_MENU_MAP.put(chatId, menuItem);
        return new MessageHolder("Done", getKeyboard(chatId));
    }

    public static ReplyKeyboard getKeyboard(String telegramChatId) {
        MenuItem menuItem = CHAT_ID_MENU_MAP.get(telegramChatId);
        if (menuItem == null) {
            menuItem = MENU_ROOT;
            CHAT_ID_MENU_MAP.put(telegramChatId, menuItem);
        }
        return menuItem.getKeyboard();
    }

    private static ReplyKeyboard getKeyboard(List<String> items) {
        ReplyKeyboardMarkup result = new ReplyKeyboardMarkup();
        result.setResizeKeyboard(true);
        result.setOneTimeKeyboard(false);
        result.setSelective(true);
        List<KeyboardRow> btn = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String item : items) {
            keyboardRow.add(new KeyboardButton(item));
        }
        btn.add(keyboardRow);
        result.setKeyboard(btn);
        return result;
    }
}
