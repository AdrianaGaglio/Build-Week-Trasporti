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
            exchange.sendResponseHeaders(500, -1); // Errore interno del server
        } finally {
            em.close(); // Assicurati di chiudere l'EntityManager
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        TesseraDAO tesseraDAO = new TesseraDAO(em);
        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);

        try {
            // Leggi il corpo della richiesta come JSON
            Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);


            long utenteId = Long.parseLong(requestData.get("utenteId").toString());
            // Deserializza l'utente
            Utente utente = utenteDAO.getById(utenteId);

            // Deserializza il rivenditore in base al tipo
            long rivId = Long.parseLong(requestData.get("rivenditoreId").toString());
            Rivenditore rivenditore = rivenditoreDAO.findById(rivId);

            RivFisico rivFisico = null;
            RivAutomatico rivAutomatico = null;

            if (rivenditore.getTipo().toLowerCase().equals("rivfisico")) {
                rivFisico = (RivFisico) rivenditore;
                System.out.println(rivFisico);
                if (!LocalDate.now().getDayOfWeek().equals(rivFisico.getGiornoChiusura()) &&
                        LocalTime.now().isAfter(rivFisico.getOraApertura()) &&
                        LocalTime.now().isBefore(rivFisico.getOraChiusura())) {

                    // Inizia la transazione

                    if (utente.getTessera() == null) {

                        System.out.println("Creazione tessera in corso...");
                        Tessera tessera = new Tessera();
                        tessera.setValidita(LocalDateTime.now().plusYears(1));
                        utente.setTessera(tessera);
                        tessera.setUtente(utente);
                        em.getTransaction().begin();
                        em.persist(tessera);
                        em.merge(utente);
                        em.getTransaction().commit();
                    } else if (utente.getTessera().getValidita().isBefore(LocalDateTime.now())) {
                        System.out.println("Tessera scaduta, necessita rinnovo");
                    } else {
                        System.out.println("Utente già in possesso di tessera valida");
                    }

                } else {
                    System.out.println("Il rivenditore fisico è chiuso");
                }
            } else if (rivenditore.getTipo().toLowerCase().equals("rivautomatico")) {

                rivAutomatico = (RivAutomatico) rivenditore;
                System.out.println(rivAutomatico);
                if (rivAutomatico.isAttivo()) {

                    if (utente.getTessera() == null) {
                        System.out.println("Creazione tessera in corso...");
                        Tessera tessera = new Tessera();
                        tessera.setValidita(LocalDateTime.now().plusYears(1));
                        utente.setTessera(tessera);
                        tessera.setUtente(utente);
                        em.getTransaction().begin();
                        em.persist(tessera);
                        em.merge(utente);
                        em.getTransaction().commit();
                    } else if (utente.getTessera().getValidita().isBefore(LocalDateTime.now())) {
                        System.out.println("Tessera scaduta, necessita rinnovo");
                    } else {
                        System.out.println("Utente già in possesso di tessera valida");
                    }

                } else {
                    System.out.println("Rivenditore automatico fuori servizio");
                }
            }

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
        TesseraDAO dao = new TesseraDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());

        Tessera tessera = dao.getById(id);
        if (tessera == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }
        LocalDate validita = LocalDate.parse(requestData.get("validita").toString());
        if (validita.isBefore(LocalDate.now())) {
            tessera.setValidita(LocalDateTime.now().plusYears(1));
            em.getTransaction().begin();
            em.merge(tessera);
            em.getTransaction().commit();
        } else {
            System.out.println("La tessera è già valida");
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
