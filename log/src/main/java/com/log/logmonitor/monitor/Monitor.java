package com.log.logmonitor.monitor;

/**
 * Refer to Refer to org.apache.commons.io.monitor.FileAlterationMonitor.
 */
public interface Monitor {

    /**
     * Start the monitor. Will not block the thread
     */
    void start() throws Exception;

    /**
     *  Pause.
     */
    void pause() throws Exception;

    /**
     * Pause the monitor and release all resources
     */
    void release() throws Exception;
}
