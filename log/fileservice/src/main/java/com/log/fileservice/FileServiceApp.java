package com.log.fileservice;

import com.log.fileservice.monitor.LogMonitor;
import com.log.fileservice.notify.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
public class FileServiceApp implements CommandLineRunner {
    private final LogMonitor logMonitor;
    private final NotificationService notificationService;

    @Autowired
    public FileServiceApp(LogMonitor logMonitor, NotificationService notificationService) {
        this.logMonitor = logMonitor;
        this.notificationService = notificationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApp.class, args);
    }

    @Override
    public void run(String... args) {
        logMonitor.startAsync();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            notificationService.destroy();
            logMonitor.destroy();
        }));
    }
}
