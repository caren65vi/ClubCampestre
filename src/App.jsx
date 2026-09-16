import BarraNavegador from './Componets/BarraNavegador/BarraNavegador.jsx'
import Header from './Componets/Header/Header.jsx'
import Presentacion from './Componets/Presentacion/Presentacion.jsx'
import Servicios from './Componets/Servicios/Servicios.jsx'
import Aside from './Componets/Aside/Aside.jsx'
import Footer from './Componets/Footer/Footer.jsx'
import './App.css'


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
