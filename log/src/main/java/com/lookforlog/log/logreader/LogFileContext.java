package com.lookforlog.log.logreader;

import com.lookforlog.util.SpringUtils;
import com.lookforlog.protocol.codec.LogProtocolCodec;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class LogFileContext {
    boolean ready = false;
    private TreeMap<Long, Long> indexes = new TreeMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastRowNo;
    private long lastRowIndex;
    private final File file;
    private final int samplingInterval;

    LogFileContext(File file) {
        this.file = file;
        this.samplingInterval = SpringUtils.getProperty("system.reader.samplingInterval", Integer.class);
    }

    Map.Entry<Long, Long> findIndex(long target) {
        return indexes.floorEntry(target);
    }

    void initIndex() throws Exception {
        lock.writeLock().lock();
        indexes.clear();
        lastRowIndex = 0;
        lastRowIndex = 0;
        try (RandomAccessFile rf = new RandomAccessFile(file, "r");
             FileInputStream is = new FileInputStream(rf.getFD());
             InputStreamReader isr = new InputStreamReader(is, LogProtocolCodec.CHARSET.newDecoder());
             BufferedReader br = new BufferedReader(isr)) {
            long rowIndex = 0;
            long rowNo = 1L;
            String str;
            while ((str = br.readLine()) != null) {
                this.addRowIndex(rowNo, rowIndex);
                rowNo++;
                rowIndex += str.getBytes(LogProtocolCodec.CHARSET).length + LogReader.FEED_LINE_SIZE;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    void updateIndex() throws Exception {
        lock.writeLock().lock();
        try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
            rf.seek(this.lastRowIndex);
            rf.readLine();
            long rowIndex = rf.getFilePointer();
            long rowNo = lastRowNo + 1;
            try (FileInputStream is = new FileInputStream(rf.getFD());
                 InputStreamReader isr = new InputStreamReader(is, LogProtocolCodec.CHARSET.newDecoder());
                 BufferedReader br = new BufferedReader(isr)) {
                String str;
                while ((str = br.readLine()) != null) {
                    this.addRowIndex(rowNo, rowIndex);
                    rowNo++;
                    rowIndex += str.getBytes(LogProtocolCodec.CHARSET).length + LogReader.FEED_LINE_SIZE;
                }
            }
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

    long getLastRowNo(){
        return this.lastRowNo;
    }
}
