package com.log.uiapi.service.bean;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Host {
    private String id;
    private String ip;
    private String name;
    private String desc;
    private int rpcPort;
}
