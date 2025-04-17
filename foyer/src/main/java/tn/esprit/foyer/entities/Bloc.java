package tn.esprit.foyer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bloc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idBloc")
    private Long idBloc;

    private String nomBloc;

    private Long capaciteBloc;

    @OneToMany(mappedBy = "bloc", fetch = FetchType.EAGER)
    private transient List<Chambre> chambres;

    @ManyToOne
    @JsonIgnore
    private Foyer foyer;

    @Override
    public String toString() {
        return "Bloc{" +
                "idBloc=" + idBloc +
                ", nomBloc='" + nomBloc + '\'' +
                ", capaciteBloc=" + capaciteBloc +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bloc)) return false;
        var bloc = (Bloc) o;
        return Objects.equals(idBloc, bloc.idBloc)
                && Objects.equals(nomBloc, bloc.nomBloc)
                && Objects.equals(capaciteBloc, bloc.capaciteBloc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBloc, nomBloc, capaciteBloc);
    }
}
