package fc;

public class CookieTest {
    public static void main(String[] args) {
        Cookie c = new Cookie();
        String cookie = c.getRandomCookie("cookie-file.txt");
        System.out.println(cookie);
    }
}
