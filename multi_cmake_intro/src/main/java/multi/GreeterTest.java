package multi;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GreeterTest {
    public static void main(String[] args) {
        String language = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Running GreeterTest1");

        do {
            try {
                System.out.print("\nLanguage? ");
                language = br.readLine().trim();

                if (language.length() > 0) {
                    Greeter greeter = GreeterFactory.make(language);
                    System.out.println(greeter.greet());
                }
            } catch (Exception ex) {
                System.out.println("Sorry I dont know speak " + language + ".");
            }
        } while (language.length() > 0);
    }
}
