package com.lookforlog.log;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class Touch {

    @Test
    public void touchFile() throws Exception {
        File referenceFile = new File("G:\\log\\log-col.log");
        long targetSize = 1024L * 1024L * 10L;
        String name = "G:\\log\\10mb.log";

        List<String> referenceContent = Files.readAllLines(referenceFile.toPath());

        File targetFile = new File(name);
        if (targetFile.exists()) {
            assert targetFile.delete();
        }
        assert targetFile.createNewFile();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8))) {
            long lineNo = 1;
            while (targetFile.length() < targetSize) {
                StringBuilder content = new StringBuilder();
                content.append(lineNo);
                content.append("  ");
                content.append(referenceContent.get((int) (Math.random() * referenceContent.size())));
                writer.write(content.toString());
                writer.newLine();
                lineNo += 1;
            }
            writer.close();
        }
        System.out.println(targetFile.getAbsolutePath() + " created!!!");
    }

}
