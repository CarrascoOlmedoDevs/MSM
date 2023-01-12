package com.pengrad.telegrambot.request;

import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.request.ParseMode;
import org.jetbrains.annotations.NotNull;

/**
 * stas
 * 5/1/16.
 */
public class SendMessage extends AbstractSendRequest<SendMessage> {

    public SendMessage(Object chatId, String text) {
        super(chatId);
        add("disable_web_page_preview", true);
        add("text", text);
    }

    public SendMessage parseMode(@NotNull ParseMode parseMode) {
        return add("parse_mode", parseMode.name());
    }

    public SendMessage entities(MessageEntity... entities) {
        return add("entities", entities);
    }

    public SendMessage disableWebPagePreview(boolean disableWebPagePreview) {
        return add("disable_web_page_preview", disableWebPagePreview);
    }
}
