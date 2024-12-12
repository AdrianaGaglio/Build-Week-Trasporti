package epicode.it.utilities.menu;

import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tratta.Tratta;
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
            System.out.println("\n--- Menu rivenditore ---");
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
                case 1 -> emettiBiglietto(scanner, gestore, utenteDAO);
                case 2 -> emettiAbbonamento(scanner, gestore, utenteDAO);
                case 3 -> emettiTessera(scanner, gestore, utenteDAO);
                case 4 -> rinnovaAbbonamento(scanner, gestore, utenteDAO);
                case 5 -> rinnovaTessera(scanner, gestore, utenteDAO);
                case 6 -> {
                    System.out.println("Uscita dal menu rivenditore.");
                    return;
                }
                default -> System.err.println("Opzione non valida!");
            }
        }
    }

    private static void emettiBiglietto(Scanner scanner, GestoreRivenditoriEBiglietti gestore, UtenteDAO utenteDAO) {
        System.out.print("Inserisci email dell'utente: ");
        String email = scanner.next();
        Utente utente = utenteDAO.findByEmail(email);

        if (utente == null) {
            System.err.println("Utente non trovato!");
            return;
        }

        System.out.print("Inserisci il nome della tratta: ");
        String nomeTratta = scanner.next();
        Tratta tratta = gestore.trovaTratta(nomeTratta);

        if (tratta == null) {
            System.err.println("Tratta non trovata!");
            return;
        }

        gestore.vendiBigliettoGiornaliero(utente, tratta);
        System.out.println("Biglietto emesso con successo!");
    }

    private static void emettiAbbonamento(Scanner scanner, GestoreRivenditoriEBiglietti gestore, UtenteDAO utenteDAO) {
        System.out.print("Inserisci email dell'utente: ");
        String email = scanner.next();
        Utente utente = utenteDAO.findByEmail(email);

        if (utente == null) {
            System.err.println("Utente non trovato!");
            return;
        }

        System.out.print("Inserisci periodicità dell'abbonamento (annuale, mensile, settimanale): ");
        String periodicita = scanner.next().toLowerCase();

        try {
            gestore.emettiAbbonamento(utente, Periodicy.valueOf(periodicita));
            System.out.println("Abbonamento emesso con successo!");
        } catch (IllegalArgumentException e) {
            System.err.println("Periodicità non valida!");
        }
    }

    private static void emettiTessera(Scanner scanner, GestoreRivenditoriEBiglietti gestore, UtenteDAO utenteDAO) {
        System.out.print("Inserisci email dell'utente: ");
        String email = scanner.next();
        Utente utente = utenteDAO.findByEmail(email);

        if (utente == null) {
            System.err.println("Utente non trovato!");
            return;
        }

        gestore.emettiTessera(utente);
        System.out.println("Tessera emessa con successo!");
    }

    private static void rinnovaAbbonamento(Scanner scanner, GestoreRivenditoriEBiglietti gestore, UtenteDAO utenteDAO) {
        System.out.print("Inserisci email dell'utente: ");
        String email = scanner.next();
        Utente utente = utenteDAO.findByEmail(email);

        if (utente == null) {
            System.err.println("Utente non trovato!");
            return;
        }

        System.out.print("Inserisci periodicità del rinnovo (annuale, mensile, settimanale): ");
        String periodicita = scanner.next().toLowerCase();

        try {
            gestore.rinnovaAbbonamento(utente, Periodicy.valueOf(periodicita));
            System.out.println("Abbonamento rinnovato con successo!");
        } catch (IllegalArgumentException e) {
            System.err.println("Periodicità non valida!");
        }
    }

    private static void rinnovaTessera(Scanner scanner, GestoreRivenditoriEBiglietti gestore, UtenteDAO utenteDAO) {
        System.out.print("Inserisci email dell'utente: ");
        String email = scanner.next();
        Utente utente = utenteDAO.findByEmail(email);

        if (utente == null) {
            System.err.println("Utente non trovato!");
            return;
        }

        gestore.rinnovaTessera(utente);
        System.out.println("Tessera rinnovata con successo!");
    }
}
