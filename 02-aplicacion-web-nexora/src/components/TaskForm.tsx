import { Plus, Trash2 } from 'lucide-react'
import { useEffect, useState, type FormEvent } from 'react'
import type { Priority, Status, Subtask, Task } from '../types'
import { addDays, statusLabel, uid } from '../utils/helpers'

type TaskInput = Omit<Task, 'id' | 'projectId' | 'createdAt' | 'updatedAt'>
const blank = (): TaskInput => ({ title: '', description: '', assignee: '', priority: 'medium', dueDate: addDays(7), status: 'pending', tags: [], progress: 0, subtasks: [] })

export function TaskForm({ task, members, onSubmit, onCancel, onDelete }: { task?: Task; members: string[]; onSubmit: (value: TaskInput) => void; onCancel: () => void; onDelete?: () => void }) {
  const [form, setForm] = useState<TaskInput>(blank())
  const [tagText, setTagText] = useState('')
  const [subtaskText, setSubtaskText] = useState('')
  const [error, setError] = useState('')
  useEffect(() => {
    if (task) { const { id: _id, projectId: _projectId, createdAt: _createdAt, updatedAt: _updatedAt, ...input } = task; setForm(input); setTagText(task.tags.join(', ')) }
    else { setForm(blank()); setTagText('') }
    setError('')
  }, [task])
  const change = <K extends keyof TaskInput>(key: K, value: TaskInput[K]) => setForm(current => ({ ...current, [key]: value }))
  const save = (event: FormEvent) => {
    event.preventDefault()
    if (!form.title.trim()) return setError('El título de la tarea es obligatorio.')
    if (!form.description.trim()) return setError('Agrega una descripción breve.')
    if (!form.assignee) return setError('Selecciona un responsable.')
    if (!form.dueDate || Number.isNaN(new Date(`${form.dueDate}T12:00:00`).getTime())) return setError('Selecciona una fecha límite válida.')
    if (form.progress < 0 || form.progress > 100) return setError('El progreso debe estar entre 0 y 100.')
    onSubmit({ ...form, title: form.title.trim(), description: form.description.trim(), tags: tagText.split(',').map(tag => tag.trim()).filter(Boolean) })
  }
  const addSubtask = () => { if (subtaskText.trim()) { change('subtasks', [...form.subtasks, { id: uid(), title: subtaskText.trim(), completed: false }]); setSubtaskText('') } }
  const updateSubtask = (id: string, patch: Partial<Subtask>) => change('subtasks', form.subtasks.map(item => item.id === id ? { ...item, ...patch } : item))
  return <form onSubmit={save} className="task-form" noValidate>
    {error && <div className="form-error full-span">{error}</div>}
    <label className="full-span">Título <input value={form.title} onChange={e => change('title', e.target.value)} placeholder="¿Qué se necesita hacer?" autoFocus /></label>
    <label className="full-span">Descripción <textarea value={form.description} onChange={e => change('description', e.target.value)} rows={3} placeholder="Agrega contexto para el equipo" /></label>
    <label>Responsable <select value={form.assignee} onChange={e => change('assignee', e.target.value)}><option value="">Seleccionar</option>{members.map(name => <option key={name}>{name}</option>)}</select></label>
    <label>Prioridad <select value={form.priority} onChange={e => change('priority', e.target.value as Priority)}><option value="low">Baja</option><option value="medium">Media</option><option value="high">Alta</option></select></label>
    <label>Estado <select value={form.status} onChange={e => change('status', e.target.value as Status)}>{Object.entries(statusLabel).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></label>
    <label>Fecha límite <input type="date" value={form.dueDate} onChange={e => change('dueDate', e.target.value)} /></label>
    <label className="full-span">Progreso <div className="range-row"><input type="range" min="0" max="100" value={form.progress} onChange={e => change('progress', Number(e.target.value))} /><output>{form.progress}%</output></div></label>
    <label className="full-span">Etiquetas <input value={tagText} onChange={e => setTagText(e.target.value)} placeholder="Frontend, Diseño, Urgente" /><small>Separa las etiquetas con comas.</small></label>
    <fieldset className="subtask-editor full-span"><legend>Subtareas</legend>{form.subtasks.map(item => <div className="subtask-edit" key={item.id}><input type="checkbox" checked={item.completed} onChange={e => updateSubtask(item.id, { completed: e.target.checked })} /><input value={item.title} onChange={e => updateSubtask(item.id, { title: e.target.value })} /><button type="button" className="icon-button danger" onClick={() => change('subtasks', form.subtasks.filter(sub => sub.id !== item.id))}><Trash2 size={16} /></button></div>)}<div className="subtask-add"><input value={subtaskText} onChange={e => setSubtaskText(e.target.value)} onKeyDown={e => { if (e.key === 'Enter') { e.preventDefault(); addSubtask() } }} placeholder="Nueva subtarea" /><button type="button" className="button button-soft" onClick={addSubtask}><Plus size={16} />Agregar</button></div></fieldset>
    <div className="form-actions full-span">{onDelete && <button type="button" className="button button-danger left" onClick={onDelete}>Eliminar tarea</button>}<button type="button" className="button button-ghost" onClick={onCancel}>Cancelar</button><button className="button button-primary">{task ? 'Guardar cambios' : 'Crear tarea'}</button></div>
  </form>
}
