
package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

public class MainInsert {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        UtenteDAO utenteDAO = new UtenteDAO(em);
        TesseraDAO tesseraDAO = new TesseraDAO(em);

        TrattaDAO trattaDAO = new TrattaDAO(em);
        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);

        List<Utente> utenti = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            try {
                Utente utente = new Utente();
                utente.setNome(faker.name().firstName());
                utente.setCognome(faker.name().lastName());
                utente.setDataNascita(faker.date().past(1000, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                utente.setEmail(faker.internet().emailAddress());
                utenti.add(utente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        utenteDAO.saveAll(utenti);

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

        String city = faker.address().city();

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

            em.close();
        }

    }

}
