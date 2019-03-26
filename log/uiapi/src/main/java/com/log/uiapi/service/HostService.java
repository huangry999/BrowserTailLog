package com.log.uiapi.service;

import com.log.uiapi.config.HostsProperties;
import com.log.uiapi.config.bean.Host;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HostService {
    private final List<Host> hosts;
    private final Map<String, ManagedChannel> channelMap;

    @Autowired
    public HostService(HostsProperties hostsProperties) {
        Set<String> checkDuplicate = new HashSet<>(hostsProperties.getHosts().size());
        for (Host h : hostsProperties.getHosts()) {
            if (checkDuplicate.contains(h.getName())) {
                throw new IllegalArgumentException("Host in configuration must be unique");
            } else {
                checkDuplicate.add(h.getName());
            }
        }
        this.hosts = hostsProperties.getHosts();
        this.channelMap = new HashMap<>(hosts.size());
        for (Host host : hosts) {
            ManagedChannel channel = NettyChannelBuilder.forAddress(host.getIp(), host.getRpcPort())
                    .negotiationType(NegotiationType.PLAINTEXT)
                    .build();
            this.channelMap.put(host.getName(), channel);
        }
    }

    public Host get(String hostName) {
        return hosts.stream().filter(h -> h.getName().equals(hostName)).findFirst().orElse(null);
    }

    public List<Host> getAll() {
        return hosts;
    }

    public ManagedChannel getChannel(String hostName) {
        return this.channelMap.get(hostName);
    }
}
