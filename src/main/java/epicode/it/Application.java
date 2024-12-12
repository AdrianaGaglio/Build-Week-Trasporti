package epicode.it;

import com.github.javafaker.Faker;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.utilities.menu.AdminMenu;
import epicode.it.utilities.menu.GestioneUtentiMenu;
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
            System.out.println("\n--- Scegli il tipo di utente ---");
            System.out.println("=========================");
            System.out.println("1. Admin");
            System.out.println("=========================");
            System.out.println("2. Rivenditore");
            System.out.println("=========================");
            System.out.println("3. Gestione utenti");
            System.out.println("=========================");
            System.out.println("=> Scegli un opzione valida (0 per uscire)\n");


            int tipo = scanner.nextInt();
            scanner.nextLine();
            switch (tipo){
                case 1 -> {
                    AdminMenu.mainMenuRunning = true;
                    AdminMenu.showAdminMenu(scanner, em);
                }
                case 2 -> RivenditoreMenu.showRivenditoreMenu(em, scanner);
                case 3 -> {
                    GestioneUtentiMenu.mainMenuRunning= true;
                    GestioneUtentiMenu.showGestioneUtenteMenu(scanner, em);
                }
                case 0 -> System.exit(0);
                default -> System.out.println("Scelta non consentita");
            }

        }


    }
}