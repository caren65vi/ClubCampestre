package com.clubcampestre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.clubcampestre.dto.HabitacionDTO;
import com.clubcampestre.dto.TemporadaDTO;
import com.clubcampestre.exception.RecursoDuplicadoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.CaracteristicaRepository;
import com.clubcampestre.repository.HabitacionRepository;
import com.clubcampestre.repository.TemporadaRepository;
import com.clubcampestre.repository.TipoHabitacionRepository;

@ExtendWith(MockitoExtension.class)
class TemporadaHabitacionValidacionTest {

    @Mock private TemporadaRepository temporadaRepository;
    @Mock private HabitacionRepository habitacionRepository;
    @Mock private TipoHabitacionRepository tipoHabitacionRepository;
    @Mock private CaracteristicaRepository caracteristicaRepository;

    @InjectMocks private TemporadaService temporadaService;
    @InjectMocks private HabitacionService habitacionService;

    private static TemporadaDTO temporada(LocalDate inicio, LocalDate fin, String tipo) {
        TemporadaDTO dto = new TemporadaDTO();
        dto.setFechaInicio(inicio);
        dto.setFechaFin(fin);
        dto.setTipoTemporada(tipo);
        dto.setNombreTemporada("Fin de año");
        return dto;
    }

    // Fecha no permitida

    @Test
    void fechaFinAnteriorAInicioSeRechaza() {
        TemporadaDTO dto = temporada(LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 1), "Alta");

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> temporadaService.crear(dto));

        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", ex.getMessage());
        verify(temporadaRepository, never()).save(any());
    }

    @Test
    void tipoDeTemporadaFueraDeCatalogoSeRechaza() {
        TemporadaDTO dto = temporada(LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 20), "Invierno");

        assertThrows(SolicitudInvalidaException.class, () -> temporadaService.crear(dto));
        verify(temporadaRepository, never()).save(any());
    }

    // Duplicidad

    @Test
    void numeroDeHabitacionDuplicadoNoSeRegistra() {
        HabitacionDTO dto = new HabitacionDTO();
        dto.setNumeroHabitacion(101);
        dto.setIdTipoHabitacion(1);
        when(habitacionRepository.existsById(101)).thenReturn(true);

        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> habitacionService.crear(dto));

        assertEquals("Ya existe una habitación con el número 101", ex.getMessage());
        verify(habitacionRepository, never()).save(any());
    }
}
