package epicode.it.dao;

import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class TesseraDAO {
    private EntityManager em;

    public void save(Tessera tessera) {
        em.getTransaction().begin();
        em.persist(tessera);
        em.getTransaction().commit();
    }

    public List<Tessera> getAll() {
        return this.em.createNamedQuery("findAll_Tessera", Tessera.class).getResultList();
    }

    public Tessera getById(Long id) {
        return em.find(Tessera.class, id);
    }

    public void update(Tessera tessera) {
        em.getTransaction().begin();
        em.merge(tessera);
        em.getTransaction().commit();
    }

    public void delete(Tessera tessera) {
        em.getTransaction().begin();
        em.remove(tessera);
        em.getTransaction().commit();
    }
}