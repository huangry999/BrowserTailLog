import org.apache.commons.io.FilenameUtils;

import java.io.IOException;

public class Test {

    @org.junit.Test
    public void test() throws IOException {
        String d1 = "G:/log";
        String d2 = "G:/log";
        String d3 = "G:/log/3.log";
        String d4 = "G:/log/test";
        System.out.println(FilenameUtils.directoryContains(d1, d2));
        System.out.println(FilenameUtils.directoryContains(d1, d3));
        System.out.println(FilenameUtils.directoryContains(d1, d4));
    }
}
