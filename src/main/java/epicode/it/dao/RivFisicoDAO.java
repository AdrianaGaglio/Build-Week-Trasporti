package epicode.it.dao;

import epicode.it.entity.RivFisico;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class RivFisicoDAO {
    private EntityManager em;

    public void save(RivFisico oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public RivFisico findById(Long id) {
        return em.find(RivFisico.class, id);
    }

    public List<RivFisico> findAll() {
        return em.createNamedQuery("Trova_tutto_RivFisico", RivFisico.class).getResultList();
    }

    public void update(RivFisico oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(RivFisico oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}