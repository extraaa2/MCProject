package ella.idpchat;

public class ChatMessage {
    public User user;
    public String message;

    public ChatMessage() {
    }

    public ChatMessage(User user, String message) {
        this.user = user;
        this.message = message;
    }

    @Override
    public String toString() {
        return user + ": " + message + "\n";
    }
}
