package engine;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

// contexto das libs:
// System.in.read() do java eh line buffered, e o java não tem uma interface própria pra WinAPI 
// (A API do windows tem como tirar line buffering, mas só com c/c++)
// então eu achei essa lib aí do jline que faz uma interface de java pra c/c++ que arruma bastante dessas limitações de terminal que o java tem
// é gambiarra, mas é melhor que nada, pq é impossivel fazer qualquer coisa terminal-based nessa merda de lang

// C/C++ >>>>>>>>>>>>>>>> JAVA

public class Input {
    private static Terminal terminal;
    private static NonBlockingReader reader;

    static {
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .build();
            terminal.enterRawMode();
            reader = terminal.reader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static char getKey() {
        try {
            int c = reader.read();
            return (char) c;
        } catch (Exception e) {
            return ' ';
        }
    }

    public static void cleanup() {
        try {
            if (terminal != null) {
                terminal.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}