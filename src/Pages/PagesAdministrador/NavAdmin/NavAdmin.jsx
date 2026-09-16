import { useState } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import DashboardOutlinedIcon from '@mui/icons-material/DashboardOutlined'
import GroupOutlinedIcon from '@mui/icons-material/GroupOutlined'
import MenuIcon from '@mui/icons-material/Menu'
import CloseIcon from '@mui/icons-material/Close'
import AccountCircleIcon from '@mui/icons-material/AccountCircle'
import LogoutIcon from '@mui/icons-material/Logout'
import { getEmployeeSession, logoutEmpleado } from '../../../FireBase/authEmpleado'
import './NavAdmin.css'

const sections = [
  {
    title: 'PANEL',
    items: [
      { to: '/admin', label: 'Dashboard', icon: <DashboardOutlinedIcon />, end: true },
    ],
  },
  {
    title: 'GESTIÓN',
    items: [
      { to: '/admin/empleados', label: 'Empleados', icon: <GroupOutlinedIcon />, end: true },
    ],
  },
]

const NavAdmin = () => {
  const navigate = useNavigate()
  const [isOpen, setIsOpen] = useState(false)
  const employeeSession = getEmployeeSession()
  const userName = employeeSession?.nombreUsuario || 'Administrador'

  const close = () => setIsOpen(false)

  const signOut = async () => {
    logoutEmpleado()
    navigate('/login')
  }

  return (
    <>
      <div className="navAdminMobileBar">
        <strong>Villa Katy</strong>
        <button type="button" onClick={() => setIsOpen(true)} aria-label="Abrir menu">
          <MenuIcon />
        </button>
      </div>

      {isOpen && <div className="navAdminOverlay" onClick={close} />}

      <nav className={`navAdmin${isOpen ? ' navAdminOpen' : ''}`}>
        <header className="navAdminBrand">
          <strong>Villa Katy</strong>
          <button className="navAdminClose" type="button" onClick={close} aria-label="Cerrar menu">
            <CloseIcon />
          </button>
        </header>

        <div className="navAdminBody">
          {sections.map(({ title, items }) => (
            <section className="navAdminSection" key={title}>
              <span>{title}</span>
              {items.map(({ to, label, icon, end }) => (
                <NavLink
                  className={({ isActive }) => `navAdminLink${isActive ? ' navAdminLinkActive' : ''}`}
                  end={end}
                  key={to}
                  onClick={close}
                  to={to}
                >
                  {icon}
                  <strong>{label}</strong>
                </NavLink>
              ))}
            </section>
          ))}
        </div>

        <footer className="navAdminFooter">
          <div className="navAdminUser">
            <AccountCircleIcon className="navAdminAvatarIcon" />
            <div className="navAdminUserInfo">
              <strong className="navAdminUserName">{userName}</strong>
              <small className="navAdminUserRole">Administrador</small>
            </div>
          </div>
          <button type="button" className="navAdminSignOut" onClick={signOut}>
            <LogoutIcon />
            <span>Cerrar sesión</span>
          </button>
        </footer>
      </nav>
    </>
  )
}

export default NavAdmin
