package com.clubcampestre.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.clubcampestre.dto.CambioEstadoDTO;
import com.clubcampestre.dto.EmpleadoDTO;
import com.clubcampestre.entity.Cargo;
import com.clubcampestre.entity.Empleado;
import com.clubcampestre.entity.RolUsuario;
import com.clubcampestre.entity.TipoContacto;
import com.clubcampestre.entity.TipoDocumento;
import com.clubcampestre.entity.UsuarioTrabajador;
import com.clubcampestre.exception.RecursoDuplicadoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.CargoRepository;
import com.clubcampestre.repository.ContactoRepository;
import com.clubcampestre.repository.EmpleadoRepository;
import com.clubcampestre.repository.HistorialEmpleadoRepository;
import com.clubcampestre.repository.RolUsuarioRepository;
import com.clubcampestre.repository.TipoContactoRepository;
import com.clubcampestre.repository.TipoDocumentoRepository;
import com.clubcampestre.repository.UsuarioTrabajadorRepository;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceValidacionTest {

    @Mock private EmpleadoRepository empleadoRepository;
    @Mock private CargoRepository cargoRepository;
    @Mock private TipoDocumentoRepository tipoDocumentoRepository;
    @Mock private TipoContactoRepository tipoContactoRepository;
    @Mock private ContactoRepository contactoRepository;
    @Mock private RolUsuarioRepository rolUsuarioRepository;
    @Mock private UsuarioTrabajadorRepository usuarioRepository;
    @Mock private HistorialEmpleadoRepository historialRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmpleadoService empleadoService;

    private static EmpleadoDTO empleadoValido() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNumeroDocumento("1020304050");
        dto.setIdTipoDocumento(1);
        dto.setPrimerNombre("Mateo");
        dto.setPrimerApellido("Restrepo");
        dto.setTelefono("3124567890");
        dto.setIndicativo("+57");
        dto.setCorreo("mateo@correo.com");
        dto.setIdCargo(2);
        dto.setFechaContratacion(LocalDate.of(2026, 1, 15));
        dto.setSalario(new BigDecimal("1750905"));
        dto.setCrearUsuario(true);
        dto.setIdRol(1);
        dto.setNombreUsuario("mrestrepo");
        dto.setContrasena("Clave2026");
        return dto;
    }

    // Deja pasar el registro del empleado hasta la creación del usuario
    private void prepararRegistroHastaUsuario() {
        when(empleadoRepository.existsById("1020304050")).thenReturn(false);
        when(tipoDocumentoRepository.findById(1)).thenReturn(Optional.of(new TipoDocumento()));
        when(cargoRepository.findById(2)).thenReturn(Optional.of(new Cargo()));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(returnsFirstArg());
        when(tipoContactoRepository.findByNombreTipoContactoIgnoreCase(anyString()))
                .thenReturn(Optional.of(new TipoContacto()));
        when(contactoRepository.findByEmpleado_IdEmpleadoAndTipoContacto_IdTipoContacto(any(), any()))
                .thenReturn(Optional.empty());
        when(rolUsuarioRepository.findById(1)).thenReturn(Optional.of(new RolUsuario()));
    }

    // Campo obligatorio (reglas del servicio, además de las anotaciones del DTO)

    @Test
    void registrarSinCelularLanzaError() {
        EmpleadoDTO dto = empleadoValido();
        dto.setTelefono(" ");
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> empleadoService.registrarEmpleadoAdministrador(dto));
        assertEquals("El número de celular es obligatorio", ex.getMessage());
    }

    @Test
    void registrarSinCorreoLanzaError() {
        EmpleadoDTO dto = empleadoValido();
        dto.setCorreo(null);
        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> empleadoService.registrarEmpleadoAdministrador(dto));
        assertEquals("El correo electrónico es obligatorio", ex.getMessage());
    }

    // Duplicidad

    @Test
    void documentoDuplicadoNoSeRegistra() {
        when(empleadoRepository.existsById("1020304050")).thenReturn(true);

        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> empleadoService.registrarEmpleadoAdministrador(empleadoValido()));

        assertEquals("Ya existe un empleado con el documento 1020304050", ex.getMessage());
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    void nombreDeUsuarioDuplicadoNoSeRegistra() {
        prepararRegistroHastaUsuario();
        when(usuarioRepository.findByNombreUsuario("mrestrepo"))
                .thenReturn(Optional.of(new UsuarioTrabajador()));

        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> empleadoService.registrarEmpleadoAdministrador(empleadoValido()));

        assertEquals("Ya existe el nombre de usuario mrestrepo", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // Longitud mínima

    @Test
    void contrasenaDeMenosDe8CaracteresSeRechaza() {
        prepararRegistroHastaUsuario();
        when(usuarioRepository.findByNombreUsuario("mrestrepo")).thenReturn(Optional.empty());
        EmpleadoDTO dto = empleadoValido();
        dto.setContrasena("Clave1");

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> empleadoService.registrarEmpleadoAdministrador(dto));

        assertEquals("La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número",
                ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // Fecha no permitida

    @Test
    void fechaEfectivaAnteriorAlIngresoSeRechaza() {
        Empleado empleado = new Empleado();
        empleado.setIdEmpleado("1020304050");
        empleado.setEstado("activo");
        empleado.setFechaContratacion(LocalDate.of(2026, 1, 15));
        when(empleadoRepository.findById("1020304050")).thenReturn(Optional.of(empleado));

        CambioEstadoDTO dto = new CambioEstadoDTO();
        dto.setEstado("vacaciones");
        dto.setFechaEfectiva(LocalDate.of(2025, 12, 31));
        dto.setMotivo("Vacaciones");

        SolicitudInvalidaException ex = assertThrows(SolicitudInvalidaException.class,
                () -> empleadoService.cambiarEstado("1020304050", dto));

        assertEquals("La fecha efectiva no puede ser anterior a la fecha de ingreso", ex.getMessage());
        verify(empleadoRepository, never()).save(any());
    }

    // Caracteres especiales / valores fuera de catálogo

    @Test
    void estadoNoPermitidoSeRechaza() {
        CambioEstadoDTO dto = new CambioEstadoDTO();
        dto.setEstado("suspendido!");
        dto.setFechaEfectiva(LocalDate.of(2026, 6, 1));
        dto.setMotivo("Prueba");

        assertThrows(SolicitudInvalidaException.class,
                () -> empleadoService.cambiarEstado("1020304050", dto));
    }
}
