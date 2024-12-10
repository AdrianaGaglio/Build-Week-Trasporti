package epicode.it.dao.utente;

import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

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

    // METODDO PER VERIFICA SE L'UTENTE HA UNA TESSERA
    //Questo metodo recupera l'utente tramite il suo ID e verifica se la tessera non è null.


}