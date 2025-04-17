package tn.esprit.foyer.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.foyer.entities.Bloc;
import tn.esprit.foyer.entities.Chambre;
import tn.esprit.foyer.entities.Foyer;
import tn.esprit.foyer.entities.TypeChambre;
import tn.esprit.foyer.repository.BlocRepository;
import tn.esprit.foyer.repository.ChambreRepository;
import tn.esprit.foyer.repository.FoyerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ChambreServiceImpl implements IChambreService {

    ChambreRepository chambreRepository;
    BlocRepository blocRepository;
    FoyerRepository foyerRepository;

    @Override
    public List<Chambre> retrieveAllChambres() {
        log.info("in method retrieveAllChambres");
        return chambreRepository.findAll();
    }

    @Override
    public Chambre addChambre(Chambre c) {
        return chambreRepository.save(c);
    }

    @Override
    public Chambre updateChambre(Chambre c) {
        return chambreRepository.save(c);
    }

    @Override
    public Chambre retrieveChambre(Long idChambre) {
        return chambreRepository.findById(idChambre).orElse(null);
    }

    @Override
    public void removeChambre(Long idChambre) {
        chambreRepository.deleteById(idChambre);
    }

    @Override
    public List<Chambre> findByTypeCAndBlocIdBloc(TypeChambre typeChambre, Long idBloc) {
        return chambreRepository.findByTypeCAndBlocIdBloc(typeChambre, idBloc);
    }

    @Override
    public List<Chambre> findByReservationsEstValid(Boolean estValid) {
        return chambreRepository.findByReservationsValide(estValid);
    }

    @Override
    public List<Chambre> findByBlocIdBlocAndBlocCapaciteBlocGreaterThan(Long idBloc, Long capaciteBloc) {
        return chambreRepository.findByBlocIdBlocAndBlocCapaciteBloc(idBloc, capaciteBloc);
    }

    @Override
    public List<Chambre> getChambresParNomBloc(String nomBloc) {
        return chambreRepository.findByBlocNomBloc(nomBloc);
    }

    @Override
    public long nbChambreParTypeEtBloc(TypeChambre type, long idBloc) {
        return chambreRepository.nbChambreParTypeEtBloc(type, idBloc);
    }

    @Override
    public List<Chambre> getChambresNonReserveParNomFoyerEtTypeChambre(String nomFoyer, TypeChambre type) {
        var chambresDisponibles = new ArrayList<Chambre>();
        var startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        var endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        var foyer = foyerRepository.findByNomFoyer(nomFoyer);
        var blocsParFoyer = Optional.ofNullable(foyer.getBlocs());

        blocsParFoyer.ifPresent(blocs ->
                blocs.forEach(bloc ->
                        bloc.getChambres().forEach(chambre -> {
                            if (chambre.getTypeC().equals(type)) {
                                var nbReservationChambre = chambreRepository.checkNbReservationsChambre(startDate, endDate, type, chambre.getNumeroChambre());
                                if ((type.equals(TypeChambre.SIMPLE) && nbReservationChambre == 0) ||
                                        (type.equals(TypeChambre.DOUBLE) && nbReservationChambre < 2) ||
                                        (type.equals(TypeChambre.TRIPLE) && nbReservationChambre < 3)) {
                                    chambresDisponibles.add(chambre);
                                }
                            }
                        })
                )
        );

        return chambresDisponibles;
    }

    //@Scheduled(fixedRate = 60000)
    public void pourcentageChambreParTypeChambre() {
        var nbTotalsChambres = chambreRepository.findAll().size();
        log.info("nbTotalsChambres : {}", nbTotalsChambres);
        Arrays.stream(TypeChambre.values()).forEach(typeChambre -> {
            var nbChambresParType = chambreRepository.nbChambresParType(typeChambre);
            var pourcentageParType = (nbChambresParType.doubleValue() / nbTotalsChambres) * 100;
            log.info("Le pourcentage des chambres pour le type {} est égal à {}%", typeChambre, pourcentageParType);
        });
    }

}
