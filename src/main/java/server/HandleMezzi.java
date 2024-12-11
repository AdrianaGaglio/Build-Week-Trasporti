package server;

import com.fasterxml.jackson.databind.ObjectMapper;
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

            // Elimina la Tratta
            em.getTransaction().begin();
            mezzoDAO.delete(mezzo);
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
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        AutobusDAO autobusDAO = new AutobusDAO(em);
        TramDAO tramDAO = new TramDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        String tipo = (String) requestData.get("tipo");

        if ("autobus".equalsIgnoreCase(tipo)) {
            // Creazione di un rivenditore fisico
            Autobus autobus = new Autobus();
            autobus.setCodice((Integer) requestData.get("codice"));
            autobusDAO.save(autobus);
        } else if ("tram".equalsIgnoreCase(tipo)) {
            // Creazione di un rivenditore automatico
            Tram tram = new Tram();
            tram.setCodice((Integer) requestData.get("codice"));
            tramDAO.save(tram);
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
        MezzoDAO mezzoDAO = new MezzoDAO(em);
        AutobusDAO autobusDAO = new AutobusDAO(em);
        TramDAO tramDAO = new TramDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        Mezzo mezzo = mezzoDAO.findById(id);
        if (mezzo == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }

        if (mezzo instanceof Autobus) {
            Autobus autobus = (Autobus) mezzo;
           autobus.setCodice((Integer) requestData.get("codice"));
           autobusDAO.update(autobus);
        } else if (mezzo instanceof Tram) {
            Tram tram = (Tram) mezzo;
            tram.setCodice((Integer) requestData.get("codice"));
            tramDAO.update(tram);
        }

        em.close();
        exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
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
