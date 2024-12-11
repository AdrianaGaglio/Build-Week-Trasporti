//package insert_data;
//
//import com.github.javafaker.Faker;
//import epicode.it.dao.mezzo.AutobusDAO;
//import epicode.it.dao.mezzo.MezzoDAO;
//import epicode.it.dao.mezzo.TramDAO;
//import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
//import epicode.it.dao.stato_mezzo.ServizioDAO;
//import epicode.it.entities.mezzo.Autobus;
//import epicode.it.entities.mezzo.Mezzo;
//import epicode.it.entities.mezzo.Stato;
//import epicode.it.entities.mezzo.Tram;
//import epicode.it.entities.stato_mezzo.Manutenzione;
//import epicode.it.entities.stato_mezzo.Servizio;
//import epicode.it.entities.tratta.Tratta;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.Persistence;
//
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.Locale;
//import java.util.concurrent.TimeUnit;
//
//public class MainInsertServizioMezzi {
//    public static void main(String[] args) {
//        Faker faker = new Faker(new Locale("it"));
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
//        EntityManager em = emf.createEntityManager();
//        MezzoDAO mezzoDAO = new MezzoDAO(em);
//        AutobusDAO autobusDAO = new AutobusDAO(em);
//        TramDAO tramDAO = new TramDAO(em);
//        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
//        ServizioDAO servizioDAO = new ServizioDAO(em);
//
//
//        for (int i = 0; i < 10; i++) {
//            Autobus autobus = new Autobus();
//            autobus.setStato(Stato.values()[(int) (Math.random() *Stato.values().length)]);
//
//            autobusDAO.save(autobus);
//
//            Tram tram = new Tram();
//            tram.setStato(Stato.values()[(int) (Math.random() *Stato.values().length)]);
//
//            tramDAO.save(tram);
//        }
//
//         List<Mezzo> allMezzi = mezzoDAO.findAll();
//
//        for (Mezzo mezzo : allMezzi) {
//
//            if ( mezzo.getStato() == Stato.IN_MANUTENZIONE) {
//                Manutenzione manutenzione = new Manutenzione();
//                manutenzione.setDescrizione(faker.lorem().sentence());
//                manutenzione.setDataInizio(faker.date().past(10, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//                manutenzione.setMezzo(mezzo);
//                manutenzioneDAO.save(manutenzione);
//                mezzo.getManutenzioni().add(manutenzione);
//                mezzoDAO.update(mezzo);
//            } else {
//                Servizio servizio = new Servizio();
//                servizio.setDataInizio(LocalDate.now());
//                servizio.setMezzo(mezzo);
//                servizioDAO.save(servizio);
//                mezzo.getServizi().add(servizio);
//                mezzoDAO.update(mezzo);
//            }
//
//        }
//
//
//
//    }
//}