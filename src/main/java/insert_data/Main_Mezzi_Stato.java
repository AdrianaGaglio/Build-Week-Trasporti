package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.dao.stato_mezzo.ServizioDAO;
import epicode.it.dao.stato_mezzo.StatoMezzoDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Tram;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public class Main_Mezzi_Stato {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        StatoMezzoDAO statoMezzoDAO = new StatoMezzoDAO(em);
        ServizioDAO servizioDAO = new ServizioDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);

        MezzoDAO mezzoDAO = new MezzoDAO(em);

        TrattaDAO trattaDAO = new TrattaDAO(em);

//        for (int i = 0; i < 10; i++) {
//            Autobus autobus = new Autobus();
//            mezzoDAO.save(autobus);
//            Tram tram = new Tram();
//            mezzoDAO.save(tram);
//        }
//
//        String city = faker.country().capital();
//
//        for(int i = 0; i < 100; i++) {
//            Tratta tratta = new Tratta();
//            tratta.setPartenza(faker.address().streetAddress());
//            tratta.setCapolinea(faker.address().streetAddress());
//            tratta.setDurata(LocalTime.of(faker.random().nextInt(1,2),faker.random().nextInt(0,59), faker.random().nextInt(0,59)));
//            trattaDAO.save(tratta);
//        }

//        Mezzo mezzo = mezzoDAO.findById(1L);
//        Tratta tratta = trattaDAO.getById(1L);
//
//        mezzoDAO.aggiungiServizio(mezzo,LocalDate.of(2025, 1, 10), LocalDate.of(2025, 2, 10), tratta);

    }
}