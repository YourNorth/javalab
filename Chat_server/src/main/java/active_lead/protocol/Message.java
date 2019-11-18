package active_lead.protocol;

public class Message extends Payload {

    String user;
    String message;
    String date;

    public Message() {
    }

    public Message(String name, String message, String time) {
        this.name = name;
        this.message = message;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
