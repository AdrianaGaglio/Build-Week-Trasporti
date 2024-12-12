package epicode.it.entities.utente;

import epicode.it.entities.tessera.Tessera;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@NamedQuery(name = "findAll_Utente", query = "SELECT a FROM Utente a")
@NamedQuery(name = "findByCard", query = "SELECT a FROM Utente a WHERE a.tessera.codice = :codice")
@NamedQuery(name = "findByEmail", query = "SELECT u FROM Utente u WHERE u.email = :email")
@Table(name = "utenti")
public class Utente {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String nome;

    private String cognome;

    @Column(name = "data_nascita")
    private LocalDate dataNascita;

    private String email;

    @OneToOne
    @JoinColumn(name = "tessera_id")
    @EqualsAndHashCode.Exclude
    private Tessera tessera;

    private String ruolo;

    @Override
    public String toString() {
        return "Utente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", dataNascita=" + dataNascita +
                ", email='" + email + '\'' +
                ", tessera=" + tessera +
                '}';
    }
}
