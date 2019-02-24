package com.log.logmonitor.monitor;

import java.io.File;

/**
 * Refer to org.apache.commons.io.monitor.FileAlterationListener.
 */
public interface MonitorListener {

    /**
     * Directory created Event.
     *
     * @param directory The directory created
     */
    void onDirectoryCreate(final File directory);

    /**
     * Directory changed Event.
     *
     * @param directory The directory changed
     */
    void onDirectoryChange(final File directory);

    /**
     * Directory deleted Event.
     *
     * @param directory The directory deleted
     */
    void onDirectoryDelete(final File directory);

    /**
     * File created Event.
     *
     * @param file The file created
     */
    void onFileCreate(final File file);

    /**
     * File modify Event.
     *
     * @param file The file changed
     */
    void onFileModify(final File file);

    /**
     * File deleted Event.
     *
     * @param file The file deleted
     */
    void onFileDelete(final File file);
}
