package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.AbbonamentoDAO;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.biglietto.GiornalieroDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.dao.tessera.TesseraDAO;
import epicode.it.dao.tratta.TrattaDAO;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HandleBiglietti implements HttpHandler {


// Registrazione del modulo JavaTimeModule per supportare i tipi di data e ora di Java 8


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
            em.remove(biglietto);
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
        UtenteDAO utenteDAO = new UtenteDAO(em);

        // Crea un nuovo ObjectMapper
        ObjectMapper objectMapper2 = new ObjectMapper();
        objectMapper2.registerModule(new JavaTimeModule());

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper2.readValue(exchange.getRequestBody(), Map.class);
            String tipo = (String) requestData.get("tipo");

            // Parsing sicuro per rivenditoreId
            Long rivenditoreId = null;
            Object rivenditoreIdObj = requestData.get("rivenditoreId");

            if (rivenditoreIdObj instanceof String) {
                try {
                    rivenditoreId = Long.parseLong((String) rivenditoreIdObj);
                } catch (NumberFormatException e) {
                    // Log dell'errore di parsing e restituzione errore 400
                    exchange.sendResponseHeaders(400, -1); // ID rivenditore non valido
                    return;
                }
            } else if (rivenditoreIdObj instanceof Integer) {
                rivenditoreId = ((Integer) rivenditoreIdObj).longValue(); // Converte direttamente Integer a Long
            }

            Rivenditore rivenditore = rivenditoreDAO.findById(rivenditoreId);
            Object createdObject = null;

            // Controllo per tipo "giornaliero"
            if ("giornaliero".equalsIgnoreCase(tipo)) {
                TrattaDAO trattaDAO = new TrattaDAO(em);

                // Parsing sicuro per trattaId
                Long trattaId = null;
                Object trattaIdObj = requestData.get("trattaId");

                if (trattaIdObj instanceof String) {
                    try {
                        trattaId = Long.parseLong((String) trattaIdObj);
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(400, -1); // ID tratta non valido
                        return;
                    }
                } else if (trattaIdObj instanceof Integer) {
                    trattaId = ((Integer) trattaIdObj).longValue();
                }

                Tratta tratta = trattaDAO.getById(trattaId);

                // Crea un nuovo biglietto giornaliero
                Giornaliero giornaliero = new Giornaliero();
                giornaliero.setTipo("giornaliero");
                giornaliero.setRivenditore(rivenditore);
                giornaliero.setTratta(tratta);
                giornaliero.setDaAttivare(true);

                // Salviamo il biglietto giornaliero nel database
                giornalieroDAO.save(giornaliero);
                createdObject = giornaliero;
            }
            // Controllo per tipo "abbonamento"
            else if ("abbonamento".equalsIgnoreCase(tipo)) {



                // Parsing sicuro per utenteId
                Long utenteId = null;
                Object utenteIdObj = requestData.get("utenteId");


                if (utenteIdObj instanceof String) {
                    try {
                        utenteId = Long.parseLong((String) utenteIdObj);
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(400, -1); // ID utente non valido
                        return;
                    }
                } else if (utenteIdObj instanceof Integer) {
                    utenteId = ((Integer) utenteIdObj).longValue();
                }

                // Parsing sicuro per tesseraId
                Long tesseraId = null;
                Object tesseraIdObj = requestData.get("tesseraId");


                if (tesseraIdObj instanceof String) {
                    try {
                        tesseraId = Long.parseLong((String) tesseraIdObj);
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(400, -1); // ID tessera non valido
                        return;
                    }
                } else if (tesseraIdObj instanceof Integer) {
                    tesseraId = ((Integer) tesseraIdObj).longValue();
                }

                if (utenteIdObj instanceof String) {
                    try {
                        utenteId = Long.parseLong((String) utenteIdObj);
                    } catch (NumberFormatException e) {
                        exchange.sendResponseHeaders(400, -1); // ID utente non valido
                        return;
                    }
                } else if (utenteIdObj instanceof Integer) {
                    utenteId = ((Integer) utenteIdObj).longValue();
                }

                TesseraDAO tesseraDAO = new TesseraDAO(em);
                Tessera tessera = tesseraDAO.getById(tesseraId);



                Utente utente = utenteDAO.getById(utenteId);

                if (tessera == null) {
                    exchange.sendResponseHeaders(404, -1); // Tessera non trovata
                    return;
                }

                // Crea un nuovo abbonamento
                Abbonamento abbonamento = new Abbonamento();
                abbonamento.setRivenditore(rivenditore);
                abbonamento.setTipo("abbonamento");
                abbonamento.setAttivo(true);
                String validita = (String) requestData.get("periodicy");
                Periodicy periodicy = Periodicy.valueOf((String) requestData.get("periodicy"));
                abbonamento.setPeriodicy(periodicy);
                switch(validita) {
                    case "settimanale": abbonamento.setScadenza(LocalDateTime.now().plusDays(7)); break;
                    case "mensile": abbonamento.setScadenza(LocalDateTime.now().plusDays(30)); break;
                    case "bimestrale": abbonamento.setScadenza(LocalDateTime.now().plusDays(60)); break;
                    case "trimestrale": abbonamento.setScadenza(LocalDateTime.now().plusDays(90)); break;
                    case "annuale": abbonamento.setScadenza(LocalDateTime.now().plusDays(365)); break;
                }
                abbonamento.setUtente(utente);
                abbonamento.setTessera(tessera);
                utente.setTessera(tessera);
                tessera.getAbbonamenti().add(abbonamento);
                em.getTransaction().begin();
                em.merge(tessera);
                em.persist(abbonamento);
                em.getTransaction().commit();
                createdObject = abbonamento;
            } else {
                exchange.sendResponseHeaders(400, -1); // Tipo non valido
                return;
            }

            // Risposta di successo con l'oggetto creato
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
            exchange.sendResponseHeaders(500, -1); // Errore interno
        } finally {
            em.close(); // Assicurati di chiudere l'EntityManager
        }
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

        try {
            // Recupera i biglietti e gli abbonamenti associati alla tessera
            List<Biglietto> biglietti = bigliettoDAO.findAll();
            // Costruisci la risposta JSON
            String jsonResponse = biglietti.stream()
                    .map(b -> {
                        StringBuilder jsonBuilder = new StringBuilder();
                        if (b.getTipo().equalsIgnoreCase("giornaliero")) {
                            Giornaliero giornaliero = (Giornaliero) b;
                            jsonBuilder.append(String.format(
                                    "{\"id\":%d,\"tipo\":\"%s\",\"daAttivare\":%b}",
                                    giornaliero.getId(),
                                    giornaliero.getClass().getSimpleName(),
                                    giornaliero.isDaAttivare()
                            ));
                        } else if (b.getTipo().equalsIgnoreCase("abbonamento")) {
                            Abbonamento abbonamento = (Abbonamento) b;
                            jsonBuilder.append(String.format(
                                    "{\"id\":%d,\"tipo\":\"%s\",\"attivo\":%b,\"tariffa\":\"%s\",\"periodicy\":\"%s\"}",
                                    abbonamento.getId(),
                                    abbonamento.getClass().getSimpleName(),
                                    abbonamento.isAttivo(),
                                    abbonamento.getTariffa(),
                                    abbonamento.getPeriodicy()
                            ));
                        }

                        return jsonBuilder.toString();
                    })
                    .collect(Collectors.joining(",", "[", "]"));

            // Restituisci la risposta
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
        } catch (Exception e) {
            // Rollback in caso di errore
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Errore interno
        } finally {
            // Assicurati che l'EntityManager venga chiuso
            if (em.isOpen()) {
                em.close();
            }
        }
    }


}
