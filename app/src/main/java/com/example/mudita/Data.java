package com.example.mudita;

public class Data {
    String Title,Message;

    public Data(String title, String message) {
        Title = title;
        Message = message;
    }

    public Data() {
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
