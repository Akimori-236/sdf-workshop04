package fc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Cookie {

    public Cookie() {}

    public String getRandomCookie(String filename) {
        filename = "src/fc/" + filename;
        List<String> cookieList;
        Path p = Paths.get(filename);
        // READ FILE
        try{
            // readAlLines runs BuffferedReader under the hood
            cookieList = Files.readAllLines(p);
            Collections.shuffle(cookieList); // randomise
            return cookieList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: No cookie found";
        }
    }
}
