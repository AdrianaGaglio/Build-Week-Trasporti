package epicode.it.servizi.gestore_stati_servizio;

import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.dao.stato_mezzo.ServizioDAO;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.stato_mezzo.Manutenzione;
import epicode.it.entities.stato_mezzo.Servizio;
import jakarta.persistence.EntityManager;

import java.util.List;

public class StatisticheMezzo {

    private EntityManager em;

    public StatisticheMezzo(EntityManager em) {
        this.em = em;
    }

    public List<Servizio> periodiServizio(Mezzo mezzo) {
        ServizioDAO servizioDAO = new ServizioDAO(em);
        List<Servizio> servizi = servizioDAO.servizi(mezzo);
        return servizi;
    }

    public List<Manutenzione> periodiManutenzione(Mezzo mezzo) {
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
        List<Manutenzione> manutenzioni = manutenzioneDAO.manutenzioni(mezzo);
        return manutenzioni;
    }
}
