package com.lookforlog.log;

import com.lookforlog.util.SpringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogApplicationTests {

    @Value("${netty.address}")
    private String address;

    @Test
    public void loadConfig() {
        assertEquals("192.168.1.101", address);
        Integer interval = SpringUtils.getProperty("reader.samplingInterval", Integer.class);
        assertEquals(Integer.valueOf(1), interval);
    }


}
