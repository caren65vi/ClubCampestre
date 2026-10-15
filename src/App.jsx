/**
 * Página de inicio pública del Club Campestre Villa Katy.
 * Se compone de secciones independientes: barra de navegación, portada, presentación,
 * servicios, galería lateral y pie de página.
 */
import BarraNavegador from './Componets/BarraNavegador/BarraNavegador.jsx'
import Header from './Componets/Header/Header.jsx'
import Presentacion from './Componets/Presentacion/Presentacion.jsx'
import Servicios from './Componets/Servicios/Servicios.jsx'
import Aside from './Componets/Aside/Aside.jsx'
import Footer from './Componets/Footer/Footer.jsx'
import './App.css'


// Componente raíz del sitio público
function App() {
 

  return (
    <>
      <BarraNavegador />
      <div className="pt-28">
        <Header />
        <Presentacion />
        <Servicios />
        <Aside />
        <Footer />
      </div>
    </>
  )
}

export default App
