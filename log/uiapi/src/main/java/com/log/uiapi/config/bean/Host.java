package com.log.uiapi.config.bean;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Host {
    private String ip;
    private String name;
    private String desc;
    private int rpcPort;
}
