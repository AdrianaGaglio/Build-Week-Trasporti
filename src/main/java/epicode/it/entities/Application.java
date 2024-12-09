package epicode.it.entities;

import com.github.javafaker.Faker;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.mezzo.Tram;
import epicode.it.entities.stato_mezzo.Manutenzione;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jdk.swing.interop.SwingInterOpUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Application {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);


        for (int i = 0; i < 10; i++) {
            Autobus autobus = new Autobus();
            autobus.setStato(Stato.values()[(int) (Math.random() *Stato.values().length)]);

            mezzoDAO.save(autobus);

            Tram tram = new Tram();
            tram.setStato(Stato.values()[(int) (Math.random() *Stato.values().length)]);

            mezzoDAO.save(tram);
        }

         List<Mezzo> allMezzi = mezzoDAO.findAll();

        for (Mezzo mezzo : allMezzi) {

            if ( mezzo.getStato() == Stato.IN_MANUTENZIONE) {
                Manutenzione manutenzione = new Manutenzione();
                manutenzione.setDescrizione(faker.lorem().sentence());
                manutenzione.setDataInizio(faker.date().past(10, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                manutenzioneDAO.save(manutenzione);
                mezzo.getManutenzioni().add(manutenzione);
                mezzoDAO.update(mezzo);
            }
            System.out.println(mezzo);
        }



    }
}