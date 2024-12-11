package epicode.it.utilities.menu;

import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import jakarta.persistence.EntityManager;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class AdminMenu {

    public static void showAdminMenu(Scanner scanner, EntityManager em) {
        System.out.println("--- Menu admin ---");
        System.out.println("1. Aggiungi rivenditore");
        System.out.println("2. Gestisci rivenditori");
        System.out.println("=========================");
        System.out.println("3. Aggiungi nuovo mezzo");
        System.out.println("4. Assegna mezzo ad una tratta (metti in servizio)");
        System.out.println("5. Metti  un mezzo in manutenzione");
        System.out.println("=========================");
        System.out.println("6. Aggiungi nuova tratta");
        System.out.println("=> Scegli un opzione valida: (0 per tornare indietro)");
        switchOptions(scanner, em);
    }

    private static void switchOptions(Scanner scanner, EntityManager em) {
        int opt = scanner.nextInt();
        scanner.nextLine();
        switch (opt) {
            case 1 -> {
                System.out.println("1-Rivenditore fisico 2-Rivenditore automatico");
                int riv = scanner.nextInt();
                scanner.nextLine();
                GestoreRivenditoriEBiglietti gestoreRivenditori = new GestoreRivenditoriEBiglietti(em);
                if (riv == 1) {
                    int day;
                    int openHour;
                    int openMinutes;
                    int closeHour;
                    int closeMinutes;
                    while (true) {
                        System.out.println("Giorno di chiusura: 1-Lun 2-Mar 3-Mer 4-Gio 5-Ven 6-Sab 7-Dom (0 per tornare indietro)");
                        day = scanner.nextInt();
                        scanner.nextLine();
                        if(day == 0) return;
                        if (day > 7) {
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
                            break;
                        }else {
                            System.out.println("Orario non valido, riprova.");
                        }
                    }
                    while (true) {
                        System.out.println("Orario chiusura (hh-mm):");
                        String chiusura = scanner.nextLine();
                        closeHour = parseInt(chiusura.substring(0, 2));
                        closeMinutes = parseInt(chiusura.substring(3, 5));
                        if (closeHour > 23) {
                            System.out.println("Orario non valido, riprova.");
                        } else if (closeMinutes > 59) {
                            System.out.println("Minuti non validi, riprova.");
                        } else {
                            break;
                        }
                    }
//                    gestoreRivenditori.creaRivenditoreFisico(DayOfWeek.of(day), Time.valueOf(LocalTime.of(openHour, openMinutes)), Time.valueOf(LocalTime.of(closeHour, closeMinutes)));
                } else if (riv == 2) {
                    gestoreRivenditori.creaRivenditoreAutomatico();
                }
            }
            case 2 -> {
                System.out.println("=> Gestisti rivenditori");
            }
            default -> System.out.println("opzione non valida");
        }
    }


}
