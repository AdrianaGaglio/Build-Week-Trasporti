package epicode.it.dao.biglietto;

import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Abbonamento;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class BigliettoDAO {
    private EntityManager em;

    // Salva un nuovo Biglietto (Giornaliero o Abbonamento)
    public void save(Biglietto oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    // Trova un Biglietto generico per ID
    public Biglietto findById(Long id) {
        return em.find(Biglietto.class, id);
    }

    // Trova tutti i Biglietti generici
    public List<Biglietto> findAll() {
        return em.createNamedQuery("Trova_tutto_Biglietto", Biglietto.class).getResultList();
    }

    // Trova tutti i Biglietti di tipo Giornaliero
    public List<Giornaliero> findAllGiornalieri() {
        return em.createQuery("SELECT g FROM Giornaliero g", Giornaliero.class).getResultList();
    }

    // Trova tutti gli Abbonamenti
    public List<Abbonamento> findAllAbbonamenti() {
        return em.createQuery("SELECT a FROM Abbonamento a", Abbonamento.class).getResultList();
    }

    // Trova gli Abbonamenti associati a una Tessera
    public List<Abbonamento> findAbbonamentiPerTessera(Long tesseraId) {
        return em.createNamedQuery("perTessera", Abbonamento.class)
                .setParameter("tessera", tesseraId)
                .getResultList();
    }

    // Aggiorna un Biglietto
    public void update(Biglietto oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    // Elimina un Biglietto
    public void delete(Biglietto oggetto) {
        em.getTransaction().begin();
        em.remove(em.contains(oggetto) ? oggetto : em.merge(oggetto));
        em.getTransaction().commit();
    }
}
