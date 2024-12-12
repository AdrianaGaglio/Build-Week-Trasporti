package epicode.it.utilities.menu;

import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.dao.stato_mezzo.ServizioDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.mezzo.Tram;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.stato_mezzo.Manutenzione;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.StatisticheRivenditore;
import epicode.it.servizi.gestore_stati_servizio.GestoreStatiServizio;
import jakarta.persistence.EntityManager;

import java.sql.SQLOutput;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class AdminMenu {

    public static boolean mainMenuRunning = true;


    public static void showAdminMenu(Scanner scanner, EntityManager em) {
        while (mainMenuRunning) {
            System.out.println("\n--- Menu admin ---");
            System.out.println("=========================");
            System.out.println("1. Gestisci rivenditori");
            System.out.println("=========================");
            System.out.println("2. Gestisci mezzi");
            System.out.println("=========================");
            System.out.println("3. Gestisci tratte");
            System.out.println("=========================");
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
                gestisciTratte(scanner, em);
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

            System.out.println("\n=> Gestisti rivenditori");
            System.out.println("=========================");
            System.out.println("1. Visualizza tutti i rivenditori");
            System.out.println("=========================");
            System.out.println("2. Aggiungi un rivenditore");
            System.out.println("=========================");
            System.out.println("3. Modifica un rivenditore");
            System.out.println("=========================");
            System.out.println("4. Elimina un rivenditore");
            System.out.println("=========================");
            System.out.println("5. Controlla biglietti e abbonamenti emessi da un rivenditore in un dato periodo");
            System.out.println("=========================");
            System.out.println("0. Torna indietro\n");

            int choice = scanner.nextInt();
            scanner.nextLine();
            GestoreRivenditoriEBiglietti gestoreRivenditoriEBiglietti = new GestoreRivenditoriEBiglietti(em);
            switch (choice) {
                case 1 -> {
                    List<Rivenditore> rivenditori = gestoreRivenditoriEBiglietti.visualizzaRivenditori();
                    if (rivenditori.isEmpty()) {
                        System.out.println("Non ci sono rivenditori");
                    } else {
                        System.out.println();
                        rivenditori.forEach(System.out::println);
                        System.out.println();
                    }
                }
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
                                String[] parts = apertura.split("-");
                                openHour = Integer.parseInt(parts[0]);
                                openMinutes = Integer.parseInt(parts[1]);

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
                                String[] parts = chiusura.split("-");
                                closeHour = Integer.parseInt(parts[0]);
                                closeMinutes = Integer.parseInt(parts[1]);
                                break;

                            } else {
                                System.out.println("Orario non valido, riprova.");
                            }
                        }
                        gestoreRivenditoriEBiglietti.creaRivenditoreFisico(DayOfWeek.of(day), LocalTime.of(openHour, openMinutes), LocalTime.of(closeHour, closeMinutes));
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
                            rivFisico.setOraApertura(LocalTime.of(openHour, openMinutes));
                            rivFisico.setOraChiusura(LocalTime.of(closeHour, closeMinutes));
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
                case 5 -> {
                    System.out.println("Inserisci l' id del rivenditore che vuoi controllare");
                    Rivenditore rivenditore = rivenditoreDAO.findById(scanner.nextLong());
                    scanner.nextLine();
                    if (rivenditore == null) {
                        System.err.println("Rivenditore non trovato!");
                        break;
                    }
                    System.out.println("Inserisci il range di data che vuoi controllare");

                    Pattern patternData = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate dateFrom, dateTo;

                    while (true) {
                        System.out.println("Inserisci la data di inizio (dd/MM/yyyy):");
                        String inputDateFrom = scanner.nextLine();
                        Matcher matcherFrom = patternData.matcher(inputDateFrom);

                        if (matcherFrom.matches()) {
                            dateFrom = LocalDate.parse(inputDateFrom, formatter);
                            break;
                        } else {
                            System.err.println("Formato non valido, riprova (dd/MM/yyyy):");
                        }
                    }

                    while (true) {
                        System.out.println("Inserisci la data di fine (dd/MM/yyyy):");
                        String inputDateTo = scanner.nextLine();
                        Matcher matcherTo = patternData.matcher(inputDateTo);

                        if (matcherTo.matches()) {
                                dateTo = LocalDate.parse(inputDateTo, formatter);
                                if (!dateTo.isBefore(dateFrom)) { // Verifica che la data di fine sia successiva o uguale
                                    break;
                                } else {
                                    System.err.println("La data di fine deve essere successiva o uguale alla data di inizio. Riprova.");
                                }
                        } else {
                            System.err.println("Formato non valido, riprova (dd/MM/yyyy):");
                        }
                    }



                    List<Biglietto> biglietti =  StatisticheRivenditore.bigliettiEmessiPerRivenditoreInUnDatoPeriodo(rivenditore,dateFrom,dateTo);

                    if (biglietti.isEmpty()) {
                        System.out.println("Nessun biglietto emesso nel periodo selezionato.");
                    } else {
                        System.out.println("Biglietti emessi nel periodo selezionato:");
                        System.out.println("------------------------");
                        biglietti.forEach(System.out::println);
                        System.out.println("------------------------");
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
        TrattaDAO trattaDAO = new TrattaDAO(em);
        boolean running = true;
        while (running) {
            System.out.println("\n=> Gestisti mezzi");
            System.out.println("=========================");
            System.out.println("1. Visualizza tutti i mezzi");
            System.out.println("=========================");
            System.out.println("2. Aggiungi nuovo mezzo");
            System.out.println("=========================");
            System.out.println("3. Assegna mezzo ad una tratta (metti in servizio)");
            System.out.println("=========================");
            System.out.println("4. Metti  un mezzo in manutenzione");
            System.out.println("=========================");
            System.out.println("5. Elimina mezzo");
            System.out.println("=========================");
            System.out.println("0. Torna indietro\n");

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
                case 2 -> aggiungiNuovoMezzo(scanner, em);
                case 3 -> {
                    GestoreStatiServizio gestoreStatiServizio = new GestoreStatiServizio(em);
                    ServizioDAO servizioDAO = new ServizioDAO(em);
                    System.out.println("Seleziona il mezzo da mettere in servizio tramite id");
                    Mezzo mezzo = mezzoDAO.findById(scanner.nextLong());
                    Servizio mezzoInServizio = servizioDAO.cercaSeInServizio(mezzo, LocalDate.now());
                    if (mezzoInServizio != null) {
                        System.err.println("Mezzo già in servizio!");
                        break;
                    }
                    if (mezzo != null) {
                        System.out.println("Seleziona tratta tramite id");
                        List<Tratta> tratte = trattaDAO.getAll();
                        if (tratte.isEmpty()) {
                            System.err.println("Non ci sono tratte!");
                            break;
                        } else {
                            System.out.println();
                            tratte.forEach(System.out::println);
                            System.out.println();
                        }

                        System.out.print("Digita qui: ");
                        Tratta tratta = trattaDAO.getById(scanner.nextLong());
                        if (tratta != null) {
                            gestoreStatiServizio.aggiungiServizio(mezzo, LocalDate.now(), tratta);
                        } else {
                            System.err.println("Tratta non trovata!");
                        }
                    } else {
                        System.err.println("Mezzo non trovato!");
                    }


                }
                case 4 -> {
                    GestoreStatiServizio gestoreStatiServizio = new GestoreStatiServizio(em);
                    ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
                    System.out.println("Seleziona il mezzo da mettere in manutezione tramite id");
                    Mezzo mezzo = mezzoDAO.findById(scanner.nextLong());
                    scanner.nextLine();
                    Manutenzione mezzoInManutenzione = manutenzioneDAO.cercaSeInManutenzione(mezzo, LocalDate.now());
                    if (mezzoInManutenzione != null) {
                        System.err.println("Mezzo già in manutenzione!");
                        break;
                    }
                    if (mezzo != null) {
                        System.out.println("Scrivi i lavori da fare");
                        String descrizione = scanner.nextLine();
                        gestoreStatiServizio.aggiungiManutenzione(mezzo, LocalDate.now(), descrizione);
                    } else {
                        System.err.println("Mezzo non trovato!");
                    }


                }
                case 5 -> {
                    System.out.println("Inserisci l' id del mezzo da modificare");
                    Mezzo mezzo = mezzoDAO.findById(scanner.nextLong());
                    scanner.nextLine();
                    if (mezzo != null) {
                        mezzoDAO.delete(mezzo);
                    } else {
                        System.err.println("Mezzo non trovato!");
                    }
                }
                case 0 -> running = false;
                default -> System.err.println("Inserisci un valore valido");
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

    public static void gestisciTratte(Scanner scanner, EntityManager em) {
        TrattaDAO trattaDAO = new TrattaDAO(em);
        boolean running = true;
        while (running) {
            System.out.println("\n=> Gestisti tratte");
            System.out.println("=========================");
            System.out.println("1. Visualizza tutte le tratte");
            System.out.println("=========================");
            System.out.println("2. Aggiungi nuova tratta");
            System.out.println("=========================");
            System.out.println("3. Modifica Tratta");
            System.out.println("=========================");
            System.out.println("4. Elimina tratta");
            System.out.println("=========================");
            System.out.println("0. Torna indietro\n");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> {
                    List<Tratta> tratte = trattaDAO.getAll();
                    if (tratte.isEmpty()) {
                        System.err.println("Non ci sono tratte!");
                    } else {
                        System.out.println();
                        tratte.forEach(System.out::println);
                        System.out.println();
                    }
                }
                case 2 -> {
                    Tratta tratta = new Tratta();
                    modificaCreaTratta(tratta, scanner);
                    trattaDAO.save(tratta);
                    System.out.println("Tratta creata con successo!");

                }
                case 3 -> {
                    System.out.println("Inserisci l' id della tratta da modificare");
                    Tratta tratta = trattaDAO.getById(scanner.nextLong());
                    scanner.nextLine();
                    if (tratta != null) {
                        modificaCreaTratta(tratta, scanner);
                        trattaDAO.update(tratta);
                        System.out.println("Tratta aggiornata!");
                    } else {
                        System.err.println("Tratta non trovata!");
                    }
                }
                case 4 -> {
                    System.out.println("Inserisci l' id della tratta da eliminare");
                    Tratta tratta = trattaDAO.getById(scanner.nextLong());
                    scanner.nextLine();
                    if (tratta != null) {
                        trattaDAO.delete(tratta);
                        System.out.println("Tratta eliminata!");
                    } else {
                        System.err.println("Tratta non trovata!");
                    }
                }
                case 0 -> running = false;
                default -> System.err.println("Inserisci un valore valido");
            }
        }
    }

    private static void modificaCreaTratta(Tratta tratta, Scanner scanner) {
        System.out.println("Inserisci la via di partenza");
        tratta.setPartenza(scanner.nextLine());
        System.out.println("Inserisci il capolinea");
        tratta.setCapolinea(scanner.nextLine());
        System.out.println("Inserisci la durata prevista (hh:mm):");
        Pattern pattern = Pattern.compile("^(?:[01]?\\d|2[0-3]):[0-5]\\d$");
        LocalTime durata;

        while (true) {
            String input = scanner.nextLine();
            Matcher matcher = pattern.matcher(input);

            if (matcher.matches()) {
                String[] parts = input.split(":");
                int ore = Integer.parseInt(parts[0]);
                int minuti = Integer.parseInt(parts[1]);
                durata = LocalTime.of(ore, minuti);
                break;
            } else {
                System.err.println("Formato non valido, riprova (hh:mm):");
            }
        }
        tratta.setDurata(durata);
    }
}
