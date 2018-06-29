package model;

import java.io.Serializable;

public class Message implements Serializable {

    public byte[] payload;

    public Message() {}

    public Message(byte [] payload) {
        this.payload = payload;
    }
}
