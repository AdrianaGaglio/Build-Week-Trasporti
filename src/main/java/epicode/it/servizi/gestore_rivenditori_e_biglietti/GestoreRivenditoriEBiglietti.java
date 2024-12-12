package epicode.it.servizi.gestore_rivenditori_e_biglietti;

import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;

import java.sql.Time;
import java.time.*;
import java.util.List;

public class GestoreRivenditoriEBiglietti {
    private EntityManager em;
    private RivenditoreDAO rivenditoreDAO;

    public GestoreRivenditoriEBiglietti(EntityManager em) {
        this.em = em;
        this.rivenditoreDAO  = new RivenditoreDAO(em);
    }



    public void creaRivenditoreFisico(DayOfWeek dayOfWeek, LocalTime apertura, LocalTime chiusura) {
        RivFisico r = new RivFisico();
        r.setGiornoChiusura(dayOfWeek);
        r.setOraApertura(apertura);
        r.setOraChiusura(chiusura);
        r.setTipo("RivFisico");
        rivenditoreDAO.save(r);
        System.out.println("Rivenditore fisico creato!");
    }

    public void creaRivenditoreAutomatico() {
        RivAutomatico r = new RivAutomatico();
        r.setAttivo(true);
        r.setTipo("RivAutomatico");
        rivenditoreDAO.save(r);
        System.out.println("Rivenditore automatico creato!");
    }


    public List<Rivenditore> visualizzaRivenditori () {
        return rivenditoreDAO.findAll();
    }

    public Rivenditore richiamaRivenditore(long i){
        return rivenditoreDAO.findById(i);
    }


    //  aggiungi  biglietto
    public void creaGiornaliero(Rivenditore r, Tratta tratta) {
        if (r instanceof RivFisico) {
            RivFisico rivFisico = (RivFisico) r;
            if (
                    !LocalDate.now().getDayOfWeek().equals(rivFisico.getGiornoChiusura()) &&
                            LocalTime.now().isAfter(rivFisico.getOraApertura()) &&
                            LocalTime.now().isBefore(rivFisico.getOraChiusura())
            ) {
                creaGiornalieroTemplate(r, tratta);
                System.out.println("Biglietto creato!");
            } else {
                System.err.println("Il rivenditore fisico è chiuso");
            }
        } else if (r instanceof RivAutomatico) {
            RivAutomatico rivAuto = (RivAutomatico) r;
            if (rivAuto.isAttivo()) {
                creaGiornalieroTemplate(r, tratta);
                System.out.println("Biglietto creato!");
            } else {
                System.err.println("Rivenditore automatico fuori servizio");
            }
        }
    }

    private void creaGiornalieroTemplate(Rivenditore r, Tratta tratta) {
        Giornaliero biglietto = new Giornaliero();
        biglietto.setDaAttivare(true);
        biglietto.setTratta(tratta);
        biglietto.setScadenza(null);
        biglietto.setRivenditore(r);
        r.getBiglietti().add(biglietto);

        BigliettoDAO bigliettoDAO = new BigliettoDAO(em);
        bigliettoDAO.save(biglietto);
        rivenditoreDAO.update(r);
    }

    public void creaAbbonamento(Rivenditore r, Periodicy periodicy, Utente utente) {
        if (r instanceof RivFisico) {
            RivFisico rivFisico = (RivFisico) r;
            if (
                    !LocalDate.now().getDayOfWeek().equals(rivFisico.getGiornoChiusura()) &&
                            LocalTime.now().isAfter(rivFisico.getOraApertura()) &&
                            LocalTime.now().isBefore(rivFisico.getOraChiusura())
            ) {
                creaAbbonamentoTemplate(r, periodicy, utente);
            } else {
                System.err.println("Il rivenditore fisico è chiuso");
            }
        } else if (r instanceof RivAutomatico) {
            RivAutomatico rivAuto = (RivAutomatico) r;
            if (rivAuto.isAttivo()) {
                creaAbbonamentoTemplate(r, periodicy, utente);
            } else {
                System.err.println("Rivenditore automatico fuori servizio");
            }
        }
    }


    private void creaAbbonamentoTemplate(Rivenditore r, Periodicy periodicy, Utente utente) {
        if (utente.getTessera() != null) {
            // Controlla se la lista degli abbonamenti è vuota
            if (utente.getTessera().getAbbonamenti().isEmpty()) {
                System.out.println("Non ci sono abbonamenti precedenti. Creazione del primo abbonamento...");
                Abbonamento a = new Abbonamento();
                a.setPeriodicy(periodicy);
                a.setScadenza(LocalDateTime.now().plusDays(switch (periodicy) {
                    case annuale -> 365;
                    case bimestrale -> 60;
                    case mensile -> 30;
                    case settimanale -> 7;
                    case trimestrale -> 90;
                }));
                a.setAttivo(true);
                a.setTessera(utente.getTessera());
                utente.getTessera().getAbbonamenti().add(a);
                r.getBiglietti().add(a);
                a.setRivenditore(r);
                BigliettoDAO bigliettoDAO = new BigliettoDAO(em);
                bigliettoDAO.save(a);
                rivenditoreDAO.update(r);
                System.out.println("Abbonamento creato!");




            } else {
                // Controlla la scadenza dell'ultimo abbonamento
                utente.getTessera().getAbbonamenti().stream()
                        .filter(abbonamento -> abbonamento.getScadenza().isBefore(LocalDateTime.now()))
                        .forEach(abbonamento -> abbonamento.setAttivo(false));
                Abbonamento abbonamentoAttivo = utente.getTessera().getAbbonamenti().stream()
                        .filter(abbonamento -> abbonamento.isAttivo() && abbonamento.getScadenza().isAfter(LocalDateTime.now()))
                        .findFirst()
                        .orElse(null);
                if (abbonamentoAttivo == null) {
                    Abbonamento a = new Abbonamento();
                    a.setPeriodicy(periodicy);
                    a.setScadenza(LocalDateTime.now().plusDays(switch (periodicy) {
                        case annuale -> 365;
                        case bimestrale -> 60;
                        case mensile -> 30;
                        case settimanale -> 7;
                        case trimestrale -> 90;
                    }));
                    a.setAttivo(true);
                    a.setTessera(utente.getTessera());
                    utente.getTessera().getAbbonamenti().add(a);
                    r.getBiglietti().add(a);
                    a.setRivenditore(r);
                    BigliettoDAO bigliettoDAO = new BigliettoDAO(em);
                    bigliettoDAO.save(a);
                    rivenditoreDAO.update(r);
                    System.out.println("Abbonamento creato!");
                } else {
                    System.err.println("Hai già un abbonamento attivo con scadenza: " + abbonamentoAttivo.getScadenza());
                }
            }
        } else {
            System.err.println("Devi prima creare la tessera per fare l'abbonamento!");
        }
    }

}