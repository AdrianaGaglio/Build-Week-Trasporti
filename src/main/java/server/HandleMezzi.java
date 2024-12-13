package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.mezzo.AutobusDAO;
import epicode.it.dao.mezzo.MezzoDAO;
import epicode.it.dao.mezzo.TramDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tratta.TrattaDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
import epicode.it.entities.mezzo.Autobus;
import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.mezzo.Stato;
import epicode.it.entities.mezzo.Tram;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleMezzi implements HttpHandler {

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
        MezzoDAO mezzoDAO = new MezzoDAO(em);

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
            Mezzo mezzo = mezzoDAO.findById(id);
            if (mezzo == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }


            mezzoDAO.delete(mezzo);


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
        try {
            MezzoDAO mezzoDAO = new MezzoDAO(em);
            AutobusDAO autobusDAO = new AutobusDAO(em);
            TramDAO tramDAO = new TramDAO(em);

            // Leggi il corpo della richiesta
            MezzoRequest requestData = objectMapper.readValue(exchange.getRequestBody(), MezzoRequest.class);
            if (requestData.tipo == null || requestData.codice == null) {
                exchange.sendResponseHeaders(400, -1); // Richiesta non valida
                return;
            }

            Object createdObject;

            if ("autobus".equalsIgnoreCase(requestData.tipo)) {
                Autobus autobus = new Autobus(requestData.codice);
                autobus.setStato(Stato.DEPOSITO);
                em.getTransaction().begin();
                em.persist(autobus);
                em.getTransaction().commit();
                createdObject = autobus;
            } else if ("tram".equalsIgnoreCase(requestData.tipo)) {
                Tram tram = new Tram(requestData.codice);
                tram.setStato(Stato.DEPOSITO);
                em.getTransaction().begin();
                em.persist(tram);
                em.getTransaction().commit();
                createdObject = tram;
            } else {
                exchange.sendResponseHeaders(400, -1); // Tipo non valido
                return;
            }

            // Crea un nuovo ObjectMapper
            ObjectMapper objectMapper2 = new ObjectMapper();
            objectMapper2.registerModule(new JavaTimeModule());

            // Salva l'Utente


            if (createdObject != null) {
                String jsonResponse = objectMapper2.writeValueAsString(createdObject);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(201, jsonResponse.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(jsonResponse.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(400, -1); // Errore generico
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            exchange.sendResponseHeaders(500, -1);
        } finally {
            em.close();
        }
    }

    static class MezzoRequest {
        public String tipo;
        public Integer codice;
    }


    private void handlePut(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        AutobusDAO autobusDAO = new AutobusDAO(em);
        TramDAO tramDAO = new TramDAO(em);

        // Configura l'ObjectMapper con il modulo JSR310
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
            Long id = Long.parseLong(requestData.get("id").toString());

            // Recupera il mezzo dal database
            Mezzo mezzo = mezzoDAO.findById(id);
            if (mezzo == null) {
                exchange.sendResponseHeaders(404, -1); // Mezzo non trovato
                return;
            }

            String jsonResponse;

            // Aggiorna i campi in base al tipo di mezzo
            em.getTransaction().begin();
            if (mezzo instanceof Autobus) {
                Autobus autobus = (Autobus) mezzo;
                autobus.setCodice((Integer) requestData.get("codice"));
                autobusDAO.update(autobus);
                jsonResponse = objectMapper.writeValueAsString(autobus);
            } else if (mezzo instanceof Tram) {
                Tram tram = (Tram) mezzo;
                tram.setCodice((Integer) requestData.get("codice"));
                tramDAO.update(tram);
                jsonResponse = objectMapper.writeValueAsString(tram);
            } else {
                exchange.sendResponseHeaders(400, -1); // Tipo di mezzo non supportato
                em.getTransaction().rollback();
                return;
            }
            em.getTransaction().commit();

            // Risposta positiva con JSON dell'oggetto aggiornato
            byte[] responseBytes = jsonResponse.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            exchange.sendResponseHeaders(500, -1); // Errore interno del server
        } finally {
            em.close();
        }
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        AutobusDAO autobusDAO = new AutobusDAO(em);
        TramDAO tramDAO = new TramDAO(em);

        // Recupera tutti i rivenditori
        List<Mezzo> mezzi = mezzoDAO.findAll();
        em.close();

        // Converti la lista in JSON manualmente
        String jsonResponse = mezzi.stream()
                .map(m -> {
                    if (m instanceof Autobus) {
                        Autobus autobus = (Autobus) m;
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"capienza\":%d,\"stato\":\"%s\",\"codice\":%d}",
                                autobus.getId(),
                                autobus.getClass().getSimpleName(),
                                autobus.getCapienza(),
                                autobus.getStato(),
                                autobus.getCodice()
                        );
                    } else if (m instanceof Tram) {
                        Tram tram = (Tram) m;
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"capienza\":%d,\"stato\":\"%s\",\"codice\":%d}",
                                tram.getId(),
                                tram.getClass().getSimpleName(),
                                tram.getCapienza(),
                                tram.getStato(),
                                tram.getCodice()
                        );
                    } else {
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"capienza\":%d,\"stato\":\"%s\",\"codice\":%d}",
                                m.getId(),
                                m.getClass().getSimpleName(),
                                m.getCapienza(),
                                m.getStato(),
                                m.getCodice()
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
