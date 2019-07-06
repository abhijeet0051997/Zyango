package com.a.zyango.POJO;

public class Chat {
    String user_id, type, message, from;
    Boolean seen;
    Long timestamp;

    public Chat() {
    }

    public Chat(String type, String message, String from, Boolean seen, Long timestamp) {
        this.user_id = user_id;
        this.type = type;
        this.message = message;
        this.from = from;
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
