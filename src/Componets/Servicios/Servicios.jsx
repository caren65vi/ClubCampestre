import React from 'react'
import './Servicios.css'

const servicios = [
  { label: 'Restaurante', icon: 'restaurant' },
  { label: 'Habitaciones', icon: 'bed' },
  { label: 'WiFi', icon: 'wifi' },
  { label: 'Piscina', icon: 'pool' },
  { label: 'Jacuzzi en habitaciones', icon: 'spa' },
  { label: 'Bañera en habitaciones', icon: 'bathtub' },
]

const iconPaths = {
  restaurant: (
    <svg viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M24 10h4v36h-4zM34 10h4v36h-4zM16 10h4v26h-4zM42 10h4v26h-4z" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <path d="M14 46h36v6a2 2 0 0 1-2 2H16a2 2 0 0 1-2-2v-6Z" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
  bed: (
    <svg viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M10 28h44v20H10z" fill="none" stroke="currentColor" strokeWidth="4" />
      <path d="M10 28V20a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4v8" fill="none" stroke="currentColor" strokeWidth="4" />
      <path d="M54 28V20a4 4 0 0 0-4-4H32a4 4 0 0 0-4 4v8" fill="none" stroke="currentColor" strokeWidth="4" />
      <path d="M14 46v6M50 46v6" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
    </svg>
  ),
  wifi: (
    <svg viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M16 28a24 24 0 0 1 32 0" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <path d="M22 36a16 16 0 0 1 20 0" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <path d="M28 44a8 8 0 0 1 8 0" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <circle cx="32" cy="52" r="4" fill="currentColor" />
    </svg>
  ),
  pool: (
    <svg viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M12 38c8-10 18-10 26-4s18 2 28-6" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <path d="M14 46c8-10 18-10 26-4s18 2 28-6" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" opacity="0.5" />
      <path d="M18 54h28" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
    </svg>
  ),
  spa: (
    <svg viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <circle cx="32" cy="32" r="14" fill="none" stroke="currentColor" strokeWidth="4" />
      <path d="M24 44c4-2 8-2 12 0" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <path d="M20 24c0-8 24-8 24 0" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
    </svg>
  ),
  bathtub: (
    <svg viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
      <path d="M12 36h40v14H12z" fill="none" stroke="currentColor" strokeWidth="4" />
      <path d="M16 36V24a6 6 0 0 1 6-6h20a6 6 0 0 1 6 6v10" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
      <path d="M18 46v6M30 46v6" fill="none" stroke="currentColor" strokeWidth="4" strokeLinecap="round" />
    </svg>
  ),
}

const Servicios = () => {
  return (
    <section className="servicios-section" id="servicios">
      <div className="servicios-card">
        <div className="servicios-header">
          <h2>Servicios</h2>
          <p>En el club campestre encuentras todo lo necesario para una estadía cómoda, tranquila y sin complicaciones.</p>
        </div>

        <div className="servicios-grid">
          {servicios.map((item) => (
            <div key={item.label} className="servicio-item">
              <div className="servicio-icon">{iconPaths[item.icon]}</div>
              <span className="servicio-label">{item.label}</span>
            </div>
          ))}
        </div>

        <div className="servicios-action">
          <button type="button">Reservar ahora</button>
        </div>
      </div>
    </section>
  )
}

export default Servicios
