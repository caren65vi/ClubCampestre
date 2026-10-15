package com.clubcampestre.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clubcampestre.dto.CambioEstadoDTO;
import com.clubcampestre.dto.EmpleadoDTO;
import com.clubcampestre.dto.HistorialEmpleadoDTO;
import com.clubcampestre.service.EmpleadoService;

import jakarta.validation.Valid;

/**
 * Controlador REST del módulo Gestión de Empleados (/api/empleados).
 * Expone el CRUD de empleados (RF-0110 a RF-0114): registrar, consultar, actualizar,
 * dar de baja, cambiar estado y consultar la bitácora de cambios.
 */
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;


    /** GET /api/empleados - RF-0112: lista los empleados; con nombre, idArea o estado aplica filtros. */
    @GetMapping
    public List<EmpleadoDTO> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idArea,
            @RequestParam(required = false) String estado) {

        if (nombre == null && idArea == null && estado == null) {
            return empleadoService.listarTodos();
        }
        return empleadoService.buscarConFiltros(nombre, idArea, estado);
    }

    /** GET /api/empleados/{numeroDocumento} - RF-0112: consulta un empleado por documento (404 si no existe). */
    @GetMapping("/{numeroDocumento}")
    public EmpleadoDTO buscar(@PathVariable String numeroDocumento) {
        return empleadoService.buscarPorId(numeroDocumento);
    }


    /** POST /api/empleados - RF-0111: registra un empleado (400 datos inválidos, 409 documento duplicado). */
    @PostMapping
    public EmpleadoDTO registrarEmpleadoAdministrador(@Valid @RequestBody EmpleadoDTO dto) {
        return empleadoService.registrarEmpleadoAdministrador(dto);
    }


    /** PUT /api/empleados/{numeroDocumento} - RF-0113: actualiza la información del empleado. */
    @PutMapping("/{numeroDocumento}")
    public EmpleadoDTO modificarEmpleadoAdministrador(@PathVariable String numeroDocumento,
            @Valid @RequestBody EmpleadoDTO dto) {
        return empleadoService.modificarEmpleadoAdministrador(numeroDocumento, dto);
    }

    /** DELETE /api/empleados/{numeroDocumento} - Baja lógica: el empleado pasa a inactivo sin perder su historial. */
    @DeleteMapping("/{numeroDocumento}")
    public EmpleadoDTO eliminarEmpleadoAdministrador(@PathVariable String numeroDocumento) {
        return empleadoService.eliminarEmpleadoAdministrador(numeroDocumento);
    }

    /** PATCH /api/empleados/{numeroDocumento}/estado - RF-0114: cambia el estado con fecha efectiva y motivo. */
    @PatchMapping("/{numeroDocumento}/estado")
    public EmpleadoDTO cambiarEstado(@PathVariable String numeroDocumento,
            @Valid @RequestBody CambioEstadoDTO dto) {
        return empleadoService.cambiarEstado(numeroDocumento, dto);
    }

    /** GET /api/empleados/{numeroDocumento}/historial - Bitácora de cambios del empleado. */
    @GetMapping("/{numeroDocumento}/historial")
    public List<HistorialEmpleadoDTO> historial(@PathVariable String numeroDocumento) {
        return empleadoService.listarHistorial(numeroDocumento);
    }
}
