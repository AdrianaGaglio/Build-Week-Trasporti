package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.entities.rivenditore.Rivenditore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;

import java.sql.Time;
import java.time.DayOfWeek;



public class HandleRivenditori implements HttpHandler {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Per il parsing JSON

    @Override
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

    private void handleDelete(HttpExchange exchange) {
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        RivenditoreDAO dao = new RivenditoreDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        String tipo = (String) requestData.get("tipo");

        if ("fisico".equalsIgnoreCase(tipo)) {
            // Creazione di un rivenditore fisico
            RivFisico rivFisico = new RivFisico();
            rivFisico.setGiornoChiusura(DayOfWeek.valueOf((String) requestData.get("giornoChiusura")));
            rivFisico.setOraApertura(Time.valueOf((String) requestData.get("oraApertura")));
            rivFisico.setOraChiusura(Time.valueOf((String) requestData.get("oraChiusura")));
            dao.save(rivFisico);
        } else if ("automatico".equalsIgnoreCase(tipo)) {
            // Creazione di un rivenditore automatico
            RivAutomatico rivAutomatico = new RivAutomatico();
            rivAutomatico.setAttivo((Boolean) requestData.get("attivo"));
            dao.save(rivAutomatico);
        } else {
            exchange.sendResponseHeaders(400, -1); // Tipo non valido
            em.close();
            return;
        }

        em.close();
        exchange.sendResponseHeaders(201, -1); // Creato con successo
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
        RivenditoreDAO dao = new RivenditoreDAO(em);

        // Recupera tutti i rivenditori
        List<Rivenditore> rivenditori = dao.findAll();
        em.close();

        // Converti la lista in JSON manualmente
        String jsonResponse = rivenditori.stream()
                .map(r -> String.format("{\"id\":%d,\"tipo\":\"%s\"}", r.getId(), r.getClass().getSimpleName()))
                .collect(Collectors.joining(",", "[", "]"));

        // Restituisci la risposta
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}
