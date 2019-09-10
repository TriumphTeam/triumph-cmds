package me.mattstudios.mf.exceptions;

public class InvalidArgException extends Exception {

    private String messageId;

    public InvalidArgException(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }
}
