import React from 'react'
import './Header.css'

const Header = () => {
  return (
    <div>
       <header id="inicio">
        <div>
            <p>RESERVA AHORA</p>

            <div className="campo">
                <label>Check in:</label>
                <input type="date" />
            </div>

            <div className="campo">
                <label>Check out:</label>
                <input type="date" />
            </div>

            <button>GO</button>

        </div>
    </header>

    </div>
  
  )
}

export default Header
