package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.AbbonamentoDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.Locale;

public class MainInsertAbbonamento {
        public static void main(String[] args) {
                Faker faker = new Faker(new Locale("it"));
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
                EntityManager em = emf.createEntityManager();

                AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
                TesseraDAO tesseraDAO = new TesseraDAO(em);
                TrattaDAO trattaDAO = new TrattaDAO(em);

                Abbonamento abbonamento = new Abbonamento();
                abbonamento.setPeriodicy(Periodicy.mensile);
                abbonamento.setAttivo(true);
                abbonamento.setTariffa(faker.lorem().fixedString(10));
                abbonamento.setScadenza(LocalDateTime.now().plusDays(7));
                Tessera tessera1 = tesseraDAO.getById(1L);
                abbonamento.setTessera(tessera1);
                abbonamentoDAO.save(abbonamento);

                Tratta tratta = trattaDAO.getById(1L);
                // abbonamento.getTratte().add(tratta);
                abbonamentoDAO.update(abbonamento);

        }
}