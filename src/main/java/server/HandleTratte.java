package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleTratte implements HttpHandler {

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
        TrattaDAO trattaDAO = new TrattaDAO(em);

        try {
            // Estrai l'ID dal percorso
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1); // ID non fornito
                return;
            }

            Long trattaId = Long.parseLong(pathParts[2]);
            System.out.println(trattaId);

            // Verifica se la Tratta esiste
            Tratta tratta = trattaDAO.getById(trattaId);
            if (tratta == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }

            // Elimina la Tratta
            em.getTransaction().begin();
            trattaDAO.delete(tratta);
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
        TrattaDAO trattaDAO = new TrattaDAO(em);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);

            // Creazione di una nuova tratta
            Tratta tratta = new Tratta();
            tratta.setPartenza((String) requestData.get("partenza"));
            tratta.setCapolinea((String) requestData.get("capolinea"));

            // Parsing della durata (in minuti o in formato compatibile con LocalTime)
            String durataString = (String) requestData.get("durata");
            tratta.setDurata(LocalTime.parse(durataString));

            // Salva la nuova tratta nel database
            trattaDAO.save(tratta);

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
        TrattaDAO dao = new TrattaDAO(em);

        try {
            // Estrai l'ID dal percorso
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1); // ID non fornito
                return;
            }

            Long id = Long.parseLong(pathParts[2]);

            // Recupera la Tratta dal database
            Tratta tratta = dao.getById(id);
            if (tratta == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }

            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);

            // Aggiorna i campi della Tratta
            tratta.setPartenza((String) requestData.get("partenza"));
            tratta.setCapolinea((String) requestData.get("capolinea"));

            // Parsing della durata
            String durataString = (String) requestData.get("durata");
            tratta.setDurata(LocalTime.parse(durataString));

            // Salva le modifiche nel database
            em.getTransaction().begin();
            em.merge(tratta);
            em.getTransaction().commit();

            exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
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


    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        TrattaDAO dao = new TrattaDAO(em);

        // Recupera tutti i rivenditori
        List<Tratta> tratte = dao.getAll();
        em.close();

        // Converti la lista in JSON manualmente
        String jsonResponse = tratte.stream()
                .map(t -> {
                    return String.format(
                            "{\"id\":%d,\"partenza\":\"%s\",\"capolinea\":\"%s\",\"durata\":\"%s\"}",
                            t.getId(),
                            t.getPartenza(),
                            t.getCapolinea(),
                            t.getDurata().toString()
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
