package com.log.uiapi.service;

import com.log.fileservice.grpc.Empty;
import com.log.fileservice.grpc.FileServiceInfo;
import com.log.fileservice.grpc.ServiceInfoGrpc;
import com.log.uiapi.service.bean.Host;
import com.netflix.appinfo.InstanceInfo;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.ChannelException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class HostService {
    private final Map<String, ManagedChannel> fileServiceIdChannelMap = new ConcurrentHashMap<>();
    private final Map<String, Host> fileServiceIdContextMap = new ConcurrentHashMap<>();
    @Value("${eureka.file-service-app-group-name:fileservice}")
    private String fileServiceGroupAppName;

    @EventListener
    public void listenFileServiceRegister(EurekaInstanceRegisteredEvent event) {
        final InstanceInfo instanceInfo = event.getInstanceInfo();
        log.info("new service register, group name: {}, id:{}, ip: {}, port: {}", instanceInfo.getAppGroupName(), instanceInfo.getId(), instanceInfo.getIPAddr(), instanceInfo.getPort());
        if (instanceInfo.getAppGroupName() == null
                || !instanceInfo.getAppGroupName().equalsIgnoreCase(this.fileServiceGroupAppName)) {
            return;
        }
        ManagedChannel managedChannel = NettyChannelBuilder.forAddress(instanceInfo.getIPAddr(), instanceInfo.getPort())
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        fileServiceIdChannelMap.putIfAbsent(instanceInfo.getId(), managedChannel);
        FileServiceInfo respond = ServiceInfoGrpc.newBlockingStub(managedChannel).fileService(Empty.newBuilder().build());
        Host host = new Host();
        host.setId(instanceInfo.getId());
        host.setIp(instanceInfo.getIPAddr());
        host.setName(respond.getHostName());
        host.setRpcPort(instanceInfo.getPort());
        host.setDesc(respond.getDesc());
        fileServiceIdContextMap.put(instanceInfo.getId(), host);
        log.info("file service context insert: {}", host.toString());
    }

    @EventListener
    public void listenFileServiceCanceled(EurekaInstanceCanceledEvent event) {
        final String id = event.getServerId();
        this.fileServiceIdChannelMap.remove(id);
        this.fileServiceIdContextMap.remove(id);
        log.info("file service canceled, id:{}", id);
    }

    public Host get(String hostName) {
        return this.fileServiceIdContextMap.values()
                .stream()
                .filter(c -> c.getName().equals(hostName))
                .findFirst()
                .orElseThrow(() -> new MissingResourceException("Unknown host Exception", "Host", hostName));
    }

    public List<Host> getAll() {
        return new ArrayList<>(this.fileServiceIdContextMap.values());
    }

    /**
     * Get a ManagedChannel of the host
     *
     * @param hostName host name of host
     * @return Grpc client
     * @throws ChannelException If the client is not available
     */
    public ManagedChannel getChannel(String hostName) throws ChannelException {
        final String id = this.get(hostName).getId();
        return this.fileServiceIdChannelMap.get(id);
    }
}
