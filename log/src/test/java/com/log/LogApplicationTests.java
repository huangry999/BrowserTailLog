package com.log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogApplicationTests {

	@Value("${server.address}")
	private String address;

	@Test
	public void loadConfig() {
		assertEquals("127.0.0.1", address);
	}


}
