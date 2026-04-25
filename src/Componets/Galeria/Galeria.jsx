import React from 'react'
import './Galeria.css'
import Uno from '../../assets/Uno.png'
import Segunda from '../../assets/Segunda.png'
import Tres from '../../assets/tres.png'
import Cuarta from '../../assets/cuarta.png'
import Quinta from '../../assets/quinta.png'
import Sexta from '../../assets/sexta.png'
import LogoDos from '../../assets/logoDos.png'
import ImagenCoralinas from '../../assets/ImagenCoralinas.png'

const Galeria = () => {
    return (
        <section className="galeria-section" id="galeria">
            <div className="galeria-header">
                <h2>Galería de Villa Katy</h2>
                <p>Descubre nuestros espacios, experiencias y el encanto natural del club campestre.</p>
            </div>

            <div className="galeria-grid">
                <div className="galeria-card">
                    <img src={Uno} alt="foto de villa" />
                </div>
                <div className="galeria-card">
                    <img src={Segunda} alt="foto de villa" />
                </div>
                <div className="galeria-card">
                    <img src={Tres} alt="foto de villa" />
                </div>
                <div className="galeria-card">
                    <img src={Cuarta} alt="foto de villa" />
                </div>
                <div className="galeria-card">
                    <img src={Quinta} alt="foto de villa" />
                </div>
                <div className="galeria-card">
                    <img src={Sexta} alt="foto de villa" />
                </div>
                  <div className="galeria-card">
                    <img src={LogoDos} alt="foto de villa " />
                </div>
                  
                  <div className="galeria-card">
                    <img src={ImagenCoralinas} alt="foto de villa " />
                </div>
            </div>
        </section>
    )
}

export default Galeria
