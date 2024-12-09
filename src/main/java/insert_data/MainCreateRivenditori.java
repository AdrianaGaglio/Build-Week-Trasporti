package insert_data;

import com.github.javafaker.Faker;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.RivAutomatico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.util.Locale;

public class MainCreateRivenditori {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            //Rivenditore Fisico
            RivFisico rivFisico1 = new RivFisico();
            rivFisico1.setGiornoChiusura("Domenica");
            rivFisico1.setOraApertura(Time.valueOf("09:00:00"));
            rivFisico1.setOraChiusura(Time.valueOf("20:00:00"));

            RivFisico rivFisico2 = new RivFisico();
            rivFisico2.setGiornoChiusura("Lunedì");
            rivFisico2.setOraApertura(Time.valueOf("08:30:00"));
            rivFisico2.setOraChiusura(Time.valueOf("18:30:00"));

            //Rivenditore automatico
            RivAutomatico rivAutomatico1 = new RivAutomatico();
            rivAutomatico1.setAttivo(true);

            RivAutomatico rivAutomatico2 = new RivAutomatico();
            rivAutomatico2.setAttivo(false);

            em.persist(rivFisico1);
            em.persist(rivFisico2);
            em.persist(rivAutomatico1);
            em.persist(rivAutomatico2);

            em.getTransaction().commit();

            System.out.println("Rivenditori creati e salvati nel database!");
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
