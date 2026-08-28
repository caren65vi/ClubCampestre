import React from 'react'
import './Presentacion.css'
import imagenCoralinas from '../../assets/imagenCoralinas.png'

const Presentacion = () => {
    return (
        <div>
            <main>
                <article className='encabezado' id='sobre-nosotros'>
                    <span className="sobreTag">Nuestra historia</span>
                    <h2>Sobre nosotros</h2>

                    <p>
                        <br /><br />
                        <strong>Club Campestre Villa Katy</strong> es un espacio diseñado para el descanso,
                        la recreación y el disfrute en un ambiente natural y tranquilo.

                        Ofrece cómodas habitaciones, servicio de restaurante, zonas recreativas como piscina,
                        sauna y áreas de esparcimiento, ideales para compartir en familia o con amigos.

                        Nuestro compromiso es brindar una experiencia agradable, combinando confort,
                        buena atención y contacto con la naturaleza, para que cada visita se convierta
                        en un momento inolvidable.
                    </p>
                </article>

                <article className='datos'>
                    <p>habitaciones</p>
                    <p>Felicidad clientes</p>
                </article>

                <article className='imgContainer'>
                    <img src={imagenCoralinas} alt="imagen de Coralinas" width="60%" height="60%" />
                </article>

                <article className='botones'>
                    <button>Reservar</button>
                    <button>Más información</button>
                </article>
            </main>
        </div>
    )
}

export default Presentacion
