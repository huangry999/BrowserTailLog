package com.log.uiapi.controller;

import com.log.uiapi.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("host")
@Slf4j
public class HostController {

    private final HostService hostService;

    @Autowired
    public HostController(HostService hostService) {
        this.hostService = hostService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> listHost() {
        return hostService.getAll()
                .stream()
                .map(h -> {
                    Map<String, String> o = new HashMap<>();
                    o.put("name", h.getName());
                    o.put("desc", h.getDesc());
                    return o;
                })
                .collect(Collectors.toList());
    }
}
