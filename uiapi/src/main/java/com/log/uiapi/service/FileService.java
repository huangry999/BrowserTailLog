package com.log.uiapi.service;

import com.log.fileservice.grpc.*;
import com.log.uiapi.service.bean.LogFileAttribute;
import com.log.uiapi.service.bean.LogLineText;
import com.log.uiapi.service.bean.RollbackAttribute;
import io.grpc.ManagedChannel;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final HostService hostService;

    @Autowired
    public FileService(HostService hostService) {
        this.hostService = hostService;
    }

    public List<LogLineText> read(String hostName, String file, long skip, long take) {
        ManagedChannel channel = hostService.getChannel(hostName);
        ReadRequest request = ReadRequest.newBuilder().setFilePath(file).setSkip(skip).setTake(take).build();
        ReadRespond respond = FileServiceGrpc.newBlockingStub(channel)
                .read(request);
        return respond.getContentsList().stream().map(LogLineText::new).collect(Collectors.toList());
    }

    public long totalLineNo(String hostName, String file) {
        ManagedChannel channel = hostService.getChannel(hostName);
        LineNumberRequest request = LineNumberRequest.newBuilder().setFilePath(file).build();
        LineNumberRespond respond = FileServiceGrpc.newBlockingStub(channel)
                .totalLineNumber(request);
        return respond.getTotalLineNo();
    }

    public List<LogFileAttribute> list(String hostName, String directory) {
        ManagedChannel channel = hostService.getChannel(hostName);
        ListRequest request = ListRequest.newBuilder()
                .setDirectoryPath(directory == null ? "" : directory).build();
        ListRespond respond = FileServiceGrpc.newBlockingStub(channel)
                .listFile(request);
        return respond.getFilesList().stream().map(LogFileAttribute::new).collect(Collectors.toList());
    }

    public RollbackAttribute directoryContext(String hostName, @NotBlank String dir) {
        ManagedChannel channel = hostService.getChannel(hostName);
        DirectoryContextRequest request = DirectoryContextRequest
                .newBuilder()
                .setDirectoryPath(Strings.isBlank(dir) ? "" : dir)
                .build();
        DirectoryContextRespond respond = FileServiceGrpc.newBlockingStub(channel)
                .directoryContext(request);
        return new RollbackAttribute(respond);
    }

}
