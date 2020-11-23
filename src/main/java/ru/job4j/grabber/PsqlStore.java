package ru.job4j.grabber;


import ru.job4j.quartz.AlertRabbit;

import java.io.InputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    public static void main(String[] args) {

        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties rabbitProps = new Properties();
            rabbitProps.load(in);
            PsqlStore psqlStore = new PsqlStore(rabbitProps);


            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());

            psqlStore.save(new Post("2", "2", date, "2"));
            System.out.println(psqlStore.findById("1"));
            List<Post> posts = psqlStore.getAll();
            System.out.println(posts);
            psqlStore.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Connection cnn;

    public PsqlStore(Properties cfg) throws SQLException {
        try {
            Class.forName(cfg.getProperty("driver-class-name"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        cnn = DriverManager.getConnection(
                cfg.getProperty("url"),
                cfg.getProperty("username"),
                cfg.getProperty("password")
        );
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = this.cnn.prepareStatement("insert into post (name, text, link, created) values (?, ?, ?, ?) RETURNING id;")) {
            st.setString(1, post.getText());
            st.setString(2, post.getDescription());
            st.setString(3, post.getHref());
            st.setObject(4, post.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    post.setId(rs.getString("id"));
                }
            }
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement st = this.cnn.prepareStatement("select * from post")) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post(rs.getString("link"),
                            rs.getString("name"),
                            rs.getDate("created"),
                            rs.getString("text"));
                    post.setId(String.valueOf(rs.getInt("id")));
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(String id) {
        Post post = null;
        try (PreparedStatement st = this.cnn.prepareStatement("select * from post where id=?")) {
            st.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    post = new Post(rs.getString("link"),
                            rs.getString("name"),
                            rs.getDate("created"),
                            rs.getString("text"));
                    post.setId(id);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
