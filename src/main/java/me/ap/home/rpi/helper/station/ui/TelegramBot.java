package me.ap.home.rpi.helper.station.ui;

import me.ap.home.rpi.helper.station.service.MessageHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.io.*;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String telegramBotToken;

    @Autowired
    private MessageProcessor processor;

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if ((update.hasMessage() && update.getMessage().hasText())) {
                String telegramChatId = String.valueOf(update.getMessage().getChatId());
                String text = update.getMessage().getText();
                log.info("New request from Telegram bot with id: " + telegramChatId);
                MessageHolder message = processor.processMessage(text, telegramChatId);
                sendResponse(message, telegramChatId);
            } else {
                log.info("File: " + update.getMessage().getDocument().getFileSize());
                String fileId = update.getMessage().getDocument().getFileId();
                GetFile getFile = new GetFile();
                getFile.setFileId(fileId);
                String filePath = execute(getFile).getFilePath();
                File file = downloadFile(filePath, new File("new_app.zip"));
                log.info("File downloaded: " + file.getAbsolutePath());
            }
        } catch (TelegramApiException ex) {
            log.error("Telegram Bot is unable to process update!", ex);
        }
    }

    private void sendResponse(MessageHolder message, String chatId) {
        if (message.getMessage() != null){
            sendText(chatId, message.getMessage(), message.getKeyboard());
        } else {
            sendImage(chatId, message.getImage(), message.getKeyboard());
        }
    }

    public void sendText(String chatId, String text) {
        sendText(chatId, text, MenuProcessor.getKeyboard(chatId));
    }

    public void sendImage(String chatId, byte[] image) {
        sendImage(chatId, image, MenuProcessor.getKeyboard(chatId));
    }

    public void sendText(String chatId, String text, ReplyKeyboard markup) {
        SendMessage ad = new SendMessage();
        ad.setChatId(chatId);
        ad.setText(text);
        ad.setReplyMarkup(markup);
        try {
            execute(ad);
        } catch (Exception e) {
            log.warn("Exception during sending text message: {}", e.getMessage());
        }
    }

    public void sendImage(String chatId, byte[] image, ReplyKeyboard markup) {
        InputFile photoFile = new InputFile(new ByteArrayInputStream(image), "Image");
        SendPhoto sendPhoto = new SendPhoto(chatId, photoFile);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.warn("Exception during sending photo: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "My home bot";
    }

    @Override
    public String getBotToken() {
        return telegramBotToken;
    }
}
