import org.apache.commons.io.FilenameUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws Exception {
        String d1 = "log\\1.log";
        String d2 = "log/1.log";
        System.out.println(Arrays.toString(d1.split("[\\\\/]")));
        System.out.println(Arrays.toString(d2.split("[\\\\/]")));
    }
}
