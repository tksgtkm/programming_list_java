package multi;

public class GreeterFactory {
    public static Greeter make(String language) throws Exception {
        if (language.equals("English")) {
            return new English();
        } else if (language.equals("French")) {
            return new French();
        } else {
            throw new Exception();
        }
    }
}
