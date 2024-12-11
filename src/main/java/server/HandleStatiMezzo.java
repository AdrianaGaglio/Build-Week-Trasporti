package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.stato_mezzo.ManutenzioneDAO;
import epicode.it.dao.stato_mezzo.ServizioDAO;
import epicode.it.dao.stato_mezzo.StatoMezzoDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.stato_mezzo.Manutenzione;
import epicode.it.entities.stato_mezzo.Servizio;
import epicode.it.entities.stato_mezzo.StatoMezzo;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import epicode.it.servizi.gestore_stati_servizio.GestoreStatiServizio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleStatiMezzo implements HttpHandler {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Per il parsing JSON

    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1); // Nessun contenuto
            return;
        }

        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                System.out.println("arrivato fin qui");
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Metodo non consentito
        }


    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        StatoMezzoDAO statoMezzoDAO = new StatoMezzoDAO(em);

        try {
            // Estrai l'ID dal percorso
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1); // ID non fornito
                return;
            }

            Long id = Long.parseLong(pathParts[2]);
            System.out.println(id);

            // Verifica se la Tratta esiste
            StatoMezzo statoMezzo = statoMezzoDAO.findById(id);
            if (statoMezzo == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }

            // Elimina lo stato
            em.getTransaction().begin();
            statoMezzoDAO.update(statoMezzo);
            em.getTransaction().commit();

            // Risposta al client
            exchange.sendResponseHeaders(200, -1); // Eliminazione riuscita
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore per debugging
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            exchange.sendResponseHeaders(500, -1); // Errore interno del server
        } finally {
            em.close(); // Assicurati di chiudere l'EntityManager
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        StatoMezzoDAO statoMezzoDAO = new StatoMezzoDAO(em);
        ServizioDAO servizioDAO = new ServizioDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
            String tipo = (String) requestData.get("tipo");

            GestoreStatiServizio gestore = new GestoreStatiServizio(em);

            if ("servizio".equalsIgnoreCase(tipo)) {
                // Creazione di un oggetto Servizio
                gestore.aggiungiServizio((Mezzo) requestData.get("mezzo"),
                        Date.valueOf((String) requestData.get("dataInizio")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                        Date.valueOf((String) requestData.get("dataFine")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                        (Tratta) requestData.get("tratta"));

            } else if ("manutenzione".equalsIgnoreCase(tipo)) {
                gestore.aggiungiManutenzione((Mezzo) requestData.get("mezzo"),
                        Date.valueOf((String) requestData.get("dataInizio")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                        (String) requestData.get("descrizione"));

            } else {
                exchange.sendResponseHeaders(400, -1); // Tipo non valido
                return;
            }

            exchange.sendResponseHeaders(201, -1); // Creato con successo
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore per debugging
            exchange.sendResponseHeaders(400, -1); // Errore nella richiesta
        } finally {
            em.close(); // Assicurati di chiudere l'EntityManager
        }
    }


    private void handlePut(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        StatoMezzoDAO dao = new StatoMezzoDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
        ServizioDAO servizioDAO = new ServizioDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        StatoMezzo statoMezzo = dao.findById(id);
        if (statoMezzo == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }
        if (statoMezzo instanceof Servizio) {
            Servizio servizio = (Servizio) statoMezzo;
            servizio.setDataFine(Date.valueOf((String) requestData.get("dataFine")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            Mezzo mezzo = servizio.getMezzo();
            MezzoDAO mezzoDAO = new MezzoDAO(em);
            mezzo.setStato(Stato.valueOf((String) requestData.get("statoMezzo")));
            servizio.setTratta((Tratta) requestData.get("tratta"));
            servizio.setDataInizio(Date.valueOf((String) requestData.get("dataInizio")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            dao.update(servizio);
            mezzoDAO.update(mezzo);
        } else if (statoMezzo instanceof Manutenzione) {
            Manutenzione manutenzione = (Manutenzione) statoMezzo;
            manutenzione.setDataFine(Date.valueOf((String) requestData.get("dataFine")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            manutenzione.setDescrizione((String) requestData.get("descrizione"));
            manutenzione.setMezzo((Mezzo) requestData.get("mezzo"));
            manutenzione.setDataInizio(Date.valueOf((String) requestData.get("dataInizio")).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            Mezzo mezzo = manutenzione.getMezzo();
            MezzoDAO mezzoDAO = new MezzoDAO(em);
            mezzo.setStato(Stato.valueOf((String) requestData.get("statoMezzo")));
            manutenzioneDAO.update(manutenzione);
            mezzoDAO.update(mezzo);
        }

        em.close();
        exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        StatoMezzoDAO dao = new StatoMezzoDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em);
        ServizioDAO servizioDAO = new ServizioDAO(em);

        // Recupera tutti i rivenditori
        List<StatoMezzo> statiServizio = dao.findAll();
        em.close();

        // Converti la lista in JSON manualmente
        String jsonResponse = statiServizio.stream()
                .map(s -> {
                    if (s instanceof Servizio) {
                        Servizio servizio = (Servizio) s;
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"tratta\":{\"id\":%d,\"partenza\":\"%s\",\"capolinea\":\"%s\"},\"dataInizio\":\"%s\",\"dataFine\":\"%s\",\"mezzo\":{\"id\":%d,\"tipo\":\"%s\"}}",
                                servizio.getId(),
                                servizio.getClass().getSimpleName(),
                                servizio.getTratta() != null ? servizio.getTratta().getId() : null,
                                servizio.getTratta() != null ? servizio.getTratta().getPartenza() : null,
                                servizio.getTratta() != null ? servizio.getTratta().getCapolinea() : null,
                                servizio.getDataInizio() != null ? servizio.getDataInizio().toString() : null,
                                servizio.getDataFine() != null ? servizio.getDataFine().toString() : null,
                                servizio.getMezzo() != null ? servizio.getMezzo().getId() : null,
                                servizio.getMezzo() != null ? servizio.getMezzo().getClass().getSimpleName() : null
                        );
                    } else if (s instanceof Manutenzione) {
                        Manutenzione manutenzione = (Manutenzione) s;
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"descrizione\":\"%s\",\"dataInizio\":\"%s\",\"dataFine\":\"%s\",\"mezzo\":{\"id\":%d,\"tipo\":\"%s\"}}",
                                manutenzione.getId(),
                                manutenzione.getClass().getSimpleName(),
                                manutenzione.getDescrizione(),
                                manutenzione.getDataInizio() != null ? manutenzione.getDataInizio().toString() : null,
                                manutenzione.getDataFine() != null ? manutenzione.getDataFine().toString() : null,
                                manutenzione.getMezzo() != null ? manutenzione.getMezzo().getId() : null,
                                manutenzione.getMezzo() != null ? manutenzione.getMezzo().getClass().getSimpleName() : null
                        );
                    } else {
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"dataInizio\":\"%s\",\"dataFine\":\"%s\",\"mezzo\":{\"id\":%d,\"tipo\":\"%s\"}}",
                                s.getId(),
                                s.getClass().getSimpleName(),
                                s.getDataInizio() != null ? s.getDataInizio().toString() : null,
                                s.getDataFine() != null ? s.getDataFine().toString() : null,
                                s.getMezzo() != null ? s.getMezzo().getId() : null,
                                s.getMezzo() != null ? s.getMezzo().getClass().getSimpleName() : null
                        );
                    }
                })
                .collect(Collectors.joining(",", "[", "]"));



        // Restituisci la risposta
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}
