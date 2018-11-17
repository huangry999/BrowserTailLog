package com.log.logmonitor.commoniomonitor;

import com.log.logmonitor.monitor.Monitor;
import com.log.logmonitor.monitor.MonitorFactory;
import com.log.logmonitor.monitor.MonitorListener;
import com.log.logmonitor.monitor.MonitorParameter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

public class CommonIoMonitorFactory implements MonitorFactory {
    @Value("${monitor.intervalMs}")
    private int intervalMs;

    @Override
    public Monitor newMonitor(MonitorParameter parameter) {
        return new CommonIoMonitor(parameter, intervalMs);
    }
}

class CommonIoMonitor implements Monitor {
    private FileAlterationMonitor monitor;


    CommonIoMonitor(MonitorParameter parameter, int intervalMs) {
        monitor = new FileAlterationMonitor(intervalMs);
        if (parameter.getRoots() != null && !parameter.getRoots().isEmpty())
            for (String path : parameter.getRoots()) {
                FileAlterationObserver observer = new FileAlterationObserver(new File(path), parameter.getFileFilter());
                final MonitorListener listener = parameter.getMonitorListener();
                if (listener != null) {
                    observer.addListener(new FileAlterationListener() {

                        @Override
                        public void onStart(FileAlterationObserver observer) {

                        }

                        @Override
                        public void onDirectoryCreate(File directory) {
                            listener.onDirectoryCreate(directory);
                        }

                        @Override
                        public void onDirectoryChange(File directory) {
                            listener.onDirectoryChange(directory);
                        }

                        @Override
                        public void onDirectoryDelete(File directory) {
                            listener.onDirectoryDelete(directory);
                        }

                        @Override
                        public void onFileCreate(File file) {
                            listener.onFileCreate(file);
                        }

                        @Override
                        public void onFileChange(File file) {
                            listener.onFileChange(file);
                        }

                        @Override
                        public void onFileDelete(File file) {
                            listener.onFileDelete(file);
                        }

                        @Override
                        public void onStop(FileAlterationObserver observer) {

                        }
                    });
                }
                this.monitor.addObserver(observer);
            }
    }

    @Override
    public void start() throws Exception {
        this.monitor.start();
    }

    @Override
    public void pause() throws Exception {
        this.monitor.stop();
    }

    @Override
    public void release() throws Exception {
        this.monitor.stop();
    }
}
