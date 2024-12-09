
package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.TesseraDAO;
import epicode.it.dao.UtenteDAO;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.Long.parseLong;

public class MainInsert {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        UtenteDAO utenteDAO = new UtenteDAO(em);
        TesseraDAO tesseraDAO = new TesseraDAO(em);

        List<Utente> utenti = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Utente utente = new Utente();
            utente.setNome(faker.name().firstName());
            utente.setCognome(faker.name().lastName());
            utente.setDataNascita(faker.date().past(1000, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            utente.setEmail(faker.internet().emailAddress());
            utenti.add(utente);
        }

        utenteDAO.saveAll(utenti);

        for (int i = 0; i < 10; i++) {
            Tessera tessera = new Tessera();
            tessera.setValidita(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().plusYears(1));
            tesseraDAO.save(tessera);
            Utente utente = utenteDAO.getById((long) (i + 1));
            utente.setTessera(tessera);
            utenteDAO.update(utente);
        }
    }

}
