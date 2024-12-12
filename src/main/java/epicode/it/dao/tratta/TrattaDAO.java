package epicode.it.dao.tratta;

import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class TrattaDAO {
    private EntityManager em;

    public void save(Tratta tratta) {
        em.getTransaction().begin();
        em.persist(tratta);
        em.getTransaction().commit();
    }

    public void saveAll(List<Tratta> list) {
        em.getTransaction().begin();
        for (Tratta tratta : list) {
            em.persist(tratta);
        }
        em.getTransaction().commit();
    }

    public List<Tratta> getAll() {
        return this.em.createNamedQuery("findAll_Tratta", Tratta.class).getResultList();
    }

    public Tratta getById(Long id) {
        return em.find(Tratta.class, id);
    }

    public void update(Tratta tratta) {
        em.getTransaction().begin();
        em.merge(tratta);
        em.getTransaction().commit();
    }

    public void delete(Tratta tratta) {
        em.remove(em.contains(tratta) ? tratta : em.merge(tratta));
    }
}