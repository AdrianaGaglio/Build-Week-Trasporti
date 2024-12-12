package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import epicode.it.dao.biglietto.BigliettoDAO;
import epicode.it.dao.rivenditore.RivenditoreDAO;
import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.rivenditore.Rivenditore;
import com.fasterxml.jackson.databind.ObjectMapper;
import epicode.it.servizi.gestore_rivenditori_e_biglietti.GestoreRivenditoriEBiglietti;
import jakarta.persistence.*;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;
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

    private void handleDelete(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        RivenditoreDAO rivenditoreDAO = new RivenditoreDAO(em);

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
            Rivenditore rivenditore = rivenditoreDAO.findById(id);
            if (rivenditore == null) {
                exchange.sendResponseHeaders(404, -1); // Tratta non trovata
                return;
            }


            rivenditoreDAO.delete(rivenditore);


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
        RivenditoreDAO dao = new RivenditoreDAO(em);


        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        String tipo = (String) requestData.get("tipo");

        try {
            // Inizio della transazione


            // Logica per creare il rivenditore
            if ("rivfisico".equalsIgnoreCase(tipo)) {
                System.out.println("riv fisico request");

                RivFisico r = new RivFisico();
                DayOfWeek dayOfWeek = DayOfWeek.of((Integer) requestData.get("giornoChiusura"));
                r.setGiornoChiusura(dayOfWeek);
                r.setOraApertura(LocalTime.parse((String) requestData.get("oraApertura")));
                r.setOraChiusura(LocalTime.parse((String) requestData.get("oraChiusura")));
                dao.save(r);
            } else if ("rivautomatico".equalsIgnoreCase(tipo)) {
                System.out.println("riv automatico request");
                RivAutomatico rivAutomatico = new RivAutomatico();
                rivAutomatico.setAttivo(true);
                dao.save(rivAutomatico);
            } else {
                exchange.sendResponseHeaders(400, -1); // Tipo non valido
                return;
            }


            exchange.sendResponseHeaders(201, -1); // Creato con successo
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Rollback in caso di errore
            }
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Errore interno
        } finally {
            if (em.isOpen()) {
                em.close(); // Chiudi la sessione
            }
        }
    }


    private void handlePut(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        RivenditoreDAO dao = new RivenditoreDAO(em);

        // Leggi il corpo della richiesta come JSON
        Map<String, Object> requestData = objectMapper.readValue(exchange.getRequestBody(), Map.class);
        Long id = Long.parseLong(requestData.get("id").toString());
        String tipo = (String) requestData.get("tipo");  // Legge il tipo dal corpo della richiesta

        // Trova il rivenditore esistente
        Rivenditore rivenditore = dao.findById(id);
        if (rivenditore == null) {
            exchange.sendResponseHeaders(404, -1); // Rivenditore non trovato
            em.close();
            return;
        }

        // Controlla il tipo di rivenditore
        if (tipo == null || tipo.isEmpty()) {
            exchange.sendResponseHeaders(400, -1); // Tipo non presente nella richiesta
            em.close();
            return;
        }

        try {
            // Aggiorna solo se il tipo corrisponde al tipo dell'oggetto
            if (tipo.toLowerCase().equals("rivfisico") && rivenditore instanceof RivFisico) {
                RivFisico rivFisico = (RivFisico) rivenditore;
                if (requestData.containsKey("giornoChiusura")) {
                    rivFisico.setGiornoChiusura(DayOfWeek.of((Integer) requestData.get("giornoChiusura")));
                }
                if (requestData.containsKey("oraApertura")) {
                    rivFisico.setOraApertura(LocalTime.parse((String) requestData.get("oraApertura")));
                }
                if (requestData.containsKey("oraChiusura")) {
                    rivFisico.setOraChiusura(LocalTime.parse((String) requestData.get("oraChiusura")));
                }
                dao.update(rivFisico); // Salva le modifiche
            } else if (tipo.toLowerCase().equals("rivautomatico") && rivenditore instanceof RivAutomatico) {
                RivAutomatico rivAutomatico = (RivAutomatico) rivenditore;
                if (requestData.containsKey("attivo")) {
                    rivAutomatico.setAttivo((Boolean) requestData.get("attivo"));
                }
                dao.update(rivAutomatico); // Salva le modifiche
            } else {
                // Tipo non corrisponde con il tipo del rivenditore trovato
                exchange.sendResponseHeaders(400, -1); // Tipo non valido per l'entità trovata
                em.close();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1); // Errore interno
            em.close();
            return;
        }

        em.close();
        exchange.sendResponseHeaders(200, -1); // Aggiornamento riuscito
    }


    private void handleGet(HttpExchange exchange) throws IOException {
        EntityManager em = emf.createEntityManager();
        // DAO per il recupero dei rivenditori
        TypedQuery<Rivenditore> query = em.createNamedQuery("Trova_tutto_Rivenditore", Rivenditore.class);
        List<Rivenditore> rivenditori = query.getResultList();
        em.close();

        // Converti la lista in JSON
        String jsonResponse = rivenditori.stream()
                .map(r -> {
                    if (r instanceof RivFisico) {
                        RivFisico rivFisico = (RivFisico) r;
                        return String.format("{\"id\":%d,\"tipo\":\"RivFisico\",\"giornoChiusura\":\"%s\",\"oraApertura\":\"%s\",\"oraChiusura\":\"%s\"}",
                                rivFisico.getId(), rivFisico.getGiornoChiusura(), rivFisico.getOraApertura(), rivFisico.getOraChiusura());
                    } else if (r instanceof RivAutomatico) {
                        RivAutomatico rivAutomatico = (RivAutomatico) r;
                        return String.format("{\"id\":%d,\"tipo\":\"RivAutomatico\",\"attivo\":%b}", rivAutomatico.getId(), rivAutomatico.isAttivo());
                    } else {
                        return String.format("{\"id\":%d,\"tipo\":\"Rivenditore\"}", r.getId());
                    }
                })
                .collect(Collectors.joining(",", "[", "]"));

        // Restituisci la risposta JSON
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

}
