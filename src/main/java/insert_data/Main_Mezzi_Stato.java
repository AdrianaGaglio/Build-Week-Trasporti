//package insert_data;
//
//import com.github.javafaker.Faker;
//import epicode.it.dao.mezzo.MezzoDAO;
//import epicode.it.dao.percorrenza.PercorrenzaDAO;
//import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
//import epicode.it.dao.stato_mezzo.ServizioDAO;
//import epicode.it.dao.stato_mezzo.StatoMezzoDAO;
//import epicode.it.dao.tratta.TrattaDAO;
//import epicode.it.entities.mezzo.Autobus;
//import epicode.it.entities.mezzo.Tram;
//import epicode.it.entities.percorrenza.Percorrenza;
//import epicode.it.entities.tratta.Tratta;
//
//
//import epicode.it.servizi.gestore_stati_servizio.GestoreStatiServizio;
//import epicode.it.servizi.gestore_stati_servizio.StatisticheMezzo;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.Persistence;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//import java.util.Locale;
//
//public class Main_Mezzi_Stato {
//    public static void main(String[] args) {
//        Faker faker = new Faker(new Locale("it"));
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
//        EntityManager em = emf.createEntityManager();
//
//        StatoMezzoDAO statoMezzoDAO = new StatoMezzoDAO(em);
//        ServizioDAO servizioDAO = new ServizioDAO(em);
//        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
//
//        MezzoDAO mezzoDAO = new MezzoDAO(em);
//
//        TrattaDAO trattaDAO = new TrattaDAO(em);
//
////        for (int i = 0; i < 5; i++) {
////            int random = ((int) (Math.random() * 100)) + 1;
////            Autobus autobus = new Autobus(random);
////            mezzoDAO.save(autobus);
////        }
////
////        for (int i = 0; i < 5; i++) {
////            int random = ((int) (Math.random() * 100)) + 1;
////            Tram tram = new Tram(random);
////            mezzoDAO.save(tram);
////        }
////
////        String city = faker.country().capital();
////
////        for(int i = 0; i < 100; i++) {
////            Tratta tratta = new Tratta();
////            tratta.setPartenza(faker.address().streetAddress());
////            tratta.setCapolinea(faker.address().streetAddress());
////            tratta.setDurata(LocalTime.of(faker.random().nextInt(1,2),faker.random().nextInt(0,59), faker.random().nextInt(0,59)));
////            trattaDAO.save(tratta);
////        }
//
//        Autobus bus1 = (Autobus) mezzoDAO.findById(1L);
//        Autobus bus2 = (Autobus) mezzoDAO.findById(2L);
//        Autobus bus3 = (Autobus) mezzoDAO.findById(3L);
//        Autobus bus4 = (Autobus) mezzoDAO.findById(4L);
//        Autobus bus5 = (Autobus) mezzoDAO.findById(5L);
//        Tram tram1 = (Tram) mezzoDAO.findById(6L);
//        Tram tram2 = (Tram) mezzoDAO.findById(7L);
//        Tram tram3 = (Tram) mezzoDAO.findById(8L);
//        Tram tram4 = (Tram) mezzoDAO.findById(9L);
//        Tram tram5 = (Tram) mezzoDAO.findById(10L);
//
//        GestoreStatiServizio gestoreStati = new GestoreStatiServizio(em);
//
////        gestoreStati.aggiungiServizio(bus1, LocalDate.now(), LocalDate.now().plusDays(30), trattaDAO.getById(1L));
////        gestoreStati.aggiungiServizio(bus2, LocalDate.now(), LocalDate.now().plusDays(30), trattaDAO.getById(2L));
////        gestoreStati.aggiungiServizio(bus3, LocalDate.now(), LocalDate.now().plusDays(30), trattaDAO.getById(1L));
////
////
////        GestorePercorrenze gestorePercorrenze = new GestorePercorrenze(em);
////
////
//////        gestorePercorrenze.aggiungiPercorrenza(bus2, LocalDateTime.now(), trattaDAO.getById(1L));
////        gestorePercorrenze.aggiungiPercorrenza(bus1, LocalDateTime.now(), trattaDAO.getById(1L));
////
////        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);
////
////        List<Percorrenza> percorrenzas = percorrenzaDAO.perMezzoTratta(trattaDAO.getById(1L), bus1);
////        percorrenzas.forEach(System.out::println);
//
//        StatisticheMezzo statisticheMezzo = new StatisticheMezzo(em);
//
//        gestoreStati.aggiungiManutenzione(bus2, LocalDate.now(), "Controllo");
//
//        statisticheMezzo.periodiServizio(bus1).forEach(System.out::println);
//        statisticheMezzo.periodiManutenzione(bus2).forEach(System.out::println);
//
//    }
//
//}