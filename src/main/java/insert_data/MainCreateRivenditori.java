package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.dao.stato_mezzo.ServizioDAO;
import epicode.it.dao.stato_mezzo.StatoMezzoDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.stato_mezzo.StatoMezzo;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestire_tessera.GestoreTessera;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import epicode.it.servizi.gestore_stati_servizio.GestoreStatiServizio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainCreateRivenditori {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

//        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);
//        UtenteDAO utenteDAO = new UtenteDAO(em);
//        GestoreTessera gestoreTessera = new GestoreTessera(em);
//        GestoreRivenditoriEBiglietti gestoreRivenditori = new GestoreRivenditoriEBiglietti(em);
//
//        for (int i = 0; i < 10; i++) {
//            gestoreRivenditori.creaRivenditoreFisico(DayOfWeek.of(faker.random().nextInt(1,7)), LocalTime.of(faker.random().nextInt(8,10), faker.random().nextInt(0,59)),
//                    LocalTime.of(faker.random().nextInt(17,20), faker.random().nextInt(0,59)));
//            gestoreRivenditori.creaRivenditoreAutomatico();
//        }
        GiornalieroDAO giornalieroDAO = new GiornalieroDAO(em);
        List<Giornaliero> biglietti = giornalieroDAO.findAll();
        biglietti.forEach(System.out::println);


    }
}
