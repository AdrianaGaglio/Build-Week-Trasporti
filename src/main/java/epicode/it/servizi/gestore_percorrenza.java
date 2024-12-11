package epicode.it.servizi;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.tratta.Tratta;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class GestorePercorrenze {

    private EntityManager em;

    public GestorePercorrenze(EntityManager em) {
        this.em = em;
    }

    private List<Percorrenza> percorrenze = new ArrayList<>();

    public Percorrenza getPercorrenzaPerId(Long id) {
        return percorrenze.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void percorri(Mezzo m, LocalDateTime data, Tratta tratta) {
        em.getTransaction().begin();
        // lo stesso mezzo non può percorrere più di una tratta alla volta
        // più mezzi non possono percorrere contemporaneamente la stessa tratta
        Percorrenza percorrenza = new Percorrenza();
        int random = ((int) (Math.random() * 2)) + 1;
        int randomPlus = ((int) (Math.random() * 30)) + 1;
        int randomMinus = ((int) (Math.random() * 10)) + 1;
        percorrenza.setData(data);
        percorrenza.setTratta(tratta);
        percorrenza.setMezzo(m);
        LocalTime durataPrevista = tratta.getDurata();
        percorrenza.setDurata_effettiva(random == 1 ? durataPrevista.plusMinutes(randomPlus) : durataPrevista.minusMinutes(randomMinus));
        em.persist(percorrenza);
        tratta.getPercorrenze().add(percorrenza);
        m.getPercorrenze().add(percorrenza);
        em.merge(tratta);
        em.getTransaction().commit();
    }
}