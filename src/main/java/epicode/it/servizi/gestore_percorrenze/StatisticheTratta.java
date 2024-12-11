package epicode.it.servizi.gestore_percorrenze;

import lombok.Data;

import java.time.LocalTime;

@Data
public class StatisticheTratta {
    private int numCorse;
        private LocalTime mediaPercorrenza;

        public StatisticheTratta(int numCorse, LocalTime mediaPercorrenza) {
            this.numCorse = numCorse;
            this.mediaPercorrenza = mediaPercorrenza;
        }

}
