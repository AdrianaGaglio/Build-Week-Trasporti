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

import java.time.LocalDateTime;
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
            giornaliero.setScadenza(LocalDateTime.now());
            giornaliero.setDaAttivare(true);

            // SALVO
            giornalieroDAO.save(giornaliero);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
