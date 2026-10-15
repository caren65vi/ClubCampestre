package co.edu.sena.villakaty.modelo;

/**
 * Empleado del club tal como lo devuelve la API REST (EmpleadoDTO del back-end).
 */
public class Empleado {

    public static final String ESTADO_ACTIVO = "activo";
    public static final String ESTADO_VACACIONES = "vacaciones";
    public static final String ESTADO_INACTIVO = "inactivo";

    private String numeroDocumento;
    private Integer idTipoDocumento;
    private String nombreTipoDocumento;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String telefono;
    private String correo;
    private Integer idCargo;
    private String nombreCargo;
    private String nombreArea;
    private String fechaContratacion;
    private double salario;
    private String estado;

    /** Une nombres y apellidos omitiendo los vacíos. */
    public String getNombreCompleto() {
        StringBuilder nombre = new StringBuilder();
        for (String parte : new String[] {primerNombre, segundoNombre, primerApellido, segundoApellido}) {
            if (parte != null && !parte.trim().isEmpty()) {
                if (nombre.length() > 0) {
                    nombre.append(' ');
                }
                nombre.append(parte.trim());
            }
        }
        return nombre.toString();
    }

    /** Iniciales para el avatar de la lista (primer nombre y primer apellido). */
    public String getIniciales() {
        String inicialNombre = primerNombre == null || primerNombre.isEmpty() ? "" : primerNombre.substring(0, 1);
        String inicialApellido = primerApellido == null || primerApellido.isEmpty() ? "" : primerApellido.substring(0, 1);
        return (inicialNombre + inicialApellido).toUpperCase();
    }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public Integer getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(Integer idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }
    public String getNombreTipoDocumento() { return nombreTipoDocumento; }
    public void setNombreTipoDocumento(String nombreTipoDocumento) { this.nombreTipoDocumento = nombreTipoDocumento; }
    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public Integer getIdCargo() { return idCargo; }
    public void setIdCargo(Integer idCargo) { this.idCargo = idCargo; }
    public String getNombreCargo() { return nombreCargo; }
    public void setNombreCargo(String nombreCargo) { this.nombreCargo = nombreCargo; }
    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }
    public String getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(String fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
