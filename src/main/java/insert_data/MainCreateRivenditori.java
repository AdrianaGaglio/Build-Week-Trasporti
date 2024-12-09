package insert_data;

import com.github.javafaker.Faker;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Periodicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Locale;

public class MainCreateRivenditori {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            RivFisico rivFisico1 = new RivFisico();
            rivFisico1.setGiornoChiusura("Domenica");
            rivFisico1.setOraApertura(Time.valueOf("09:00:00"));
            rivFisico1.setOraChiusura(Time.valueOf("20:00:00"));

            RivFisico rivFisico2 = new RivFisico();
            rivFisico2.setGiornoChiusura("Lunedì");
            rivFisico2.setOraApertura(Time.valueOf("08:30:00"));
            rivFisico2.setOraChiusura(Time.valueOf("18:30:00"));

            RivAutomatico rivAutomatico1 = new RivAutomatico();
            rivAutomatico1.setAttivo(true);

            RivAutomatico rivAutomatico2 = new RivAutomatico();
            rivAutomatico2.setAttivo(false);

            em.persist(rivFisico1);
            em.persist(rivFisico2);
            em.persist(rivAutomatico1);
            em.persist(rivAutomatico2);

            // Creazione biglietti giornalieri rivenditore fisico 1
            for (int i = 0; i < 5; i++) {
                Giornaliero giornaliero = new Giornaliero();
                giornaliero.setScadenza(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
                giornaliero.setDaAttivare(faker.bool().bool());
                rivFisico1.addBiglietto(giornaliero);
                em.persist(giornaliero);
            }

            // Creazione  abbonamenti rivenditore fisico 2
            for (int i = 0; i < 3; i++) {
                Abbonamento abbonamento = new Abbonamento();
                abbonamento.setScadenza(LocalDate.now().plusMonths(faker.number().numberBetween(1, 12)));
                abbonamento.setPeriodicy(Periodicy.values()[faker.number().numberBetween(0, Periodicy.values().length)]);
                abbonamento.setAttivo(true);
                abbonamento.setTariffa(faker.commerce().price() + " EUR");
                rivFisico2.addBiglietto(abbonamento);
                em.persist(abbonamento);
            }

            // Creazione biglietti misti rivenditore automatico 1
            for (int i = 0; i < 7; i++) {
                if (i % 2 == 0) {
                    Giornaliero giornaliero = new Giornaliero();
                    giornaliero.setScadenza(LocalDate.now().plusDays(faker.number().numberBetween(1, 30)));
                    giornaliero.setDaAttivare(faker.bool().bool());
                    rivAutomatico1.addBiglietto(giornaliero);
                    em.persist(giornaliero);
                } else {
                    Abbonamento abbonamento = new Abbonamento();
                    abbonamento.setScadenza(LocalDate.now().plusMonths(faker.number().numberBetween(1, 12)));
                    abbonamento.setPeriodicy(Periodicy.values()[faker.number().numberBetween(0, Periodicy.values().length)]);
                    abbonamento.setAttivo(true);
                    abbonamento.setTariffa(faker.commerce().price() + " EUR");
                    rivAutomatico1.addBiglietto(abbonamento);
                    em.persist(abbonamento);
                }
            }

            em.getTransaction().commit();



        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
