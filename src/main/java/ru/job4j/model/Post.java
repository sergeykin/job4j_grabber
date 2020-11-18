package ru.job4j.model;

import java.util.Date;

public class Post {
    String href;
    String text;
    Date date;

    public Post(String href, String text, Date date) {
        this.href = href;
        this.text = text;
        this.date = date;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
