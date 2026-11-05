package com.airport.passengers.service;

import com.airport.common.exception.BusinessRuleException;
import com.airport.common.exception.ResourceNotFoundException;
import com.airport.common.util.CodeGenerator;
import com.airport.operations.entity.StatutVol;
import com.airport.operations.entity.Vol;
import com.airport.operations.service.VolService;
import com.airport.passengers.dto.AssistanceResponse;
import com.airport.passengers.dto.AvisResponse;
import com.airport.passengers.dto.BagageResponse;
import com.airport.passengers.dto.BilletResponse;
import com.airport.passengers.dto.PaiementRequest;
import com.airport.passengers.dto.PaiementResponse;
import com.airport.passengers.dto.ReservationDetailResponse;
import com.airport.passengers.dto.ReservationRequest;
import com.airport.passengers.dto.ReservationResponse;
import com.airport.passengers.entity.AffectationSiegeBillet;
import com.airport.passengers.entity.Billet;
import com.airport.passengers.entity.Enregistrement;
import com.airport.passengers.entity.Paiement;
import com.airport.passengers.entity.Passager;
import com.airport.passengers.entity.Reservation;
import com.airport.passengers.entity.StatutAssistance;
import com.airport.passengers.entity.StatutBillet;
import com.airport.passengers.entity.StatutPaiement;
import com.airport.passengers.entity.StatutReservation;
import com.airport.passengers.repository.AffectationSiegeBilletRepository;
import com.airport.passengers.repository.AvisPassagerRepository;
import com.airport.passengers.repository.BagageRepository;
import com.airport.passengers.repository.BilletRepository;
import com.airport.passengers.repository.DemandeAssistancePassagerRepository;
import com.airport.passengers.repository.EnregistrementRepository;
import com.airport.passengers.repository.PaiementRepository;
import com.airport.passengers.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Booking, simulated payment, ticket issue and cancellation.
 *
 * Business rules implemented here:
 *  1. A flight can be booked only if it is PROGRAMME or RETARDE and has not departed.
 *  2. A passenger cannot hold two active reservations on the same flight.
 *  3. No overbooking: active reservations in a class <= seats of that class.
 *  4. The amount is computed by the server from the class, never sent by the browser.
 *  5. Only a reservation waiting for payment can be paid; one accepted payment only.
 *  6. An accepted payment confirms the reservation and issues the ticket.
 *  7. A checked-in reservation cannot be cancelled; cancelling frees the seat
 *     and cancels the active assistance requests of the trip.
 */
@Service
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaiementRepository paiementRepository;
    private final BilletRepository billetRepository;
    private final AffectationSiegeBilletRepository affectationRepository;
    private final EnregistrementRepository enregistrementRepository;
    private final BagageRepository bagageRepository;
    private final DemandeAssistancePassagerRepository assistanceRepository;
    private final AvisPassagerRepository avisRepository;
    private final PassagerService passagerService;
    private final VolService volService;
    private final Clock clock;

    public ReservationService(ReservationRepository reservationRepository,
                              PaiementRepository paiementRepository,
                              BilletRepository billetRepository,
                              AffectationSiegeBilletRepository affectationRepository,
                              EnregistrementRepository enregistrementRepository,
                              BagageRepository bagageRepository,
                              DemandeAssistancePassagerRepository assistanceRepository,
                              AvisPassagerRepository avisRepository,
                              PassagerService passagerService,
                              VolService volService,
                              Clock clock) {
        this.reservationRepository = reservationRepository;
        this.paiementRepository = paiementRepository;
        this.billetRepository = billetRepository;
        this.affectationRepository = affectationRepository;
        this.enregistrementRepository = enregistrementRepository;
        this.bagageRepository = bagageRepository;
        this.assistanceRepository = assistanceRepository;
        this.avisRepository = avisRepository;
        this.passagerService = passagerService;
        this.volService = volService;
        this.clock = clock;
    }

    // ------------------------------------------------------------ reading

    @Transactional(readOnly = true)
    public List<ReservationResponse> findAll(StatutReservation statut, Long volId, Long passagerId) {
        return reservationRepository.findWithFilters(statut, volId, passagerId)
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationDetailResponse findDetail(Long id) {
        Reservation reservation = reservationRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", id));

        List<PaiementResponse> paiements = paiementRepository.findByReservationIdOrderByDatePaiementDesc(id)
                .stream().map(PaiementResponse::from).toList();

        BilletResponse billetResponse = null;
        List<BagageResponse> bagages = List.of();

        Optional<Billet> billet = billetRepository.findByReservationId(id);
        if (billet.isPresent()) {
            Long billetId = billet.get().getId();
            AffectationSiegeBillet affectation = affectationRepository.findByBilletId(billetId).orElse(null);
            Enregistrement enregistrement = enregistrementRepository.findByBilletId(billetId).orElse(null);
            billetResponse = BilletResponse.from(billet.get(), affectation, enregistrement);
            if (enregistrement != null) {
                bagages = bagageRepository.findByEnregistrementIdOrderByIdAsc(enregistrement.getId())
                        .stream().map(BagageResponse::from).toList();
            }
        }

        List<AssistanceResponse> assistances = assistanceRepository.findByReservationIdOrderByDateDemandeAsc(id)
                .stream().map(AssistanceResponse::from).toList();
        AvisResponse avis = avisRepository.findByReservationId(id).map(AvisResponse::from).orElse(null);

        return new ReservationDetailResponse(ReservationResponse.from(reservation), paiements, billetResponse,
                bagages, assistances, avis);
    }

    // ------------------------------------------------------------ booking

    public ReservationResponse create(ReservationRequest request) {
        Passager passager = passagerService.findEntity(request.passagerId());
        Vol vol = volService.findEntity(request.volId());

        // Rule 1
        boolean statutOuvert = vol.getStatut() == StatutVol.PROGRAMME || vol.getStatut() == StatutVol.RETARDE;
        if (!statutOuvert || !vol.getDateHeureDepart().isAfter(LocalDateTime.now(clock))) {
            throw new BusinessRuleException("Ce vol n'est plus ouvert à la réservation");
        }

        // Rule 2
        if (reservationRepository.existsByPassagerIdAndVolIdAndStatutNot(
                passager.getId(), vol.getId(), StatutReservation.ANNULEE)) {
            throw new BusinessRuleException("Ce passager a déjà une réservation active sur ce vol");
        }

        // Rule 3
        long placesPrises = reservationRepository.countByVolIdAndClasseAndStatutNot(
                vol.getId(), request.classe(), StatutReservation.ANNULEE);
        int placesClasse = SeatLayout.nombreSieges(vol.getCapacite(), request.classe());
        if (placesPrises >= placesClasse) {
            throw new BusinessRuleException("Plus aucune place disponible dans cette classe sur ce vol");
        }

        Reservation reservation = new Reservation();
        reservation.setReference(generateReference());
        reservation.setPassager(passager);
        reservation.setVol(vol);
        reservation.setClasse(request.classe());
        reservation.setMontant(request.classe().getPrixBase()); // Rule 4
        reservation.setStatut(StatutReservation.EN_ATTENTE_PAIEMENT);

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    // ------------------------------------------------------------ payment

    public PaiementResponse payer(Long reservationId, PaiementRequest request) {
        Reservation reservation = findEntity(reservationId);

        // Rule 5
        if (reservation.getStatut() != StatutReservation.EN_ATTENTE_PAIEMENT) {
            throw new BusinessRuleException("Cette réservation n'est pas en attente de paiement");
        }

        Paiement paiement = new Paiement();
        paiement.setReservation(reservation);
        paiement.setMontant(reservation.getMontant());
        paiement.setMode(request.mode());
        paiement.setStatut(request.simulerRefus() ? StatutPaiement.REFUSE : StatutPaiement.ACCEPTE);
        paiement.setReferenceTransaction(generateTransactionReference());
        paiementRepository.save(paiement);

        // Rule 6
        if (paiement.getStatut() == StatutPaiement.ACCEPTE) {
            reservation.setStatut(StatutReservation.CONFIRMEE);
            Billet billet = new Billet();
            billet.setReservation(reservation);
            billet.setNumeroBillet(generateNumeroBillet());
            billet.setStatut(StatutBillet.EMIS);
            billetRepository.save(billet);
        }
        // A refused payment is still saved: the history of attempts is kept.
        return PaiementResponse.from(paiement);
    }

    // ------------------------------------------------------------ cancellation

    public ReservationResponse annuler(Long reservationId) {
        Reservation reservation = findEntity(reservationId);

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessRuleException("Cette réservation est déjà annulée");
        }

        Optional<Billet> billet = billetRepository.findByReservationId(reservationId);
        if (billet.isPresent()) {
            Long billetId = billet.get().getId();
            // Rule 7
            if (enregistrementRepository.findByBilletId(billetId).isPresent()) {
                throw new BusinessRuleException("Le passager est déjà enregistré : annulation impossible");
            }
            affectationRepository.findByBilletId(billetId).ifPresent(affectationRepository::delete);
            billet.get().setStatut(StatutBillet.ANNULE);
        }

        LocalDateTime maintenant = LocalDateTime.now(clock);
        assistanceRepository.findByReservationIdOrderByDateDemandeAsc(reservationId).stream()
                .filter(d -> d.getStatut().canTransitionTo(StatutAssistance.ANNULEE))
                .forEach(d -> {
                    d.setStatut(StatutAssistance.ANNULEE);
                    d.setDateMiseAJour(maintenant);
                });

        reservation.setStatut(StatutReservation.ANNULEE);
        reservation.setDateAnnulation(maintenant);
        return ReservationResponse.from(reservation);
    }

    // ------------------------------------------------------------ helpers

    public Reservation findEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", id));
    }

    private String generateReference() {
        String reference;
        do {
            reference = CodeGenerator.alphanumeric(6);
        } while (reservationRepository.existsByReference(reference));
        return reference;
    }

    private String generateTransactionReference() {
        String reference;
        do {
            reference = "TRX-" + CodeGenerator.alphanumeric(12);
        } while (paiementRepository.existsByReferenceTransaction(reference));
        return reference;
    }

    /** 13 characters: "TKT" + 10 digits. */
    private String generateNumeroBillet() {
        String numero;
        do {
            numero = "TKT" + CodeGenerator.digits(10);
        } while (billetRepository.existsByNumeroBillet(numero));
        return numero;
    }
}
