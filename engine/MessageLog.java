package engine;

import java.util.ArrayList;
import java.util.List;

public class MessageLog {
    private static final int MAX_MESSAGES = 5;
    private List<String> messages;

    public MessageLog() {
        messages = new ArrayList<>();
    }

    public void adicionar(String mensagem) {
        messages.add(mensagem);
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }
    }

    public List<String> getMessages() {
        return messages;
    }

    public void limpar() {
        messages.clear();
    }
}