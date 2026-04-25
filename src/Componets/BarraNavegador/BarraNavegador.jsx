import React, { useState } from 'react';
import './barraNavegador.css';
import LogoDos from '../../assets/LogoDos.png';

const BarraNavegador = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <nav className="barra-navegador">
      <div className="barra-navegador__inner">
        <a href="#inicio" className="barra-navegador__brand">
          <img src={LogoDos} alt="Logo Villa Katy" className="barra-navegador__logo" />
          <div>
            <p className="barra-navegador__title">Villa Katy</p>
            <p className="barra-navegador__subtitle">Club Campestre</p>
          </div>
        </a>

        <button
          className="barra-navegador__toggle"
          type="button"
          aria-expanded={menuOpen}
          aria-label="Abrir menú"
          onClick={() => setMenuOpen(!menuOpen)}>
          <span />
          <span />
          <span />
        </button>

        <div className={`barra-navegador__links ${menuOpen ? 'barra-navegador__links--open' : ''}`}>
          <a href="#inicio" onClick={() => setMenuOpen(false)}>Inicio</a>
          <a href="#sobre-nosotros" onClick={() => setMenuOpen(false)}>Sobre Nosotros</a>
          <a href="#servicios" onClick={() => setMenuOpen(false)}>Servicios</a>
          <a href="#galeria" onClick={() => setMenuOpen(false)}>Galería</a>
          <a href="#restaurante" onClick={() => setMenuOpen(false)}>Restaurante</a>
        </div>
      </div>
    </nav>
  );
};

export default BarraNavegador;
