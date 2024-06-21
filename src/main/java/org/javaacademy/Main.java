package org.javaacademy;

import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.javaacademy.entity.Comment;
import org.javaacademy.entity.User;
import org.javaacademy.entity.Video;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties properties = getProperties();
        @Cleanup SessionFactory sessionFactory = new Configuration().addProperties(properties)
                .addAnnotatedClass(Comment.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Video.class)
                .buildSessionFactory();

        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();
        User john = new User("john");
        User rick = new User("rick");
        session.persist(john);
        session.persist(rick);

        Video firstInterview = new Video("Мое первое интервью", "какое-то описание", john);
        Video secondInterview = new Video("Мое второе интервью", "какое-то описание", john);
        session.persist(firstInterview);
        session.persist(secondInterview);

        Comment comment = new Comment(firstInterview, rick, "классное интервью");
        session.persist(comment);
        session.getTransaction().commit();
        session.clear();

        User user = session.get(User.class, john.getId());
        List<Comment> comments = user.getVideos().stream()
                .filter(video -> video.getName().equals("Мое первое интервью"))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Видео не найдено"))
                .getComments();

        System.out.println(comments);
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.connection.url", "jdbc:postgresql://localhost:5432/youtube");
        properties.put("hibernate.connection.username", "postgres");
        properties.put("hibernate.connection.password", "1703");
        properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put(Environment.SHOW_SQL, true);
        properties.put(Environment.FORMAT_SQL, true);
        return properties;
    }
}