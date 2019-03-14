//package com.log.logreader;
//
//import com.log.service.bean.LogLineText;
//import com.log.subscribe.Subscriber;
//import com.log.subscribe.SubscriberManager;
//import com.log.protocol.codec.LogProtocolCodec;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
//@Component
//public class LogReader {
//    private final SubscriberManager subscriberManager;
//    private final static Logger logger = LoggerFactory.getLogger(LogReader.class);
//    private final Map<File, LogFileContext> contextHolder = new ConcurrentHashMap<>();
//    static final int FEED_LINE_SIZE = System.lineSeparator().getBytes().length;
//
//    @Autowired
//    public LogReader(SubscriberManager subscriberManager) {
//        this.subscriberManager = subscriberManager;
//    }
//
//    /**
//     * read file. while use file index if it's available.
//     *
//     * @param file the file
//     * @param skip skip lines
//     * @param take take lines
//     * @return list of LogLineText
//     * @throws IOException io Exception
//     */
//    public List<LogLineText> read(File file, long skip, int take) throws IOException {
//        final List<String> raw = this.read0(file, skip, take);
//        final List<LogLineText> result = new ArrayList<>(raw.size());
//        for (int i = 0; i < raw.size(); i++) {
//            result.add(new LogLineText(skip + i + 1, raw.get(i)));
//        }
//        return result;
//    }
//
//    /**
//     * get file line count
//     *
//     * @param file log file
//     * @return line count
//     * @throws IOException io Exception
//     */
//    public long count(File file) throws IOException {
//        if (!file.exists() || !file.isFile()){
//            return 0;
//        }
//        final LogFileContext context = getContext(file);
//        if (context == null || !context.ready){
//            return 0;
//        }
//        return context.getLastRowNo();
//    }
//
//    private List<String> read0(File file, long skip, int take) throws IOException {
//        if (!file.exists() || !file.isFile()){
//            return new ArrayList<>();
//        }
//        final LogFileContext context = getContext(file);
//        if (context == null || !context.ready) {
//            return new ArrayList<>();
//        }
//
//        try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
//            if (skip != 0) {
//                final Map.Entry<Long, Long> index = context.findIndex(skip);
//                if (index == null) {
//                    return new ArrayList<>();
//                }
//                long ni = index.getValue();
//                long nr = index.getKey();
//                rf.seek(ni);
//                try (FileInputStream is = new FileInputStream(rf.getFD());
//                     InputStreamReader isr = new InputStreamReader(is, LogProtocolCodec.CHARSET.newDecoder());
//                     BufferedReader br = new BufferedReader(isr)) {
//                    return br.lines().skip(skip - nr + 1).limit(take).collect(Collectors.toList());
//                }
//            }
//            try (FileInputStream is = new FileInputStream(rf.getFD());
//                 InputStreamReader isr = new InputStreamReader(is);
//                 BufferedReader br = new BufferedReader(isr)) {
//                return br.lines().limit(take).collect(Collectors.toList());
//            }
//        }
//    }
//
//    private LogFileContext getContext(final File file) {
//        if (!file.exists() || !file.isFile()) {
//            return null;
//        }
//        if (contextHolder.containsKey(file)) {
//            return contextHolder.get(file);
//        }
//        synchronized (this) {
//            if (contextHolder.containsKey(file)) {
//                return contextHolder.get(file);
//            }
//            final LogFileContext context = new LogFileContext(file);
//            try {
//                context.initIndex();
//            } catch (Exception e) {
//                logger.error("init file {} context error: ", file.getAbsolutePath(), e);
//            }
//            final Subscriber subscriber = new Subscriber(file);
//            subscriber.setModifyHandler(sub -> {
//                try {
//                    context.updateIndex();
//                } catch (Exception e) {
//                    logger.error("file {} context modify update error: ", file.getAbsolutePath(), e);
//                }
//            });
//            subscriber.setDeleteHandler(sub -> {
//                contextHolder.remove(file);
//                subscriberManager.remove(subscriber);
//            });
//            subscriberManager.subscribe(subscriber);
//            context.ready = true;
//            contextHolder.put(file, context);
//            return context;
//        }
//    }
//}
