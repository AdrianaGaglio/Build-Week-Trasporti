package epicode.it.dao.mezzo;

import epicode.it.dao.stato_mezzo.StatoMezzoDAO;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class MezzoDAO {
    private EntityManager em;

    public void save(Mezzo oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public void saveAll(List<Mezzo> mezzi) {
        em.getTransaction().begin();
        for (Mezzo mezzo : mezzi) {
            em.persist(mezzo);
        }
        em.getTransaction().commit();
    }

    public Mezzo findById(Long id) {
        return em.find(Mezzo.class, id);
    }

    public List<Mezzo> findAll() {
        return em.createNamedQuery("Trova_tutto_Mezzo", Mezzo.class).getResultList();
    }

    public void update(Mezzo oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(Mezzo oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }

    public List<Mezzo> mezziPerLinea(Integer num) {
        return em.createNamedQuery("trovaPerNumeroLinea", Mezzo.class)
                .setParameter("linea", num).getResultList();
    }

    public List<Mezzo> mezziPerStato(Stato stato) {
        return em.createNamedQuery("trovaMezzoPerStato", Mezzo.class)
                .setParameter("stato", stato).getResultList();
    }

}