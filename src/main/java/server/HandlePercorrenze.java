package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.percorrenza.PercorrenzaDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.tratta.Tratta;
import epicode.it.servizi.gestore_percorrenze.GestorePercorrenze;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandlePercorrenze implements HttpHandler {

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
        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);

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
            Percorrenza percorrenza = percorrenzaDAO.getById(id);
            if (percorrenza == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }

            // Elimina la Tratta
            em.getTransaction().begin();
            percorrenzaDAO.delete(percorrenza);
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
        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);

            // Creazione di una nuova Percorrenza
            GestorePercorrenze gestore = new GestorePercorrenze(em);
            LocalDate date = LocalDate.parse((String) requestData.get("data"));
            LocalTime time = LocalTime.parse((String) requestData.get("time"));
            gestore.aggiungiPercorrenza((Mezzo) requestData.get("mezzo"), LocalDateTime.of(date, time),(Tratta) requestData.get("tratta"));

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
        PercorrenzaDAO dao = new PercorrenzaDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        Percorrenza percorrenza = dao.getById(id);
        if (percorrenza == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }

        percorrenza.setTratta((Tratta) requestData.get("tratta"));
        percorrenza.setMezzo((Mezzo) requestData.get("mezzo"));
        percorrenza.setDurata_effettiva(LocalTime.parse((String) requestData.get("durata")));
        dao.update(percorrenza);
        em.close();
        exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);

        // Recupera tutti i rivenditori
        List<Percorrenza> percorrenze = percorrenzaDAO.getAll();
        em.close();

        String jsonResponse = percorrenze.stream()
                .map(p -> {
                    // Controllo se Tratta è null
                    String trattaJson = p.getTratta() != null
                            ? String.format("{\"id\":%d,\"partenza\":\"%s\",\"capolinea\":\"%s\"}",
                            p.getTratta().getId(),
                            p.getTratta().getPartenza(),
                            p.getTratta().getCapolinea())
                            : null;

                    // Controllo se Mezzo è null
                    String mezzoJson = p.getMezzo() != null
                            ? String.format("{\"id\":%d,\"tipo\":\"%s\"}",
                            p.getMezzo().getId(),
                            p.getMezzo().getClass().getSimpleName())
                            : null;

                    // Creazione del JSON completo della Percorrenza
                    return String.format(
                            "{\"id\":%d,\"tratta\":%s,\"mezzo\":%s,\"durata_effettiva\":\"%s\",\"data\":\"%s\"}",
                            p.getId(),
                            trattaJson,
                            mezzoJson,
                            p.getDurata_effettiva() != null ? p.getDurata_effettiva().toString() : null,
                            p.getData() != null ? p.getData().toString() : null
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
