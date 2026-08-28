import React from 'react'
import './Footer.css'

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footerGrid">
        <div className="footerBrand">
          <h2>Villa Katy</h2>
          <p className="footerSubtitle">CLUB CAMPESTRE</p>
          <p className="footerText">
            Descanso, naturaleza y bienestar en un solo lugar. Tu escape perfecto en el corazón de Colombia.
          </p>
          <div className="footerSocials">
            <a href="https://www.instagram.com/hotel.lascoralinasoficial" target="_blank" rel="noreferrer noopener">I</a>
            <a href="https://wa.me/573243376439" target="_blank" rel="noreferrer noopener">W</a>
            <a href="mailto:contacto@villakaty.com">M</a>
          </div>
        </div>

        <div className="footerColumn">
          <h3>Navegación</h3>
          <ul>
            <li><a href="#inicio">Inicio</a></li>
            <li><a href="#sobre-nosotros">Sobre nosotros</a></li>
            <li><a href="#galeria">Galería</a></li>
            <li>Servicios</li>
            <li>Habitaciones</li>
            <li>Contacto</li>
          </ul>
        </div>

        <div className="footerColumn">
          <h3>Servicios</h3>
          <ul>
            <li>Restaurante</li>
            <li>Piscina</li>
            <li>Jacuzzi privado</li>
            <li>Baño de lujo</li>
            <li>WiFi incluido</li>
            <li>Reservas</li>
          </ul>
        </div>

        <div className="footerColumn footerContact">
          <h3>Contacto</h3>
          <ul>
            <li>Florencia, Caquetá Colombia</li>
            <li>+57 300 000 0000</li>
            <li>contacto@villakaty.com</li>
            <li>Lun – Dom: 8:00 am – 6:00 pm</li>
          </ul>
        </div>
      </div>

      <div className="footerHero">
        <span>Reserva 100% segura</span>
        <span>Atención personalizada</span>
        <span>Entorno natural único</span>
      </div>

      <div className="footerBottom">
        <p>© 2026 Club Campestre Villa Katy. Todos los derechos reservados.</p>
        <div className="footerLegal">
          <span>Política de privacidad</span>
          <span>Términos de uso</span>
          <span>Cookies</span>
        </div>
      </div>
    </footer>
  )
}

export default Footer
