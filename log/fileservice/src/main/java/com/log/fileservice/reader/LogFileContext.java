package com.log.fileservice.reader;

import com.log.common.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
class LogFileContext {
    private TreeMap<Long, Long> indexes = new TreeMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastRowNo;
    private long lastRowIndex;
    private final File file;
    private final int samplingInterval;
    private long updateTime;
    //to check context has been overwrite when update indexes.
    private LinkedHashMap<Long, String> eigenvalues = new LinkedHashMap<Long, String>(10) {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 10;
        }
    };

    LogFileContext(File file) {
        this.file = file;
        this.samplingInterval = SpringUtils.getProperty("file-reader.sampling-interval", Integer.class);
        init();
    }

    /**
     * Get index map which is the most close to target line no
     *
     * @param target target line no
     * @return index map, key is the index and value is the line no of this index
     */
    Map.Entry<Long, Long> findIndex(long target) {
        return indexes.floorEntry(target);
    }

    long getLastRowNo() {
        return this.lastRowNo;
    }

    /**
     * update index
     * <p>
     * if file isn't exists, will reset context to initialization.
     * </p>
     * <p>
     * if file content changed by checking last 10 lines text, will rebuild index.
     * </p>
     *
     * @return true if refresh index
     */
    boolean updateIndex() {
        log.debug("update file index, file: {}", file.getAbsolutePath());
        lock.writeLock().lock();
        try {
            if (!file.exists()) {
                indexes.clear();
                lastRowNo = 0;
                return false;
            }
            if (this.updateTime == file.lastModified()) {
                return false;
            }
            if (checkEigenvalues()) {
                init();
                return true;
            }
            try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
                rf.seek(this.lastRowIndex);
                rf.readLine();
                long rowIndex = rf.getFilePointer();
                long rowNo = lastRowNo + 1;
                try (FileInputStream is = new FileInputStream(rf.getFD());
                     InputStreamReader isr = new InputStreamReader(is, LogFileService.DEFAULT_CHARSET.newDecoder());
                     BufferedReader br = new BufferedReader(isr)) {
                    setIndex(br, rowNo, rowIndex);
                }
            } catch (Exception e) {
                log.error("update file index exception, file: {}", file.getAbsolutePath(), e);
            } finally {
                updateTime = file.lastModified();
            }
        } finally {
            lock.writeLock().unlock();
        }
        return false;
    }

    private void init() {
        indexes.clear();
        eigenvalues.clear();
        lastRowIndex = 0;
        try (RandomAccessFile rf = new RandomAccessFile(file, "r");
             FileInputStream is = new FileInputStream(rf.getFD());
             InputStreamReader isr = new InputStreamReader(is, LogFileService.DEFAULT_CHARSET.newDecoder());
             BufferedReader br = new BufferedReader(isr)) {
            long rowIndex = 0;
            long rowNo = 1L;
            setIndex(br, rowNo, rowIndex);
        } catch (Exception e) {
            log.error("Init file: {} error", file.getAbsolutePath(), e);
        } finally {
            updateTime = file.lastModified();
        }
    }

    /**
     * Check if eigenvalues is the same as record.
     *
     * @return true if context changed.
     */
    private boolean checkEigenvalues() {
        try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
            for (Long offset : eigenvalues.keySet()) {
                rf.seek(offset);
                String l = rf.readLine();
                if (l == null || !l.equals(eigenvalues.get(offset))) {
                    log.debug("file: {} content changed", file.getAbsolutePath());
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("check eigenvalues exception, file: {}", file.getAbsolutePath(), e);
            return true;
        }
    }

    private void setIndex(BufferedReader br, long initRowNo, long initIndex) throws IOException {
        String str;
        while ((str = br.readLine()) != null) {
            eigenvalues.put(initIndex, str);
            this.addRowIndex(initRowNo, initIndex);
            initRowNo++;
            initIndex += str.getBytes(LogFileService.DEFAULT_CHARSET).length + LogFileService.FEED_LINE_SIZE;
        }
    }

    private void addRowIndex(long rowNo, long index) {
        if (rowNo == 1L || rowNo % this.samplingInterval == 0) {
            indexes.put(rowNo, index);
        }
        lastRowNo = rowNo;
        lastRowIndex = index;
    }
}
