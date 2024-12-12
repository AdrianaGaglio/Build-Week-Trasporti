package epicode.it.servizi.gestore_rivenditori_e_biglietti;

import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
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
    private TesseraDAO tesseraDAO;
    private TrattaDAO trattaDAO;
    private BigliettoDAO bigliettoDAO;

    public GestoreRivenditoriEBiglietti(EntityManager em) {
        this.em = em;
        this.rivenditoreDAO = new RivenditoreDAO(em);
        this.tesseraDAO = new TesseraDAO(em);
        this.trattaDAO = new TrattaDAO(em);
        this.bigliettoDAO = new BigliettoDAO(em);
    }


    public Rivenditore richiamaRivenditore(long id) {
        return rivenditoreDAO.findById(id);
    }

    public void rinnovaTessera(Rivenditore rivenditore, Utente utente) {
        if (rivenditore == null) {
            System.err.println("Rivenditore non valido.");
            return;
        }

        Tessera tessera = tesseraDAO.getTessera(utente);
        if (tessera != null) {
            tessera.setValidita(LocalDateTime.now().plusYears(1));
            tesseraDAO.update(tessera);
            System.out.println("Tessera rinnovata con successo!");
        } else {
            System.err.println("Tessera non trovata per l'utente.");
        }
    }

    public void rinnovaAbbonamento(Rivenditore rivenditore, Utente utente, Periodicy periodicy) {
        if (rivenditore == null) {
            System.err.println("Rivenditore non valido.");
            return;
        }

        Tessera tessera = tesseraDAO.getTessera(utente);
        if (tessera != null) {
            Abbonamento abbonamentoAttivo = tessera.getAbbonamenti().stream()
                    .filter(abbonamento -> abbonamento.isAttivo() && abbonamento.getScadenza().isAfter(LocalDateTime.now()))
                    .findFirst()
                    .orElse(null);

            if (abbonamentoAttivo != null) {
                abbonamentoAttivo.setScadenza(LocalDateTime.now().plusDays(switch (periodicy) {
                    case annuale -> 365;
                    case bimestrale -> 60;
                    case mensile -> 30;
                    case settimanale -> 7;
                    case trimestrale -> 90;
                }));
                abbonamentoAttivo.setAttivo(true);
                bigliettoDAO.update(abbonamentoAttivo);
                System.out.println("Abbonamento rinnovato con successo!");
            } else {
                System.err.println("Non ci sono abbonamenti attivi o validi da rinnovare.");
            }
        } else {
            System.err.println("Tessera non trovata per l'utente.");
        }
    }

    public void vendiBigliettoGiornaliero(Utente utente, Tratta tratta) {
        Rivenditore rivenditore = rivenditoreDAO.findAll().stream().findFirst().orElse(null);
        if (rivenditore == null) {
            System.err.println("Nessun rivenditore disponibile.");
            return;
        }

        Giornaliero biglietto = new Giornaliero();
        biglietto.setUtente(utente);
        biglietto.setTratta(tratta);
        biglietto.setEmissione(LocalDateTime.now());
        biglietto.setScadenza(LocalDateTime.now().plusHours(24));
        biglietto.setDaAttivare(true);
        biglietto.setCodice(generateUniqueBigliettoCode());

        bigliettoDAO.save(biglietto);
        System.out.println("Biglietto giornaliero venduto con successo! Codice: " + biglietto.getCodice());
    }

    public void creaGiornaliero(Rivenditore rivenditore, Tratta tratta) {
        if (rivenditore == null) {
            System.err.println("Rivenditore non valido.");
            return;
        }

        Giornaliero biglietto = new Giornaliero();
        biglietto.setTratta(tratta);
        biglietto.setEmissione(LocalDateTime.now());
        biglietto.setScadenza(LocalDateTime.now().plusHours(24));
        biglietto.setDaAttivare(true);
        biglietto.setCodice(generateUniqueBigliettoCode());

        bigliettoDAO.save(biglietto);
        System.out.println("Biglietto giornaliero creato con successo! Codice: " + biglietto.getCodice());
    }

    public void creaAbbonamento(Rivenditore rivenditore, Periodicy periodicy, Utente utente) {
        if (rivenditore == null) {
            System.err.println("Rivenditore non valido.");
            return;
        }

        Tessera tessera = tesseraDAO.getTessera(utente);
        if (tessera == null) {
            System.err.println("Tessera non trovata per l'utente.");
            return;
        }

        boolean hasActiveSubscription = tessera.getAbbonamenti().stream()
                .anyMatch(abb -> abb.isAttivo() && abb.getScadenza().isAfter(LocalDateTime.now()));

        if (hasActiveSubscription) {
            System.err.println("L'utente ha già un abbonamento attivo.");
            return;
        }

        Abbonamento abbonamento = new Abbonamento();
        abbonamento.setTessera(tessera);
        abbonamento.setEmissione(LocalDateTime.now());
        abbonamento.setScadenza(LocalDateTime.now().plusDays(switch (periodicy) {
            case annuale -> 365;
            case bimestrale -> 60;
            case mensile -> 30;
            case settimanale -> 7;
            case trimestrale -> 90;
        }));
        abbonamento.setAttivo(true);
        abbonamento.setCodice(generateUniqueBigliettoCode());

        bigliettoDAO.save(abbonamento);
        System.out.println("Abbonamento creato con successo! Codice: " + abbonamento.getCodice());
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

    public Tratta trovaTratta(String nome) {
        return em.createQuery("SELECT t FROM Tratta t WHERE t.partenza = :nome OR t.capolinea = :nome", Tratta.class)
                .setParameter("nome", nome)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void emettiAbbonamento(Utente utente, Periodicy periodicy) {
        Rivenditore rivenditore = rivenditoreDAO.findAll().stream().findFirst().orElse(null);
        if (rivenditore == null) {
            System.err.println("Nessun rivenditore disponibile.");
            return;
        }
        creaAbbonamento(rivenditore, periodicy, utente);
    }

    public void emettiTessera(Utente utente) {
        Tessera tessera = new Tessera();
        tessera.setUtente(utente);
        tessera.setValidita(LocalDateTime.now().plusYears(1));
        tessera.setCodice(generateUniqueBigliettoCode());
        tesseraDAO.save(tessera);
        System.out.println("Tessera emessa con successo! Codice: " + tessera.getCodice());
    }

    public void rinnovaAbbonamento(Utente utente, Periodicy periodicy) {
        Rivenditore rivenditore = rivenditoreDAO.findAll().stream().findFirst().orElse(null);
        if (rivenditore == null) {
            System.err.println("Nessun rivenditore disponibile.");
            return;
        }
        rinnovaAbbonamento(rivenditore, utente, periodicy);
    }

    public void rinnovaTessera(Utente utente) {
        Rivenditore rivenditore = rivenditoreDAO.findAll().stream().findFirst().orElse(null);
        if (rivenditore == null) {
            System.err.println("Nessun rivenditore disponibile.");
            return;
        }
        rinnovaTessera(rivenditore, utente);
    }

    private String generateUniqueBigliettoCode() {
        return "TKT" + System.currentTimeMillis();
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