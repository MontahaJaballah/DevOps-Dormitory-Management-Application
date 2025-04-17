package tn.esprit.foyer.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Etudiant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idEtudiant; // Clé primaire

    @NonNull
    String nomEt;

    @NonNull
    String prenomEt;

    Long cin;

    String ecole;

    LocalDate dateNaissance;

    @OneToMany(mappedBy = "etudiant")
    transient List<Tache> taches;

    Float montantInscription;

    @Enumerated(EnumType.STRING)
    TypeEtudiant typeEtudiant;

    @ManyToMany(mappedBy = "etudiants", fetch = FetchType.EAGER)
    transient List<Reservation> reservations;

    @OneToOne
    Tache tache;

    public Etudiant(String nomEt, String prenomEt, String ecole) {
        this.nomEt = nomEt;
        this.prenomEt = prenomEt;
        this.ecole = ecole;
    }

    @Override
    public String toString() {
        return "Etudiant{" +
                "idEtudiant=" + idEtudiant +
                ", nomEt='" + nomEt + '\'' +
                ", prenomEt='" + prenomEt + '\'' +
                ", cin=" + cin +
                ", ecole='" + ecole + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", montantInscription=" + montantInscription +
                ", typeEtudiant=" + typeEtudiant +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Etudiant)) return false;
        var etudiant = (Etudiant) o;
        return idEtudiant != null && idEtudiant.equals(etudiant.idEtudiant);
    }

    @Override
    public int hashCode() {
        return idEtudiant != null ? idEtudiant.hashCode() : 0;
    }
}
