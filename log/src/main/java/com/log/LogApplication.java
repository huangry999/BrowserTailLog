package com.log;

import com.log.logmonitor.LogMonitor;
import com.log.subscribe.Subscriber;
import com.log.subscribe.SubscriberManager;
import com.log.socket.LogWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.net.InetSocketAddress;

@SpringBootApplication
public class LogApplication implements CommandLineRunner {

    @Autowired
    private LogWebSocketServer logWebSocketServer;
    @Value("${server.address}")
    private String bindAddress;
    @Value("${netty.port}")
    private int bindPort;
    private LogMonitor logMonitor;
    @Autowired
    private SubscriberManager subscriberManager;

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //start websocket server.
        InetSocketAddress address = new InetSocketAddress(bindAddress, bindPort);
        logWebSocketServer.start(address);

        //start binding files events
        logMonitor = new LogMonitor();
        logMonitor.startAsync();

        //clean
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logWebSocketServer.destroy();
            if (logMonitor != null) {
                logMonitor.destroy();
            }
        }));

        //test
        File file = new File("G:\\log\\3.log");
        Subscriber subscriber = new Subscriber(file);
        subscriber.setCreateHandler(System.out::println);
        subscriber.setModifyHandler(System.out::println);
        subscriberManager.subscribe(subscriber);
    }
}
