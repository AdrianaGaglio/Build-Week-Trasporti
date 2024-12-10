package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.tessera.Tessera;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.Locale;

import static java.lang.Long.parseLong;

public class MainTest {
        public static void main(String[] args) {
                Faker faker = new Faker(new Locale("it"));
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
                EntityManager em = emf.createEntityManager();

                // 1. creare N rivenditori
                // - conteggio biglietti vendutiè

                // 2. creare N utenti
                // - cercare utente per tessera
                // - cercare utente per email
                // - verificare se l'utente ha abbonamenti attivi

                // 3. creare alcune tessere da associare agli utenti

                // 4. emettere alcuni biglietti per diversi rivenditori

                // 5. emettere alcuni abbonamenti per diversi rivenditori associati a delle
                // tessere

                UtenteDAO utenteDAO = new UtenteDAO(em);
                TesseraDAO tesseraDAO = new TesseraDAO(em);

                TrattaDAO trattaDAO = new TrattaDAO(em);
                PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);

                // AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
                // Abbonamento abbonamento = new Abbonamento();
                // Tessera tesseta1 = tesseraDAO.getById(1L);
                // abbonamento.setTessera(tesseta1);
                // abbonamento.setPeriodicy(Periodicy.settimanale);
                // abbonamento.setScadenza(LocalDateTime.now().plusDays(7));
                // abbonamento.setAttivo(true);
                // abbonamento.getTratte().add(trattaDAO.getById(1L));
                //
                // abbonamentoDAO.save(abbonamento);

                AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
                Abbonamento abbonamento = new Abbonamento();
                Tessera tesseta2 = tesseraDAO.getById(2L);
                abbonamento.setTessera(tesseta2);
                abbonamento.setPeriodicy(Periodicy.settimanale);
                abbonamento.setScadenza(LocalDateTime.now().minusDays(8));
                abbonamento.setAttivo(true);
                // abbonamento.getTratte().add(trattaDAO.getById(2L));

                abbonamentoDAO.save(abbonamento);


//        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
//        Abbonamento abbonamento = new Abbonamento();
//        Tessera tesseta1 = tesseraDAO.getById(1L);
//        abbonamento.setTessera(tesseta1);
//        abbonamento.setPeriodicy(Periodicy.settimanale);
//        abbonamento.setScadenza(LocalDateTime.now().plusDays(7));
//        abbonamento.setAttivo(true);
//        abbonamento.getTratte().add(trattaDAO.getById(1L));
//
//        abbonamentoDAO.save(abbonamento);

        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
        Abbonamento abbonamento = new Abbonamento();
        Tessera tesseta2 = tesseraDAO.getById(2L);
        abbonamento.setTessera(tesseta2);
        abbonamento.setPeriodicy(Periodicy.settimanale);
        abbonamento.setScadenza(LocalDateTime.now().minusDays(8));
        abbonamento.setAttivo(true);
//        abbonamento.getTratte().add(trattaDAO.getById(2L));

        abbonamentoDAO.save(abbonamento);








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
//
//        Utente utente1 = utenteDAO.getById(10L);
//        Tessera tessera1 = new Tessera();
//        tessera1.setUtente(utente1);
//        tessera1.setValidita(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().plusYears(1));
//        tesseraDAO.save(tessera1);
//        utente1.setTessera(tessera1);
//        utenteDAO.update(utente1);
//
//        Utente utente2 = utenteDAO.getById(5L);
//        Tessera tessera2 = new Tessera();
//        tessera2.setUtente(utente2);
//        tessera2.setValidita(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().plusYears(1));
//        tesseraDAO.save(tessera2);
//        utente2.setTessera(tessera2);
//        utenteDAO.save(utente2);
//
//
//        String city = faker.country().capital();
//
//        for (int i = 0; i < 50; i++) {
//            try {
//                Tratta tratta = new Tratta();
//                tratta.setPartenza(city + " " + faker.address().streetAddress());
//                tratta.setCapolinea(city + " " + faker.address().streetAddress());
//                tratta.setDurata(LocalTime.of(faker.random().nextInt(0, 2), faker.random().nextInt(10, 45)));
//                trattaDAO.save(tratta);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (int i = 0; i < 150; i++) {
//            try {
//                Percorrenza percorrenza = new Percorrenza();
//                percorrenza.setData(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
//                percorrenza.setDurata_effettiva(LocalTime.of(faker.random().nextInt(0, 2), faker.random().nextInt(10, 45)));
//                percorrenza.setTratta(trattaDAO.getById(parseLong(faker.random().nextInt(1, 51).toString())));
//                percorrenzaDAO.save(percorrenza);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
                // List<Utente> utenti = new ArrayList<>();
                //
                // for (int i = 0; i < 10; i++) {
                // try {
                // Utente utente = new Utente();
                // utente.setNome(faker.name().firstName());
                // utente.setCognome(faker.name().lastName());
                // utente.setDataNascita(faker.date().past(1000,
                // TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                // utente.setEmail(faker.internet().emailAddress());
                // utenti.add(utente);
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                //
                // utenteDAO.saveAll(utenti);
                //
                // Utente utente1 = utenteDAO.getById(10L);
                // Tessera tessera1 = new Tessera();
                // tessera1.setUtente(utente1);
                // tessera1.setValidita(faker.date().past(365,
                // TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().plusYears(1));
                // tesseraDAO.save(tessera1);
                // utente1.setTessera(tessera1);
                // utenteDAO.update(utente1);
                //
                // Utente utente2 = utenteDAO.getById(5L);
                // Tessera tessera2 = new Tessera();
                // tessera2.setUtente(utente2);
                // tessera2.setValidita(faker.date().past(365,
                // TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().plusYears(1));
                // tesseraDAO.save(tessera2);
                // utente2.setTessera(tessera2);
                // utenteDAO.save(utente2);
                //
                //
                // String city = faker.country().capital();
                //
                // for (int i = 0; i < 50; i++) {
                // try {
                // Tratta tratta = new Tratta();
                // tratta.setPartenza(city + " " + faker.address().streetAddress());
                // tratta.setCapolinea(city + " " + faker.address().streetAddress());
                // tratta.setDurata(LocalTime.of(faker.random().nextInt(0, 2),
                // faker.random().nextInt(10, 45)));
                // trattaDAO.save(tratta);
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                //
                // for (int i = 0; i < 150; i++) {
                // try {
                // Percorrenza percorrenza = new Percorrenza();
                // percorrenza.setData(faker.date().past(365,
                // TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                // percorrenza.setDurata_effettiva(LocalTime.of(faker.random().nextInt(0, 2),
                // faker.random().nextInt(10, 45)));
                // percorrenza.setTratta(trattaDAO.getById(parseLong(faker.random().nextInt(1,
                // 51).toString())));
                // percorrenzaDAO.save(percorrenza);
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
        }
}