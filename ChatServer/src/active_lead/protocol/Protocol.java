package active_lead.protocol;

public class Protocol {

    String header;
    Payload payload;

    public Protocol() { }

    public Protocol(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
