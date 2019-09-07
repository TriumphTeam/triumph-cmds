package me.mattstudios.mf.exceptions;

import me.mattstudios.mf.components.Message;

public class InvalidArgumentException extends Exception {

    private Message messageEnum;

    public InvalidArgumentException(Message messageEnum) {
        this.messageEnum = messageEnum;
    }

    public Message getMessageEnum() {
        return messageEnum;
    }
}
