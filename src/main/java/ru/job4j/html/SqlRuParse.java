package ru.job4j.html;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SqlRuParse implements Parse {
    public static void main(String[] args) throws Exception {
        String url = "https://www.sql.ru/forum/job-offers/";
        SqlRuParse sqlRuParse = new SqlRuParse();
        System.out.println(sqlRuParse.list(url));
    }

    public static Date convertSqlDate(String s) throws ParseException {
        LocalDate date = LocalDate.now();
        String pattern = "d MMM yy, hh:mm";
        String strout = s;
        String[] part = s.split(",");
        if (part[0].trim().equalsIgnoreCase("сегодня") || part[0].trim().equalsIgnoreCase("вчера")) {
            pattern = "yyyy-MM-dd hh:mm";
            if (part[0].trim().equalsIgnoreCase("вчера")) {
                date = date.minusDays(1);
            }
            strout = date.toString().concat(" ").concat(part[1].trim());
        }
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
        dateFormatSymbols.setShortMonths(new String[]{"янв", "фев", "мар", "апр", "май", "июн", "июл", "авг", "сен", "окт", "ноя", "дек"});
        return new SimpleDateFormat(pattern, dateFormatSymbols).parse(strout);

    }

    public static String getDescriptionPost(String url) throws IOException {
        String description = "";

        Document doc = Jsoup.connect(url).get();
        Elements row = doc.select(".msgBody");
        if (row.size() > 1) {
            description = row.get(1).text();
        }
        return description;
    }

    @Override
    public List<Post> list(String link) throws IOException, ParseException {
        List<Post> list = new ArrayList<>();
        Document doc = Jsoup.connect(link).get();
        Elements row = doc.select(".postslisttopic");
        Elements altCol = doc.select(".altCol");
        for (int i = 0; i < row.size(); i++) {
            Element href = row.get(i).child(0);
            list.add(detail(href.attr("href"), href.text(), convertSqlDate(altCol.get(2 * i + 1).text())));
        }
        return list;
    }

    @Override
    public Post detail(String link, String text, Date date) throws IOException {
        String description = getDescriptionPost(link);
        return new Post(link, text, date, description);
    }
}
