import { useState } from 'react';
import { ArrowRight, Eye, EyeOff, Lock, LogOut, UserRoundCheck } from 'lucide-react';

export function CampoTexto({ id, label, icono: Icono, extra, error, ...props }) {
  return (
    <div className="lg-field">
      <div className="lg-field__top">
        <label htmlFor={id}>{label}</label>
        {extra}
      </div>
      <div className="lg-input">
        <Icono size={16} aria-hidden="true" />
        <input id={id} aria-invalid={Boolean(error)} {...props} />
      </div>
    </div>
  );
}

export function CampoContrasena({ id, label, extra, error, ...props }) {
  const [visible, setVisible] = useState(false);
  return (
    <div className="lg-field">
      <div className="lg-field__top">
        <label htmlFor={id}>{label}</label>
        {extra}
      </div>
      <div className="lg-input">
        <Lock size={16} aria-hidden="true" />
        <input id={id} type={visible ? 'text' : 'password'} aria-invalid={Boolean(error)} {...props} />
        <button
          type="button"
          className="lg-input__toggle"
          onClick={() => setVisible((v) => !v)}
          aria-label={visible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
          aria-pressed={visible}
        >
          {visible ? <EyeOff size={16} /> : <Eye size={16} />}
        </button>
      </div>
    </div>
  );
}

export function Recordar({ checked, onChange }) {
  return (
    <label className="lg-check">
      <input type="checkbox" checked={checked} onChange={(e) => onChange(e.target.checked)} />
      Recordar en este equipo
    </label>
  );
}

// Se muestra cuando ya hay una sesión abierta, en lugar de redirigir sin avisar
export function SesionActiva({ nombre, detalle, onContinuar, onCerrar, cerrando }) {
  return (
    <div className="lg-sesion" role="status">
      <span className="lg-sesion__icono"><UserRoundCheck size={20} aria-hidden="true" /></span>
      <div className="lg-sesion__texto">
        <strong>Ya iniciaste sesión</strong>
        <span>{nombre}{detalle ? ` · ${detalle}` : ''}</span>
      </div>
      <div className="lg-sesion__acciones">
        <button type="button" className="lg-btn lg-btn--primary" onClick={onContinuar}>
          Continuar <ArrowRight size={16} aria-hidden="true" />
        </button>
        <button type="button" className="lg-btn lg-btn--soft" onClick={onCerrar} disabled={cerrando}>
          <LogOut size={16} aria-hidden="true" /> {cerrando ? 'Cerrando...' : 'Cerrar sesión'}
        </button>
      </div>
    </div>
  );
}
