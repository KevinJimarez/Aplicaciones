import { AlertTriangle, ArrowRight, CheckCircle2, CircleDashed, Clock3, FolderKanban, ListTodo, TrendingUp } from 'lucide-react'
import { useNexora } from '../hooks/useNexora'
import type { View } from '../types'
import { daysUntil, formatDate, isOverdue, projectProgress, statusLabel, statusOrder } from '../utils/helpers'
import { EmptyState } from '../components/EmptyState'

export function Dashboard({ navigate }: { navigate: (view: View) => void }) {
  const { data, selectedProject, projectTasks } = useNexora()
  const completed = projectTasks.filter(task => task.status === 'completed').length
  const pending = projectTasks.length - completed
  const progress = projectProgress(projectTasks)
  const dueSoon = projectTasks.filter(task => task.status !== 'completed' && daysUntil(task.dueDate) <= 7).sort((a, b) => a.dueDate.localeCompare(b.dueDate)).slice(0, 4)
  const stats = [
    { label: 'Proyectos', value: data.projects.length, helper: 'en tu espacio', icon: FolderKanban, tone: 'steel' },
    { label: 'Total de tareas', value: projectTasks.length, helper: 'del proyecto activo', icon: ListTodo, tone: 'slate' },
    { label: 'Completadas', value: completed, helper: `${progress}% de avance`, icon: CheckCircle2, tone: 'green' },
    { label: 'Pendientes', value: pending, helper: `${projectTasks.filter(isOverdue).length} fuera de fecha`, icon: CircleDashed, tone: 'amber' },
  ]
  if (!selectedProject) return <EmptyState title="Comienza con un proyecto" text="Crea un proyecto para visualizar métricas, actividad y fechas importantes." action="Crear proyecto" onAction={() => navigate('projects')} />
  return <div className="page-stack">
    <section className="welcome-row"><div><span className="eyebrow">Resumen del espacio</span><h2>Hola, {data.settings.userName}</h2><p>Aquí tienes una lectura clara del avance de tu equipo.</p></div><button className="button button-primary" onClick={() => navigate('board')}>Abrir tablero <ArrowRight size={17} /></button></section>
    <section className="stats-grid">{stats.map(stat => <article className="stat-card" key={stat.label}><div className={`stat-icon ${stat.tone}`}><stat.icon size={20} /></div><div><span>{stat.label}</span><strong>{stat.value}</strong><small>{stat.helper}</small></div></article>)}</section>
    <section className="dashboard-grid">
      <article className="panel progress-panel"><header className="panel-header"><div><span className="eyebrow">Rendimiento</span><h3>Avance general</h3></div><TrendingUp size={20} /></header><div className="progress-summary"><div className="progress-ring" style={{ '--progress': `${progress * 3.6}deg` } as React.CSSProperties}><div><strong>{progress}%</strong><span>completo</span></div></div><div className="progress-breakdown"><p>Promedio calculado con el progreso real de cada tarea.</p>{statusOrder.map(status => { const count = projectTasks.filter(task => task.status === status).length; const pct = projectTasks.length ? Math.round(count / projectTasks.length * 100) : 0; return <div className="breakdown-row" key={status}><span><i className={`status-dot ${status}`} />{statusLabel[status]}</span><b>{count}</b><div><span style={{ width: `${pct}%` }} /></div></div> })}</div></div></article>
      <article className="panel due-panel"><header className="panel-header"><div><span className="eyebrow">Prioridad diaria</span><h3>Próximas a vencer</h3></div><button className="text-button" onClick={() => navigate('board')}>Ver tablero</button></header>{dueSoon.length ? <div className="due-list">{dueSoon.map(task => <div className="due-item" key={task.id}><div className={isOverdue(task) ? 'due-icon overdue' : 'due-icon'}>{isOverdue(task) ? <AlertTriangle size={17} /> : <Clock3 size={17} />}</div><div><strong>{task.title}</strong><span>{task.assignee}</span></div><time className={isOverdue(task) ? 'overdue-text' : ''}>{isOverdue(task) ? 'Atrasada' : formatDate(task.dueDate)}</time></div>)}</div> : <div className="mini-empty"><CheckCircle2 /><p>No hay vencimientos próximos.</p></div>}</article>
    </section>
    <section className="panel activity-panel"><header className="panel-header"><div><span className="eyebrow">Últimos cambios</span><h3>Actividad reciente</h3></div></header><div className="activity-list">{data.activities.slice(0, 5).map(item => <div className="activity-item" key={item.id}><span className={`activity-marker ${item.type}`} /><div><strong>{item.text}</strong><time>{new Intl.RelativeTimeFormat('es', { numeric: 'auto' }).format(Math.round((new Date(item.date).getTime() - Date.now()) / 3600000), 'hour')}</time></div></div>)}</div></section>
  </div>
}
