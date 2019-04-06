package com.log.fileservice.service;

import com.log.fileservice.grpc.Empty;
import com.log.fileservice.grpc.FileServiceInfo;
import com.log.fileservice.grpc.ServiceInfoGrpc;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Value;

@GRpcService
public class FileServiceInfoImpl extends ServiceInfoGrpc.ServiceInfoImplBase {
    @Value("${log-host.name}")
    private String hostName;
    @Value("${log-host.desc}")
    private String hostDesc;

    @Override
    public void fileService(Empty request, StreamObserver<FileServiceInfo> responseObserver) {
        FileServiceInfo info = FileServiceInfo.newBuilder().setHostName(hostName).setDesc(hostDesc).build();
        responseObserver.onNext(info);
        responseObserver.onCompleted();
    }
}
