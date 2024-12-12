package epicode.it.utilities.menu;

import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestore_controllore.Controllore;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GestioneUtentiMenu {
    private static UtenteDAO utenteDAO;
    public static boolean mainMenuRunning = true;

    public static void showGestioneUtenteMenu(Scanner scanner, EntityManager em) {
        utenteDAO = new UtenteDAO(em);

        while (mainMenuRunning) {
            System.out.println("\n--- Menu gestione utenti ---");
            System.out.println("=========================");
            System.out.println("1. Visualizza tutti gli utenti");
            System.out.println("=========================");
            System.out.println("2. Crea utente");
            System.out.println("=========================");
            System.out.println("3. Elimina un utente");
            System.out.println("=========================");
            System.out.println("4. Seleziona un utente");
            System.out.println("=========================");
            System.out.println("0. Torna indietro\n");
            switchOptions(scanner, em);
        }


    }


    private static void switchOptions(Scanner scanner, EntityManager em) {

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {

            case 1 -> {
                List<Utente> utenti = utenteDAO.getAll();
                if (utenti.isEmpty()){
                    System.err.println("Non ci sono utenti inseriti");
                    break;
                }
                System.out.println("\n------------------------");
                utenti.forEach(System.out::println);
                System.out.println("------------------------\n");

            }
            case 2 -> {
                Utente utente = new Utente();
                System.out.println("Inserisci il nome");
                String nome = scanner.nextLine();
                System.out.println("Inserisci il congnome");
                String cognome = scanner.nextLine();
                System.out.println("Inserisci l' email");
                String email = scanner.nextLine();
                System.out.println("Inserisci la data di nascita (formato: dd/MM/yyyy):");
                Pattern patternData = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate dataNascita;

                while (true) {
                    String inputData = scanner.nextLine();
                    Matcher matcherData = patternData.matcher(inputData);
                    if (matcherData.matches()) {
                            dataNascita = LocalDate.parse(inputData, formatter);
                            break;
                    } else {
                        System.err.println("Formato non valido, riprova (dd/MM/yyyy):");
                    }
                }
                utenteDAO.creazioneUtente(nome,cognome,dataNascita,email);
//                System.out.println("Utente creato con successo!");
            }
            case 3 -> {
                System.out.println("Inserisci l' id dell' utente da eliminare");
                Utente utente = utenteDAO.getById(scanner.nextLong());
                if (utente != null) {
                    utenteDAO.delete(utente);
                    System.out.println("Utente eliminato!");
                } else {
                    System.err.println("Utente non trovato!");
                }
            }
            case 4 -> {
                System.out.println("Inserisci il tuo id utente");
                Utente utente = utenteDAO.getById(scanner.nextLong());
                if (utente != null) {
                    UtenteMenu.showUtenteMenu(scanner,em,utente);
                    System.out.println("Utente eliminato!");
                } else {
                    System.err.println("Utente non trovato!");
                }
            }
            case 0 -> mainMenuRunning = false;
            default -> System.out.println("Opzione non valida");
        }

    }
}