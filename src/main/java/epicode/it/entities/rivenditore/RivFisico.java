package epicode.it.entities.rivenditore;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;

@Data
@Entity
@Table(name = "rivenditori_fisici")
@NamedQuery(name = "Trova_tutto_RivFisico", query = "SELECT a FROM RivFisico a")
public class RivFisico extends Rivenditore  {

    @Enumerated(EnumType.STRING)
    @Column(name="giorno_chiusura")
    private DayOfWeek giornoChiusura;

    @Column(name="ora_apertura")
    private Time oraApertura;

    @Column(name="ora_chiusura")
    private Time oraChiusura;

    @Override
    public String toString() {
        return "\nRivFisico{" +
                "id= " + getId() + ", " +
                "giornoChiusura=" + giornoChiusura +
                ", oraApertura=" + oraApertura +
                ", oraChiusura=" + oraChiusura +
                '}';
    }
}