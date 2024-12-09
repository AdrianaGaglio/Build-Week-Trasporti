package epicode.it.dao.stato_mezzo;

import epicode.it.entities.stato_mezzo.Servizio;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import java.util.List;

@AllArgsConstructor
public class ServizioDAO {
    private EntityManager em;

    public void save(Servizio oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public Servizio findById(Long id) {
        return em.find(Servizio.class, id);
    }

    public List<Servizio> findAll() {
        return em.createNamedQuery("Trova_tutto_Servizio", Servizio.class).getResultList();
    }
    
    public void update(Servizio oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Servizio oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}