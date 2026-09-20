import { Bell, Bot, ChevronDown, Columns3, FolderKanban, LayoutDashboard, Menu, Settings, X } from 'lucide-react'
import { useState, type ReactNode } from 'react'
import type { View } from '../types'
import { useNexora } from '../hooks/useNexora'

const items: { id: View; label: string; icon: typeof LayoutDashboard }[] = [
  { id: 'dashboard', label: 'Inicio', icon: LayoutDashboard },
  { id: 'projects', label: 'Mis proyectos', icon: FolderKanban },
  { id: 'board', label: 'Tablero de tareas', icon: Columns3 },
  { id: 'assistant', label: 'Asistente NORA', icon: Bot },
  { id: 'settings', label: 'Configuración', icon: Settings },
]

export function Layout({ view, setView, children }: { view: View; setView: (view: View) => void; children: ReactNode }) {
  const { data, selectedProject, selectProject, toasts } = useNexora()
  const [open, setOpen] = useState(false)
  const current = items.find(item => item.id === view)!
  const navigate = (next: View) => { setView(next); setOpen(false); window.scrollTo({ top: 0, behavior: 'smooth' }) }
  return <div className="app-shell">
    <aside className={`sidebar ${open ? 'is-open' : ''}`}>
      <div className="brand"><div className="brand-mark">N</div><div><strong>NEXORA</strong><span>Gestor inteligente</span></div><button className="sidebar-close" onClick={() => setOpen(false)}><X /></button></div>
      <nav className="main-nav" aria-label="Navegación principal">
        <span className="nav-label">Espacio de trabajo</span>
        {items.slice(0, 4).map(item => <button key={item.id} className={view === item.id ? 'active' : ''} onClick={() => navigate(item.id)}><item.icon size={19} /><span>{item.label}</span>{item.id === 'assistant' && <small>LOCAL</small>}</button>)}
      </nav>
      <div className="sidebar-project">
        <span className="nav-label">Proyecto activo</span>
        <label className="project-select"><span className="project-dot" /><select value={data.selectedProjectId} onChange={event => selectProject(event.target.value)} aria-label="Proyecto activo"><option value="">Sin proyecto</option>{data.projects.map(project => <option key={project.id} value={project.id}>{project.name}</option>)}</select><ChevronDown size={15} /></label>
      </div>
      <div className="sidebar-footer"><button className={view === 'settings' ? 'active' : ''} onClick={() => navigate('settings')}><Settings size={19} /><span>Configuración</span></button><div className="user-chip"><span>{data.settings.userName.charAt(0).toUpperCase() || 'U'}</span><div><strong>{data.settings.userName || 'Usuario'}</strong><small>Espacio personal</small></div></div></div>
    </aside>
    {open && <button className="sidebar-overlay" aria-label="Cerrar menú" onClick={() => setOpen(false)} />}
    <div className="main-area">
      <header className="topbar"><div className="topbar-title"><button className="menu-button" onClick={() => setOpen(true)}><Menu /></button><div><span>{selectedProject?.name ?? 'Espacio de trabajo'}</span><h1>{current.label}</h1></div></div><div className="topbar-actions"><button className="icon-button" aria-label="Notificaciones"><Bell size={19} /><i /></button><div className="topbar-avatar">{data.settings.userName.charAt(0).toUpperCase() || 'U'}</div></div></header>
      <main className="content">{children}</main>
    </div>
    <div className="toast-stack" aria-live="polite">{toasts.map(toast => <div key={toast.id} className={`toast toast-${toast.type}`}><span />{toast.message}</div>)}</div>
  </div>
}
