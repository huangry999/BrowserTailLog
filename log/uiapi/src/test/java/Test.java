import org.apache.commons.io.FilenameUtils;
import org.assertj.core.util.Lists;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        String d1 = "log\\1.log";
        String d2 = "log/1.log";
        System.out.println(Arrays.toString(d1.split("[\\\\/]")));
        System.out.println(Arrays.toString(d2.split("[\\\\/]")));

        String d = Lists.newArrayList(
                "/usr/local/javaApp/log", "/usr/local/javaApp/logtimer/log")
                .stream()
                .filter(p -> {
                    try {
                        return FilenameUtils.directoryContains(p, "/usr/local/javaApp/logtimer/log/timer.log");
                    } catch (IOException e) {
                        return false;
                    }
                })
                .findFirst()
                .orElse(null);
        System.out.println(d);
    }
}
