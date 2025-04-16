package tn.esprit.foyer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import tn.esprit.foyer.entities.Reservation;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Before("execution(* tn.esprit.foyer.services.*.*(..))")
    public void logMethodEntry(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        log.info("In method: " + name + " : ");
    }

    @AfterReturning("execution(* tn.esprit.foyer.services.EtudiantServiceImpl.retrieveEtudiant(..))")
    public void logMethodExit1(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        log.info("Out of method without errors: " + name);
    }

    @AfterReturning(pointcut = "execution(* tn.esprit.foyer.services.ReservationServicImpl.addReservation(..))", returning = "result")
    public void logAddReservationSuccess(JoinPoint joinPoint, Object result) {
        String name = joinPoint.getSignature().getName();
        Reservation reservation = (Reservation) result;
        log.info("Successfully added reservation in method {}: Reservation ID: {}, Year: {}, Valid: {}",
                name, reservation.getIdReservation(), reservation.getAnneeUniversitaire(), reservation.getEstValid());
    }

    @AfterReturning(pointcut = "execution(* tn.esprit.foyer.services.ReservationServicImpl.updateReservation(..))", returning = "result")
    public void logUpdateReservationSuccess(JoinPoint joinPoint, Object result) {
        String name = joinPoint.getSignature().getName();
        Reservation reservation = (Reservation) result;
        log.info("Successfully updated reservation in method {}: Reservation ID: {}, Year: {}, Valid: {}",
                name, reservation.getIdReservation(), reservation.getAnneeUniversitaire(), reservation.getEstValid());
    }

    @AfterReturning(pointcut = "execution(* tn.esprit.foyer.services.ReservationServicImpl.retrieveReservation(..))", returning = "result")
    public void logRetrieveReservationSuccess(JoinPoint joinPoint, Object result) {
        String name = joinPoint.getSignature().getName();
        Reservation reservation = (Reservation) result;
        if (reservation != null) {
            log.info("Successfully retrieved reservation in method {}: Reservation ID: {}", name, reservation.getIdReservation());
        } else {
            log.info("No reservation found in method {} for given ID", name);
        }
    }

    @After("execution(* tn.esprit.foyer.services.ReservationServicImpl.removeReservation(..))")
    public void logRemoveReservation(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        String idReservation = (String) joinPoint.getArgs()[0];
        log.info("Completed removal of reservation in method {}: Reservation ID: {}", name, idReservation);
    }

    @After("execution(* tn.esprit.foyer.services.ReservationServicImpl.ajouterReservationEtAssignerAChambreEtAEtudiant(..))")
    public void logAjouterReservationEtAssigner(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        Long numChambre = (Long) joinPoint.getArgs()[1];
        Long cin = (Long) joinPoint.getArgs()[2];
        log.info("Completed reservation assignment in method {}: Chambre Number: {}, Student CIN: {}", name, numChambre, cin);
    }

    @After("execution(* tn.esprit.foyer.services.ReservationServicImpl.reassignStudentsAfterCancellation(..))")
    public void logReassignStudentsAfterCancellation(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        String canceledReservationId = (String) joinPoint.getArgs()[0];
        log.info("Completed reassignment of students in method {}: Canceled Reservation ID: {}", name, canceledReservationId);
    }

    @AfterThrowing("execution(* tn.esprit.foyer.services.*.*(..))")
    public void logMethodExit2(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        log.error("Out of method with errors: " + name);
    }

    @After("execution(* tn.esprit.foyer.services.*.*(..))")
    public void logMethodExit(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        log.info("Out of method: " + name);
    }
}