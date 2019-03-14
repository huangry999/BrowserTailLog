//package com.log;
//
//import com.log.logmonitor.LogMonitor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.net.InetSocketAddress;
//
//@SpringBootApplication
//public class LogApplication implements CommandLineRunner {
//
//    @Value("${netty.address}")
//    private String bindAddress;
//    @Value("${netty.port}")
//    private int bindPort;
//    private final LogMonitor logMonitor;
//
//    @Autowired
//    public LogApplication(LogMonitor logMonitor) {
//        this.logMonitor = logMonitor;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(LogApplication.class, args);
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//
//        //start websocket server.
//        InetSocketAddress address = new InetSocketAddress(bindAddress, bindPort);
//
//        //start binding files events
//        logMonitor.startAsync();
//
//        //clean
//        Runtime.getRuntime().addShutdownHook(new Thread(logMonitor::destroy));
//    }
//}
