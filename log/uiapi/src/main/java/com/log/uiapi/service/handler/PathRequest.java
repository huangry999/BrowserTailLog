package com.log.uiapi.service.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathRequest implements RequestParam {
    protected String path;
    protected String hostName;
}
