package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void testCreateAppointmentSuccess() {
        Appointment appointment = new Appointment();
        appointment.setPatientId(1);
        when(patientRepository.existsById(1)).thenReturn(true);
        when(appointmentRepository.save(appointment)).thenReturn(appointment);

        ResponseEntity<?> response = appointmentController.createAppointment(appointment);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(appointment, response.getBody());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void testCreateAppointmentPatientNotFound() {
        Appointment appointment = new Appointment();
        appointment.setPatientId(1);
        when(patientRepository.existsById(1)).thenReturn(false);

        ResponseEntity<?> response = appointmentController.createAppointment(appointment);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    void testGetAllAppointments() {
        List<Appointment> appointments = List.of(new Appointment());
        when(appointmentRepository.findAll()).thenReturn(appointments);

        ResponseEntity<Iterable<Appointment>> response = appointmentController.getAllAppointments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointments, response.getBody());
    }

}