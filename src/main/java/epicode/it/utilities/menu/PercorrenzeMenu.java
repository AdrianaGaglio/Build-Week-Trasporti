package epicode.it.utilities.menu;

import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import epicode.it.servizi.gestore_percorrenze.GestorePercorrenze;
import epicode.it.servizi.gestore_percorrenze.StatisticheTratta;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
            System.out.println("3. Percorrenze per mezzo");
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
                TrattaDAO trattaDAO = new TrattaDAO(em);
                GestorePercorrenze gestorePercorrenze = new GestorePercorrenze(em);

                System.out.println("Tratte disponibili:");
                List<Tratta> tratte = trattaDAO.getAll();
                System.out.println("\n------------------------");
                tratte.forEach(System.out::println);
                System.out.println("------------------------\n");

                System.out.println("Scegli l'ID della tratta: ");
                long idTratta = scanner.nextLong();
                scanner.nextLine();

                Tratta tratta = tratte.stream()
                        .filter(t -> t.getId() == idTratta)
                        .findFirst()
                        .orElse(null);

                if (tratta == null) {
                    System.err.println("Tratta non trovata!");
                    break;
                }

                List<Mezzo> mezziAssociati = tratta.getPercorrenze().stream()
                        .map(Percorrenza::getMezzo)
                        .distinct()
                        .toList();

                if (mezziAssociati.isEmpty()) {
                    System.out.println("Nessun mezzo associato alla tratta selezionata.");
                    break;
                }

                System.out.println("Statistiche per la tratta selezionata:");
                System.out.println("\n------------------------");

                for (Mezzo mezzo : mezziAssociati) {
                    StatisticheTratta statistiche = gestorePercorrenze.mediaPercorrenzaPerTrattaEMezzo(tratta, mezzo);

                    System.out.printf(
                            "Mezzo ID: %d, Media Durata Percorrenze: %s (Totale Percorrenze: %d)%n",
                            mezzo.getId(),
                            statistiche.getMediaPercorrenza(),
                            statistiche.getNumCorse()
                    );
                }
                System.out.println("------------------------\n");


            }
            case 3 -> {
                MezzoDAO mezzoDAO = new MezzoDAO(em);


                System.out.println("Mezzi disponibili:");
                List<Mezzo> mezzi = mezzoDAO.findAll();
                System.out.println("\n------------------------");
                mezzi.forEach(System.out::println);
                System.out.println("------------------------\n");

                System.out.println("Scegli l'ID del mezzo: ");
                long idMezzo = scanner.nextLong();
                scanner.nextLine();

                Mezzo mezzo = mezzi.stream()
                        .filter(m -> m.getId() == idMezzo)
                        .findFirst()
                        .orElse(null);

                if (mezzo == null) {
                    System.err.println("Mezzo non trovato!");
                    break;
                }

                List<Percorrenza> percorrenze = mezzo.getPercorrenze();
                if (percorrenze.isEmpty()) {
                    System.out.println("Nessuna percorrenza trovata per il mezzo selezionato.");
                    break;
                }

                System.out.println("Percorrenze del mezzo:");
                System.out.println("\n------------------------");
                percorrenze.forEach(System.out::println);
                System.out.println("------------------------\n");


            }

            case 0 -> mainMenuRunning = false;
            default -> System.err.println("Opzione non valida");
        }
    }
}
