package me.mattstudios.mf.exceptions;

public class InvalidArgExceptionMsg extends Exception {

    private String message;

    public InvalidArgExceptionMsg(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
