package com.lookforlog.log;

import com.lookforlog.log.logmonitor.LogMonitor;
import com.lookforlog.log.socket.LogWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class LogApplication implements CommandLineRunner {

    private final LogWebSocketServer logWebSocketServer;
    @Value("${netty.address}")
    private String bindAddress;
    @Value("${netty.port}")
    private int bindPort;
    private final LogMonitor logMonitor;

    @Autowired
    public LogApplication(LogMonitor logMonitor, LogWebSocketServer logWebSocketServer) {
        this.logMonitor = logMonitor;
        this.logWebSocketServer = logWebSocketServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //start websocket server.
        InetSocketAddress address = new InetSocketAddress(bindAddress, bindPort);
        logWebSocketServer.start(address);

        //start binding files events
        logMonitor.startAsync();

        //clean
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logWebSocketServer.destroy();
            logMonitor.destroy();
        }));
    }
}
