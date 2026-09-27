import { Bath, BedDouble, Bubbles, CalendarDays, Sparkles, UtensilsCrossed, WavesLadder, Wifi } from 'lucide-react'
import './Servicios.css'

const servicios = [
  {
    label: 'Restaurante',
    icon: UtensilsCrossed,
    descripcion: 'Gastronomía típica y platos gourmet con el auténtico sabor campestre.',
  },
  {
    label: 'Habitaciones',
    icon: BedDouble,
    descripcion: 'Suites campestres climatizadas, confortables y rodeadas de naturaleza.',
  },
  {
    label: 'WiFi',
    icon: Wifi,
    descripcion: 'Conexión de alta velocidad y cobertura estable en todas las áreas del club.',
  },
  {
    label: 'Piscina',
    icon: WavesLadder,
    descripcion: 'Piscina refrescante para adultos y niños con solárium y zonas de descanso.',
  },
  {
    label: 'Jacuzzi en habitaciones',
    icon: Bubbles,
    descripcion: 'Sesión de hidromasaje privada para revitalizar el cuerpo y la mente.',
  },
  {
    label: 'Bañera en habitaciones',
    icon: Bath,
    descripcion: 'Espacios diseñados para una relajación placentera y descanso absoluto.',
  },
]

const Servicios = () => {
  return (
    <section className="serviciosSection" id="servicios">
      <div className="serviciosCard">
        <div className="serviciosHeader">
          <span className="serviciosBadge">
            <Sparkles size={14} aria-hidden="true" />
            Experiencia Villa Katy • Amenidades & confort
          </span>
          <h2>Servicios</h2>
          <p>En el club campestre encuentras todo lo necesario para una estadía cómoda, tranquila y sin complicaciones.</p>
        </div>

        <ul className="serviciosGrid">
          {servicios.map(({ label, icon: Icon, descripcion }) => (
            <li key={label} className="servicioItem">
              <div className="servicioIcon">
                <Icon size={28} strokeWidth={1.75} aria-hidden="true" />
              </div>
              <h3 className="servicioLabel">{label}</h3>
              <p className="servicioDescripcion">{descripcion}</p>
            </li>
          ))}
        </ul>

        <div className="serviciosAction">
          <a className="serviciosReservar" href="#reservar">
            Reservar ahora
            <CalendarDays size={18} aria-hidden="true" />
          </a>
          <small>Reserva directa garantizada • Atención personalizada</small>
        </div>
      </div>
    </section>
  )
}

export default Servicios
