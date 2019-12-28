package servlets;

public enum Page {

    find("find"),login("login"),profile("profile"),newAdd("newAdd"),main("main"),main2("index"),
    signUp("signUp"),cart("cart"), goods("goods"),good("good"),anotherProfile("anotherProfile");

    String path;
    private final String root = "/page/";
    private final String suf = ".ftl";

    Page(String path) {
        this.path = path;
    }

    public String get() {
        return root + path + suf;
    }

}
