package intro;

public class IfSample {
    public static void main(String[] args) {
        int a = 100;
        if (a < 3) {
            System.out.println("small");
        } else if (a < 7) {
            System.out.println("middle");
        } else {
            System.out.println("bigger");
        }
    }
}
