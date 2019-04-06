package com.log.fileservice.service;

import com.log.fileservice.config.LogFileProperties;
import com.log.fileservice.grpc.*;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@GRpcService
public final class LogFileService extends FileServiceGrpc.FileServiceImplBase {
    private final Map<File, LogFileContext> contextHolder = new ConcurrentHashMap<>();
    static final int FEED_LINE_SIZE = System.lineSeparator().getBytes().length;
    static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final LogFileProperties logFileProperties;

    @Autowired
    public LogFileService(LogFileProperties logFileProperties) {
        this.logFileProperties = logFileProperties;
    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadRespond> responseObserver) {
        String path = this.convertToRealPath(request.getFilePath());
        if (checkPathInvalid(path, responseObserver)) {
            return;
        }
        File file = new File(path);
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
        responseObserver.onCompleted();
    }

    @Override
    public void totalLineNumber(LineNumberRequest request, StreamObserver<LineNumberRespond> responseObserver) {
        String path = this.convertToRealPath(request.getFilePath());
        if (checkPathInvalid(path, responseObserver)) {
            return;
        }
        File file = new File(path);
        LogFileContext context = this.getContext(file);
        LineNumberRespond respond;
        if (context != null) {
            respond = LineNumberRespond.newBuilder().setTotalLineNo(context.getLastRowNo()).build();
        } else {
            respond = LineNumberRespond.newBuilder().setTotalLineNo(0L).build();
        }
        responseObserver.onNext(respond);
        responseObserver.onCompleted();
    }

    @Override
    public void listFile(ListRequest request, StreamObserver<ListRespond> responseObserver) {
        List<FileContext> files = new ArrayList<>();
        if (Strings.isBlank(request.getDirectoryPath())) {
            files = logFileProperties.getPath().stream()
                    .map(com.log.fileservice.config.bean.Path::getPath)
                    .map(File::new)
                    .map(f -> FileContext.newBuilder()
                            .setFilePath(this.convertToAliasPath(f.getPath()))
                            .setType(FileType.DIRECTORY)
                            .setModifyTime(f.exists() ? f.lastModified() : 0)
                            .build()
                    )
                    .filter(c -> c.getFilePath() != null)
                    .collect(Collectors.toList());
        } else {
            String dirPath = this.convertToRealPath(request.getDirectoryPath());
            if (checkPathInvalid(dirPath, responseObserver)) {
                return;
            }
            com.log.fileservice.config.bean.Path root = this.rootOf(dirPath);
            File dir = new File(dirPath);
            try {
                files = Files.list(dir.toPath())
                        .map(Path::toFile)
                        .filter(f -> this.logFileProperties.getFilter().accept(f))
                        .map(f -> FileContext.newBuilder()
                                .setFilePath(this.convertToAliasPath(f.getPath(), root))
                                .setModifyTime(f.lastModified())
                                .setType(f.isFile() ? FileType.FILE : FileType.DIRECTORY)
                                .setSize(f.length())
                                .build())
                        .filter(c -> c.getFilePath() != null)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                log.error("list dir {} files error", dirPath, e);
            }
        }
        responseObserver.onNext(ListRespond.newBuilder().addAllFiles(files).build());
        responseObserver.onCompleted();
    }

    @Override
    public void directoryContext(DirectoryContextRequest request, StreamObserver<DirectoryContextRespond> responseObserver) {
        String path = this.convertToRealPath(request.getDirectoryPath());
        if (checkPathInvalid(path, responseObserver)) {
            return;
        }
        DirectoryContextRespond.Builder builder = DirectoryContextRespond.newBuilder();
        boolean root = logFileProperties.getPath().stream().anyMatch(p -> p.getPath().equals(path));
        if (root) {
            builder.setLevel(DirectoryLevel.ROOT);
        } else if (Strings.isBlank(path)) {
            builder.setLevel(DirectoryLevel.HOST);
        } else {
            File dir = new File(path);
            if (dir.exists()) {
                builder.setLevel(DirectoryLevel.NORMAL);
                builder.setRollback(this.convertToAliasPath(dir.getParent()));
            } else {
                builder.setLevel(DirectoryLevel.ROOT);
            }
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    public String convertToAliasPath(String realPath) {
        return convertToAliasPath(realPath, this.rootOf(realPath));
    }

    private String convertToAliasPath(String realPath, com.log.fileservice.config.bean.Path root) {
        if (root == null) {
            return null;
        }
        return realPath.replace(root.getPath(), root.getAlias());
    }

    private String convertToRealPath(String path) {
        String alias = path.split("[\\\\/]")[0];
        String rootPath = logFileProperties.getPath()
                .stream()
                .filter(p -> p.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new MissingResourceException("Unknown path alias", "RootPath", path)).getPath();
        return Paths.get(rootPath, path.substring(alias.length())).toString();
    }

    private boolean checkPathInvalid(String path, StreamObserver observer) {
        boolean r = checkPathInvalid(path);
        if (r) {
            observer.onError(new IllegalArgumentException("Invalid path: " + path));
        }
        return r;
    }

    private boolean checkPathInvalid(String path) {
        com.log.fileservice.config.bean.Path root = this.rootOf(path);
        if (root == null) {
            return true;
        }
        File file = new File(path);
        return !file.exists() || !this.logFileProperties.getFilter().accept(file);
    }

    private com.log.fileservice.config.bean.Path rootOf(String path) {
        Path toJude = Paths.get(path);
        return logFileProperties.getPath()
                .stream()
                .filter(p -> toJude.startsWith(Paths.get(p.getPath())))
                .findFirst()
                .orElse(null);
    }

    public void removeFileContext(File file) {
        this.contextHolder.remove(file);
    }

    public boolean updateFileContext(File file) {
        if (this.contextHolder.containsKey(file)) {
            return this.contextHolder.get(file).updateIndex();
        }
        return false;
    }

    private List<String> read0(File file, long skip, long take) throws IOException {
        if (!file.exists() || !file.isFile()) {
            return new ArrayList<>();
        }
        final LogFileContext context = getContext(file);
        if (context == null) {
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
            contextHolder.put(file, context);
            return context;
        }
    }
}
