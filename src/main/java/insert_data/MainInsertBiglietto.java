package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.AbbonamentoDAO;
import epicode.it.dao.GiornalieroDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Giornaliero;

import epicode.it.entities.biglietto.Periodicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.Locale;

public class MainInsertBiglietto {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        // PRENDO I METODI DALLE DAO
        GiornalieroDAO giornalieroDAO = new GiornalieroDAO(em);
        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);

        try {
            // CREO UN NUOVO BIGLIETTO GIORNALIERO
            Giornaliero giornaliero = new Giornaliero();
            giornaliero.setScadenza(LocalDate.now());
            giornaliero.setDaAttivare(true);

            // CREO UN NUOVO ABBONAMENTO
            Abbonamento abbonamento = new Abbonamento();
            abbonamento.setScadenza(LocalDate.now().plusMonths(1));
            abbonamento.setPeriodicy(Periodicy.mensile);
            abbonamento.setAttivo(true);
            abbonamento.setTariffa("Standard");

            // SALVO
            giornalieroDAO.save(giornaliero);
            abbonamentoDAO.save(abbonamento);

            System.out.println("Biglietti inseriti madaffakka!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
