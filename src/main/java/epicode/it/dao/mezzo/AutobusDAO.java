package epicode.it.dao.mezzo;

import epicode.it.entities.mezzo.Autobus;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AutobusDAO {
    private EntityManager em;

    public void save(Autobus oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public Autobus findById(Long id) {
        return em.find(Autobus.class, id);
    }

    public List<Autobus> findAll() {
        return em.createNamedQuery("Trova_tutto_Autobus", Autobus.class).getResultList();
    }

    public void update(Autobus oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Autobus oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}