package epicode.it.servizi.gestore_rivenditori_e_biglietti;

import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

public class RinnovoAbbonamento {

    private EntityManager em;

    public RinnovoAbbonamento(EntityManager em) {
        this.em = em;
    }

    public void rinnova(Tessera tessera, Periodicy periodicy) {
        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
        TesseraDAO tesseraDAO = new TesseraDAO(em);
        List<Abbonamento> abbonamenti = abbonamentoDAO.perTessera(tessera);
        if(abbonamenti.size() > 0) {
            if(abbonamenti.getLast().getScadenza().isBefore(LocalDateTime.now())) {
                Abbonamento abbonamento = new Abbonamento();
                abbonamento.setEmissione(LocalDateTime.now());
                abbonamento.setPeriodicy(periodicy);
                abbonamento.setTessera(tessera);
                abbonamento.setAttivo(true);
                abbonamentoDAO.save(abbonamento);
                tessera.getAbbonamenti().add(abbonamento);
                tesseraDAO.update(tessera);
            }
        }

    }
}
