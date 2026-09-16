import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Alert,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Link,
  TextField,
  Typography,
} from '@mui/material';
import GoogleIcon from '@mui/icons-material/Google';
import MicrosoftIcon from '@mui/icons-material/Microsoft';
import { signIn as firebaseSignIn, signInGoogle, signInMicrosoft, onAuthChange, fetchUserDataForAuth, resetPassword } from '../../FireBase/auth';
import { loginEmpleado } from '../../FireBase/authEmpleado';
import LogoDos from '../../assets/LogoDos.png';
import './Login.css';

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

const ROLES_ACCESO_TOTAL = ['ADMIN', 'ADMINISTRADOR', 'LIDER'];

const Login = () => {
  const [sessionData, setSessionData] = React.useState(null);
  const [cargando, setCargando] = React.useState(true);
  const [resetOpen, setResetOpen] = React.useState(false);
  const [resetEmail, setResetEmail] = React.useState('');
  const [resetMessage, setResetMessage] = React.useState('');
  const [resetError, setResetError] = React.useState('');
  const [resetSending, setResetSending] = React.useState(false);
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [authError, setAuthError] = React.useState('');
  const [submitLoading, setSubmitLoading] = React.useState(false);
  const navigate = useNavigate();

  React.useEffect(() => {
    const unsubscribe = onAuthChange(async (firebaseUser) => {
      if (firebaseUser) {
        const userData = await fetchUserDataForAuth(firebaseUser)
          ?? { rol: 'usuario', email: firebaseUser.email, nombre: firebaseUser.displayName };
        setSessionData({ user: firebaseUser, userData });
      } else {
        setSessionData(null);
      }
      setCargando(false);
    });
    return unsubscribe;
  }, []);

  React.useEffect(() => {
    if (!sessionData?.userData) return;
    const { rol, tipo } = sessionData.userData;
    const destination = tipo === 'empleado'
      ? (ROLES_ACCESO_TOTAL.includes(rol?.trim().toUpperCase()) ? '/admin' : '/')
      : '/';
    navigate(destination, { replace: true });
  }, [sessionData, navigate]);

  const handleCredentialsLogin = async (event) => {
    event.preventDefault();
    setAuthError('');
    setSubmitLoading(true);

    try {
      const empleadoData = await loginEmpleado(email.trim(), password);
      setSessionData({
        user: { uid: empleadoData.idUsuario, email },
        userData: {
          rol: empleadoData.rol,
          nombre: empleadoData.nombreUsuario,
          tipo: 'empleado',
        },
      });
      return;
    } catch (employeeError) {
      if (employeeError?.isEmpleadoAuthError) {
        setAuthError(employeeError.message || 'Usuario o contraseña de empleado incorrectos.');
        setSubmitLoading(false);
        return;
      }

      if (employeeError?.isBackendUnavailable) {
        setAuthError('El servicio de empleados no está disponible en este momento. Intenta más tarde o usa tu cuenta de cliente.');
        setSubmitLoading(false);
        return;
      }
    }

    try {
      const result = await firebaseSignIn(email, password);
      setSessionData({ ...result, userData: { ...result.userData, tipo: 'cliente' } });
    } catch (firebaseError) {
      setAuthError(traducirError(firebaseError.code));
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleGoogleLogin = async () => {
    setAuthError('');
    setSubmitLoading(true);
    try {
      const result = await signInGoogle();
      setSessionData(result);
    } catch (err) {
      setAuthError(traducirError(err.code));
    } finally {
      setSubmitLoading(false);
    }
  };

  const handleMicrosoftLogin = async () => {
    setAuthError('');
    setSubmitLoading(true);
    try {
      const result = await signInMicrosoft();
      setSessionData(result);
    } catch (err) {
      setAuthError(traducirError(err.code));
    } finally {
      setSubmitLoading(false);
    }
  };

  const openResetPassword = () => {
    setResetMessage('');
    setResetError('');
    setResetOpen(true);
  };

  const closeResetPassword = () => {
    if (!resetSending) setResetOpen(false);
  };

  const handleResetPassword = async (event) => {
    event.preventDefault();
    setResetMessage('');
    setResetError('');

    const trimmedEmail = resetEmail.trim();
    if (!trimmedEmail) {
      setResetError('Ingresa tu correo electrónico.');
      return;
    }

    setResetSending(true);
    try {
      await resetPassword(trimmedEmail);
      setResetMessage('Si existe una cuenta con ese correo, recibirás un enlace para cambiar tu contraseña.');
    } catch (err) {
      setResetError(traducirError(err.code));
    } finally {
      setResetSending(false);
    }
  };

  if (cargando) {
    return (
      <div className="login-loading">
        <CircularProgress />
      </div>
    );
  }

  if (sessionData) {
    return (
      <div className="login-loading">
        <p>Redirigiendo al dashboard...</p>
      </div>
    );
  }

  return (
    <div className="login-wrapper">
      <div className="login-header-brand">
        <div className="login-brand-block">
          <img src={LogoDos} alt="Logo Villa Katy" className="login-logo" />
          <div>
            <p className="login-brand-kicker">Club Campestre</p>
            <h1>Villa Katy</h1>
          </div>
        </div>
        <p className="login-brand-copy">
          Experiencia, descanso y tranquilidad en un espacio pensado para vivir momentos inolvidables.
        </p>
        <small>Reservas • Eventos • Naturaleza</small>
      </div>

      <div className="login-form-col">
        <div className="login-card">
          <Typography variant="h5" className="login-title">Bienvenido</Typography>
          <Typography variant="body2" className="login-subtitle">Accede a tu cuenta de Villa Katy</Typography>

          <form onSubmit={handleCredentialsLogin} className="login-form">
            <TextField
              fullWidth
              label="Usuario o correo electrónico"
              type="text"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              variant="outlined"
            />

            <TextField
              fullWidth
              label="Contraseña"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              variant="outlined"
            />

            <div className="login-form-actions">
              <Link component="button" type="button" underline="hover" className="login-link" onClick={openResetPassword}>
                ¿Olvidaste tu contraseña?
              </Link>
            </div>

            {authError && <Alert severity="error">{authError}</Alert>}

            <Button type="submit" variant="contained" className="login-primary-button" disabled={submitLoading}>
              {submitLoading ? 'Ingresando...' : 'Iniciar sesión'}
            </Button>
          </form>

          <Divider className="login-divider">o</Divider>

          <div className="login-socials">
            <Button variant="outlined" className="login-secondary-button" onClick={handleGoogleLogin} disabled={submitLoading}>
              <GoogleIcon className="login-provider-icon" />
              Continuar con Google
            </Button>
            <Button variant="outlined" className="login-secondary-button" onClick={handleMicrosoftLogin} disabled={submitLoading}>
              <MicrosoftIcon className="login-provider-icon" />
              Continuar con Microsoft
            </Button>
          </div>

          <p className="loginRegisterHint">
            ¿No tienes cuenta? <a href="/register">Regístrate aquí</a>
          </p>
        </div>
      </div>

      <Dialog open={resetOpen} onClose={closeResetPassword} fullWidth maxWidth="xs">
        <form onSubmit={handleResetPassword}>
          <DialogTitle>Recuperar contraseña</DialogTitle>
          <DialogContent>
            <p>Ingresa el correo de tu cuenta. Te enviaremos un enlace para crear una nueva contraseña.</p>
            <TextField
              autoFocus
              fullWidth
              label="Correo electrónico"
              margin="normal"
              value={resetEmail}
              onChange={(event) => setResetEmail(event.target.value)}
              type="email"
            />
            {resetMessage && <Alert severity="success">{resetMessage}</Alert>}
            {resetError && <Alert severity="error">{resetError}</Alert>}
          </DialogContent>
          <DialogActions>
            <Button onClick={closeResetPassword} disabled={resetSending}>Cancelar</Button>
            <Button type="submit" variant="contained" disabled={resetSending}>
              {resetSending ? 'Enviando...' : 'Enviar enlace'}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </div>
  );
};

export default Login;
