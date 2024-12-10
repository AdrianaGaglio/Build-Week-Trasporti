package epicode.it.dao;

import epicode.it.entities.biglietto.Giornaliero;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@AllArgsConstructor
public class GiornalieroDAO {
    private EntityManager em;

    public void save(Giornaliero giornaliero) {
        em.getTransaction().begin();
        em.persist(giornaliero);
        em.getTransaction().commit();
    }

    public Giornaliero findById(Long id) {
        return em.find(Giornaliero.class, id);
    }

    public List<Giornaliero> findAll() {
        return em.createNamedQuery("Trova_tutto_Giornaliero", Giornaliero.class).getResultList();
    }
    
    public void update(Giornaliero giornaliero) {
        em.getTransaction().begin();
        em.merge(giornaliero);
        em.getTransaction().commit();
    }

    public void delete(Giornaliero giornaliero) {
        em.getTransaction().begin();
        em.remove(giornaliero);
        em.getTransaction().commit();
    }


    // RELAZIONE CON TRATTA
    public List<Giornaliero> findByTratta(Long trattaId) {
        return em.createQuery("SELECT g FROM Giornaliero g WHERE g.tratta.id = :trattaId", Giornaliero.class)
                .setParameter("trattaId", trattaId)
                .getResultList();
    }

    // RELAZIONE CON MEZZO

    public List<Giornaliero> findByMezzo(Long mezzoId) {
        return em.createQuery("SELECT g FROM Giornaliero g WHERE g.mezzo.id = :mezzoId", Giornaliero.class)
                .setParameter("mezzoId", mezzoId)
                .getResultList();
    }


}
