package com.log.uiapi;

import com.log.uiapi.socket.UiWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class UiApplication implements CommandLineRunner {

    private final UiWebSocketServer uiWebSocketServer;
    @Value("${server.address}")
    private String bindAddress;
    @Value("${uiapi-properties.netty.port}")
    private int bindPort;

    @Autowired
    public UiApplication(UiWebSocketServer uiWebSocketServer) {
        this.uiWebSocketServer = uiWebSocketServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(UiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //start websocket server.
        InetSocketAddress address = new InetSocketAddress(bindAddress, bindPort);
        uiWebSocketServer.start(address);

        //clean
        Runtime.getRuntime().addShutdownHook(new Thread(uiWebSocketServer::destroy));
    }
}
