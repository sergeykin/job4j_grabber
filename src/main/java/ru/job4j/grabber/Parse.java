package ru.job4j.grabber;


import ru.job4j.model.Post;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface Parse {
    List<Post> list(String link) throws IOException, ParseException;

    Post detail(String link, String text, Date date) throws IOException;
}
