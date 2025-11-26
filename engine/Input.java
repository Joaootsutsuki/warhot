package engine;

import java.io.IOException;

public class Input {
    public static char getKey() {
        try {
            char c;
            do {
                c = (char) System.in.read();
            } while (c == '\n' || c == '\r');
            return c;
        } catch (Exception e) {
            return ' ';
        }
    }
}
