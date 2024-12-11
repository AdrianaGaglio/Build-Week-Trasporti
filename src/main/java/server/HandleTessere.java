package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleTessere implements HttpHandler {

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
        TesseraDAO dao = new TesseraDAO(em);

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
            Tessera tessera = dao.getById(id);
            if (tessera == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }

            // Elimina la Tratta
            em.getTransaction().begin();
            dao.delete(tessera);
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
        TesseraDAO tesseraDAO = new TesseraDAO(em);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);

            // Creazione di una nuova Tessera
            Tessera tessera = new Tessera();
            tessera.setValidita(LocalDateTime.parse((String) requestData.get("validita")));

            // Verifica se è associata a un utente
            Map<String, Object> utenteData = (Map<String, Object>) requestData.get("utente");
            if (utenteData != null) {
                Long utenteId = Long.parseLong(utenteData.get("id").toString());
                UtenteDAO utenteDAO = new UtenteDAO(em);
                Utente utente = utenteDAO.getById(utenteId);

                if (utente != null) {
                    tessera.setUtente(utente);
                    utente.setTessera(tessera);
                    utenteDAO.update(utente);
                } else {
                    exchange.sendResponseHeaders(404, -1); // Utente non trovato
                    return;
                }
            }

            // Salva la Tessera
            tesseraDAO.save(tessera);

            // Risposta al client
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
        RivenditoreDAO dao = new RivenditoreDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        Rivenditore rivenditore = dao.findById(id);
        if (rivenditore == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }

        if (rivenditore instanceof RivFisico) {
            RivFisico rivFisico = (RivFisico) rivenditore;
            rivFisico.setGiornoChiusura(DayOfWeek.valueOf((String) requestData.get("giornoChiusura")));
            rivFisico.setOraApertura(Time.valueOf((String) requestData.get("oraApertura")));
            rivFisico.setOraChiusura(Time.valueOf((String) requestData.get("oraChiusura")));
            dao.update(rivFisico);
        } else if (rivenditore instanceof RivAutomatico) {
            RivAutomatico rivAutomatico = (RivAutomatico) rivenditore;
            rivAutomatico.setAttivo((Boolean) requestData.get("attivo"));
            dao.update(rivAutomatico);
        }

        em.close();
        exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        TesseraDAO dao = new TesseraDAO(em);

        // Recupera tutti i rivenditori
        List<Tessera> tessere = dao.getAll();
        em.close();

        // Converti la lista in JSON manualmente
        String jsonResponse = tessere.stream()
                .map(t -> {
                    return String.format(
                            "{\"id\":%d,\"codice\":\"%s\",\"validita\":\"%s\",\"utente\":%s}",
                            t.getId(),
                            t.getCodice(),
                            t.getValidita() != null ? t.getValidita().toString() : null,
                            t.getUtente() != null ? String.format("{\"id\":%d,\"nome\":\"%s\"}", t.getUtente().getId(), t.getUtente().getNome()) : null
                    );
                })
                .collect(Collectors.joining(",", "[", "]"));



        // Restituisci la risposta
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}
