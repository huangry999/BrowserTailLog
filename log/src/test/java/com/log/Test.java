package com.log;

import java.io.File;
import java.net.URI;

public class Test {
    public static void main(String[] args) throws Exception {
        URI uri = new URI("ws://127.0.0.1:8080/websocket");
        System.out.println(uri.getHost());
    }
}
