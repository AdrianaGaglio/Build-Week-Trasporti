package epicode.it.dao.stato_mezzo;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.stato_mezzo.Manutenzione;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ManutenzioneDAO {
    private EntityManager em;

    public void save(Manutenzione oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        Mezzo mezzo = oggetto.getMezzo();
        mezzo.setStato(Stato.IN_MANUTENZIONE);
        mezzo.getManutenzioni().add(oggetto);
        em.merge(mezzo);
        em.getTransaction().commit();
    }

    public Manutenzione findById(Long id) {
        return em.find(Manutenzione.class, id);
    }

    public List<Manutenzione> findAll() {
        return em.createNamedQuery("Trova_tutto_Manutenzione", Manutenzione.class).getResultList();
    }

    public void update(Manutenzione oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Manutenzione oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}