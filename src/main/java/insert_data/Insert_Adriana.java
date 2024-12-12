package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.mezzo.Tram;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestire_tessera.GestioneTessera;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalTime;
import java.util.Locale;

public class Insert_Adriana {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);
        TrattaDAO trattaDAO = new TrattaDAO(em);
        MezzoDAO mezzoDAO = new MezzoDAO(em);

        UtenteDAO utenteDAO = new UtenteDAO(em);

        TesseraDAO tesseraDAO = new TesseraDAO(em);
        GestioneTessera gestoreTessera = new GestioneTessera(em);

//        for (int i = 0; i < 100; i++) {
//            Tratta tratta = new Tratta();
//            tratta.setPartenza(faker.address().streetAddress());
//            tratta.setCapolinea(faker.address().streetAddress());
//            tratta.setDurata(LocalTime.of(faker.random().nextInt(1,2),faker.random().nextInt(0,59), faker.random().nextInt(0,59)));
//            trattaDAO.save(tratta);
//        }
//
//        for (int i = 0; i < 10; i++) {
//            Autobus autobus = new Autobus();
//            autobus.setCodice(faker.random().nextInt(1,100));
//            autobus.setStato(Stato.DEPOSITO);
//            mezzoDAO.save(autobus);
//            Tram tram = new Tram();
//            tram.setCodice(faker.random().nextInt(1,100));
//            tram.setStato(Stato.DEPOSITO);
//            mezzoDAO.save(tram);
//        }

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

//        for (int i = 0; i < 30; i++) {
//            Utente utente = new Utente();
//            utente.setNome(faker.name().firstName());
//            utente.setCognome(faker.name().lastName());
//            utente.setEmail(faker.internet().emailAddress());
//            utente.setDataNascita(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
//            int random = ((int) (Math.random() * 3)) + 1;
//            switch (random) {
//                case 1: utente.setRuolo("admin");
//                    break;
//                case 2: utente.setRuolo("utente");
//                    break;
//                case 3: utente.setRuolo("rivenditore");
//                    break;
//            }
//            utenteDAO.save(utente);
//        }

        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);

        Rivenditore riv1 = rivenditoreDAO.findById(1L);
        Rivenditore riv2 = rivenditoreDAO.findById(3L);
        Rivenditore riv3 = rivenditoreDAO.findById(4L);

        gestoreTessera.creaTessera(riv1, utenteDAO.getById(52L));
        gestoreTessera.creaTessera(riv2, utenteDAO.getById(57L));

    }
}