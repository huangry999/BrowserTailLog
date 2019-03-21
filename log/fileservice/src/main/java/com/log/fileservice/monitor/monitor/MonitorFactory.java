package com.log.fileservice.monitor.monitor;

public interface MonitorFactory {

    /**
     * create a new monitor
     *
     * @param parameter the parametee
     * @return monitor
     */
    Monitor newMonitor(MonitorParameter parameter);
}
