package epicode.it.utilities.menu;

import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.ConvalidaBiglietto;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import epicode.it.servizi.gestore_controllore.Controllore;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Scanner;

public class UtenteMenu {
    private static RivenditoreDAO rivenditoreDAO;
    private static BigliettoDAO bigliettoDAO;
    private static GiornalieroDAO giornalieroDAO;
    private static TrattaDAO trattaDAO;
    private static MezzoDAO mezzoDAO;
    private static UtenteDAO utenteDAO;

    public static void showUtenteMenu(Scanner scanner, EntityManager em, Utente utente) {
        rivenditoreDAO = new RivenditoreDAO(em);
        bigliettoDAO = new BigliettoDAO(em);
        giornalieroDAO = new GiornalieroDAO(em);
        trattaDAO = new TrattaDAO(em);
        mezzoDAO = new MezzoDAO(em);
        utenteDAO = new UtenteDAO(em);

        System.out.println("--- Menu utente di " + utente.getNome() + " " + utente.getCognome() + " ---");
        System.out.println("1. Compra biglietto giornaliero");
        System.out.println("2. Convalida biglietto");
        System.out.println("3. Compra abbonamento");
        System.out.println("=> Scegli un opzione valida: (0 per tornare indietro)");
        switchOptions(scanner, em, utente);
    }

    private static void mostraRivenditori() {
        List<Rivenditore> rivenditori = rivenditoreDAO.findAll();
        System.out.println("\nRivenditori disponibili:");
        System.out.println("------------------------");
        for (Rivenditore r : rivenditori) {
            System.out.println("ID: " + r.getId() + " - Tipo: " + r.getClass().getSimpleName());
        }
        System.out.println("------------------------");
    }

    private static void mostraTratte() {
        List<Tratta> tratte = trattaDAO.getAll();
        System.out.println("\nTratte disponibili:");
        System.out.println("------------------------");
        for (Tratta t : tratte) {
            System.out.println("ID: " + t.getId() +
                    " - Da: " + t.getPartenza() +
                    " a: " + t.getCapolinea() +
                    " - Durata: " + t.getDurata());
        }
        System.out.println("------------------------");
    }

    private static void gestisciBiglietto(Scanner scanner, EntityManager em, GestoreRivenditoriEBiglietti gestore) {
        System.out.println("=> Acquisto biglietto giornaliero");

        mostraRivenditori();
        System.out.println("Inserisci l'ID del rivenditore:");
        long rivenditoreId = scanner.nextLong();
        scanner.nextLine();

        Rivenditore rivenditore = rivenditoreDAO.findById(rivenditoreId);
        if (rivenditore == null) {
            System.out.println("Rivenditore non trovato!");
            return;
        }

        mostraTratte();
        System.out.println("Inserisci l'ID della tratta:");
        long trattaId = scanner.nextLong();
        scanner.nextLine();

        try {
            Tratta tratta = trattaDAO.getById(trattaId);
            if (tratta == null) {
                System.out.println("Tratta non trovata!");
                return;
            }

            gestore.creaGiornaliero(rivenditore, tratta);

        } catch (Exception e) {
            System.out.println("Errore durante l'acquisto del biglietto: " + e.getMessage());
        }
    }

    private static void gestisciConvalida(Scanner scanner, EntityManager em, Controllore controllore) {
        System.out.println("=> Convalida biglietto");
        System.out.println("Inserisci l' id del biglietto:");
        long codice = scanner.nextLong();
        scanner.nextLine();

        Giornaliero biglietto = giornalieroDAO.findById(codice);


        if (biglietto == null) {
            System.out.println("Biglietto non trovato!");
            return;
        }

        System.out.println("Inserisci l'ID del mezzo:");
        long mezzoId = scanner.nextLong();
        scanner.nextLine();

        try {
            Mezzo mezzo = mezzoDAO.findById(mezzoId);
            if (mezzo == null) {
                System.out.println("Mezzo non trovato!");
                return;
            }

            if (mezzo.getStato() == Stato.IN_SERVIZIO) {
                Servizio inServizio = mezzo.getServizi()
                        .stream()
                        .filter(servizio -> servizio.getDataFine() == null)
                        .findFirst()
                        .orElse(null);

                if (inServizio.getTratta().getId() == biglietto.getTratta().getId()) {
                    if (biglietto.isDaAttivare()) {
                        controllore.controlloBiglietto(biglietto);
                        ConvalidaBiglietto convalida = new ConvalidaBiglietto(em);
                        convalida.convalida(biglietto, mezzo);

                    } else {
                        System.out.println("Il biglietto è già stato convalidato!");
                    }
                } else {
                    System.out.println("La tratta del biglietto non corrisponde alla tratta del mezzo!");
                }
            }


        } catch (IllegalStateException e) {
            System.out.println("Errore di validazione: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Errore durante la convalida: " + e.getMessage());
        }
    }

    private static void gestisciAbbonamento(Scanner scanner, EntityManager em, GestoreRivenditoriEBiglietti gestore, Utente utente) {
        System.out.println("=> Acquisto abbonamento");

        if (utente.getTessera() == null) {
            System.out.println("L'utente non ha una tessera! Devi prima creare una tessera.");
            return;
        }

        System.out.println("Tipo abbonamento:");
        System.out.println("1. Settimanale");
        System.out.println("2. Mensile");
        System.out.println("3. Bimestrale");
        System.out.println("4. Trimestrale");
        System.out.println("5. Annuale");
        int tipoAbb = scanner.nextInt();
        scanner.nextLine();

        Periodicy periodicy = switch (tipoAbb) {
            case 1 -> Periodicy.settimanale;
            case 2 -> Periodicy.mensile;
            case 3 -> Periodicy.bimestrale;
            case 4 -> Periodicy.trimestrale;
            case 5 -> Periodicy.annuale;
            default -> null;
        };

        if (periodicy == null) {
            System.out.println("Tipo abbonamento non valido!");
            return;
        }

        mostraRivenditori();
        System.out.println("Inserisci l'ID del rivenditore:");
        long rivenditoreId = scanner.nextLong();
        scanner.nextLine();

        try {
            Rivenditore rivenditore = rivenditoreDAO.findById(rivenditoreId);
            if (rivenditore == null) {
                System.out.println("Rivenditore non trovato!");
                return;
            }

            gestore.creaAbbonamento(rivenditore, periodicy, utente);

        } catch (Exception e) {
            System.out.println("Errore durante l'acquisto dell'abbonamento: " + e.getMessage());
        }
    }

    private static void switchOptions(Scanner scanner, EntityManager em, Utente utente) {
        GestoreRivenditoriEBiglietti gestore = new GestoreRivenditoriEBiglietti(em);
        Controllore controllore = new Controllore();

        int opt = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (opt) {
            case 0 -> {
                return; // torna al menu precedente
            }
            case 1 -> gestisciBiglietto(scanner, em, gestore);
            case 2 -> gestisciConvalida(scanner, em, controllore);
            case 3 -> gestisciAbbonamento(scanner, em, gestore, utente);
            default -> System.out.println("Opzione non valida");
        }

        showUtenteMenu(scanner, em, utente);
    }
}