
package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.GestoreTessera;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

public class MainInsertUserTessera {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

//        UtenteDAO utenteDAO = new UtenteDAO(em);
          TesseraDAO tesseraDAO = new TesseraDAO(em);

          TrattaDAO trattaDAO = new TrattaDAO(em);
          PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);

//        List<Utente> utenti = new ArrayList<>();
//
//        for (int i = 0; i < 10; i++) {
//            try {
//                Utente utente = new Utente();
//                utente.setNome(faker.name().firstName());
//                utente.setCognome(faker.name().lastName());
//                utente.setDataNascita(faker.date().past(1000, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
//                utente.setEmail(faker.internet().emailAddress());
//                utenti.add(utente);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        utenteDAO.saveAll(utenti);

        UtenteDAO utenteDAO = new UtenteDAO(em);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");


        System.out.println("inserisci il tuo nome");
        String nome = scanner.nextLine();

        System.out.println("inserisci il tuo cognome");
        String cognome = scanner.nextLine();

        System.out.println("Inserisci la tua data di nascita (formato dd-MM-yyyy)");
        LocalDate dataNascita = LocalDate.parse(scanner.nextLine(), formatter);


        Tessera tessera1 = new Tessera();
        tessera1.setValidita(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().plusYears(1));
        tesseraDAO.save(tessera1);
        Utente utente1 = utenteDAO.getById(10L);
        utente1.setTessera(tessera1);
        utenteDAO.update(utente1);

        Tessera tessera2 = new Tessera();
        tessera2.setValidita(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime().plusYears(1));
        tesseraDAO.save(tessera2);
        Utente utente2 = utenteDAO.getById(5L);
        utente2.setTessera(tessera2);
        utenteDAO.save(utente2);


        System.out.println("inserisci la tua email");
        String email = scanner.nextLine();

        utenteDAO.creazioneUtente(nome,cognome,dataNascita, email);


        RivFisico rivFisico1 = new RivFisico();
        rivFisico1.setGiornoChiusura(DayOfWeek.MONDAY);
        rivFisico1.setOraApertura(Time.valueOf("09:00:00"));
        rivFisico1.setOraChiusura(Time.valueOf("20:00:00"));



        for (int i = 0; i < 10; i++) {
            try {
                Tessera tessera = new Tessera();
                tessera.setValidita(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().plusYears(1));
                tesseraDAO.save(tessera);
                Utente utente = utenteDAO.getById((long) (i + 1));
                utente.setTessera(tessera);
                utenteDAO.update(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        String city = faker.country().capital();

        for (int i = 0; i < 50; i++) {
            try {
                Tratta tratta = new Tratta();
                tratta.setPartenza(city + " " + faker.address().streetAddress());
                tratta.setCapolinea(city + " " + faker.address().streetAddress());
                tratta.setDurata(LocalTime.of(faker.random().nextInt(0, 2), faker.random().nextInt(10, 45)));
                trattaDAO.save(tratta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 150; i++) {
            try {
                Percorrenza percorrenza = new Percorrenza();
                percorrenza.setData(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                percorrenza.setDurata_effettiva(LocalTime.of(faker.random().nextInt(0, 2), faker.random().nextInt(10, 45)));
                percorrenza.setTratta(trattaDAO.getById(parseLong(faker.random().nextInt(1, 51).toString())));
                percorrenzaDAO.save(percorrenza);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        Utente utente1 = utenteDAO.getById(10L);

        GestoreTessera gestoreTessera = new GestoreTessera(em);

        gestoreTessera.creaTessera(rivFisico1, utente1, scanner);


        em.close();

    }

}
