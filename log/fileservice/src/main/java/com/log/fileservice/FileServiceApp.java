package com.log.fileservice;

import com.log.fileservice.monitor.LogMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FileServiceApp implements CommandLineRunner {
    private final LogMonitor logMonitor;

    @Autowired
    public FileServiceApp(LogMonitor logMonitor) {
        this.logMonitor = logMonitor;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileServiceApp.class, args);
    }

    @Override
    public void run(String... args) {
        logMonitor.startAsync();
        Runtime.getRuntime().addShutdownHook(new Thread(logMonitor::destroy));
    }
}
