package epicode.it.dao.utente;

import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

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

    public Utente creazioneUtente(String nome, String cognome, LocalDate dataDiNascita, String email){
        Utente utente = new Utente();
        utente.setNome(nome);
        utente.setCognome(cognome);
        utente.setDataNascita(dataDiNascita);
        utente.setEmail(email);
        System.out.println("creato nuovo utente "+ utente);
        save(utente);
        return utente;
    }

}