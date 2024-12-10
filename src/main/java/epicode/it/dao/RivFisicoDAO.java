package epicode.it.dao;

import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.List;

@AllArgsConstructor
public class RivFisicoDAO {
    private EntityManager em;

    public void createRivFisico(DayOfWeek day, Time oraChiusura,Time oraApertura) {
        RivFisico r = new RivFisico();
        r.setGiornoChiusura(day);
        r.setOraChiusura(oraChiusura);
        r.setOraApertura(oraApertura);
        save(r);
    }

    public void save(RivFisico oggetto) {
        em.getTransaction().begin();
        em.persist(oggetto);
        em.getTransaction().commit();
    }

    public RivFisico findById(Long id) {
        return em.find(RivFisico.class, id);
    }

    public List<RivFisico> findAll() {
        return em.createNamedQuery("Trova_tutto_RivFisico", RivFisico.class).getResultList();
    }

    public void update(RivFisico oggetto) {
        em.getTransaction().begin();
        em.merge(oggetto);
        em.getTransaction().commit();
    }

    public void delete(RivFisico oggetto) {
        em.getTransaction().begin();
        em.remove(oggetto);
        em.getTransaction().commit();
    }


}