package epicode.it.dao;

import epicode.it.entities.rivenditore.Rivenditore;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RivenditoreDAO {
    private EntityManager em;

    public void save(Rivenditore oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public Rivenditore findById(Long id) {
        return em.find(Rivenditore.class, id);
    }

    public List<Rivenditore> findAll() {
        return em.createNamedQuery("Trova_tutto_Rivenditore", Rivenditore.class).getResultList();
    }

    public void update(Rivenditore oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Rivenditore oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}