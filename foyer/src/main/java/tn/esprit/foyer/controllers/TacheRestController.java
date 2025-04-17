package tn.esprit.foyer.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.foyer.entities.Tache;
import tn.esprit.foyer.services.ITacheService;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/tache")
public class TacheRestController {

    ITacheService tacheService;

    // http://localhost:8089/foyer/tache/retrieve-all-taches
    @GetMapping("/retrieve-all-taches")
    @ResponseBody
    public List<Tache> getFoyers() {
        return tacheService.retrieveAllTaches();
    }

    // http://localhost:8089/foyer/tache/retrieve-tache/8
    @GetMapping("/retrieve-tache/{tacheId}")
    @ResponseBody
    public Tache retrieveTache(@PathVariable("tacheId") Long tacheId) {
        return tacheService.retrieveTache(tacheId);
    }

    // http://localhost:8089/foyer/tache/add-tache
    @PostMapping("/add-tache")
    @ResponseBody
    public Tache addTache(@RequestBody Tache t) {
        return tacheService.addTache(t);
    }

    // http://localhost:8089/foyer/tache/update-tache
    @PutMapping("/update-tache")
    @ResponseBody
    public Tache updateTache(@RequestBody Tache t) {
        return tacheService.updateTache(t);
    }

    // http://localhost:8089/foyer/tache/removeidTache
    @DeleteMapping("/removeTache/{idTache}")
    @ResponseBody
    public void removeTache(@PathVariable("idTache") Long idTache) {
        tacheService.removeTache(idTache);
    }

    // http://localhost:8089/foyer/tache/addTachesAndAffectToEtudiant
    @PostMapping("/addTachesAndAffectToEtudiant/{nomEt}/{prenomEt}")
    @ResponseBody
    public List<Tache> addTachesAndAffectToEtudiant(@RequestBody List<Tache> taches, @PathVariable("nomEt") String nomEt, @PathVariable("prenomEt") String prenomEt) {
        return tacheService.addTachesAndAffectToEtudiant(taches, nomEt, prenomEt);
    }

    // http://localhost:8089/foyer/tache/calculNouveauMontantInscriptionDesEtudiants
    @GetMapping("/calculNouveauMontantInscriptionDesEtudiants")
    public Map<String, Float> calculNouveauMontantInscriptionDesEtudiants() {
        return tacheService.calculNouveauMontantInscriptionDesEtudiants();
    }
}