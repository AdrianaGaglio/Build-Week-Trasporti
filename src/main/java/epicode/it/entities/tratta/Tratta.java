package epicode.it.entities.tratta;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Data
@Entity
@Table(name="tratte")
public class Tratta {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String partenza;
    private String capolinea;
    private LocalTime tempoPrevisto;

    @OneToMany(mappedBy = "tratta")
    private List<Percorrenza> percorrenze = new ArrayList<>();

    public LocalTime calcolaTempoMedioPercorrenza(Mezzo mezzo) {
        if (percorrenze == null || percorrenze.isEmpty()) {
            return LocalTime.of(0, 0);
        }

        List<Percorrenza> percorrenzeMezzo = percorrenze.stream()
                .filter(p -> p.getMezzi().contains(mezzo))
                .toList();

        if (percorrenzeMezzo.isEmpty()) {
            return LocalTime.of(0, 0);
        }

        OptionalDouble mediaMinuti = percorrenzeMezzo.stream()
                .mapToInt(p -> {
                    LocalTime durata = (LocalTime) p.getDurata_effettiva();
                    return durata.getHour() * 60 + durata.getMinute();
                })
                .average();

        if (mediaMinuti.isPresent()) {
            int media = (int) mediaMinuti.getAsDouble();
            return LocalTime.of(media / 60, media % 60);
        }
        return LocalTime.of(0, 0);
    }

    public long contaPercorrenzeMezzo(Mezzo mezzo) {
        if (percorrenze == null) return 0;
        return percorrenze.stream()
                .filter(p -> p.getMezzi().contains(mezzo))
                .count();
    }

    // Modifica questi metodi che erano sbagliati
    public void setPercorrenze(List<Percorrenza> percorrenze) {
        this.percorrenze = percorrenze;
    }

    public String getCapolinea() {
        return this.capolinea;
    }

    public List<Percorrenza> getPercorrenze() {
        return this.percorrenze;
    }

    public LocalTime getTempoPrevisto() {
        return this.tempoPrevisto;
    }

    public String getPartenza() {
        return this.partenza;
    }

    public Percorrenza percorriTratta(List<Mezzo> mezzi, LocalDate data) {
        return null;
    }
}