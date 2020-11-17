package ru.job4j.html;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;



public class SqlRuParseTest {

    @Test
    public void convertSqlDate() throws ParseException {
        SqlRuParse sqlRuParse = new SqlRuParse();
        assertThat(sqlRuParse.convertSqlDate("2 дек 17, 22:11").toString(), is("Sat Dec 02 22:11:00 MSK 2017"));
        assertThat(sqlRuParse.convertSqlDate("сегодня, 22:11"), is(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(LocalDate.now().toString() + " 22:11")));
        assertThat(sqlRuParse.convertSqlDate("вчера, 22:11"), is(new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(LocalDate.now().minusDays(1).toString() + " 22:11")));
    }
}