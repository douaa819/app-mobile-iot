package com.example.smartsecurityapp;

public class Alert {
    private String timestamp;
    private String imageUrl;




    public Alert() {
        // Required for Firebase
    }

    public Alert(String timestamp, String imageUrl,double distance) {
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;

    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}


