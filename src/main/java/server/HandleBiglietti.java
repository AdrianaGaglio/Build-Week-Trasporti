package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.entities.biglietto.Abbonamento;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.biglietto.Giornaliero;
import epicode.it.entities.biglietto.Periodicy;
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

public class HandleBiglietti implements HttpHandler {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
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

    private void handleDelete(HttpExchange exchange) {
        // Implementazione per il DELETE
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        BigliettoDAO dao = new BigliettoDAO(em);

        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        String tipo = (String) requestData.get("tipo");

        if ("giornaliero".equalsIgnoreCase(tipo)) {
            Giornaliero giornaliero = new Giornaliero();
            giornaliero.setDaAttivare((Boolean) requestData.get("daAttivare"));
            // Associazioni (opzionali) con Tratta e Mezzo
            dao.save(giornaliero);
        } else if ("abbonamento".equalsIgnoreCase(tipo)) {
            Abbonamento abbonamento = new Abbonamento();
            abbonamento.setAttivo((Boolean) requestData.get("attivo"));
            abbonamento.setTariffa((String) requestData.get("tariffa"));
            abbonamento.setPeriodicy(Periodicy.valueOf((String) requestData.get("periodicy")));
            // Associazioni con Tessera o Tratta
            dao.save(abbonamento);
        } else {
            exchange.sendResponseHeaders(400, -1);
            em.close();
            return;
        }

        em.close();
        exchange.sendResponseHeaders(201, -1);
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        BigliettoDAO dao = new BigliettoDAO(em);

        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        Biglietto biglietto = dao.findById(id);
        if (biglietto == null) {
            exchange.sendResponseHeaders(404, -1);
            em.close();
            return;
        }

        if (biglietto instanceof Giornaliero) {
            Giornaliero giornaliero = (Giornaliero) biglietto;
            giornaliero.setDaAttivare((Boolean) requestData.get("daAttivare"));
            // Aggiornamenti per relazioni opzionali
            dao.update(giornaliero);
        } else if (biglietto instanceof Abbonamento) {
            Abbonamento abbonamento = (Abbonamento) biglietto;
            abbonamento.setAttivo((Boolean) requestData.get("attivo"));
            abbonamento.setTariffa((String) requestData.get("tariffa"));
            abbonamento.setPeriodicy(Periodicy.valueOf((String) requestData.get("periodicy")));
            // Aggiornamenti per relazioni opzionali
            dao.update(abbonamento);
        }

        em.close();
        exchange.sendResponseHeaders(200, -1);
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        BigliettoDAO dao = new BigliettoDAO(em);

        List<Biglietto> biglietti = dao.findAll();
        System.out.println(biglietti);
        em.close();

        String jsonResponse = biglietti.stream()
                .map(b -> {
                    if (b instanceof Giornaliero) {
                        Giornaliero giornaliero = (Giornaliero) b;
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"codice\":\"%s\",\"daAttivare\":%b}",
                                giornaliero.getId(),
                                giornaliero.getClass().getSimpleName(),
                                giornaliero.getCodice(),
                                giornaliero.isDaAttivare()
                        );
                    } else if (b instanceof Abbonamento) {
                        Abbonamento abbonamento = (Abbonamento) b;
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"codice\":\"%s\",\"attivo\":%b,\"tariffa\":\"%s\",\"periodicy\":\"%s\"}",
                                abbonamento.getId(),
                                abbonamento.getClass().getSimpleName(),
                                abbonamento.getCodice(),
                                abbonamento.isAttivo(),
                                abbonamento.getTariffa(),
                                abbonamento.getPeriodicy()
                        );
                    } else {
                        return String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"codice\":\"%s\"}",
                                b.getId(),
                                b.getClass().getSimpleName(),
                                b.getCodice()
                        );
                    }
                })
                .collect(Collectors.joining(",", "[", "]"));

        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}
