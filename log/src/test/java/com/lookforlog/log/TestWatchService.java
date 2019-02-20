package com.lookforlog.log;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class TestWatchService {

    public static void main(String[] args) throws Exception {
        FileAlterationMonitor monitor = new FileAlterationMonitor(1000);
        FileAlterationObserver observer = new FileAlterationObserver("G:\\test");
        monitor.addObserver(observer);
        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver fileAlterationObserver) {

            }

            @Override
            public void onDirectoryCreate(File file) {
                System.out.println("onDirectoryCreate " + file.getAbsolutePath());
            }

            @Override
            public void onDirectoryChange(File file) {
                System.out.println("onDirectoryChange " + file.getAbsolutePath());
            }

            @Override
            public void onDirectoryDelete(File file) {
                System.out.println("onDirectoryDelete " + file.getAbsolutePath());
            }

            @Override
            public void onFileCreate(File file) {
                System.out.println("onFileCreate " + file.getAbsolutePath());
            }

            @Override
            public void onFileChange(File file) {
                System.out.println("onFileChange " + file.getAbsolutePath());
            }

            @Override
            public void onFileDelete(File file) {
                System.out.println("onFileDelete " + file.getAbsolutePath());
            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {

            }
        });
        monitor.start();
    }
}
