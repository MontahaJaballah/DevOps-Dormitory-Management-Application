package tn.esprit.foyer.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.foyer.entities.Chambre;
import tn.esprit.foyer.entities.Etudiant;
import tn.esprit.foyer.entities.Reservation;
import tn.esprit.foyer.entities.TypeChambre;
import tn.esprit.foyer.repository.ChambreRepository;
import tn.esprit.foyer.repository.EtudiantRepository;
import tn.esprit.foyer.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class ReservationServicImpl implements IReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ChambreRepository chambreRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Override
    public List<Reservation> retrieveAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public Reservation addReservation(Reservation r) {
        log.info("Saving reservation with id: {}, anneeUniversitaire: {}, estValid: {}",
                r.getIdReservation(), r.getAnneeUniversitaire(), r.getEstValid());
        Reservation saved = reservationRepository.save(r);
        log.info("Saved reservation: {}", saved);
        Reservation retrieved = reservationRepository.findById(r.getIdReservation()).orElse(null);
        log.info("Retrieved reservation after save: {}", retrieved);
        return saved;
    }

    @Override
    @Transactional
    public Reservation updateReservation(Reservation r) {
        return reservationRepository.save(r);
    }

    @Override
    public Reservation retrieveReservation(String idReservation) {
        return reservationRepository.findById(idReservation).orElse(null);
    }

    @Override
    @Transactional
    public void removeReservation(String idReservation) {
        reservationRepository.deleteById(idReservation);
    }

    public String generateReservationId(Long numChambre, Long cin, int year) {
        return numChambre + cin.toString() + year;
    }

    @Transactional
    public Reservation ajouterReservationEtAssignerAChambreEtAEtudiant(Reservation res, Long numChambre, long cin) {
        LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        Etudiant e = etudiantRepository.findByCin(cin);
        Chambre c = chambreRepository.findByNumeroChambre(numChambre);
        res.setIdReservation(numChambre + e.getCin().toString() + LocalDate.now().getYear());
        res.setEstValid(true);
        List<Etudiant> etudiants = new ArrayList<>();
        if (res.getEtudiants() != null) {
            etudiants.addAll(res.getEtudiants());
        }
        etudiants.add(e);
        res.setEtudiants(etudiants);
        if (c.getReservations() != null) {
            Integer reservationSize = reservationRepository.getReservationsCurrentYear(startDate, endDate, numChambre);
            switch (reservationSize) {
                case 0:
                    log.info("case reservation vide");
                    Reservation r = reservationRepository.save(res);
                    c.getReservations().add(r);
                    chambreRepository.save(c);
                    break;
                case 1:
                    log.info("case reservation courante egale à 1");
                    if (c.getTypeC().equals(TypeChambre.DOUBLE) || c.getTypeC().equals(TypeChambre.TRIPLE)) {
                        Reservation r1 = reservationRepository.save(res);
                        c.getReservations().add(r1);
                        chambreRepository.save(c);
                    } else {
                        log.info("chambre simple déja réservée");
                    }
                    break;
                case 2:
                    log.info("case reservation courante egale à 2");
                    if (c.getTypeC().equals(TypeChambre.TRIPLE)) {
                        Reservation r2 = reservationRepository.save(res);
                        c.getReservations().add(r2);
                        chambreRepository.save(c);
                    } else {
                        log.info("chambre double déja complete");
                    }
                    break;
                default:
                    log.info("case default");
                    log.info("Capacité chambre atteinte");
            }
        } else {
            Reservation r = reservationRepository.save(res);
            List<Reservation> reservations = new ArrayList<>();
            reservations.add(r);
            c.setReservations(reservations);
            chambreRepository.save(c);
        }
        return null;
    }

    @Override
    public List<Reservation> getReservationParAnneeUniversitaire(LocalDate dateDebut, LocalDate dateFin) {
        return reservationRepository.findByAnneeUniversitaireBetween(dateDebut, dateFin);
    }

    // @Scheduled(fixedRate = 60000)
    public void nbPlacesDisponibleParChambreAnneeEnCours() {
        LocalDate currentdate = LocalDate.now();
        LocalDate dateDebut = LocalDate.of(currentdate.getYear(), 12, 31);
        LocalDate dateFin = LocalDate.of(currentdate.getYear(), 1, 1);
        List<Chambre> chambresDisponibles = chambreRepository.findAll();
        chambresDisponibles.forEach(
                chambre -> {
                    AtomicReference<Integer> nbChambresOccupes = new AtomicReference<>(0);

                    if (chambre.getReservations() != null) {
                        List<Reservation> reservations = chambre.getReservations();
                        reservations.forEach(
                                reservation -> {
                                    if (reservation.getEstValid() && reservation.getAnneeUniversitaire().isAfter(dateDebut) && reservation.getAnneeUniversitaire().isBefore(dateFin))
                                        nbChambresOccupes.getAndSet(nbChambresOccupes.get() + 1);
                                }
                        );
                    }
                    if (chambre.getTypeC().equals(TypeChambre.SIMPLE)) {
                        log.info("nb places restantes en " + currentdate.getYear() + " pour la chambre " + chambre.getNumeroChambre()
                                + " est égale à " + (1 - nbChambresOccupes.get()));
                    } else if (chambre.getTypeC().equals(TypeChambre.DOUBLE)) {
                        log.info("nb places restantes en " + currentdate.getYear() + " pour la chambre " + chambre.getNumeroChambre()
                                + " est égale à " + (2 - nbChambresOccupes.get()));
                    } else { // cas triple
                        log.info("nb places restantes en " + currentdate.getYear() + " pour la chambre " + chambre.getNumeroChambre()
                                + " est égale à " + (3 - nbChambresOccupes.get()));
                    }
                }
        );
    }

    @Transactional
    public void reassignStudentsAfterCancellation(String canceledReservationId) {
        Reservation canceledReservation = reservationRepository.findById(canceledReservationId).orElse(null);
        if (canceledReservation == null) {
            log.warn("Reservation with ID {} not found for reassignment.", canceledReservationId);
            return;
        }

        List<Etudiant> affectedEtudiants = canceledReservation.getEtudiants();
        if (affectedEtudiants == null || affectedEtudiants.isEmpty()) {
            log.info("No students to reassign from reservation {}.", canceledReservationId);
            return;
        }

        // Find available chambres for the current year
        LocalDate currentYear = LocalDate.now();
        List<Chambre> availableChambres = chambreRepository.findAll().stream()
                .filter(chambre -> {
                    long validReservations = chambre.getReservations().stream()
                            .filter(r -> r.getAnneeUniversitaire() != null &&
                                    r.getAnneeUniversitaire().getYear() == currentYear.getYear() &&
                                    r.getEstValid())
                            .count();
                    return switch (chambre.getTypeC()) {
                        case SIMPLE -> validReservations < 1;
                        case DOUBLE -> validReservations < 2;
                        case TRIPLE -> validReservations < 3;
                    };
                })
                .toList();

        if (availableChambres.isEmpty()) {
            log.warn("No available chambres to reassign students from reservation {}.", canceledReservationId);
            return;
        }

        // Reassign each student to the first available chambre
        for (Etudiant etudiant : affectedEtudiants) {
            Chambre newChambre = availableChambres.get(0);
            Reservation newReservation = new Reservation();
            newReservation.setIdReservation(generateReservationId(newChambre.getNumeroChambre(), etudiant.getCin(), currentYear.getYear()));

            newReservation.setAnneeUniversitaire(currentYear);
            newReservation.setEtudiants(List.of(etudiant));

            reservationRepository.save(newReservation);
            newChambre.getReservations().add(newReservation);
            chambreRepository.save(newChambre);
            etudiant.getReservations().add(newReservation);
            etudiantRepository.save(etudiant);

            log.info("Reassigned student with CIN {} to chambre {} with reservation {}.",
                    etudiant.getCin(), newChambre.getNumeroChambre(), newReservation.getIdReservation());
        }
    }
}