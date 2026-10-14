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

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;


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

    @GetMapping("/{numeroDocumento}")
    public EmpleadoDTO buscar(@PathVariable String numeroDocumento) {
        return empleadoService.buscarPorId(numeroDocumento);
    }


    @PostMapping
    public EmpleadoDTO registrarEmpleadoAdministrador(@Valid @RequestBody EmpleadoDTO dto) {
        return empleadoService.registrarEmpleadoAdministrador(dto);
    }


    @PutMapping("/{numeroDocumento}")
    public EmpleadoDTO modificarEmpleadoAdministrador(@PathVariable String numeroDocumento,
            @Valid @RequestBody EmpleadoDTO dto) {
        return empleadoService.modificarEmpleadoAdministrador(numeroDocumento, dto);
    }

    @DeleteMapping("/{numeroDocumento}")
    public EmpleadoDTO eliminarEmpleadoAdministrador(@PathVariable String numeroDocumento) {
        return empleadoService.eliminarEmpleadoAdministrador(numeroDocumento);
    }

    @PatchMapping("/{numeroDocumento}/estado")
    public EmpleadoDTO cambiarEstado(@PathVariable String numeroDocumento,
            @Valid @RequestBody CambioEstadoDTO dto) {
        return empleadoService.cambiarEstado(numeroDocumento, dto);
    }

    @GetMapping("/{numeroDocumento}/historial")
    public List<HistorialEmpleadoDTO> historial(@PathVariable String numeroDocumento) {
        return empleadoService.listarHistorial(numeroDocumento);
    }
}
