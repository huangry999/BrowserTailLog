package com.log.fileservice.monitor.monitor;

import java.io.FileFilter;
import java.util.List;

public class MonitorParameter {
    private MonitorListener monitorListener;
    private List<String> roots;
    private FileFilter fileFilter;

    public MonitorListener getMonitorListener() {
        return monitorListener;
    }

    public void setMonitorListener(MonitorListener monitorListener) {
        this.monitorListener = monitorListener;
    }

    public List<String> getRoots() {
        return roots;
    }

    public void setRoots(List<String> roots) {
        this.roots = roots;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }
}
