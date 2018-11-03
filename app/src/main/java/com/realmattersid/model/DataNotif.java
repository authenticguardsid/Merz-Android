package com.realmattersid.model;

public class DataNotif {
    private int id;
    String  key, date_notif, title_notif, summary_notif, image_notif;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate_notif() {
        return date_notif;
    }

    public void setDate_notif(String date_notif) {
        this.date_notif = date_notif;
    }

    public String getTitle_notif() {
        return title_notif;
    }

    public void setTitle_notif(String title_notif) {
        this.title_notif = title_notif;
    }

    public String getSummary_notif() {
        return summary_notif;
    }

    public void setSummary_notif(String summary_notif) {
        this.summary_notif = summary_notif;
    }

    public String getImage_notif() {
        return image_notif;
    }

    public void setImage_notif(String image_notif) {
        this.image_notif = image_notif;
    }
}
