package epicode.it.utilities.menu;

import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            System.out.println("4. Esci");
            System.out.print("Scegli un'opzione: ");

            int scelta = scanner.nextInt();
            scanner.nextLine(); // Consuma newline

            switch (scelta) {
                case 1 -> {
                    System.out.print("Inserisci ID del rivenditore: ");
                    long idRivenditore = scanner.nextLong();
                    Rivenditore rivenditore = gestore.richiamaRivenditore(idRivenditore);

                    System.out.print("Inserisci la tratta (partenza): ");
                    String nomeTratta = scanner.next();
                    Tratta tratta = new Tratta();
                    tratta.setPartenza(nomeTratta);

                    gestore.creaGiornaliero(rivenditore, tratta);
                }
                case 2 -> {
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

                    System.out.print("Inserisci il tipo di periodicità (annuale, mensile, settimanale, ecc.): ");
                    String periodicita = scanner.next().toLowerCase();

                    try {
                        gestore.creaAbbonamento(rivenditore, Periodicy.valueOf(periodicita), utente);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Periodicità non valida!");
                    }
                }
                case 3 -> {
                    System.out.print("Inserisci nome: ");
                    String nome = scanner.next();
                    System.out.print("Inserisci cognome: ");
                    String cognome = scanner.next();
                    System.out.print("Inserisci data di nascita (yyyy-mm-dd): ");
                    String dataNascita = scanner.next();
                    System.out.print("Inserisci email: ");
                    String email = scanner.next();

                    try {
                        LocalDate nascita = LocalDate.parse(dataNascita);
                        utenteDAO.creazioneUtente(nome, cognome, nascita, email);
                    } catch (Exception e) {
                        System.err.println("Errore nella creazione dell'utente!");
                    }
                }
                case 4 -> {
                    System.out.println("Uscita dal menu rivenditore.");
                    return;
                }
                default -> System.err.println("Opzione non valida!");
            }
        }
    }
}
