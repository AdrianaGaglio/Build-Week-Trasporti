package insert_data;

import com.github.javafaker.Faker;
import epicode.it.dao.RivAutomaticoDAO;
import epicode.it.dao.RivenditoreDAO;
import epicode.it.dao.utente.UtenteDAO;
import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.Time;
import java.util.Locale;

public class MainTest {
    public static void main(String[] args) {
        Faker faker = new Faker(new Locale("it"));
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("unit-jpa");
        EntityManager em = emf.createEntityManager();

        // 1. creare N rivenditori
        // - conteggio biglietti vendutiè


        // 2. creare N utenti
        // - cercare utente per tessera
        // - cercare utente per email
        // - verificare se l'utente ha abbonamenti attivi

        // 3. creare alcune tessere da associare agli utenti

        // 4. emettere alcuni biglietti per diversi rivenditori

        // 5. emettere alcuni abbonamenti per diversi rivenditori associati a delle tessere


    }
}