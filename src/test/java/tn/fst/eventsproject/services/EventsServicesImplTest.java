package tn.fst.eventsproject.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.fst.eventsproject.entities.Event;
import tn.fst.eventsproject.entities.Logistics;
import tn.fst.eventsproject.entities.Participant;
import tn.fst.eventsproject.entities.Tache;
import tn.fst.eventsproject.repositories.EventRepository;
import tn.fst.eventsproject.repositories.LogisticsRepository;
import tn.fst.eventsproject.repositories.ParticipantRepository;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class EventServicesImplTest {


    @Mock
    private EventRepository eventRepository;


    @Mock
    private ParticipantRepository participantRepository;


    @Mock
    private LogisticsRepository logisticsRepository;


    @InjectMocks
    private EventServicesImpl eventServices;


    private Participant participant;
    private Event event;
    private Logistics logistics;


    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("Tounsi");
        participant.setPrenom("Ahmed");
        participant.setTache(Tache.ORGANISATEUR);
        participant.setEvents(new HashSet<>());


        event = new Event();
        event.setIdEvent(1);
        event.setDescription("Test Event");
        event.setParticipants(new HashSet<>());
        event.setLogistics(new HashSet<>());


        logistics = new Logistics();
        logistics.setIdLog(1);
        logistics.setPrixUnit(100f);
        logistics.setQuantite(5);
        logistics.setReserve(true);
    }


    @Test
    void addParticipant() {
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);
        Participant savedParticipant = eventServices.addParticipant(participant);
        assertNotNull(savedParticipant);
        verify(participantRepository, times(1)).save(participant);
    }


    @Test
    void addAffectEvenParticipant_WithId() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenReturn(event);


        Event savedEvent = eventServices.addAffectEvenParticipant(event, 1);


        assertNotNull(savedEvent);
        assertTrue(participant.getEvents().contains(event));
        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(event);
    }


    @Test
    void addAffectEvenParticipant_WithObject() {
        // Prepare data
        Set<Participant> participants = new HashSet<>();
        participants.add(participant);
        event.setParticipants(participants);


        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenReturn(event);


        Event savedEvent = eventServices.addAffectEvenParticipant(event);


        assertNotNull(savedEvent);
        assertTrue(participant.getEvents().contains(event));
        verify(eventRepository, times(1)).save(event);
    }


    @Test
    void addAffectLog() {
        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(any(Logistics.class))).thenReturn(logistics);


        Logistics savedLogistics = eventServices.addAffectLog(logistics, "Test Event");


        assertNotNull(savedLogistics);
        assertTrue(event.getLogistics().contains(logistics));
        verify(eventRepository, times(1)).findByDescription("Test Event");
        verify(logisticsRepository, times(1)).save(logistics);
    }


    @Test
    void getLogisticsDates() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
       
        event.getLogistics().add(logistics);
        List<Event> events = new ArrayList<>();
        events.add(event);


        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(events);


        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);


        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(logistics, result.get(0));
    }
   
    @Test
    void getLogisticsDates_EmptyLogistics() {
         LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
       
        // Event with no logistics
        Event emptyEvent = new Event();
        emptyEvent.setLogistics(new HashSet<>());
        List<Event> events = new ArrayList<>();
        events.add(emptyEvent);


        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(events);


        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);


        assertNull(result); // Based on code: if(event.getLogistics().isEmpty()) return null;
    }




    @Test
    void calculCout() {
        event.getLogistics().add(logistics);
        List<Event> events = new ArrayList<>();
        events.add(event);


        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR)).thenReturn(events);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
       
        eventServices.calculCout();


        assertEquals(500f, event.getCout()); // 100 * 5
        verify(eventRepository, times(1)).save(event);
    }
}
