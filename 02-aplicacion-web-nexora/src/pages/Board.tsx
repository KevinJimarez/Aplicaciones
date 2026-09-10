import { Calendar, Check, ChevronLeft, ChevronRight, Filter, Plus, Search, SlidersHorizontal, UserRound, X } from 'lucide-react'
import { useMemo, useState } from 'react'
import { EmptyState } from '../components/EmptyState'
import { Modal } from '../components/Modal'
import { Progress } from '../components/Progress'
import { TaskForm } from '../components/TaskForm'
import { useNexora } from '../hooks/useNexora'
import type { Priority, Task, View } from '../types'
import { formatDate, isOverdue, priorityLabel, statusLabel, statusOrder } from '../utils/helpers'

export function Board({ navigate }: { navigate: (view: View) => void }) {
  const { selectedProject, projectTasks, addTask, updateTask, deleteTask, moveTask } = useNexora()
  const [activeTask, setActiveTask] = useState<Task | null | undefined>(undefined)
  const [search, setSearch] = useState('')
  const [priority, setPriority] = useState<Priority | 'all'>('all')
  const [assignee, setAssignee] = useState('all')
  const filtered = useMemo(() => projectTasks.filter(task => task.title.toLowerCase().includes(search.toLowerCase()) && (priority === 'all' || task.priority === priority) && (assignee === 'all' || task.assignee === assignee)), [projectTasks, search, priority, assignee])
  const close = () => setActiveTask(undefined)
  const submit = (input: Parameters<typeof addTask>[0]) => { activeTask ? updateTask(activeTask.id, input) : addTask(input); close() }
  const remove = () => { if (activeTask && confirm(`¿Eliminar la tarea “${activeTask.title}”?`)) { deleteTask(activeTask.id); close() } }
  const clearFilters = () => { setSearch(''); setPriority('all'); setAssignee('all') }
  if (!selectedProject) return <EmptyState title="Selecciona un proyecto" text="El tablero necesita un proyecto activo para mostrar y crear tareas." action="Ir a proyectos" onAction={() => navigate('projects')} />
  return <div className="board-page">
    <section className="board-heading"><div><span className="eyebrow">Proyecto activo</span><h2>{selectedProject.name}</h2><p>{projectTasks.length} tareas · {selectedProject.members.length} integrantes</p></div><button className="button button-primary" onClick={() => setActiveTask(null)}><Plus size={17} />Nueva tarea</button></section>
    <section className="toolbar"><div className="search-box"><Search size={17} /><input value={search} onChange={e => setSearch(e.target.value)} placeholder="Buscar por nombre..." />{search && <button onClick={() => setSearch('')}><X size={15} /></button>}</div><label><Filter size={16} /><select value={priority} onChange={e => setPriority(e.target.value as Priority | 'all')}><option value="all">Todas las prioridades</option><option value="high">Prioridad alta</option><option value="medium">Prioridad media</option><option value="low">Prioridad baja</option></select></label><label><UserRound size={16} /><select value={assignee} onChange={e => setAssignee(e.target.value)}><option value="all">Todos los responsables</option>{selectedProject.members.map(name => <option key={name}>{name}</option>)}</select></label>{(search || priority !== 'all' || assignee !== 'all') && <button className="text-button" onClick={clearFilters}>Limpiar filtros</button>}</section>
    <section className="kanban" aria-label="Tablero Kanban">{statusOrder.map((status, statusIndex) => { const columnTasks = filtered.filter(task => task.status === status); return <div className={`kanban-column column-${status}`} key={status}><header><div><i /><h3>{statusLabel[status]}</h3><span>{columnTasks.length}</span></div><button className="icon-button" onClick={() => setActiveTask(null)} aria-label={`Agregar en ${statusLabel[status]}`}><Plus size={17} /></button></header><div className="task-list">{columnTasks.map(task => <article key={task.id} className="task-card" onClick={() => setActiveTask(task)} tabIndex={0} onKeyDown={event => event.key === 'Enter' && setActiveTask(task)}>
      <div className="task-card-top"><span className={`priority priority-${task.priority}`}>{priorityLabel[task.priority]}</span>{isOverdue(task) && <span className="overdue-badge">Atrasada</span>}</div><h4>{task.title}</h4><p>{task.description}</p>{task.tags.length > 0 && <div className="tag-row">{task.tags.slice(0, 2).map(tag => <span key={tag}>{tag}</span>)}</div>}<Progress value={task.progress} /><div className="task-card-bottom"><span className={isOverdue(task) ? 'overdue-text' : ''}><Calendar size={14} />{formatDate(task.dueDate)}</span><span className="assignee-avatar" title={task.assignee}>{task.assignee.split(' ').map(part => part[0]).slice(0, 2).join('')}</span></div>{task.subtasks.length > 0 && <div className="subtask-count"><Check size={14} />{task.subtasks.filter(item => item.completed).length}/{task.subtasks.length} subtareas</div>}<div className="move-controls">{statusIndex > 0 && <button onClick={event => { event.stopPropagation(); moveTask(task.id, statusOrder[statusIndex - 1]) }} aria-label="Mover a la izquierda"><ChevronLeft size={16} /></button>}<span>Mover</span>{statusIndex < statusOrder.length - 1 && <button onClick={event => { event.stopPropagation(); moveTask(task.id, statusOrder[statusIndex + 1]) }} aria-label="Mover a la derecha"><ChevronRight size={16} /></button>}</div>
    </article>)}{!columnTasks.length && <div className="column-empty"><SlidersHorizontal size={20} /><span>{filtered.length === 0 && projectTasks.length ? 'Sin resultados' : 'Sin tareas'}</span></div>}</div></div> })}</section>
    <Modal open={activeTask !== undefined} title={activeTask ? 'Detalles de la tarea' : 'Nueva tarea'} onClose={close} wide><TaskForm task={activeTask ?? undefined} members={selectedProject.members} onSubmit={submit} onCancel={close} onDelete={activeTask ? remove : undefined} /></Modal>
  </div>
}
