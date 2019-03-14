import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String e1 = encoder.encode("clientpassword");
        String e2 = encoder.encode("clientpassword");
        System.out.println(e1);
        System.out.println(e2);
        System.out.println(encoder.matches("clientpassword", e1));
        System.out.println(encoder.matches("clientpassword", e2));
    }
}
