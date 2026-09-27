import { useSearchParams, Link } from 'react-router-dom';
import { ArrowLeft, Briefcase, ShieldCheck, Sprout, User } from 'lucide-react';
import LogoDos from '../../assets/LogoDos.png';
import LoginClientes from './LoginClientes';
import LoginEmpleados from './LoginEmpleados';
import './Login.css';

// Cada pestaña muestra su propio componente: clientes -> Firebase, corporativo -> backend (JWT)
const ACCESOS = {
  clientes: {
    pestana: 'Clientes y huéspedes',
    icono: User,
    etiqueta: 'Experiencias y gestión campestre',
    titulo: 'Portal de huéspedes, reservas y gestión integral',
    descripcion: 'Experiencias campestres inolvidables para nuestros huéspedes y visitantes, con una gestión operativa de excelencia en Villa Katy Club Campestre.',
    componente: LoginClientes,
  },
  corporativo: {
    pestana: 'Acceso corporativo',
    icono: Briefcase,
    etiqueta: 'Colaboradores y administración',
    titulo: 'Portal corporativo, talento humano y gestión operativa',
    descripcion: 'Acceso seguro para colaboradores, supervisores de área y administración de Villa Katy Club Campestre.',
    componente: LoginEmpleados,
  },
};

const Login = () => {
  const [params, setParams] = useSearchParams();
  const acceso = params.get('acceso') === 'corporativo' ? 'corporativo' : 'clientes';
  const actual = ACCESOS[acceso];
  const Formulario = actual.componente;

  const cambiarAcceso = (nuevo) => {
    setParams(nuevo === 'clientes' ? {} : { acceso: nuevo }, { replace: true });
  };

  return (
    <div className="lg-page">
      <div className={`lg-shell lg-shell--${acceso}`}>
        <aside className="lg-brand-panel">
          <div className="lg-brand">
            <span className="lg-brand__logo"><img src={LogoDos} alt="" /></span>
            <div>
              <strong>Villa Katy</strong>
              <small>Club Campestre</small>
            </div>
          </div>

          <div className="lg-brand-panel__contenido">
            <span className="lg-pill"><Sprout size={14} aria-hidden="true" /> {actual.etiqueta}</span>
            <h1>{actual.titulo}</h1>
            <p>{actual.descripcion}</p>
          </div>

          <footer className="lg-brand-panel__pie">
            <span><ShieldCheck size={15} aria-hidden="true" /> Armonía, tradición y eficiencia operativa</span>
            <span>© {new Date().getFullYear()} Villa Katy Club Campestre</span>
          </footer>
        </aside>

        <main className="lg-form-panel">
          <div className="lg-form-panel__inner">
            <div className="lg-tabs" role="tablist" aria-label="Tipo de acceso">
              {Object.entries(ACCESOS).map(([clave, { pestana, icono: Icono }]) => (
                <button
                  key={clave}
                  type="button"
                  role="tab"
                  id={`lg-tab-${clave}`}
                  aria-selected={acceso === clave}
                  aria-controls="lg-tabpanel"
                  className={`lg-tab${acceso === clave ? ' lg-tab--activa' : ''}`}
                  onClick={() => cambiarAcceso(clave)}
                >
                  <Icono size={16} aria-hidden="true" /> {pestana}
                </button>
              ))}
            </div>

            <section id="lg-tabpanel" role="tabpanel" aria-labelledby={`lg-tab-${acceso}`}>
              <Formulario key={acceso} />
            </section>

            <footer className="lg-form-panel__pie">
              <Link to="/" className="lg-link"><ArrowLeft size={14} aria-hidden="true" /> Volver al inicio</Link>
            </footer>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Login;
