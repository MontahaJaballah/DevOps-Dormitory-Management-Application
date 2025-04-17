package tn.esprit.foyer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chambre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChambre; // Clé primaire

    private Long numeroChambre;

    @Enumerated(EnumType.STRING)
    private TypeChambre typeC;

    @OneToMany(fetch = FetchType.EAGER)
    private transient List<Reservation> reservations;

    @ManyToOne
    @JsonIgnore
    private Bloc bloc;

    @Override
    public String toString() {
        return "Chambre{" +
                "idChambre=" + idChambre +
                ", numeroChambre=" + numeroChambre +
                ", typeC=" + typeC +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chambre)) return false;
        var chambre = (Chambre) o;
        return idChambre != null && idChambre.equals(chambre.idChambre);
    }

    @Override
    public int hashCode() {
        return idChambre != null ? idChambre.hashCode() : 0;
    }
}
