package com.log.logmonitor.commoniomonitor;

import com.log.logmonitor.monitor.Monitor;
import com.log.logmonitor.monitor.MonitorFactory;
import com.log.logmonitor.monitor.MonitorListener;
import com.log.logmonitor.monitor.MonitorParameter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(CommonIoMonitorFactory.class);

    CommonIoMonitor(MonitorParameter parameter, int intervalMs) {
        monitor = new FileAlterationMonitor(intervalMs);
        if (parameter.getRoots() != null && !parameter.getRoots().isEmpty())
            for (String path : parameter.getRoots()) {
                FileAlterationObserver observer = new FileAlterationObserver(path, parameter.getFileFilter());
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
                            if (path.equals(file.getParent())){
                                this.onDirectoryChange(file.getParentFile());
                            }
                        }

                        @Override
                        public void onFileChange(File file) {
                            if (file.exists()){
                                listener.onFileModify(file);
                                if (path.equals(file.getParent())){
                                    this.onDirectoryChange(file.getParentFile());
                                }
                            }else{
                                this.onFileDelete(file);
                            }
                        }

                        @Override
                        public void onFileDelete(File file) {
                            listener.onFileDelete(file);
                            if (path.equals(file.getParent())){
                                this.onDirectoryChange(file.getParentFile());
                            }
                        }

                        @Override
                        public void onStop(FileAlterationObserver observer) {

                        }
                    });
                }
                this.monitor.addObserver(observer);
                logger.debug("add {} to monitor", path);

            }
    }

    @Override
    public void start() throws Exception {
        this.monitor.start();
        logger.debug("monitor start");
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
