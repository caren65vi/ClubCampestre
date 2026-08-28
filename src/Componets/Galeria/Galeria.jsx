import React from 'react'
import './Galeria.css'
import Uno from '../../assets/Uno.png'
import Segunda from '../../assets/Segunda.png'
import Tres from '../../assets/tres.png'
import Cuarta from '../../assets/cuarta.png'
import Quinta from '../../assets/quinta.png'
import Sexta from '../../assets/sexta.png'
import LogoDos from '../../assets/LogoDos.png'
import ImagenCoralinas from '../../assets/imagenCoralinas.png'

const experiencias = [
    { src: Uno, alt: 'Piscina principal de Villa Katy al atardecer' },
    { src: Segunda, alt: 'Habitación con vista al club campestre' },
    { src: Tres, alt: 'Zona social y restaurante' },
    { src: Cuarta, alt: 'Espacios verdes del club' },
    { src: Quinta, alt: 'Evento en Villa Katy' },
    { src: Sexta, alt: 'Detalle de las instalaciones' },
    { src: LogoDos, alt: 'Fachada del club campestre' },
    { src: ImagenCoralinas, alt: 'Vista panorámica de Las Coralinas' },
]

const Galeria = () => {
    const [destacada, ...resto] = experiencias

    return (
        <section className="galeriaSection" id="galeria">
            <div className="galeriaHeader">
                <span className="galeriaEyebrow">Vive Villa Katy</span>
                <h2>Experiencias Club Campestre</h2>
                <p>
                    Piscina, hospedaje, eventos y naturaleza: descubre por qué Villa Katy
                    es el escape perfecto en el Oriente Antioqueño.
                </p>
                <a href="#reservar" className="galeriaVerMas">
                    Ver más <span aria-hidden="true">→</span>
                </a>
            </div>

            <div className="galeriaGrid">
                <div className="galeriaCard galeriaCardGrande">
                    <img src={destacada.src} alt={destacada.alt} />
                </div>

                <div className="galeriaSubGrid">
                    {resto.map((foto, index) => (
                        <div className="galeriaCard" key={index}>
                            <img src={foto.src} alt={foto.alt} />
                        </div>
                    ))}
                </div>
            </div>
        </section>
    )
}

export default Galeria
