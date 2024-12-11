package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;

import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

public class MainInsertBiglietto {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        // PRENDO I METODI DALLE DAO
        GiornalieroDAO giornalieroDAO = new GiornalieroDAO(em);
        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);

        TrattaDAO trattaDAO = new TrattaDAO(em);

//        for (int i = 0; i < 10; i++) {
//            Tratta tratta = new Tratta();
//            tratta.setDurata(LocalTime.of(1,20));
//            tratta.setCapolinea(faker.address().fullAddress());
//            tratta.setPartenza(faker.address().fullAddress());
//            trattaDAO.save(tratta);
//        }
//
//        for (int i = 0; i < 10; i++) {
//            Giornaliero giornaliero = new Giornaliero();
//            giornaliero.setTratta(trattaDAO.getById(faker.random().nextInt(1,10).longValue()));
//            giornalieroDAO.save(giornaliero);
//        }

        BigliettoDAO bigliettoDAO = new BigliettoDAO(em);

        List<Biglietto> bigliettos = bigliettoDAO.findAll();
        bigliettos.forEach(System.out::println);
    }
}
