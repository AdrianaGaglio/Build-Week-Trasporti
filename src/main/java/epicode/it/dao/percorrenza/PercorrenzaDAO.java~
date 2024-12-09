package epicode.it.dao.percorrenza;

import epicode.it.entities.percorrenza.Percorrenza;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PercorrenzaDAO {
    private EntityManager em;

    public void save(Percorrenza percorrenza) {
        em.getTransaction().begin();
        em.persist(percorrenza);
        em.getTransaction().commit();
    }

    public void saveAll(List<Percorrenza> list) {
        em.getTransaction().begin();
        for (Percorrenza obj : list) {
            em.persist(obj);
        }
        em.getTransaction().commit();
    }

    public List<Percorrenza> getAll() {
        return this.em.createNamedQuery("findAll_Percorrenza", Percorrenza.class).getResultList();
    }

    public Percorrenza getById(Long id) {
        return em.find(Percorrenza.class, id);
    }

    public void update(Percorrenza percorrenza) {
        em.getTransaction().begin();
        em.merge(percorrenza);
        em.getTransaction().commit();
    }

    public void delete(Percorrenza percorrenza) {
        em.getTransaction().begin();
        em.remove(percorrenza);
        em.getTransaction().commit();
    }
}