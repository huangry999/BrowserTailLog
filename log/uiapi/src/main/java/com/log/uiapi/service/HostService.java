package com.log.uiapi.service;

import com.log.uiapi.config.HostsProperties;
import com.log.uiapi.config.bean.Host;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.channel.ChannelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HostService {
    private final List<Host> hosts;
    private final Map<String, ManagedChannel> channelMap = new ConcurrentHashMap<>();

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
    }

    public Host get(String hostName) {
        return hosts.stream().filter(h -> h.getName().equals(hostName)).findFirst().orElse(null);
    }

    public List<Host> getAll() {
        return hosts;
    }

    /**
     * Get a ManagedChannel of the host
     *
     * @param hostName host name of host
     * @return Grpc client
     * @throws ChannelException If the client is not available
     */
    public ManagedChannel getChannel(String hostName) throws ChannelException {
        ManagedChannel result = null;
        if (!channelMap.containsKey(hostName)) {
            synchronized (this) {
                if (!channelMap.containsKey(hostName)) {
                    ManagedChannel channel = this.initChannel(hostName);
                    channelMap.put(hostName, channel);
                    result = channel;
                }
            }
        } else {
            result = channelMap.get(hostName);
        }
        if (result != null) {
            checkConnectionState(result, hostName);
        }
        return result;
    }

    private void checkConnectionState(ManagedChannel channel, String hostName) {
        ConnectivityState state = channel.getState(false);
        if (state == ConnectivityState.SHUTDOWN || state == ConnectivityState.TRANSIENT_FAILURE) {
            channelMap.remove(hostName);
            throw new ChannelException(String.format("host %s is not available", hostName));
        }
    }

    private ManagedChannel initChannel(String hostName) {
        Host host = hosts.stream()
                .filter(h -> h.getName().equals(hostName))
                .findFirst()
                .orElseThrow(() -> new MissingResourceException("Unknown host Exception", "Host", hostName));
        return NettyChannelBuilder.forAddress(host.getIp(), host.getRpcPort())
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
    }
}
