package com.log;

import java.io.File;

public class Test {
    public static void main(String[] args) throws Exception {
        File file = new File("G:\\log\\3.log");
        System.out.println(file.getParent());
    }
}
