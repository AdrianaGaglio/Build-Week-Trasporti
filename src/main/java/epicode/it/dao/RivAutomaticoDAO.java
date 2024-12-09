package epicode.it.dao;

import epicode.it.entities.rivenditore.RivAutomatico;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RivAutomaticoDAO {
    private EntityManager em;

    public void save(RivAutomatico oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public RivAutomatico findById(Long id) {
        return em.find(RivAutomatico.class, id);
    }

    public List<RivAutomatico> findAll() {
        return em.createNamedQuery("Trova_tutto_RivAutomatico", RivAutomatico.class).getResultList();
    }

    public void update(RivAutomatico oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(RivAutomatico oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}