package com.a.zyango.POJO;

public class Messages {
    String message;
    String from;
    String type;
    String key;
    long timestamp;
    Boolean seen;

    public Messages() {
    }

    public Messages(String message, String from, String type, String key, long timestamp, Boolean seen) {
        this.message = message;
        this.from = from;
        this.type = type;
        this.key = key;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }


}
