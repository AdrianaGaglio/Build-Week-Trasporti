package epicode.it.dao.biglietto;

import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.tessera.Tessera;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.util.List;


@AllArgsConstructor
public class AbbonamentoDAO {
    private EntityManager em;

    public void save(Abbonamento abbonamento) {
        em.getTransaction().begin();
        em.persist(abbonamento);
        em.getTransaction().commit();
    }

    public Abbonamento findById(Long id) {
        return em.find(Abbonamento.class, id);
    }

    public List<Abbonamento> findAll() {
        return em.createNamedQuery("Trova_tutto_Abbonamento", Abbonamento.class).getResultList();
    }

    public void update(Abbonamento abbonamento) {
        em.getTransaction().begin();
        em.merge(abbonamento);
        em.getTransaction().commit();
    }

    public void delete(Abbonamento abbonamento) {
        em.getTransaction().begin();
        em.remove(abbonamento);
        em.getTransaction().commit();
    }

    public List<Abbonamento> perTessera(Tessera tessera) {
        return em.createNamedQuery("perTessera", Abbonamento.class)
                .setParameter("tessera", tessera).getResultList();
    }
}
