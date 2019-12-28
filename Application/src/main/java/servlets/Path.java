package servlets;

public enum Path {

    goods(Constants.GOODS), signUp(Constants.SIGN_UP);

    private final String path;

    Path(String path) {
        this.path = path;
    }

    public static class Constants {
        public static final String root = "/";
        public static final String GOODS = root + "goods";
        public static final String SIGN_UP = root + "signUp";
    }
}
