package epicode.it;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Locale;
import java.util.Scanner;

public class Application {

    private static Scanner scanner = new Scanner(System.in);

    private static void showAdminMenu() {
        System.out.println("1. Aggiungi rivenditore");
        System.out.println("2. Gestisci rivenditori");
        System.out.println("3. Gestisci rivenditori");
    }

    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        while(true) {

        }
    }
}