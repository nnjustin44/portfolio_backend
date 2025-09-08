package org.acme.dto.response;

public class ChatResponse {
    private String message;
    private String response;
    private long timestamp;

    public ChatResponse() {
    }

    public ChatResponse(String message, String response, long timestamp) {
        this.message = message;
        this.response = response;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}