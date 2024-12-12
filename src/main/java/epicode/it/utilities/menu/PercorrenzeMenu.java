package epicode.it.utilities.menu;

import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.servizi.gestore_percorrenze.GestorePercorrenze;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class PercorrenzeMenu {
    public static boolean mainMenuRunning = true;


    public static void showPercorrenzeMenu(Scanner scanner, EntityManager em) {
        while (mainMenuRunning) {
            System.out.println("\n--- Menu admin ---");
            System.out.println("=========================");
            System.out.println("1. Avvia percorrenza");
            System.out.println("=========================");
            System.out.println("2. Statistiche percorrenze per tratta e mezzo");
            System.out.println("=========================");
            System.out.println("=> Scegli un opzione valida: (0 per tornare indietro)");
            switchOptions(scanner, em);

        }

    }

    private static void switchOptions(Scanner scanner, EntityManager em) {
        int choice = scanner.nextInt();
        scanner.nextLine();
        switch (choice) {
            case 1 -> {
                MezzoDAO mezzoDAO = new MezzoDAO(em);
                GestorePercorrenze gestorePercorrenze = new GestorePercorrenze(em);
                System.out.println("Mezzi in servizio da far partire:");
                List<Mezzo> mezzi = mezzoDAO.mezziPerStato(Stato.IN_SERVIZIO);
                System.out.println("\n------------------------");
                mezzi.forEach(System.out::println);
                System.out.println("------------------------\n");
                System.out.println("Scegli il mezzo: ");
                long idMezzo = scanner.nextLong();
                scanner.nextLine();
                Mezzo mezzo = mezzi.stream().filter(mezzoo -> mezzoo.getId() == idMezzo).findFirst().orElse(null);
                if (mezzo == null) {
                    System.out.println("Mezzo non trovato!");
                    break;
                }  else {
                    Servizio inServizio = mezzo.getServizi()
                            .stream()
                            .filter(servizio -> servizio.getDataFine() == null)
                            .findFirst()
                            .orElse(null);
                    if (inServizio != null) {
                    gestorePercorrenze.aggiungiPercorrenza(mezzo, LocalDateTime.now(), inServizio.getTratta());
                    } else {
                        System.out.println("Mezzo non in servizio!");
                    }
                }

            }
            case 2 -> {
                System.out.println("Scegli la tratta: ");


            }
            case 0 -> mainMenuRunning = false;
            default -> System.err.println("Opzione non valida");
        }
    }
}
