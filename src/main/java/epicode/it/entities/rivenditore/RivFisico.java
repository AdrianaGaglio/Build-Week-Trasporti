package epicode.it.entities.rivenditore;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "rivenditori_fisici")
@NamedQuery(name = "Trova_tutto_RivFisico", query = "SELECT a FROM RivFisico a")
public class RivFisico extends Rivenditore  {

    @Enumerated(EnumType.STRING)
    @Column(name="giorno_chiusura")
    private DayOfWeek giornoChiusura;

    @Column(name="ora_apertura")
    private LocalTime oraApertura;

    @Column(name="ora_chiusura")
    private LocalTime oraChiusura;

    @Override
    public String toString() {
        return "RivFisico{" +
                "id= " + getId() + ", " +
                "giornoChiusura=" + giornoChiusura +
                ", oraApertura=" + oraApertura +
                ", oraChiusura=" + oraChiusura +
                '}';
    }
}