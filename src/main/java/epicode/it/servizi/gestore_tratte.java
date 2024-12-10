package epicode.it.servizi;

import epicode.it.entities.mezzo.Mezzo;
import epicode.it.entities.percorrenza.Percorrenza;
import epicode.it.entities.tratta.Tratta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class GestoreTratte {
    private List<Tratta> tratte = new ArrayList<>();

    public Percorrenza registraPercorrenza(Tratta tratta, List<Mezzo> mezzi, LocalTime tempoEffettivo, LocalDate data) {
        Percorrenza percorrenza = new Percorrenza();
        percorrenza.setTratta(tratta);
        percorrenza.setMezzi(mezzi);
        percorrenza.setDurata_effettiva(tempoEffettivo);
        percorrenza.setData(data);

        if (tratta.getPercorrenze() == null) {
            tratta.setPercorrenze(new ArrayList<>());
        }
        tratta.getPercorrenze().add(percorrenza);
        return percorrenza;
    }

    public String getStatisticheMezzo(Tratta tratta, Mezzo mezzo) {
        long numeroPercorrenze = tratta.contaPercorrenzeMezzo(mezzo);
        LocalTime tempoMedio = tratta.calcolaTempoMedioPercorrenza(mezzo);

        return String.format(
                "Statistiche per il mezzo ID %d sulla tratta %s-%s:%nNumero totale percorrenze: %d%nTempo medio di percorrenza: %s%nTempo previsto: %s",
                mezzo.getId(),
                tratta.getPartenza(),
                tratta.getCapolinea(),
                numeroPercorrenze,
                tempoMedio,
                tratta.getTempoPrevisto()
        );
    }
}