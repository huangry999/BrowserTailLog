package com.log.fileservice.reader;

import com.log.common.spring.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
class LogFileContext {
    boolean ready = false;
    private TreeMap<Long, Long> indexes = new TreeMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastRowNo;
    private long lastRowIndex;
    private final File file;
    private final int samplingInterval;
    private long updateTime;

    LogFileContext(File file) {
        this.file = file;
        this.samplingInterval = SpringUtils.getProperty("system.reader.samplingInterval", Integer.class);
    }

    Map.Entry<Long, Long> findIndex(long target) {
        this.updateIndex();
        return indexes.floorEntry(target);
    }

    void initIndex() throws Exception {
        lock.writeLock().lock();
        indexes.clear();
        lastRowIndex = 0;
        try (RandomAccessFile rf = new RandomAccessFile(file, "r");
             FileInputStream is = new FileInputStream(rf.getFD());
             InputStreamReader isr = new InputStreamReader(is, LogFileService.DEFAULT_CHARSET.newDecoder());
             BufferedReader br = new BufferedReader(isr)) {
            long rowIndex = 0;
            long rowNo = 1L;
            setIndex(br, rowNo, rowIndex);
        } finally {
            updateTime = file.lastModified();
            lock.writeLock().unlock();
        }
    }

    private void updateIndex() {
        if (this.updateTime == file.lastModified()) {
            return;
        }
        lock.writeLock().lock();
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
            lock.writeLock().unlock();
        }
    }

    private void setIndex(BufferedReader br, long initRowNo, long initIndex) throws IOException {
        String str;
        while ((str = br.readLine()) != null) {
            this.addRowIndex(initRowNo, initIndex);
            initRowNo++;
            initIndex += str.getBytes(LogFileService.DEFAULT_CHARSET).length + LogFileService.FEED_LINE_SIZE;
        }
    }

    private void addRowIndex(long rowNo, long index) {
        if (rowNo == 1L || rowNo % this.samplingInterval == 0) {
            indexes.put(rowNo, index);
        }
        if (rowNo > lastRowNo) {
            lastRowNo = rowNo;
            lastRowIndex = index;
        }
    }

    long getLastRowNo() {
        this.updateIndex();
        return this.lastRowNo;
    }
}
