package epicode.it.servizi.gestore_rivenditori_e_biglietti;

import epicode.it.entities.biglietto.Biglietto;
import epicode.it.entities.rivenditore.Rivenditore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticheRivenditore {

    public List<Biglietto> bigliettiEmessiPerRivenditore(Rivenditore rivenditore, LocalDate dateFrom, LocalDate dateTo) {
        if (rivenditore == null) {
            throw new IllegalArgumentException("Rivenditore non può essere null");
        }
        if (dateFrom == null || dateTo == null) {
            throw new IllegalArgumentException("Le date non possono essere null");
        }
        if (dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("La data di inizio deve essere precedente alla data di fine");
        }

        LocalDateTime startDateTime = dateFrom.atStartOfDay();
        LocalDateTime endDateTime = dateTo.atTime(23, 59, 59);

        return rivenditore.getBiglietti().stream()
                .filter(biglietto -> {
                    LocalDateTime emissione = biglietto.getEmissione();
                    return !emissione.isBefore(startDateTime) && !emissione.isAfter(endDateTime);
                })
                .collect(Collectors.toList());
    }
}