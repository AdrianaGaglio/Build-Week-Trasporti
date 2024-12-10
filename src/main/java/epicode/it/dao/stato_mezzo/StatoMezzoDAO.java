package epicode.it.dao.stato_mezzo;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.stato_mezzo.StatoMezzo;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class StatoMezzoDAO {
    private EntityManager em;

    public void save(StatoMezzo oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public StatoMezzo findById(Long id) {
        return em.find(StatoMezzo.class, id);
    }

    public List<StatoMezzo> findAll() {
        return em.createNamedQuery("Trova_tutto_StatoMezzo", StatoMezzo.class).getResultList();
    }

    public void update(StatoMezzo oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(StatoMezzo oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }

    public Servizio cercaSeInServizio(Mezzo mezzo, LocalDate data) {
        return (Servizio) em.createNamedQuery("cercaSeInServizio", StatoMezzo.class)
                .setParameter("mezzo", mezzo).setParameter("data", data).getResultStream().findFirst().orElse(null);
    }

}