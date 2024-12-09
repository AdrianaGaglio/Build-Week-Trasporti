package epicode.it.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "rivenditori")
@NamedQuery(name = "Trova_tutto_Rivenditore", query = "SELECT a FROM Rivenditore a")
public abstract class Rivenditore {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    //List<Biglietto> biglietti = new ArrayList<>();

}