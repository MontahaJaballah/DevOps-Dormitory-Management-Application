package tn.esprit.foyer.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.foyer.entities.Chambre;
import tn.esprit.foyer.entities.Etudiant;
import tn.esprit.foyer.entities.Reservation;
import tn.esprit.foyer.entities.TypeChambre;
import tn.esprit.foyer.repository.ChambreRepository;
import tn.esprit.foyer.repository.EtudiantRepository;
import tn.esprit.foyer.repository.ReservationRepository;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceImplTest {

    // Mockito setup for tests that require mocking
    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private ChambreRepository chambreRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServicImpl reservationService;

    private Etudiant etudiant;
    private Chambre chambre;
    private Reservation reservation;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        // Initialize test data for Mockito tests
        etudiant = new Etudiant();
        etudiant.setCin(12345678L);

        chambre = new Chambre();
        chambre.setNumeroChambre(101L);
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.setReservations(new ArrayList<>());

        reservation = new Reservation();
        reservation.setEstValid(false); // Will be set to true by the method
        reservation.setEtudiants(new ArrayList<>());

        startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
    }

    // ------------------- Pure JUnit Tests (No Mocking) -------------------

    @Test
    void testGenerateReservationId() {
        // Scenario: Test the reservation ID generation logic
        // Arrange
        Long numChambre = 101L;
        Long cin = 12345678L;
        int year = 2025;
        ReservationServicImpl service = new ReservationServicImpl();

        // Act
        String reservationId = service.generateReservationId(numChambre, cin, year);

        // Assert
        assertEquals("101123456782025", reservationId);
    }

    // ------------------- Mockito Tests (With Mocking) -------------------

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_NoPriorReservations() {
        // Scenario: Room has 0 reservations, should add the reservation
        // Arrange
        when(etudiantRepository.findByCin(12345678L)).thenReturn(etudiant);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(reservationRepository.getReservationsCurrentYear(startDate, endDate, 101L)).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(reservation, 101L, 12345678L);

        // Assert
        verify(reservationRepository, times(1)).save(reservation);
        verify(chambreRepository, times(1)).save(chambre);
        assertTrue(reservation.getEstValid()); // Use correct getter for Boolean
        assertEquals(1, reservation.getEtudiants().size());
        assertTrue(reservation.getEtudiants().contains(etudiant));
        assertEquals(1, chambre.getReservations().size());
        assertTrue(chambre.getReservations().contains(reservation));
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_SimpleRoomFull() {
        // Scenario: Room is SIMPLE with 1 reservation, should not add
        // Arrange
        chambre.setTypeC(TypeChambre.SIMPLE);
        chambre.getReservations().add(new Reservation());
        when(etudiantRepository.findByCin(12345678L)).thenReturn(etudiant);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(reservationRepository.getReservationsCurrentYear(startDate, endDate, 101L)).thenReturn(1);

        // Act
        reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(reservation, 101L, 12345678L);

        // Assert
        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(chambreRepository, never()).save(any(Chambre.class));
        assertTrue(reservation.getEstValid()); // Use correct getter for Boolean
        assertEquals(1, reservation.getEtudiants().size());
        assertTrue(reservation.getEtudiants().contains(etudiant));
        assertEquals(1, chambre.getReservations().size()); // No change
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_DoubleRoomWithOneReservation() {
        // Scenario: Room is DOUBLE with 1 reservation, should add
        // Arrange
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.getReservations().add(new Reservation());
        when(etudiantRepository.findByCin(12345678L)).thenReturn(etudiant);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(reservationRepository.getReservationsCurrentYear(startDate, endDate, 101L)).thenReturn(1);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(reservation, 101L, 12345678L);

        // Assert
        verify(reservationRepository, times(1)).save(reservation);
        verify(chambreRepository, times(1)).save(chambre);
        assertEquals(2, chambre.getReservations().size());
        assertTrue(chambre.getReservations().contains(reservation));
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_TripleRoomWithTwoReservations() {
        // Scenario: Room is TRIPLE with 2 reservations, should add
        // Arrange
        chambre.setTypeC(TypeChambre.TRIPLE);
        chambre.getReservations().add(new Reservation());
        chambre.getReservations().add(new Reservation());
        when(etudiantRepository.findByCin(12345678L)).thenReturn(etudiant);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(reservationRepository.getReservationsCurrentYear(startDate, endDate, 101L)).thenReturn(2);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(reservation, 101L, 12345678L);

        // Assert
        verify(reservationRepository, times(1)).save(reservation);
        verify(chambreRepository, times(1)).save(chambre);
        assertEquals(3, chambre.getReservations().size());
        assertTrue(chambre.getReservations().contains(reservation));
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_DoubleRoomFull() {
        // Scenario: Room is DOUBLE with 2 reservations, should not add
        // Arrange
        chambre.setTypeC(TypeChambre.DOUBLE);
        chambre.getReservations().add(new Reservation());
        chambre.getReservations().add(new Reservation());
        when(etudiantRepository.findByCin(12345678L)).thenReturn(etudiant);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(reservationRepository.getReservationsCurrentYear(startDate, endDate, 101L)).thenReturn(2);

        // Act
        reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(reservation, 101L, 12345678L);

        // Assert
        verify(reservationRepository, never()).save(any(Reservation.class));
        verify(chambreRepository, never()).save(any(Chambre.class));
        assertEquals(2, chambre.getReservations().size());
    }

    @Test
    void testAjouterReservationEtAssignerAChambreEtAEtudiant_ReservationsListNull() {
        // Scenario: Room's reservations list is null, should create new list and add
        // Arrange
        chambre.setReservations(null);
        when(etudiantRepository.findByCin(12345678L)).thenReturn(etudiant);
        when(chambreRepository.findByNumeroChambre(101L)).thenReturn(chambre);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        reservationService.ajouterReservationEtAssignerAChambreEtAEtudiant(reservation, 101L, 12345678L);

        // Assert
        verify(reservationRepository, times(1)).save(reservation);
        verify(chambreRepository, times(1)).save(chambre);
        assertNotNull(chambre.getReservations());
        assertEquals(1, chambre.getReservations().size());
        assertTrue(chambre.getReservations().contains(reservation));
    }
}