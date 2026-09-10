import { CalendarDays, Edit3, FolderPlus, MoreHorizontal, Plus, Trash2, Users } from 'lucide-react'
import { useState } from 'react'
import { EmptyState } from '../components/EmptyState'
import { Modal } from '../components/Modal'
import { Progress } from '../components/Progress'
import { ProjectForm } from '../components/ProjectForm'
import { useNexora } from '../hooks/useNexora'
import type { Project, View } from '../types'
import { formatDate, projectProgress } from '../utils/helpers'

export function Projects({ navigate }: { navigate: (view: View) => void }) {
  const { data, addProject, updateProject, deleteProject, selectProject } = useNexora()
  const [editing, setEditing] = useState<Project | null | undefined>(undefined)
  const close = () => setEditing(undefined)
  const submit = (value: { name: string; description: string; members: string[] }) => { editing ? updateProject(editing.id, value) : addProject(value); close() }
  const remove = (project: Project) => { if (confirm(`¿Eliminar “${project.name}” y todas sus tareas? Esta acción no se puede deshacer.`)) deleteProject(project.id) }
  const open = (project: Project) => { selectProject(project.id); navigate('board') }
  return <div className="page-stack">
    <section className="page-heading"><div><span className="eyebrow">Portafolio</span><h2>Mis proyectos</h2><p>Organiza el trabajo en espacios simples y enfocados.</p></div><button className="button button-primary" onClick={() => setEditing(null)}><Plus size={17} />Nuevo proyecto</button></section>
    {data.projects.length ? <section className="project-grid">{data.projects.map(project => { const tasks = data.tasks.filter(task => task.projectId === project.id); const progress = projectProgress(tasks); return <article key={project.id} className={`project-card ${data.selectedProjectId === project.id ? 'selected' : ''}`}>
      <div className="project-card-top"><div className="folder-icon"><FolderPlus size={21} /></div><div className="card-menu"><button className="icon-button" aria-label="Más opciones"><MoreHorizontal size={19} /></button><div><button onClick={() => setEditing(project)}><Edit3 size={15} />Editar</button><button className="danger-text" onClick={() => remove(project)}><Trash2 size={15} />Eliminar</button></div></div></div>
      <h3>{project.name}</h3><p>{project.description}</p><div className="project-meta"><span><Users size={15} />{project.members.length} integrantes</span><span><CalendarDays size={15} />{formatDate(project.createdAt.slice(0, 10))}</span></div><div className="project-stats"><span><b>{tasks.length}</b> tareas</span><span><b>{progress}%</b> avance</span></div><Progress value={progress} label={false} /><button className="button button-project" onClick={() => open(project)}>{data.selectedProjectId === project.id ? 'Abrir proyecto activo' : 'Seleccionar y abrir'}</button>
    </article> })}</section> : <EmptyState title="Aún no hay proyectos" text="Crea tu primer proyecto y empieza a organizar las tareas del equipo." action="Crear proyecto" onAction={() => setEditing(null)} />}
    <Modal open={editing !== undefined} title={editing ? 'Editar proyecto' : 'Nuevo proyecto'} onClose={close}><ProjectForm project={editing ?? undefined} onSubmit={submit} onCancel={close} /></Modal>
  </div>
}
