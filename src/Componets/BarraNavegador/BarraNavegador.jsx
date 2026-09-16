import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './barraNavegador.css';
import LogoDos from '../../assets/LogoDos.png';

const BarraNavegador = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const navigate = useNavigate();

  return (
    <nav className="barraNavegador">
      <div className="barraNavegadorInner">
        <a href="#inicio" className="barraNavegadorBrand">
          <img src={LogoDos} alt="Logo Villa Katy" className="barraNavegadorLogo" />
          <div>
            <p className="barraNavegadorTitle">Villa Katy</p>
            <p className="barraNavegadorSubtitle">Club Campestre</p>
          </div>
        </a>

        <button
          className="barraNavegadorToggle"
          type="button"
          aria-expanded={menuOpen}
          aria-label="Abrir menú"
          onClick={() => setMenuOpen(!menuOpen)}>
          <span />
          <span />
          <span />
        </button>

        <div className={`barraNavegadorLinks ${menuOpen ? 'barraNavegadorLinksOpen' : ''}`}>
          <a href="#inicio" onClick={() => setMenuOpen(false)}>Inicio</a>
          <a href="#sobre-nosotros" onClick={() => setMenuOpen(false)}>Sobre Nosotros</a>
          <a href="#servicios" onClick={() => setMenuOpen(false)}>Servicios</a>
          <a href="#galeria" onClick={() => setMenuOpen(false)}>Galería</a>
          <a href="#restaurante" onClick={() => setMenuOpen(false)}>Restaurante</a>
        </div>

        <button type="button" onClick={() => navigate('/login')}>Iniciar sesión</button>
      </div>
    </nav>
  );
};

export default BarraNavegador;
