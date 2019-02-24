package com.log.controller;

import com.log.config.bean.Host;
import com.log.config.HostsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("host")
public class HostController {

    private final HostsProperties hostsProperties;

    @Autowired
    public HostController(HostsProperties hostsProperties) {
        this.hostsProperties = hostsProperties;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Host> listHost(){
        return hostsProperties.getHosts();
    }
}
