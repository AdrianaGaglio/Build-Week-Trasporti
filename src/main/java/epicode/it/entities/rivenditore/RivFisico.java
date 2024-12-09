package epicode.it.entities.rivenditore;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Time;

@Data
@Entity
@Table(name = "rivenditori_fisici")
@NamedQuery(name = "Trova_tutto_RivFisico", query = "SELECT a FROM RivFisico a")
public class RivFisico extends Rivenditore  {

    @Column(name="giorno_chiusura")
    private String giornoChiusura;

    @Column(name="ora_apertura")
    private Time oraApertura;

    @Column(name="ora_chiusura")
    private Time oraChiusura;


}