package com.log.uiapi.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/index")
public class IndexController {
    @Value("${uiapi-properties.window-size}")
    private int windowSize;
    @Value("${uiapi-properties.shrink-threshold}")
    private Integer shrinkThreshold;
    @Value("${uiapi-properties.security.auth:''}")
    private String systemPassword;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> index() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("windowSize", windowSize);
        ret.put("shrinkThreshold", shrinkThreshold);
        ret.put("needAuth", Strings.isNotBlank(systemPassword));
        return ret;
    }
}
