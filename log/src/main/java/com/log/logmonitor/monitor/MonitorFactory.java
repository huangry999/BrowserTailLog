package com.log.logmonitor.monitor;

public interface MonitorFactory {

    /**
     * create a new monitor
     *
     * @param parameter the parametee
     * @return monitor
     */
    Monitor newMonitor(MonitorParameter parameter);
}
