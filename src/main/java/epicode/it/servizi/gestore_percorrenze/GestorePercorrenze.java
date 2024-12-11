//package epicode.it.servizi;
//
//import epicode.it.dao.percorrenza.PercorrenzaDAO;
//import epicode.it.entities.mezzo.Mezzo;
//import epicode.it.entities.percorrenza.Percorrenza;
//import epicode.it.entities.tratta.Tratta;
//import jakarta.persistence.EntityManager;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//import java.util.Set;
//
//public class GestorePercorrenze {
//
//    private EntityManager em;
//
//    public GestorePercorrenze(EntityManager em) {
//        this.em = em;
//    }
//
//    private List<Percorrenza> percorrenze = new ArrayList<>();
//
//    public Percorrenza getPercorrenzaPerId(Long id) {
//        return percorrenze.stream()
//                .filter(p -> p.getId().equals(id))
//                .findFirst()
//                .orElse(null);
//    }
//
//    public void aggiungiPercorrenza(Mezzo m, LocalDateTime data, Tratta tratta) {
//        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);
//        Percorrenza foundPM = percorrenzaDAO.trovaPerMezzo(m, data);
//        Percorrenza foundPT = percorrenzaDAO.trovaPerTratta(tratta, data);
//        if (foundPM != null) {
//            System.out.println("Percorrenza già presente per il mezzo " + m);
//            return;
//        }
//        if (foundPT != null) {
//            System.out.println("Percorrenza già presente per la tratta " + tratta);
//            return;
//        }
//        Percorrenza percorrenza = new Percorrenza();
//        int random = ((int) (Math.random() * 2)) + 1;
//        int randomPlus = ((int) (Math.random() * 30)) + 1;
//        int randomMinus = ((int) (Math.random() * 10)) + 1;
//        percorrenza.setData(data);
//        percorrenza.setTratta(tratta);
//        percorrenza.setMezzo(m);
//        LocalTime durataPrevista = tratta.getDurata();
//        percorrenza.setDurata_effettiva(random == 1 ? durataPrevista.plusMinutes(randomPlus) : durataPrevista.minusMinutes(randomMinus));
//        em.getTransaction().begin();
//        em.persist(percorrenza);
//        tratta.getPercorrenze().add(percorrenza);
//        m.getPercorrenze().add(percorrenza);
//        em.merge(tratta);
//        em.getTransaction().commit();
//        System.out.println("Percorrenza inserita correttamente: " + percorrenza);
//    }
//
//    public void mediaPercorrenzaPerTratta(Tratta tratta, Mezzo mezzo) {
//        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);
//        List<Percorrenza> percorrenze = percorrenzaDAO.perMezzoTratta(tratta, mezzo);
//        double sommaDurata = 0;
//        for(Percorrenza percorrenza : percorrenze) {
//            percorrenza.getDurata_effettiva().toEpochSecond()
//        }
//
//    }
//
//}