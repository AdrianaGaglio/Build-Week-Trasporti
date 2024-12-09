package epicode.it.entities.utente;

import epicode.it.entities.tessera.Tessera;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@NamedQuery(name = "findAll", query = "SELECT a FROM Utente a")
@NamedQuery(name = "findByCard", query = "SELECT a FROM Utente a WHERE a.tessera.codice = :codice")
@Table(name="utenti")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nome;

    private String cognome;

    @Column(name="data_nascita")
    private LocalDate dataNascita;

    private String email;

    @OneToOne
    @JoinColumn(name = "tessera_id")
    private Tessera tessera;

}