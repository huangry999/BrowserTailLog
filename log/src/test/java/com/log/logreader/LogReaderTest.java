package com.log.logreader;

import com.log.logreader.LogReader;
import com.log.service.bean.LogLineText;
import com.log.util.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogReaderTest {
    @Autowired
    private LogReader logReader;

    @Test
    public void testRead() throws IOException {
        long t1 = System.currentTimeMillis();
        File file = new File("G:\\log\\10mb.log");
        List<LogLineText> texts = logReader.read(file, 100, 100);
        assertEquals(100, texts.size());
        System.out.println("LogReaderTest#testRead takes time: " + (System.currentTimeMillis() - t1));
    }

    @Test
    public void testReadTime() throws IOException {
        File file = new File("G:\\log\\2gb.log");
        Files.lines(file.toPath()).count();

        long t1 = System.currentTimeMillis();
        FileUtils.getLogText(file, 10000000, 10000);
        long t2 = System.currentTimeMillis();
        logReader.read(file, 10000000, 10000);
        long t3 = System.currentTimeMillis();
        FileUtils.getLogText(file, 10000000, 10000);
        long t4 = System.currentTimeMillis();
        logReader.read(file, 10000000, 10000);
        long t5 = System.currentTimeMillis();

        System.out.println("LogReaderTest#testReadTime FileUtils 1st takes time: " + (t2 - t1));
        System.out.println("LogReaderTest#testReadTime LogReader 1st takes time: " + (t3 - t2));
        System.out.println("LogReaderTest#testReadTime FileUtils 2nd takes time: " + (t4 - t3));
        System.out.println("LogReaderTest#testReadTime LogReader 1st takes time: " + (t5 - t4));

    }
}
