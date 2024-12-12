package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.mezzo.Tram;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

public class Insert_Tratte_Percorrenze {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);
        TrattaDAO trattaDAO = new TrattaDAO(em);
        MezzoDAO mezzoDAO = new MezzoDAO(em);

//        for (int i = 0; i < 100; i++) {
//            Tratta tratta = new Tratta();
//            tratta.setPartenza(faker.address().streetAddress());
//            tratta.setCapolinea(faker.address().streetAddress());
//            tratta.setDurata(LocalTime.of(faker.random().nextInt(1,2),faker.random().nextInt(0,59), faker.random().nextInt(0,59)));
//            trattaDAO.save(tratta);
//        }

        for (int i = 0; i < 10; i++) {
            Autobus autobus = new Autobus();
            autobus.setCodice(faker.random().nextInt(1,100));
            autobus.setStato(Stato.DEPOSITO);
            mezzoDAO.save(autobus);
            Tram tram = new Tram();
            tram.setCodice(faker.random().nextInt(1,100));
            tram.setStato(Stato.DEPOSITO);
            mezzoDAO.save(tram);
        }

//        for (int i = 0; i < 100; i++) {
//            Percorrenza percorrenza = new Percorrenza();
//            Tratta tratta = trattaDAO.getById(faker.random().nextInt(1,10).longValue());
//            percorrenza.setTratta(tratta);
//            percorrenza.setDurata_effettiva(LocalTime.of(faker.random().nextInt(1,2), faker.random().nextInt(1,59)));
//            Mezzo mezzo = mezzoDAO.findById(faker.random().nextInt(1,10).longValue());
//            percorrenza.setMezzo(mezzo);
//            percorrenza.setData(LocalDateTime.now());
//            percorrenzaDAO.save(percorrenza);
//        }


    }
}