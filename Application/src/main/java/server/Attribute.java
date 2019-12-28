package server;

public enum Attribute {

   componentsContext("componentsContext"), username("username"), password("password"),goods("goods");

    String path;

    Attribute(String path) {
        this.path = path;
    }

    public String get() {
        return path;
    }
}
