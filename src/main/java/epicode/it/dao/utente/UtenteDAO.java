package epicode.it.dao.utente;

import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class UtenteDAO {
    private EntityManager em;

    public void save(Utente utente) {
        em.getTransaction().begin();
        em.persist(utente);
        em.getTransaction().commit();
    }

    public void saveAll(List<Utente> list) {
        em.getTransaction().begin();
        for (Utente user : list) {
            em.persist(user);
        }
        em.getTransaction().commit();
    }

    public List<Utente> getAll() {
        return this.em.createNamedQuery("findAll_Utente", Utente.class).getResultList();
    }

    public Utente getById(Long id) {
        return em.find(Utente.class, id);
    }

    public void update(Utente utente) {
        em.getTransaction().begin();
        em.merge(utente);
        em.getTransaction().commit();
    }

    public void delete(Utente utente) {
        em.getTransaction().begin();
        em.remove(utente);
        em.getTransaction().commit();
    }

    public Utente findByCard(String codice) {
        return em.createNamedQuery("findByCard", Utente.class)
                .setParameter("codice", codice).getResultStream().findFirst().orElse(null);
    }

    // RICERCA PER EMAIL
    public Utente findByEmail(String email) {
        return em.createNamedQuery("findByEmail", Utente.class)
                .setParameter("email", email).getResultStream().findFirst().orElse(null);
    }

    public Utente creaTessera(Rivenditore r, Utente u) {
        em.getTransaction().begin();

        if (u.getTessera() == null) {
            System.out.println("Creazione tessera in corso...");
            Tessera tessera = new Tessera();
            tessera.setValidita(LocalDateTime.now().plusYears(1));
            u.setTessera(tessera);
            em.persist(tessera);
            em.merge(u);
        } else if (u.getTessera().getValidita().isBefore(LocalDateTime.now())) {
            System.out.println("Tessera scaduta, necessita rinnovo");
//            u.getTessera().setValidita(LocalDateTime.now().plusYears(1));
//            em.merge(u.getTessera());
//            em.merge(u);
        } else {
            System.out.println("Utente già in possesso di tessera valida");
        }
        em.getTransaction().commit();
        return u;
    }
}