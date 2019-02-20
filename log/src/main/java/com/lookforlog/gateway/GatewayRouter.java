package com.lookforlog.gateway;

import com.lookforlog.gateway.socket.GatewaySocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class GatewayRouter implements CommandLineRunner {
    private final GatewaySocketServer gatewaySocketServer;
    @Value("${proxy.address}")
    private String bindAddress;
    @Value("${proxy.port}")
    private int bindPort;

    @Autowired
    public GatewayRouter(GatewaySocketServer gatewaySocketServer) {
        this.gatewaySocketServer = gatewaySocketServer;
    }

    public static void main(String[] args) {
        SpringApplication.run(GatewayRouter.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(bindAddress, bindPort);
        gatewaySocketServer.start(address);
        //clean
        Runtime.getRuntime().addShutdownHook(new Thread(gatewaySocketServer::destroy));
    }
}
