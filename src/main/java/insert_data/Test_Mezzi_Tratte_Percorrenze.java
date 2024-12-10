package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.mezzo.AutobusDAO;
import epicode.it.dao.mezzo.MezzoDAO;
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

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Test_Mezzi_Tratte_Percorrenze {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        TrattaDAO trattaDAO = new TrattaDAO(em);

//        String city = faker.country().capital();
//
//        List<Tratta> tratte = new ArrayList<>();
//
//        for (int i = 0; i < 50; i++) {
//            Tratta tratta = new Tratta();
//            tratta.setPartenza(faker.address().streetAddress());
//            tratta.setCapolinea(faker.address().streetAddress());
//            tratta.setDurata(LocalTime.of(faker.number().numberBetween(1,2), faker.number().numberBetween(0,59), faker.number().numberBetween(0,59)));
//            tratte.add(tratta);
//        }
//
//        trattaDAO.saveAll(tratte);

//        MezzoDAO mezzoDAO = new MezzoDAO(em);
//
//        List<Mezzo> mezzi = new ArrayList<>();
//
//        for (int i = 0; i < 100; i++) {
//            Autobus autobus = new Autobus();
//            Tram tram = new Tram();
//            mezzi.add(autobus);
//            mezzi.add(tram);
//        }
//
//        mezzoDAO.saveAll(mezzi);

        ServizioDAO servizioDAO = new ServizioDAO(em);
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        AutobusDAO autobusDAO = new AutobusDAO(em);

        Servizio servizio1 = new Servizio();
        Tratta tratta1 = trattaDAO.getById(faker.random().nextInt(1,50).longValue());
        servizio1.setTratta(tratta1);
        servizio1.setDataInizio(faker.date().past(90, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        servizio1.setDataFine(servizio1.getDataInizio().plusDays(60));
        Mezzo mezzo1 = mezzoDAO.findById(faker.random().nextInt(1,100).longValue());
        servizio1.setMezzo(mezzo1);
        servizioDAO.save(servizio1);

        for (int i = 0; i < 50; i++) {
            Servizio servizio = new Servizio();
            Tratta tratta = trattaDAO.getById(faker.random().nextInt(1,50).longValue());
            servizio.setDataInizio(faker.date().past(90, TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            servizio.setDataFine(servizio.getDataInizio().plusDays(60));
            Mezzo autobus = autobusDAO.findById(faker.random().nextInt(1,100).longValue());


        }
    }
}