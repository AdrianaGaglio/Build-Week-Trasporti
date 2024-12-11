package epicode.it.servizi.gestire_tessera;

import epicode.it.entities.rivenditore.RivAutomatico;
import epicode.it.entities.rivenditore.RivFisico;
import epicode.it.entities.rivenditore.Rivenditore;
import epicode.it.entities.tessera.Tessera;
import epicode.it.entities.utente.Utente;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class GestoreTessera {

    private EntityManager em;

    public GestoreTessera(EntityManager em) {
        this.em = em;
    }


    public void creaTessera(Rivenditore r, Utente u, Scanner scanner){
        if (r instanceof RivFisico) {
            RivFisico rivFisico = (RivFisico) r;
            if (
                    !LocalDate.now().getDayOfWeek().equals(rivFisico.getGiornoChiusura()) &&
                            LocalTime.now().isAfter(rivFisico.getOraApertura().toLocalTime()) &&
                            LocalTime.now().isBefore(rivFisico.getOraChiusura().toLocalTime())
            ) {
                creaTesseraTemplate(r,u,scanner);
            } else {
                System.out.println("Il rivenditore fisico è chiuso");
            }
        } else if (r instanceof RivAutomatico) {
            RivAutomatico rivAuto = (RivAutomatico) r;
            if (rivAuto.isAttivo()){
                creaTesseraTemplate(r, u,scanner);
            } else {
                System.out.println("Rivenditore automatico fuori servizio");
            }
        }
    }

    private void  creaTesseraTemplate(Rivenditore r, Utente u, Scanner scanner) {
        em.getTransaction().begin();

        if (u.getTessera() == null) {
            System.out.println("Creazione tessera in corso...");
            Tessera tessera = new Tessera();
            tessera.setValidita(LocalDateTime.now().plusYears(1));
            u.setTessera(tessera);
            tessera.setUtente(u);
            em.persist(tessera);
            em.merge(u);
        } else if (u.getTessera().getValidita().isBefore(LocalDateTime.now())) {
            System.out.println("Tessera scaduta, necessita rinnovo");
            if (chiediRinnovo(scanner)) {
                u.getTessera().setValidita(LocalDateTime.now().plusYears(1));
                em.merge(u.getTessera());
                System.out.println("Tessera rinnovata");
            } else {
                System.out.println("Rinnovo tessera annullato");
            }
        } else {
            System.out.println("Utente già in possesso di tessera valida");
        }

        em.getTransaction().commit();
    }

    private boolean chiediRinnovo(Scanner scanner) {
        System.out.print("Vuoi rinnovare la tessera per un altro anno? (S/N): ");
        String risposta = scanner.nextLine().trim().toUpperCase();
        return risposta.equals("S");
    }

    public Tessera getTessera(Utente utente) {
        return em.createNamedQuery("findAll_UserCard", Tessera.class)
                .setParameter("id", utente.getId()).getResultStream().findFirst().orElse(null);
    }
}
