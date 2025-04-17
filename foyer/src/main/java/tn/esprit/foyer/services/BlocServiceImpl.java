package tn.esprit.foyer.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.foyer.entities.Bloc;
import tn.esprit.foyer.entities.Chambre;
import tn.esprit.foyer.repository.BlocRepository;
import tn.esprit.foyer.repository.ChambreRepository;
import tn.esprit.foyer.repository.ReservationRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BlocServiceImpl implements IBlocService {

    BlocRepository blocRepository;
    ChambreRepository chambreRepository;
    ReservationRepository reservationRepository;

    @Override
    public List<Bloc> retrieveAllBlocs() {
        return blocRepository.findAll();
    }

    @Override
    public Bloc addBloc(Bloc b) {
        log.info("Entrée dans la méthode addBloc");
        log.trace("entrée dans la méthode addBloc logging");
        return blocRepository.save(b);
    }

    @Override
    public Bloc updateBloc(Bloc b) {
        return blocRepository.save(b);
    }

    @Override
    public Bloc retrieveBloc(Long idBloc) {
        return blocRepository.findById(idBloc).orElse(null);
    }

    @Override
    public void removeBloc(Long idBloc) {
        log.debug("Suppression du bloc avec ID: {}", idBloc);
        blocRepository.deleteById(idBloc);
    }

    @Override
    public List<Bloc> findByFoyerUniversiteIdUniversite(Long idUniversite) {
        return blocRepository.findByFoyerUniversite(idUniversite);
    }

    @Override
    public Bloc affecterChambresABloc(List<Long> numChambre, String nomBloc) {
        var bloc = blocRepository.findByNomBloc(nomBloc);
        numChambre.forEach(
                chambreNumber -> {
                    var c = chambreRepository.findByNumeroChambre(chambreNumber);
                    c.setBloc(bloc);
                    chambreRepository.save(c);
                }
        );
        return bloc;
    }

    // @Scheduled(fixedRate = 60000)
    public void fixedRateMethod() {
        log.info("Méthode planifiée exécutée à intervalle fixe");
    }

    @Scheduled(fixedRate = 60000)
    public void listeChambresParBloc() {
        var blocs = blocRepository.findAll();
        blocs.forEach(bloc -> {
            log.info("Bloc : {} avec une capacité de : {}", bloc.getNomBloc(), bloc.getCapaciteBloc());
            if (bloc.getChambres() != null && !bloc.getChambres().isEmpty()) {
                log.info("Liste des chambres du bloc {} :", bloc.getNomBloc());
                bloc.getChambres().forEach(chambre ->
                        log.info("Chambre numéro {} de type {}", chambre.getNumeroChambre(), chambre.getTypeC())
                );
            } else {
                log.info("Bloc vide pour le moment");
            }
        });
    }
}
