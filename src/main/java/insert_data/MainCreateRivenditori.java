package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestire_tessera.GestoreTessera;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MainCreateRivenditori {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);
        GestoreTessera gestoreTessera = new GestoreTessera(em);

        GestoreRivenditoriEBiglietti gestoreRiEVe = new GestoreRivenditoriEBiglietti(em);
        gestoreRiEVe.creaRivenditoreFisico(DayOfWeek.SATURDAY, Time.valueOf("08:30:00"), Time.valueOf("20:00:00"));
        gestoreRiEVe.creaRivenditoreFisico(DayOfWeek.MONDAY, Time.valueOf("10:00:00"), Time.valueOf("23:00:00"));
        gestoreRiEVe.creaRivenditoreFisico(DayOfWeek.TUESDAY, Time.valueOf("06:30:00"), Time.valueOf("20:00:00"));


        gestoreRiEVe.creaRivenditoreAutomatico();
        gestoreRiEVe.creaRivenditoreAutomatico();
        gestoreRiEVe.creaRivenditoreAutomatico();


        List<Rivenditore> rivenditori = rivenditoreDAO.findAll();
        Rivenditore rivenditoreCasuale = rivenditori.get(faker.number().numberBetween(0, 5));
        System.out.println(rivenditoreCasuale);
        Utente utente = new Utente();
        utente.setNome("Marco");
        utente.setCognome("Cipolletta");
        utente.setEmail("Marco@Cipo.it");
        utente.setDataNascita(faker.date().birthday().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
        utenteDAO.save(utente);
        Utente utenteRichiamato = utenteDAO.getById(1L);
//        gestoreTessera.creaTessera(rivenditoreCasuale, utenteRichiamato, new Scanner(System.in));
        gestoreRiEVe.creaAbbonamento(rivenditoreCasuale,Periodicy.settimanale,utenteRichiamato);

//        gestoreRiEVe.creaGiornaliero(rivenditoreCasuale, null);

//        System.out.println("BIglietti venduti " + rivenditoreCasuale.getBiglietti().getFirst().getId());

    }
}
