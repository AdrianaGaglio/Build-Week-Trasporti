package epicode.it.dao.percorrenza;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public Percorrenza trovaPerMezzo(Mezzo m, LocalDateTime data) {
        return em.createNamedQuery("trovaPerMezzo", Percorrenza.class)
                .setParameter("mezzo", m).getResultStream()
                .filter(p -> p.getData().truncatedTo(ChronoUnit.MINUTES).equals(data.truncatedTo(ChronoUnit.MINUTES))).findFirst().orElse(null);
    }

    public Percorrenza trovaPerTratta(Tratta t, LocalDateTime data) {
        return em.createNamedQuery("trovaPerTratta", Percorrenza.class)
                .setParameter("tratta", t).getResultStream()
                .filter(p -> p.getData().truncatedTo(ChronoUnit.MINUTES).equals(data.truncatedTo(ChronoUnit.MINUTES))).findFirst().orElse(null);
    }

    public List<Percorrenza> perMezzoTratta(Tratta t, Mezzo m) {
        return em.createNamedQuery("perMezzoTratta", Percorrenza.class)
                .setParameter("tratta", t).setParameter("mezzo",m).getResultList();
    }

}