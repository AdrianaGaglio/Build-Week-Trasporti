package epicode.it.dao.mezzo;

import epicode.it.entities.mezzo.Tram;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TramDAO {
    private EntityManager em;

    public void save(Tram oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public Tram findById(Long id) {
        return em.find(Tram.class, id);
    }

    public List<Tram> findAll() {
        return em.createNamedQuery("Trova_tutto_Tram", Tram.class).getResultList();
    }

    public void update(Tram oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Tram oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}