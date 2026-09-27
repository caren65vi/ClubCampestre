import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, AtSign, CircleAlert, Info, Lock, LogIn } from 'lucide-react';
import { getEmployeeSession, loginEmpleado, logoutEmpleado } from '../../FireBase/authEmpleado';
import { CampoContrasena, CampoTexto, Recordar, SesionActiva } from './LoginCampos';

const ROLES_ACCESO_TOTAL = ['ADMIN', 'ADMINISTRADOR', 'LIDER'];

const destinoSegunRol = (rol) => (ROLES_ACCESO_TOTAL.includes(rol?.trim().toUpperCase()) ? '/admin' : '/');

export default function LoginEmpleados() {
  const navigate = useNavigate();
  const [sesion, setSesion] = useState(getEmployeeSession);

  const [usuario, setUsuario] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [recordar, setRecordar] = useState(true);
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);
  const [ayudaContrasena, setAyudaContrasena] = useState(false);

  const ingresar = async (e) => {
    e.preventDefault();
    setError('');
    if (!usuario.trim()) {
      setError('Escribe tu usuario empresarial.');
      document.getElementById('lg-empleado-usuario')?.focus();
      return;
    }
    if (!contrasena) {
      setError('Escribe tu contraseña.');
      document.getElementById('lg-empleado-contrasena')?.focus();
      return;
    }

    setCargando(true);
    try {
      const datos = await loginEmpleado(usuario, contrasena, recordar);
      navigate(destinoSegunRol(datos.rol), { replace: true });
    } catch (err) {
      setError(err.isBackendUnavailable
        ? 'No se pudo conectar con el servidor del club. Verifica tu conexión o intenta más tarde.'
        : err.message || 'Usuario o contraseña incorrectos.');
      setCargando(false);
    }
  };

  const cerrarSesion = () => {
    logoutEmpleado();
    setSesion(null);
  };

  return (
    <div className="lg-form-area">
      <p className="lg-kicker"><Lock size={13} aria-hidden="true" /> Portal colaboradores y staff</p>
      <h2 className="lg-title">Bienvenido, colaborador</h2>
      <p className="lg-lead">Ingresa tus credenciales institucionales para gestionar reservas, inventario, talento humano y servicios del club.</p>

      {sesion ? (
        <SesionActiva
          nombre={sesion.nombreUsuario}
          detalle={sesion.rol}
          onContinuar={() => navigate(destinoSegunRol(sesion.rol), { replace: true })}
          onCerrar={cerrarSesion}
        />
      ) : (
        <>
          <p className="lg-divider"><span>Ingresa con tus credenciales</span></p>

          <form className="lg-form" onSubmit={ingresar} noValidate>
            <CampoTexto
              id="lg-empleado-usuario"
              label="Usuario empresarial"
              icono={AtSign}
              type="text"
              autoComplete="username"
              placeholder="Ej: nombre.apellido o correo"
              value={usuario}
              onChange={(e) => setUsuario(e.target.value)}
            />
            <CampoContrasena
              id="lg-empleado-contrasena"
              label="Contraseña de seguridad"
              autoComplete="current-password"
              placeholder="••••••••"
              value={contrasena}
              onChange={(e) => setContrasena(e.target.value)}
              extra={(
                <button type="button" className="lg-link" onClick={() => setAyudaContrasena((v) => !v)} aria-expanded={ayudaContrasena}>
                  ¿Olvidaste tu contraseña?
                </button>
              )}
            />

            {ayudaContrasena && (
              <p className="lg-alert lg-alert--info" role="status">
                <Info size={16} />
                Las contraseñas de los colaboradores no se recuperan por correo. Comunícate con el administrador del sistema para que la restablezca.
              </p>
            )}

            <Recordar checked={recordar} onChange={setRecordar} />

            {error && <p className="lg-alert lg-alert--error" role="alert"><CircleAlert size={16} />{error}</p>}

            <button type="submit" className="lg-btn lg-btn--primary lg-btn--block" disabled={cargando}>
              <LogIn size={17} aria-hidden="true" />
              {cargando ? 'Ingresando...' : 'Ingresar al panel corporativo'}
              {!cargando && <ArrowRight size={17} aria-hidden="true" />}
            </button>
          </form>
        </>
      )}
    </div>
  );
}
