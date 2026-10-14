package com.clubcampestre.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clubcampestre.dto.CambioEstadoDTO;
import com.clubcampestre.dto.ContactoDTO;
import com.clubcampestre.dto.EmpleadoDTO;
import com.clubcampestre.dto.HistorialEmpleadoDTO;
import com.clubcampestre.entity.Cargo;
import com.clubcampestre.entity.Contacto;
import com.clubcampestre.entity.Empleado;
import com.clubcampestre.entity.HistorialEmpleado;
import com.clubcampestre.entity.RolUsuario;
import com.clubcampestre.entity.TipoContacto;
import com.clubcampestre.entity.TipoDocumento;
import com.clubcampestre.entity.UsuarioTrabajador;
import com.clubcampestre.exception.RecursoDuplicadoException;
import com.clubcampestre.exception.RecursoNoEncontradoException;
import com.clubcampestre.exception.SolicitudInvalidaException;
import com.clubcampestre.repository.CargoRepository;
import com.clubcampestre.repository.ContactoRepository;
import com.clubcampestre.repository.EmpleadoRepository;
import com.clubcampestre.repository.HistorialEmpleadoRepository;
import com.clubcampestre.repository.RolUsuarioRepository;
import com.clubcampestre.repository.TipoContactoRepository;
import com.clubcampestre.repository.TipoDocumentoRepository;
import com.clubcampestre.repository.UsuarioTrabajadorRepository;

@Service
public class EmpleadoService {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("activo", "vacaciones", "inactivo");
    private static final String NOMBRE_TIPO_CONTACTO_CELULAR = "Celular";
    private static final String NOMBRE_TIPO_CONTACTO_CORREO = "Correo electrónico";
    private static final Pattern CONTRASENA_SEGURA = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");

    @Autowired
    private EmpleadoRepository empleadoRepository;
    @Autowired
    private CargoRepository cargoRepository;
    @Autowired
    private TipoDocumentoRepository tipoDocumentoRepository;
    @Autowired
    private TipoContactoRepository tipoContactoRepository;
    @Autowired
    private ContactoRepository contactoRepository;
    @Autowired
    private RolUsuarioRepository rolUsuarioRepository;
    @Autowired
    private UsuarioTrabajadorRepository usuarioRepository;
    @Autowired
    private HistorialEmpleadoRepository historialRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private EmpleadoDTO convertirADTO(Empleado e) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNumeroDocumento(e.getIdEmpleado());
        dto.setIdTipoDocumento(e.getTipoDocumento().getIdTipoDocumento());
        dto.setNombreTipoDocumento(e.getTipoDocumento().getNombreTipoDocumento());
        dto.setPrimerNombre(e.getPrimerNombre());
        dto.setSegundoNombre(e.getSegundoNombre());
        dto.setPrimerApellido(e.getPrimerApellido());
        dto.setSegundoApellido(e.getSegundoApellido());
        dto.setIdCargo(e.getCargo().getIdCargo());
        dto.setNombreCargo(e.getCargo().getNombreCargo());
        dto.setIdArea(e.getCargo().getAreaTrabajo().getIdAreaTrabajo());
        dto.setNombreArea(e.getCargo().getAreaTrabajo().getNombreAreaTrabajo());
        dto.setFechaContratacion(e.getFechaContratacion());
        dto.setSalario(e.getSalario());
        dto.setEstado(e.getEstado());
        dto.setFechaCambioEstado(e.getFechaCambioEstado() == null ? null : e.getFechaCambioEstado().toString());

        buscarContacto(e, NOMBRE_TIPO_CONTACTO_CELULAR).ifPresent(c -> {
            dto.setTelefono(c.getValor());
            dto.setIndicativo(c.getIndicativo());
        });
        buscarContacto(e, NOMBRE_TIPO_CONTACTO_CORREO).ifPresent(c -> dto.setCorreo(c.getValor()));

        return dto;
    }

    private Optional<Contacto> buscarContacto(Empleado empleado, String nombreTipo) {
        return tipoContactoRepository.findByNombreTipoContactoIgnoreCase(nombreTipo)
                .flatMap(tc -> contactoRepository.findByEmpleado_IdEmpleadoAndTipoContacto_IdTipoContacto(
                        empleado.getIdEmpleado(), tc.getIdTipoContacto()));
    }

    // Crea o actualiza el contacto principal del empleado para ese tipo (Celular o Correo electrónico)
    private void guardarContactoPrincipal(Empleado empleado, String nombreTipo, String indicativo, String valor) {
        if (valor == null || valor.isBlank()) {
            return;
        }
        TipoContacto tipo = tipoContactoRepository
                .findByNombreTipoContactoIgnoreCase(nombreTipo)
                .orElseThrow(() -> new SolicitudInvalidaException(
                "No existe el tipo de contacto '" + nombreTipo + "' en el catálogo"));

        Contacto contacto = contactoRepository
                .findByEmpleado_IdEmpleadoAndTipoContacto_IdTipoContacto(
                        empleado.getIdEmpleado(), tipo.getIdTipoContacto())
                .orElseGet(Contacto::new);

        contacto.setValor(valor.trim());
        contacto.setIndicativo(indicativo);
        contacto.setTipoContacto(tipo);
        contacto.setEmpleado(empleado);
        contactoRepository.save(contacto);
    }

    private void guardarContactosAdicionales(Empleado empleado, List<ContactoDTO> contactos) {
        if (contactos == null) {
            return;
        }
        for (ContactoDTO dto : contactos) {
            TipoContacto tipo = tipoContactoRepository.findById(dto.getIdTipoContacto())
                    .orElseThrow(() -> new SolicitudInvalidaException("Tipo de contacto no válido"));
            Contacto contacto = new Contacto();
            contacto.setValor(dto.getValor().trim());
            contacto.setIndicativo(dto.getIndicativo());
            contacto.setTipoContacto(tipo);
            contacto.setEmpleado(empleado);
            contactoRepository.save(contacto);
        }
    }

    // RF-0062
    public List<EmpleadoDTO> listarTodos() {
        return empleadoRepository.findAll().stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    public List<EmpleadoDTO> buscarConFiltros(String nombre, Integer idArea, String estado) {
        if (estado != null && !ESTADOS_VALIDOS.contains(estado)) {
            throw new SolicitudInvalidaException("Estado de filtro inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
        List<EmpleadoDTO> resultado = empleadoRepository.buscarConFiltros(nombre, idArea, estado)
                .stream().map(this::convertirADTO).collect(Collectors.toList());
        if (resultado.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay empleados que coincidan con los criterios de búsqueda");
        }
        return resultado;
    }

    public EmpleadoDTO buscarPorId(String numeroDocumento) {
        return convertirADTO(obtenerOLanzarError(numeroDocumento));
    }
    @Transactional
    public EmpleadoDTO registrarEmpleadoAdministrador(EmpleadoDTO dto) {
        if (dto.getTelefono() == null || dto.getTelefono().isBlank()) {
            throw new SolicitudInvalidaException("El número de celular es obligatorio");
        }
        if (dto.getCorreo() == null || dto.getCorreo().isBlank()) {
            throw new SolicitudInvalidaException("El correo electrónico es obligatorio");
        }
        if (empleadoRepository.existsById(dto.getNumeroDocumento())) {
            throw new RecursoDuplicadoException(
                    "Ya existe un empleado con el documento " + dto.getNumeroDocumento());
        }
        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
                .orElseThrow(() -> new SolicitudInvalidaException("Tipo de documento no válido"));
        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> new SolicitudInvalidaException("Cargo no válido"));

        Empleado e = new Empleado();
        e.setIdEmpleado(dto.getNumeroDocumento());
        e.setTipoDocumento(tipoDoc);
        e.setPrimerNombre(dto.getPrimerNombre());
        e.setSegundoNombre(dto.getSegundoNombre());
        e.setPrimerApellido(dto.getPrimerApellido());
        e.setSegundoApellido(dto.getSegundoApellido());
        e.setCargo(cargo);
        e.setFechaContratacion(dto.getFechaContratacion());
        e.setSalario(dto.getSalario());
        e.setEstado("activo");

        Empleado guardado = empleadoRepository.save(e);
        guardarContactoPrincipal(guardado, NOMBRE_TIPO_CONTACTO_CELULAR, dto.getIndicativo(), dto.getTelefono());
        guardarContactoPrincipal(guardado, NOMBRE_TIPO_CONTACTO_CORREO, null, dto.getCorreo());
        guardarContactosAdicionales(guardado, dto.getContactosAdicionales());
        if (Boolean.TRUE.equals(dto.getCrearUsuario())) {
            crearUsuario(guardado, dto);
        }
        registrarHistorial(guardado, HistorialEmpleado.TIPO_ESTADO, null, "activo",
                guardado.getFechaContratacion(), "Registro inicial del empleado");
        return convertirADTO(guardado);
    }

    private void crearUsuario(Empleado empleado, EmpleadoDTO dto) {
        if (dto.getIdRol() == null) {
            throw new SolicitudInvalidaException("El rol es obligatorio");
        }
        RolUsuario rol = rolUsuarioRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new SolicitudInvalidaException("Rol no válido"));

        String nombreUsuario = dto.getNombreUsuario() == null ? "" : dto.getNombreUsuario().trim();
        if (nombreUsuario.isEmpty()) {
            throw new SolicitudInvalidaException("El nombre de usuario es obligatorio");
        }
        if (usuarioRepository.findByNombreUsuario(nombreUsuario).isPresent()) {
            throw new RecursoDuplicadoException("Ya existe el nombre de usuario " + nombreUsuario);
        }

        String contrasena = dto.getContrasena();
        if (contrasena == null || !CONTRASENA_SEGURA.matcher(contrasena).matches()) {
            throw new SolicitudInvalidaException(
                    "La contraseña debe tener mínimo 8 caracteres, una mayúscula, una minúscula y un número");
        }

        UsuarioTrabajador usuario = new UsuarioTrabajador();
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setRolUsuario(rol);
        usuario.setEmpleado(empleado);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public EmpleadoDTO modificarEmpleadoAdministrador(String numeroDocumento, EmpleadoDTO dto) {
        Empleado e = obtenerOLanzarError(numeroDocumento);
        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(dto.getIdTipoDocumento())
            .orElseThrow(() -> new SolicitudInvalidaException("Tipo de documento no válido"));
        Cargo cargo = cargoRepository.findById(dto.getIdCargo())
                .orElseThrow(() -> new SolicitudInvalidaException("Cargo no válido"));

        List<String> cambios = detectarCambios(e, dto, tipoDoc, cargo);

        e.setTipoDocumento(tipoDoc);
        e.setPrimerNombre(dto.getPrimerNombre());
        e.setSegundoNombre(dto.getSegundoNombre());
        e.setPrimerApellido(dto.getPrimerApellido());
        e.setSegundoApellido(dto.getSegundoApellido());
        e.setCargo(cargo);
        e.setFechaContratacion(dto.getFechaContratacion());
        e.setSalario(dto.getSalario());

        Empleado actualizado = empleadoRepository.save(e);
        guardarContactoPrincipal(actualizado, NOMBRE_TIPO_CONTACTO_CELULAR, dto.getIndicativo(), dto.getTelefono());
        guardarContactoPrincipal(actualizado, NOMBRE_TIPO_CONTACTO_CORREO, null, dto.getCorreo());

        if (!cambios.isEmpty()) {
            registrarHistorial(actualizado, HistorialEmpleado.TIPO_EDICION, actualizado.getEstado(),
                    actualizado.getEstado(), LocalDate.now(), "Actualización de datos: " + String.join(", ", cambios));
        }
        return convertirADTO(actualizado);
    }

    // Compara los datos guardados con los recibidos para dejar en la bitácora qué se modificó
    private List<String> detectarCambios(Empleado e, EmpleadoDTO dto, TipoDocumento tipoDoc, Cargo cargo) {
        List<String> cambios = new ArrayList<>();
        if (!Objects.equals(e.getTipoDocumento().getIdTipoDocumento(), tipoDoc.getIdTipoDocumento())) {
            cambios.add("tipo de documento");
        }
        if (!Objects.equals(e.getPrimerNombre(), dto.getPrimerNombre())
                || !Objects.equals(e.getSegundoNombre(), dto.getSegundoNombre())
                || !Objects.equals(e.getPrimerApellido(), dto.getPrimerApellido())
                || !Objects.equals(e.getSegundoApellido(), dto.getSegundoApellido())) {
            cambios.add("nombre");
        }
        if (!Objects.equals(e.getCargo().getIdCargo(), cargo.getIdCargo())) {
            cambios.add("cargo");
        }
        if (!Objects.equals(e.getFechaContratacion(), dto.getFechaContratacion())) {
            cambios.add("fecha de ingreso");
        }
        if (e.getSalario() == null || dto.getSalario() == null || e.getSalario().compareTo(dto.getSalario()) != 0) {
            cambios.add("salario");
        }

        Optional<Contacto> celular = buscarContacto(e, NOMBRE_TIPO_CONTACTO_CELULAR);
        if (dto.getTelefono() != null && !dto.getTelefono().isBlank()
                && (celular.isEmpty()
                || !Objects.equals(celular.get().getValor(), dto.getTelefono().trim())
                || !Objects.equals(celular.get().getIndicativo(), dto.getIndicativo()))) {
            cambios.add("teléfono");
        }
        Optional<Contacto> correo = buscarContacto(e, NOMBRE_TIPO_CONTACTO_CORREO);
        if (dto.getCorreo() != null && !dto.getCorreo().isBlank()
                && (correo.isEmpty() || !Objects.equals(correo.get().getValor(), dto.getCorreo().trim()))) {
            cambios.add("correo electrónico");
        }
        return cambios;
    }

    @Transactional
    public EmpleadoDTO eliminarEmpleadoAdministrador(String numeroDocumento) {
        Empleado e = obtenerOLanzarError(numeroDocumento);
        if ("inactivo".equals(e.getEstado())) {
            return convertirADTO(e);
        }
        return aplicarCambioEstado(e, "inactivo", LocalDate.now(), "Baja desde gestión de empleados");
    }

    @Transactional
    public EmpleadoDTO cambiarEstado(String numeroDocumento, CambioEstadoDTO dto) {
        String nuevoEstado = dto.getEstado() == null ? null : dto.getEstado().trim().toLowerCase();
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado)) {
            throw new SolicitudInvalidaException("Estado inválido. Valores permitidos: " + ESTADOS_VALIDOS);
        }
        Empleado e = obtenerOLanzarError(numeroDocumento);
        if (nuevoEstado.equals(e.getEstado())) {
            throw new SolicitudInvalidaException("El empleado ya se encuentra en estado " + nuevoEstado);
        }
        if (e.getFechaContratacion() != null && dto.getFechaEfectiva().isBefore(e.getFechaContratacion())) {
            throw new SolicitudInvalidaException("La fecha efectiva no puede ser anterior a la fecha de ingreso");
        }
        return aplicarCambioEstado(e, nuevoEstado, dto.getFechaEfectiva(), dto.getMotivo().trim());
    }

    private EmpleadoDTO aplicarCambioEstado(Empleado e, String nuevoEstado, LocalDate fechaEfectiva, String motivo) {
        String estadoAnterior = e.getEstado();
        e.setEstado(nuevoEstado);
        e.setFechaCambioEstado(LocalDateTime.now());
        Empleado guardado = empleadoRepository.save(e);
        registrarHistorial(guardado, HistorialEmpleado.TIPO_ESTADO, estadoAnterior, nuevoEstado, fechaEfectiva, motivo);
        return convertirADTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<HistorialEmpleadoDTO> listarHistorial(String numeroDocumento) {
        obtenerOLanzarError(numeroDocumento);
        return historialRepository.findByEmpleado_IdEmpleadoOrderByFechaRegistroDescIdHistorialDesc(numeroDocumento)
                .stream().map(this::convertirHistorialADTO).collect(Collectors.toList());
    }

    private void registrarHistorial(Empleado empleado, String tipoEvento, String estadoAnterior,
            String estadoNuevo, LocalDate fechaEfectiva, String motivo) {
        HistorialEmpleado h = new HistorialEmpleado();
        h.setEmpleado(empleado);
        h.setTipoEvento(tipoEvento);
        h.setEstadoAnterior(estadoAnterior);
        h.setEstadoNuevo(estadoNuevo);
        h.setFechaEfectiva(fechaEfectiva == null ? LocalDate.now() : fechaEfectiva);
        h.setMotivo(motivo);
        h.setUsuarioResponsable(usuarioActual());
        h.setFechaRegistro(LocalDateTime.now());
        historialRepository.save(h);
    }

    private HistorialEmpleadoDTO convertirHistorialADTO(HistorialEmpleado h) {
        HistorialEmpleadoDTO dto = new HistorialEmpleadoDTO();
        dto.setIdHistorial(h.getIdHistorial());
        dto.setTipoEvento(h.getTipoEvento());
        dto.setEstadoAnterior(h.getEstadoAnterior());
        dto.setEstadoNuevo(h.getEstadoNuevo());
        dto.setFechaEfectiva(h.getFechaEfectiva());
        dto.setMotivo(h.getMotivo());
        dto.setFechaRegistro(h.getFechaRegistro());
        dto.setUsuarioResponsable(h.getUsuarioResponsable());

        if (h.getUsuarioResponsable() != null) {
            usuarioRepository.findByNombreUsuario(h.getUsuarioResponsable()).ifPresent(u -> {
                Empleado responsable = u.getEmpleado();
                dto.setNombreResponsable(responsable.getPrimerNombre() + " " + responsable.getPrimerApellido());
                dto.setCargoResponsable(responsable.getCargo().getNombreCargo());
            });
        }
        return dto;
    }

    // Usuario autenticado por el JWT (el filtro guarda el nombre de usuario como principal)
    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String nombreUsuario)) {
            return null;
        }
        return "anonymousUser".equals(nombreUsuario) ? null : nombreUsuario;
    }

    private Empleado obtenerOLanzarError(String numeroDocumento) {
        return empleadoRepository.findById(numeroDocumento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado"));
    }
}
