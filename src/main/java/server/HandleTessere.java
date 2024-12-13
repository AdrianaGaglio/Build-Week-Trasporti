package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import epicode.it.servizi.gestire_tessera.GestioneTessera;
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

public class HandleTessere implements HttpHandler {

    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
    private static final ObjectMapper objectMapper = configureObjectMapper();

    private static ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

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
        TesseraDAO dao = new TesseraDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);

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

            Utente utente = utenteDAO.getById(tessera.getUtente().getId());
            utente.setTessera(null);

            em.getTransaction().begin();
            em.remove(tessera);
            em.merge(utente);
            em.getTransaction().commit();

            // Risposta al client
            exchange.sendResponseHeaders(200, -1); // Eliminazione riuscita
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore per debugging
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            exchange.sendResponseHeaders(500, -1); // Errore interno
        } finally {
            em.close(); // Assicurati di chiudere l'EntityManager
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        TesseraDAO tesseraDAO = new TesseraDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);

            long utenteId = Long.parseLong(requestData.get("utenteId").toString());
            Utente utente = utenteDAO.getById(utenteId);

            if (utente.getTessera() == null || utente.getTessera().getValidita().isBefore(LocalDateTime.now())) {
                Tessera tessera = new Tessera();
                tessera.setValidita(LocalDateTime.now().plusYears(1));
                utente.setTessera(tessera);
                tessera.setUtente(utente);

                em.getTransaction().begin();
                em.persist(tessera);
                em.merge(utente);
                em.getTransaction().commit();

                // Serializza la tessera creata come JSON e invia la risposta
                String jsonResponse = objectMapper.writeValueAsString(tessera);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, jsonResponse.getBytes().length);
                exchange.getResponseBody().write(jsonResponse.getBytes());
            } else {
                exchange.sendResponseHeaders(400, -1); // Nessuna tessera creata o aggiornata
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Errore interno del server
        } finally {
            em.close();
        }
    }



    private void handlePut(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        TesseraDAO dao = new TesseraDAO(em);

        try {
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
            Long id = Long.parseLong(requestData.get("id").toString());

            Tessera tessera = dao.getById(id);
            if (tessera == null) {
                exchange.sendResponseHeaders(404, -1); // Tessera non trovata
                em.close();
                return;
            }

            LocalDate validita = LocalDate.parse(requestData.get("validita").toString());
            if (validita.isBefore(LocalDate.now())) {
                tessera.setValidita(LocalDateTime.now().plusYears(1));
                em.getTransaction().begin();
                em.merge(tessera);
                em.getTransaction().commit();
            }

            String jsonResponse = objectMapper.writeValueAsString(tessera);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            exchange.getResponseBody().write(jsonResponse.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        } finally {
            em.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        TesseraDAO dao = new TesseraDAO(em);

        try {
            // Recupera tutte le tessere
            List<Tessera> tessere = dao.getAll();

            // Serializza la lista delle tessere in JSON
            String jsonResponse = objectMapper.writeValueAsString(tessere);

            // Imposta gli header e restituisce la risposta
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log dell'errore per debugging
            exchange.sendResponseHeaders(500, -1); // Errore interno del server
        } finally {
            em.close(); // Assicurati di chiudere l'EntityManager
        }
    }

}