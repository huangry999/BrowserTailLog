package com.log;

import com.log.config.LogFileProperties;
import com.log.util.SpringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogApplicationTests {

    @Value("${netty.address}")
    private String address;

    @Test
    public void loadConfig() {
        assertEquals("192.168.1.101", address);
        Integer interval = SpringUtils.getProperty("system.reader.samplingInterval", Integer.class);
        assertEquals(Integer.valueOf(1000), interval);

        LogFileProperties logFileProperties = SpringUtils.get(LogFileProperties.class);
        assertEquals(true, logFileProperties.isRecursive());

        HostsProperties hostsProperties = SpringUtils.get(HostsProperties.class);
        assertNotNull("host is null", hostsProperties.getHosts());
    }


}
