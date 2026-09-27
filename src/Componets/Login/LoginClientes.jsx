import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowRight, CalendarCheck, CircleAlert, CircleCheck, Lock, Mail } from 'lucide-react';
import {
  doSignOut,
  onAuthChange,
  resetPassword,
  setRecordarSesion,
  signIn as firebaseSignIn,
  signInGoogle,
} from '../../FireBase/auth';
import { CampoContrasena, CampoTexto, Recordar, SesionActiva } from './LoginCampos';

const CORREO = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const traducirError = (code) => {
  const errores = {
    'auth/user-not-found': 'No existe una cuenta con ese correo.',
    'auth/wrong-password': 'Contraseña incorrecta.',
    'auth/invalid-credential': 'Correo o contraseña incorrectos.',
    'auth/invalid-email': 'El correo no es válido.',
    'auth/too-many-requests': 'Demasiados intentos. Intenta más tarde.',
    'auth/popup-closed-by-user': 'Se cerró la ventana antes de completar el acceso.',
    'auth/cancelled-popup-request': 'Operación cancelada.',
    'auth/popup-blocked': 'El navegador bloqueó la ventana emergente. Permite ventanas emergentes para este sitio e intenta de nuevo.',
    'auth/unauthorized-domain': 'Este dominio no está autorizado en Firebase. Contacta al administrador.',
    'auth/operation-not-allowed': 'Este método de inicio de sesión no está habilitado. Contacta al administrador.',
    'auth/account-exists-with-different-credential': 'Ya existe una cuenta con ese correo usando otro método de acceso.',
    'auth/network-request-failed': 'Error de red. Verifica tu conexión a internet.',
  };
  return errores[code] || `Ocurrió un error inesperado (${code ?? 'desconocido'}). Intenta de nuevo.`;
};

function GoogleLogo() {
  return (
    <svg width="18" height="18" viewBox="0 0 48 48" aria-hidden="true">
      <path fill="#FFC107" d="M43.6 20.5H42V20H24v8h11.3C33.7 32.7 29.2 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.8 1.2 7.9 3.1l5.7-5.7C34 6.1 29.3 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.4-.4-3.5z" />
      <path fill="#FF3D00" d="m6.3 14.7 6.6 4.8C14.7 15.1 19 12 24 12c3.1 0 5.8 1.2 7.9 3.1l5.7-5.7C34 6.1 29.3 4 24 4 16.3 4 9.7 8.3 6.3 14.7z" />
      <path fill="#4CAF50" d="M24 44c5.2 0 9.9-2 13.4-5.2l-6.2-5.2C29.2 35.1 26.7 36 24 36c-5.2 0-9.6-3.3-11.3-7.9l-6.5 5C9.5 39.6 16.2 44 24 44z" />
      <path fill="#1976D2" d="M43.6 20.5H42V20H24v8h11.3c-.8 2.2-2.2 4.2-4.1 5.6l6.2 5.2C37 39.2 44 34 44 24c0-1.3-.1-2.4-.4-3.5z" />
    </svg>
  );
}

export default function LoginClientes() {
  const navigate = useNavigate();
  const [sesion, setSesion] = useState(null);
  const [comprobando, setComprobando] = useState(true);
  const [cerrando, setCerrando] = useState(false);

  const [correo, setCorreo] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [recordar, setRecordar] = useState(true);
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  const [recuperando, setRecuperando] = useState(false);
  const [correoRecuperar, setCorreoRecuperar] = useState('');
  const [avisoRecuperar, setAvisoRecuperar] = useState({ tipo: '', texto: '' });
  const [enviando, setEnviando] = useState(false);

  useEffect(() => onAuthChange((usuario) => {
    setSesion(usuario);
    setComprobando(false);
  }), []);

  const entrar = () => navigate('/', { replace: true });

  const cerrarSesion = async () => {
    setCerrando(true);
    try {
      await doSignOut();
    } finally {
      setCerrando(false);
    }
  };

  const ingresarConCorreo = async (e) => {
    e.preventDefault();
    setError('');
    if (!CORREO.test(correo.trim())) {
      setError('Escribe un correo válido, por ejemplo nombre@correo.com.');
      document.getElementById('lg-cliente-correo')?.focus();
      return;
    }
    if (!contrasena) {
      setError('Escribe tu contraseña.');
      document.getElementById('lg-cliente-contrasena')?.focus();
      return;
    }

    setCargando(true);
    try {
      await setRecordarSesion(recordar);
      await firebaseSignIn(correo, contrasena);
      entrar();
    } catch (err) {
      setError(traducirError(err.code));
      setCargando(false);
    }
  };

  const ingresarConProveedor = (proveedor) => async () => {
    setError('');
    setCargando(true);
    // Sin await antes del popup para que el navegador no lo bloquee; Firebase encola el cambio de persistencia
    setRecordarSesion(recordar).catch(() => {});
    try {
      await proveedor();
      entrar();
    } catch (err) {
      setError(traducirError(err.code));
      setCargando(false);
    }
  };

  const enviarRecuperacion = async (e) => {
    e.preventDefault();
    setAvisoRecuperar({ tipo: '', texto: '' });
    if (!CORREO.test(correoRecuperar.trim())) {
      setAvisoRecuperar({ tipo: 'error', texto: 'Escribe el correo de tu cuenta.' });
      return;
    }
    setEnviando(true);
    try {
      await resetPassword(correoRecuperar.trim());
      setAvisoRecuperar({ tipo: 'exito', texto: 'Si existe una cuenta con ese correo, recibirás un enlace para crear una nueva contraseña.' });
    } catch (err) {
      setAvisoRecuperar({ tipo: 'error', texto: traducirError(err.code) });
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="lg-form-area">
      <p className="lg-kicker"><Lock size={13} aria-hidden="true" /> Portal clientes y visitantes</p>
      <h2 className="lg-title">Bienvenido a Villa Katy</h2>
      <p className="lg-lead">Consulta tus reservas, tu historial de estadías, los servicios campestres y tus beneficios.</p>

      {comprobando && <p className="lg-cargando">Verificando sesión...</p>}

      {!comprobando && sesion && (
        <SesionActiva
          nombre={sesion.displayName || sesion.email}
          detalle="Cuenta de huésped"
          onContinuar={entrar}
          onCerrar={cerrarSesion}
          cerrando={cerrando}
        />
      )}

      {!comprobando && !sesion && recuperando && (
        <form className="lg-form" onSubmit={enviarRecuperacion} noValidate>
          <p className="lg-divider"><span>Recuperar contraseña</span></p>
          <p className="lg-help">Te enviaremos un enlace al correo de tu cuenta para crear una nueva contraseña.</p>
          <CampoTexto
            id="lg-recuperar-correo"
            label="Correo electrónico"
            icono={Mail}
            type="email"
            autoComplete="email"
            placeholder="nombre@correo.com"
            value={correoRecuperar}
            onChange={(e) => setCorreoRecuperar(e.target.value)}
            autoFocus
          />
          {avisoRecuperar.texto && (
            <p className={`lg-alert lg-alert--${avisoRecuperar.tipo}`} role={avisoRecuperar.tipo === 'error' ? 'alert' : 'status'}>
              {avisoRecuperar.tipo === 'error' ? <CircleAlert size={16} /> : <CircleCheck size={16} />}
              {avisoRecuperar.texto}
            </p>
          )}
          <button type="submit" className="lg-btn lg-btn--primary lg-btn--block" disabled={enviando}>
            {enviando ? 'Enviando...' : 'Enviar enlace'}
          </button>
          <button type="button" className="lg-link lg-link--center" onClick={() => setRecuperando(false)}>
            Volver a iniciar sesión
          </button>
        </form>
      )}

      {!comprobando && !sesion && !recuperando && (
        <>
          <p className="lg-divider lg-divider--left"><span>Ingreso rápido con identidad digital</span></p>
          <div className="lg-socials">
            <button type="button" className="lg-btn lg-btn--social" onClick={ingresarConProveedor(signInGoogle)} disabled={cargando}>
              <GoogleLogo /> Continuar con Google
            </button>
            <p className="lg-help lg-help--center">¿No tienes cuenta? Se crea automáticamente la primera vez que entras con Google.</p>
          </div>

          <p className="lg-divider"><span>O ingresa con tus credenciales</span></p>

          <form className="lg-form" onSubmit={ingresarConCorreo} noValidate>
            <CampoTexto
              id="lg-cliente-correo"
              label="Correo electrónico"
              icono={Mail}
              type="email"
              autoComplete="email"
              placeholder="nombre@correo.com"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
            />
            <CampoContrasena
              id="lg-cliente-contrasena"
              label="Contraseña"
              autoComplete="current-password"
              placeholder="••••••••"
              value={contrasena}
              onChange={(e) => setContrasena(e.target.value)}
              extra={(
                <button type="button" className="lg-link" onClick={() => { setCorreoRecuperar(correo); setAvisoRecuperar({ tipo: '', texto: '' }); setRecuperando(true); }}>
                  ¿Olvidaste tu contraseña?
                </button>
              )}
            />
            <Recordar checked={recordar} onChange={setRecordar} />

            {error && <p className="lg-alert lg-alert--error" role="alert"><CircleAlert size={16} />{error}</p>}

            <button type="submit" className="lg-btn lg-btn--primary lg-btn--block" disabled={cargando}>
              <CalendarCheck size={17} aria-hidden="true" />
              {cargando ? 'Ingresando...' : 'Ingresar a mi cuenta'}
              {!cargando && <ArrowRight size={17} aria-hidden="true" />}
            </button>
          </form>

        </>
      )}
    </div>
  );
}
