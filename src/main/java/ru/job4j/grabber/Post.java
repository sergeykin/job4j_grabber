package ru.job4j.grabber;

import java.util.Date;

public class Post {
    private String id;
    private String href;
    private String text;
    private Date date;
    private String description;

    public Post(String href, String text, Date date, String description) {
        this.href = href;
        this.text = text;
        this.date = date;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Post{"
                + "href='" + href + '\''
                + ", text='" + text + '\''
                + ", date=" + date
                + ", description='" + description + '\''
                + '}';
    }
}
