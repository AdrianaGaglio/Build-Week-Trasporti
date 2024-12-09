package insert_data;

import epicode.it.dao.GiornalieroDAO;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDate;
import java.util.List;

public class MainTestBiglietto {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        GiornalieroDAO giornalieroDAO = new GiornalieroDAO(em);

        Giornaliero giornaliero = new Giornaliero();
        giornaliero.setDaAttivare(true);
        giornaliero.setScadenza(LocalDate.now().plusDays(1));


        Tratta tratta = em.find(Tratta.class, 1L);
        Mezzo mezzo = em.find(Mezzo.class, 1L);
        giornaliero.setTratta(tratta);
        giornaliero.setMezzo(mezzo);

        giornalieroDAO.save(giornaliero);

        List<Giornaliero> giornalieriPerTratta = giornalieroDAO.findByTratta(1L);
        System.out.println("Biglietti per tratta ID 1: " + giornalieriPerTratta.size());
    }
}
