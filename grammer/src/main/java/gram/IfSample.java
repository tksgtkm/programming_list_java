package gram;

public class IfSample {
    public static void main(String[] args) {
        var a = 2;
        if (a < 3) {
            System.out.println("small");
        } else if (a < 7) {
            System.out.println("middle");
        } else {
            System.out.println("large");
        }
    }
}