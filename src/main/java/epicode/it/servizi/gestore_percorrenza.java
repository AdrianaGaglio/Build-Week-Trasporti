package epicode.it.servizi;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.tratta.Tratta;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class GestorePercorrenze {
    private List<Percorrenza> percorrenze = new ArrayList<>();

    public Percorrenza creaPercorrenza(Tratta tratta, List<Mezzo> mezzi, LocalDate data) {
        Percorrenza nuovaPercorrenza = tratta.percorriTratta(mezzi, data);
        percorrenze.add(nuovaPercorrenza);
        return nuovaPercorrenza;
    }

    public List<Percorrenza> getPercorrenze() {
        return percorrenze;
    }

    public Percorrenza getPercorrenzaPerId(Long id) {
        return percorrenze.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}