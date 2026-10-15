/**
 * Llamadas a la API para los catálogos (tipos de documento, tipos de contacto, cargos y roles).
 */
import { request } from './empleadosApi'

export const listarTiposDocumento = () => request('/tipos-documento')

export const listarTiposContacto = () => request('/tipos-contacto')

export const listarCargos = () => request('/cargos')

export const listarRoles = () => request('/roles')
