package epicode.it.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rivenditori_automatici")
@NamedQuery(name = "Trova_tutto_RivAutomatico", query = "SELECT a FROM RivAutomatico a")
public class RivAutomatico extends Rivenditore  {

    private boolean attivo;
}