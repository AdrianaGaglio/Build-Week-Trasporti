package epicode.it;

import com.github.javafaker.Faker;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.utilities.menu.AdminMenu;
import epicode.it.utilities.menu.RivenditoreMenu;
import epicode.it.utilities.menu.UtenteMenu;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.awt.*;
import java.util.Locale;
import java.util.Scanner;

public class Application {

    private static Scanner scanner = new Scanner(System.in);



    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();



        while(true) {
            System.out.println("Scegli il tipo di utente: 1-Admin 2-Rivenditore 3-Utente (0-Per uscire)");
            int tipo = scanner.nextInt();
            scanner.nextLine();
            switch (tipo){
                case 1 -> AdminMenu.showAdminMenu(scanner, em);
                case 2 -> RivenditoreMenu.showRivenditoreMenu(em, scanner);
                case 3 -> UtenteMenu.showUtenteMenu(scanner, em);
                case 0 -> System.exit(0);
                default -> System.out.println("Scelta non consentita");
            }

        }


    }
}