package com.log.fileservice.reader;

import com.log.fileservice.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
//import org.lognet.springboot.grpc.GRpcService;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@GRpcService
public final class LogFileService extends ReadServiceGrpc.ReadServiceImplBase {
    private final Map<File, LogFileContext> contextHolder = new ConcurrentHashMap<>();
    static final int FEED_LINE_SIZE = System.lineSeparator().getBytes().length;
    static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    public void read(ReadRequest request, StreamObserver<ReadRespond> responseObserver) {
        File file = new File(request.getFilePath());
        long skip = request.getSkip();
        try {
            final List<String> raw = this.read0(file, skip, request.getTake());
            ReadRespond.Builder builder = ReadRespond.newBuilder();
            for (int i = 0; i < raw.size(); i++) {
                LineText lt = LineText.newBuilder().setLineNo(skip + i + 1)
                        .setText(raw.get(i))
                        .build();
                builder.addContents(lt);
            }
            responseObserver.onNext(builder.build());
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void count(CountRequest request, StreamObserver<CountRespond> responseObserver) {
        File file = new File(request.getFilePath());
        LogFileContext context = this.getContext(file);
        CountRespond respond;
        if (context != null) {
            respond = CountRespond.newBuilder().setTotalLineNo(context.getLastRowNo()).build();
        } else {
            respond = CountRespond.newBuilder().setTotalLineNo(0L).build();
        }
        responseObserver.onNext(respond);
    }

    public void removeFileContext(File file) {
        this.contextHolder.remove(file);
    }

    private List<String> read0(File file, long skip, long take) throws IOException {
        if (!file.exists() || !file.isFile()) {
            return new ArrayList<>();
        }
        final LogFileContext context = getContext(file);
        if (context == null || !context.ready) {
            return new ArrayList<>();
        }

        try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
            if (skip != 0) {
                final Map.Entry<Long, Long> index = context.findIndex(skip);
                if (index == null) {
                    return new ArrayList<>();
                }
                long ni = index.getValue();
                long nr = index.getKey();
                rf.seek(ni);
                try (FileInputStream is = new FileInputStream(rf.getFD());
                     InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8.newDecoder());
                     BufferedReader br = new BufferedReader(isr)) {
                    return br.lines().skip(skip - nr + 1).limit(take).collect(Collectors.toList());
                }
            }
            try (FileInputStream is = new FileInputStream(rf.getFD());
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader br = new BufferedReader(isr)) {
                return br.lines().limit(take).collect(Collectors.toList());
            }
        }
    }

    private LogFileContext getContext(final File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        if (contextHolder.containsKey(file)) {
            return contextHolder.get(file);
        }
        synchronized (this) {
            if (contextHolder.containsKey(file)) {
                return contextHolder.get(file);
            }
            final LogFileContext context = new LogFileContext(file);
            try {
                context.initIndex();
            } catch (Exception e) {
                log.error("init file {} context error: ", file.getAbsolutePath(), e);
            }
            contextHolder.put(file, context);
            return context;
        }
    }
}
