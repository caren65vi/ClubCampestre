/**
 * Encabezado genérico para las páginas del panel administrativo.
 */
import './AdminPage.css'

const AdminPage = ({ title, description }) => {
  return (
    <section className="adminPage">
      <span>Panel administrativo</span>
      <h1>{title}</h1>
      <p>{description}</p>
    </section>
  )
}

export default AdminPage
