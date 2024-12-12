package epicode.it.utilities.menu;

import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import epicode.it.entities.biglietto.Periodicy;
import jakarta.persistence.EntityManager;

import java.util.Scanner;

public class RivenditoreMenu {

    public static void showRivenditoreMenu(EntityManager em, Scanner scanner) {
        GestoreRivenditoriEBiglietti gestore = new GestoreRivenditoriEBiglietti(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);

        while (true) {
            System.out.println("--- Menu rivenditore ---");
            System.out.println("1. Emetti biglietto");
            System.out.println("2. Emetti nuovo abbonamento");
            System.out.println("3. Emetti nuova tessera");
            System.out.println("4. Rinnova abbonamento");
            System.out.println("5. Rinnova tessera");
            System.out.println("6. Esci");
            System.out.print("Scegli un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma newline

            switch (scelta) {
                case 1 -> {
                    // Codice per emettere biglietto (già presente)
                }
                case 2 -> {
                    // Codice per emettere abbonamento (già presente)
                }
                case 3 -> {
                    // Codice per emettere tessera (già presente)
                }
                case 4 -> {
                    // Rinnovo abbonamento
                    System.out.print("Inserisci ID del rivenditore: ");
                    long idRivenditore = scanner.nextLong();
                    Rivenditore rivenditore = gestore.richiamaRivenditore(idRivenditore);

                    System.out.print("Inserisci email dell'utente: ");
                    String email = scanner.next();
                    Utente utente = utenteDAO.findByEmail(email);

                    if (utente == null) {
                        System.err.println("Utente non trovato!");
                        break;
                    }

                    System.out.print("Inserisci periodicità del rinnovo (annuale, mensile, ecc.): ");
                    String periodicita = scanner.next().toLowerCase();

                    try {
                        gestore.rinnovaAbbonamento(rivenditore, utente, Periodicy.valueOf(periodicita));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Periodicità non valida!");
                    }
                }
                case 5 -> {
                    // Rinnovo tessera
                    System.out.print("Inserisci ID del rivenditore: ");
                    long idRivenditore = scanner.nextLong();
                    Rivenditore rivenditore = gestore.richiamaRivenditore(idRivenditore);

                    System.out.print("Inserisci email dell'utente: ");
                    String email = scanner.next();
                    Utente utente = utenteDAO.findByEmail(email);

                    if (utente == null) {
                        System.err.println("Utente non trovato!");
                        break;
                    }

                    gestore.rinnovaTessera(rivenditore, utente);
                }
                case 6 -> {
                    System.out.println("Uscita dal menu rivenditore.");
                    return;
                }
                default -> System.err.println("Opzione non valida!");
            }
        }
    }
}
