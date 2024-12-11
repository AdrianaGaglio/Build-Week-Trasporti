package epicode.it.utilities.menu;

import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.mezzo.Tram;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import jakarta.persistence.EntityManager;

import java.sql.SQLOutput;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class AdminMenu {

    private static boolean mainMenuRunning = true;


    public static void showAdminMenu(Scanner scanner, EntityManager em) {
        while (mainMenuRunning) {
            System.out.println("--- Menu admin ---");
            System.out.println("1.Gestisci rivenditori");
            System.out.println("=========================");
            System.out.println("2. Gestisci mezzi");
            System.out.println("2. Aggiungi nuovo mezzo");
            System.out.println("3. Visualizza tutti i mezzi");
            System.out.println("4. Assegna mezzo ad una tratta (metti in servizio)");
            System.out.println("5. Metti  un mezzo in manutenzione");
            System.out.println("6. Elimina mezzo");
            System.out.println("=========================");
            System.out.println("7. Aggiungi nuova tratta");
            System.out.println("8. Elimina tratta");
            System.out.println("=> Scegli un opzione valida: (0 per tornare indietro)");
            switchOptions(scanner, em);

        }

    }

    private static void switchOptions(Scanner scanner, EntityManager em) {
        int opt = scanner.nextInt();
        scanner.nextLine();
        switch (opt) {
            case 1 -> {
                gestisciRivenditori(scanner, em);

            }
            case 2 -> {

                gestisciMezzi(scanner, em);
            }
            case 3 -> {

            }
            case 4 -> {
            }
            case 0 -> mainMenuRunning = false;
            default -> System.err.println("opzione non valida");
        }
    }

    private static void gestisciRivenditori(Scanner scanner, EntityManager em) {
        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);
        boolean running = true;
        while (running) {

            System.out.println("=> Gestisti rivenditori");
            System.out.println("1. Visualizza tutti i rivenditori");
            System.out.println("2. Aggiungi un rivenditore");
            System.out.println("3. Modifica un rivenditore");
            System.out.println("4. Elimina un rivenditore");
            System.out.println("=> Scegli un opzione valida: (0 per tornare indietro)");

            int choice = scanner.nextInt();
            scanner.nextLine();
            GestoreRivenditoriEBiglietti gestoreRivenditoriEBiglietti = new GestoreRivenditoriEBiglietti(em);
            switch (choice) {
                case 1 -> System.out.println(gestoreRivenditoriEBiglietti.visualizzaRivenditori());
                case 2 -> {
                    System.out.println("1-Rivenditore fisico 2-Rivenditore automatico");
                    int riv = scanner.nextInt();
                    scanner.nextLine();
                    if (riv == 1) {
                        int day;
                        int openHour;
                        int openMinutes;
                        int closeHour;
                        int closeMinutes;
                        while (true) {
                            System.out.println("Giorno di chiusura: 1-Lun 2-Mar 3-Mer 4-Gio 5-Ven 6-Sab 7-Dom");
                            day = scanner.nextInt();
                            scanner.nextLine();

                            if (day <= 0 || day > 7) {
                                System.out.println("Giorno non valido, riprova.");
                            } else {
                                break;
                            }
                        }
                        Pattern pattern = Pattern.compile("^(?:[01]?\\d|2[0-3])-[0-5]\\d$");

                        while (true) {
                            System.out.println("Orario apertura (hh-mm):");
                            String apertura = scanner.nextLine();
                            Matcher matcher = pattern.matcher(apertura);

                            if (matcher.matches()) {
                                openHour = Integer.parseInt(apertura.substring(0, 2));
                                openMinutes = Integer.parseInt(apertura.substring(3, 5));
                                break;
                            } else {
                                System.out.println("Orario non valido, riprova.");
                            }
                        }

                        while (true) {
                            System.out.println("Orario chiusura (hh-mm):");
                            String chiusura = scanner.nextLine();
                            Matcher matcher = pattern.matcher(chiusura);
                            if (matcher.matches()) {
                                closeHour = parseInt(chiusura.substring(0, 2));
                                closeMinutes = parseInt(chiusura.substring(3, 5));
                                break;
                            } else {
                                System.out.println("Orario non valido, riprova.");
                            }
                        }
                        gestoreRivenditoriEBiglietti.creaRivenditoreFisico(DayOfWeek.of(day), Time.valueOf(LocalTime.of(openHour, openMinutes)), Time.valueOf(LocalTime.of(closeHour, closeMinutes)));
                    } else if (riv == 2) {
                        gestoreRivenditoriEBiglietti.creaRivenditoreAutomatico();
                    }
                }
                case 3 -> {
                    System.out.println("Scrivi l' id del rivenditore da modificare");
                    long idRiv = scanner.nextInt();
                    scanner.nextLine();
                    Rivenditore rivenditore = gestoreRivenditoriEBiglietti.richiamaRivenditore(idRiv);
                    if (rivenditore != null) {
                        if (rivenditore instanceof RivFisico) {
                            RivFisico rivFisico = (RivFisico) rivenditore;
                            int day;
                            int openHour;
                            int openMinutes;
                            int closeHour;
                            int closeMinutes;
                            while (true) {
                                System.out.println("Giorno di chiusura: 1-Lun 2-Mar 3-Mer 4-Gio 5-Ven 6-Sab 7-Dom");
                                day = scanner.nextInt();
                                scanner.nextLine();

                                if (day <= 0 || day > 7) {
                                    System.out.println("Giorno non valido, riprova.");
                                } else {
                                    break;
                                }
                            }
                            Pattern pattern = Pattern.compile("^(?:[01]?\\d|2[0-3])-[0-5]\\d$");

                            while (true) {
                                System.out.println("Orario apertura (hh-mm):");
                                String apertura = scanner.nextLine();
                                Matcher matcher = pattern.matcher(apertura);

                                if (matcher.matches()) {
                                    openHour = Integer.parseInt(apertura.substring(0, 2));
                                    openMinutes = Integer.parseInt(apertura.substring(3, 5));
                                    break;
                                } else {
                                    System.out.println("Orario non valido, riprova.");
                                }
                            }

                            while (true) {
                                System.out.println("Orario chiusura (hh-mm):");
                                String chiusura = scanner.nextLine();
                                Matcher matcher = pattern.matcher(chiusura);
                                if (matcher.matches()) {
                                    closeHour = parseInt(chiusura.substring(0, 2));
                                    closeMinutes = parseInt(chiusura.substring(3, 5));
                                    break;
                                } else {
                                    System.out.println("Orario non valido, riprova.");
                                }
                            }
                            rivFisico.setGiornoChiusura(DayOfWeek.of(day));
                            rivFisico.setOraApertura(Time.valueOf(LocalTime.of(openHour, openMinutes)));
                            rivFisico.setOraChiusura(Time.valueOf(LocalTime.of(closeHour, closeMinutes)));
                            rivenditoreDAO.update(rivFisico);
                            System.out.println("Rivenditore fisico aggiornato!");
                        } else {
                            RivAutomatico riv = (RivAutomatico) rivenditore;
                            if (riv.isAttivo()) {
                                System.out.print("Vuoi mettere il rivenditore automatico fuori servizio? (S/N): ");
                                String risposta = scanner.nextLine().trim().toUpperCase();
                                if (risposta.equals("S")) {
                                    riv.setAttivo(false);
                                    System.out.println("Rivenditore automatico messo fuori servizio");
                                } else {
                                    System.out.println("Rivenditore automatico rimasto in servizio");
                                }

                            } else {
                                System.out.print("Vuoi mettere il rivenditore automatico in servizio? (S/N): ");
                                String risposta = scanner.nextLine().trim().toUpperCase();
                                if (risposta.equals("S")) {
                                    riv.setAttivo(true);
                                    System.out.println("Rivenditore automatico messo in servizio");
                                } else {
                                    System.out.println("Rivenditore automatico rimasto fuori servizio");
                                }
                            }
                        }
                    } else {
                        System.err.println("Rivenditore non trovato");
                    }
                }
                case 4 -> {
                    System.out.println("Scrivi l' id del rivenditore da eliminare");
                    long idRiv = scanner.nextInt();
                    scanner.nextLine();
                    Rivenditore rivenditore = gestoreRivenditoriEBiglietti.richiamaRivenditore(idRiv);
                    if (rivenditore != null) {

                        rivenditoreDAO.delete(rivenditore);
                        System.out.println("Rivenditore eliminato");
                    } else {
                        System.err.println("Rivenditore non trovato");
                    }
                }
                case 0 -> {
                    running = false;
                }
                default -> System.err.println("Inserisci un valore valido");

            }
        }
    }

    public static void gestisciMezzi(Scanner scanner, EntityManager em) {
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        boolean running = true;
        while (running) {
            System.out.println("=> Gestisti mezzi");
            System.out.println("1. Visualizza tutti i mezzi");
            System.out.println("2. Aggiungi nuovo mezzo");
            System.out.println("3. Assegna mezzo ad una tratta (metti in servizio)");
            System.out.println("4. Metti  un mezzo in manutenzione");
            System.out.println("5. Elimina mezzo");
            System.out.println("0. Torna indietro");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
case 1 -> {
    List<Mezzo> mezzi = mezzoDAO.findAll();
    if (mezzi.isEmpty()) {
        System.out.println("\nNon ci sono mezzi\n");
    } else {
        System.out.println();
        mezzi.forEach(System.out::println);
        System.out.println();
    }
}
case 2 -> aggiungiNuovoMezzo(scanner,em);
case 3 -> {
    System.out.println("Seleziona tratta");
}
                case 0 -> running = false;
                default -> System.err.println("Inserisci un valore valido")
            }
        }
    }

    public static void aggiungiNuovoMezzo(Scanner scanner, EntityManager em) {
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        System.out.println("Scegli tra: 1-Autobus 2-Tram");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 1) {
            Autobus autobus = new Autobus();
            System.out.println("Inserisci il codice dell' autobus");
            int codice = scanner.nextInt();
            scanner.nextLine();
            autobus.setCodice(codice);
            autobus.setStato(Stato.DEPOSITO);
            mezzoDAO.save(autobus);
            System.out.println("Autobus salvato!");
        } else if (choice == 2) {
            Tram tram = new Tram();
            System.out.println("Inserisci il codice del tram");
            int codice = scanner.nextInt();
            scanner.nextLine();
            tram.setCodice(codice);
            tram.setStato(Stato.DEPOSITO);
            mezzoDAO.save(tram);
            System.out.println("Tram salvato!");
        } else {
            System.err.println("Input non valido");
        }
    }

}
