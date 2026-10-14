package com.clubcampestre.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

// Pruebas de las anotaciones de EmpleadoDTO / CambioEstadoDTO (lo que responde 400 vía @Valid)
class EmpleadoDTOValidacionTest {

    private static ValidatorFactory factory;
    private static Validator validator;
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @BeforeAll
    static void iniciar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrar() {
        factory.close();
    }

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
        return dto;
    }

    private static Set<String> camposConError(Object dto) {
        return validator.validate(dto).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    private static String mensaje(Object dto, String campo) {
        return validator.validate(dto).stream()
                .filter(v -> v.getPropertyPath().toString().equals(campo))
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse(null);
    }

    @Test
    void empleadoValidoNoTieneErrores() {
        assertTrue(camposConError(empleadoValido()).isEmpty());
    }

    @Nested
    class CampoObligatorio {

        @Test
        void documentoVacio() {
            EmpleadoDTO dto = empleadoValido();
            dto.setNumeroDocumento("  ");
            assertEquals("El número de documento es obligatorio", mensaje(dto, "numeroDocumento"));
        }

        @Test
        void nombreYApellidoVacios() {
            EmpleadoDTO dto = empleadoValido();
            dto.setPrimerNombre("");
            dto.setPrimerApellido(null);
            assertEquals("El primer nombre es obligatorio", mensaje(dto, "primerNombre"));
            assertEquals("El primer apellido es obligatorio", mensaje(dto, "primerApellido"));
        }

        @Test
        void camposDeSeleccionSinValor() {
            EmpleadoDTO dto = empleadoValido();
            dto.setIdTipoDocumento(null);
            dto.setIdCargo(null);
            dto.setFechaContratacion(null);
            dto.setSalario(null);
            assertEquals(Set.of("idTipoDocumento", "idCargo", "fechaContratacion", "salario"), camposConError(dto));
        }

        @Test
        void cambioEstadoSinMotivo() {
            CambioEstadoDTO dto = cambioEstadoValido();
            dto.setMotivo("");
            assertEquals("El motivo o acta es obligatorio", mensaje(dto, "motivo"));
        }
    }

    @Nested
    class LongitudMaxima {

        @Test
        void documentoDeMasDe20Caracteres() {
            EmpleadoDTO dto = empleadoValido();
            dto.setNumeroDocumento("1".repeat(21));
            assertEquals(Set.of("numeroDocumento"), camposConError(dto));
        }

        @Test
        void documentoDeExactamente20Caracteres() {
            EmpleadoDTO dto = empleadoValido();
            dto.setNumeroDocumento("1".repeat(20));
            assertTrue(camposConError(dto).isEmpty());
        }

        @Test
        void celularDeMasDe20Caracteres() {
            EmpleadoDTO dto = empleadoValido();
            dto.setTelefono("3".repeat(21));
            assertEquals(Set.of("telefono"), camposConError(dto));
        }

        @Test
        void nombreUsuarioDeMasDe50Caracteres() {
            EmpleadoDTO dto = empleadoValido();
            dto.setNombreUsuario("u".repeat(51));
            assertEquals(Set.of("nombreUsuario"), camposConError(dto));
        }

        @Test
        void motivoDeMasDe300Caracteres() {
            CambioEstadoDTO dto = cambioEstadoValido();
            dto.setMotivo("m".repeat(301));
            assertEquals("El motivo no puede superar 300 caracteres", mensaje(dto, "motivo"));
        }
    }

    @Nested
    class Numero {

        @Test
        void salarioPositivoEsValido() {
            EmpleadoDTO dto = empleadoValido();
            dto.setSalario(new BigDecimal("0.01"));
            assertTrue(camposConError(dto).isEmpty());
        }

        @Test
        void salarioCeroONegativoEsInvalido() {
            EmpleadoDTO dto = empleadoValido();
            dto.setSalario(BigDecimal.ZERO);
            assertEquals("El salario debe ser mayor a 0", mensaje(dto, "salario"));
            dto.setSalario(new BigDecimal("-1500"));
            assertEquals("El salario debe ser mayor a 0", mensaje(dto, "salario"));
        }

        @Test
        void textoEnSalarioSeRechazaAlLeerElJson() {
            String json = "{\"salario\": \"mil quinientos\"}";
            assertThrows(JacksonException.class, () -> JSON.readValue(json, EmpleadoDTO.class));
        }

        @Test
        void textoEnIdCargoSeRechazaAlLeerElJson() {
            String json = "{\"idCargo\": \"gerente\"}";
            assertThrows(JacksonException.class, () -> JSON.readValue(json, EmpleadoDTO.class));
        }
    }

    @Nested
    class Fecha {

        @Test
        void fechaIsoValidaSeAcepta() {
            EmpleadoDTO dto = JSON.readValue("{\"fechaContratacion\": \"2026-01-15\"}", EmpleadoDTO.class);
            assertEquals(LocalDate.of(2026, 1, 15), dto.getFechaContratacion());
        }

        @Test
        void fechaConFormatoIncorrectoSeRechaza() {
            String json = "{\"fechaContratacion\": \"15/01/2026\"}";
            assertThrows(JacksonException.class, () -> JSON.readValue(json, EmpleadoDTO.class));
        }

        @Test
        void fechaInexistenteSeRechaza() {
            String json = "{\"fechaContratacion\": \"2026-02-30\"}";
            assertThrows(JacksonException.class, () -> JSON.readValue(json, EmpleadoDTO.class));
        }
    }

    @Nested
    class CaracteresEspeciales {

        @Test
        void correoConFormatoInvalido() {
            EmpleadoDTO dto = empleadoValido();
            dto.setCorreo("mateo@@correo.com");
            assertEquals("El correo electrónico no es válido", mensaje(dto, "correo"));
        }

        @Test
        void contactoAdicionalInvalidoSeValidaEnCascada() {
            ContactoDTO contacto = new ContactoDTO();
            contacto.setIdTipoContacto(null);
            contacto.setValor("");
            EmpleadoDTO dto = empleadoValido();
            dto.setContactosAdicionales(java.util.List.of(contacto));
            assertEquals(
                    Set.of("contactosAdicionales[0].idTipoContacto", "contactosAdicionales[0].valor"),
                    camposConError(dto));
        }
    }

    private static CambioEstadoDTO cambioEstadoValido() {
        CambioEstadoDTO dto = new CambioEstadoDTO();
        dto.setEstado("vacaciones");
        dto.setFechaEfectiva(LocalDate.of(2026, 6, 1));
        dto.setMotivo("Vacaciones programadas");
        return dto;
    }
}
