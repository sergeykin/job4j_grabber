package ru.job4j.quartz;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {

        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            Properties rabbitProps = new Properties();
            rabbitProps.load(in);
            Class.forName(rabbitProps.getProperty("driver-class-name"));
            Integer interval = Integer.valueOf(rabbitProps.getProperty("rabbit.interval"));
            try (Connection connection = DriverManager.getConnection(
                    rabbitProps.getProperty("url"),
                    rabbitProps.getProperty("username"),
                    rabbitProps.getProperty("password")
            )) {
                try {
                    List<Long> store = new ArrayList<>();
                    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                    scheduler.start();
                    JobDataMap data = new JobDataMap();
                    data.put("connection", connection);
                    JobDetail job = newJob(Rabbit.class)
                            .usingJobData(data)
                            .build();
                    SimpleScheduleBuilder times = simpleSchedule()
                            .withIntervalInSeconds(interval)
                            .repeatForever();
                    Trigger trigger = newTrigger()
                            .startNow()
                            .withSchedule(times)
                            .build();
                    scheduler.scheduleJob(job, trigger);
                    Thread.sleep(10000);
                    scheduler.shutdown();
                    System.out.println(store);
                } catch (Exception se) {
                    se.printStackTrace();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static class Rabbit implements Job {

        public Rabbit() {
            System.out.println(hashCode());
        }

        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement prSt = connection.prepareStatement("INSERT INTO rabbit(createdDate) VALUES(?)", Statement.RETURN_GENERATED_KEYS)) {
                prSt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                prSt.executeUpdate();
                ResultSet generatedKeys = prSt.getGeneratedKeys();
            } catch (SQLException eSQL) {
                eSQL.printStackTrace();
            }
        }
    }
}
