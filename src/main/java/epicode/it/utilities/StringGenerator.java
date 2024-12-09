package epicode.it.utilities;

import java.util.Random;

public class StringGenerator {
    public static String random(int length) {
        // caratteri per generare la stringa
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";

        Random random = new Random();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            // Estrai un carattere casuale dalla stringa alfanumerica
            int index = random.nextInt(alphanumeric.length());
            result.append(alphanumeric.charAt(index));
        }
        return result.toString().toUpperCase();
    }
}
