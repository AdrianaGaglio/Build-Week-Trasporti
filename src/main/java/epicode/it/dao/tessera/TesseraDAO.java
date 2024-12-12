package epicode.it.dao.tessera;

import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
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

    public void update(Tessera tessera) {
        em.getTransaction().begin();
        em.merge(tessera);
        em.getTransaction().commit();
    }

    public Tessera getTessera(Utente utente) {
        return em.createQuery("SELECT t FROM Tessera t WHERE t.utente.id = :id", Tessera.class)
                .setParameter("id", utente.getId())
                .getResultStream().findFirst().orElse(null);
    }

    public Tessera getById(Long id) {
        return em.find(Tessera.class, id);
    }

    public void delete(Tessera tessera) {
        em.getTransaction().begin();
        em.remove(tessera);
        em.getTransaction().commit();
    }

    public List<Tessera> getAll() {
        return em.createQuery("SELECT t FROM Tessera t", Tessera.class)
                .getResultList();
    }
}