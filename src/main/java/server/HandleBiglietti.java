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
import epicode.it.entities.utente.Utente;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.ConvalidaBiglietto;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.RinnovoAbbonamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleBiglietti implements HttpHandler {

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
        BigliettoDAO bigliettoDAO = new BigliettoDAO(em);

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
            Biglietto biglietto = bigliettoDAO.findById(id);
            if (biglietto == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }

            // Elimina la Tratta
            em.getTransaction().begin();
            bigliettoDAO.delete(biglietto);
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
        GiornalieroDAO giornalieroDAO = new GiornalieroDAO(em);
        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);
        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        String tipo = (String) requestData.get("tipo");

        GestoreRivenditoriEBiglietti gestore = new GestoreRivenditoriEBiglietti(em);

        if ("giornaliero".equalsIgnoreCase(tipo)) {
            gestore.creaGiornaliero((Rivenditore) requestData.get("rivenditore"), (Tratta) requestData.get("tratta"));
        } else if ("abbonamento".equalsIgnoreCase(tipo)) {
            gestore.creaAbbonamento((Rivenditore) requestData.get("rivenditore"), Periodicy.valueOf((String) requestData.get("periodicy")), (Utente) requestData.get("utente"));
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
        BigliettoDAO dao = new BigliettoDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        Biglietto biglietto = dao.findById(id);
        if (biglietto == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }

        if (biglietto instanceof Giornaliero) {
            Giornaliero giornaliero = (Giornaliero) biglietto;
            ConvalidaBiglietto convalida = new ConvalidaBiglietto(em);
//            convalida.convalida(giornaliero);
        } else if (biglietto instanceof Abbonamento) {
           Abbonamento abbonamento = (Abbonamento) biglietto;
            RinnovoAbbonamento rinnovo = new RinnovoAbbonamento(em);
            rinnovo.rinnova((Tessera) requestData.get("tessera"), Periodicy.valueOf((String) requestData.get("periodicy")));
        }

        em.close();
        exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        BigliettoDAO bigliettoDAO = new BigliettoDAO(em);
        GiornalieroDAO giornalieroDAO = new GiornalieroDAO(em);
        AbbonamentoDAO abbonamentoDAO = new AbbonamentoDAO(em);

        // Recupera l'ID della tessera dalla richiesta (esempio: query parameter o path parameter)
        String query = exchange.getRequestURI().getQuery();
        String tesseraIdStr = null;
        if (query != null) {
            tesseraIdStr = query.split("=")[1];  // supponendo una query del tipo "tesseraId=123"
        }

        Long tesseraId = (tesseraIdStr != null) ? Long.parseLong(tesseraIdStr) : null;

        // Recupera la tessera dalla base di dati
        Tessera tessera = null;
        if (tesseraId != null) {
            tessera = em.find(Tessera.class, tesseraId);
        }

        // Recupera i biglietti e gli abbonamenti associati alla tessera
        List<Biglietto> biglietti = bigliettoDAO.findAll();
        List<Abbonamento> abbonamenti = new ArrayList<>();

        if (tessera != null) {
            abbonamenti = abbonamentoDAO.perTessera(tessera);  // Estrai gli abbonamenti associati alla tessera
        }

        final List<Abbonamento> finalAbbonamenti = abbonamenti;

        em.close();

        // Costruisci la risposta JSON
        String jsonResponse = biglietti.stream()
                .map(b -> {
                    StringBuilder jsonBuilder = new StringBuilder();
                    if (b instanceof Giornaliero) {
                        Giornaliero giornaliero = (Giornaliero) b;
                        jsonBuilder.append(String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"daAttivare\":%b}",
                                giornaliero.getId(),
                                giornaliero.getClass().getSimpleName(),
                                giornaliero.isDaAttivare()
                        ));
                    } else if (b instanceof Abbonamento) {
                        Abbonamento abbonamento = (Abbonamento) b;
                        jsonBuilder.append(String.format(
                                "{\"id\":%d,\"tipo\":\"%s\",\"attivo\":%b,\"tariffa\":\"%s\",\"periodicy\":\"%s\"}",
                                abbonamento.getId(),
                                abbonamento.getClass().getSimpleName(),
                                abbonamento.isAttivo(),
                                abbonamento.getTariffa(),
                                abbonamento.getPeriodicy()
                        ));
                    } else {
                        jsonBuilder.append(String.format(
                                "{\"id\":%d,\"tipo\":\"%s\"}",
                                b.getId(),
                                b.getClass().getSimpleName()
                        ));
                    }

                    // Aggiungi gli abbonamenti se presenti
                    if (!finalAbbonamenti.isEmpty()) {
                        jsonBuilder.append(",\"abbonamenti\":[");

                        jsonBuilder.append(finalAbbonamenti.stream()
                                .map(a -> String.format(
                                        "{\"id\":%d,\"tipo\":\"%s\",\"attivo\":%b,\"tariffa\":\"%s\",\"periodicy\":\"%s\"}",
                                        a.getId(),
                                        a.getClass().getSimpleName(),
                                        a.isAttivo(),
                                        a.getTariffa(),
                                        a.getPeriodicy()
                                ))
                                .collect(Collectors.joining(",", "", "]")));
                    }

                    return jsonBuilder.toString();
                })
                .collect(Collectors.joining(",", "[", "]"));

        // Restituisci la risposta
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

}
