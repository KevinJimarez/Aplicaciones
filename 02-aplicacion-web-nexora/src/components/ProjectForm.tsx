import { useEffect, useState, type FormEvent } from 'react'
import type { Project } from '../types'

export function ProjectForm({ project, onSubmit, onCancel }: { project?: Project; onSubmit: (value: { name: string; description: string; members: string[] }) => void; onCancel: () => void }) {
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [members, setMembers] = useState('')
  const [error, setError] = useState('')
  useEffect(() => { setName(project?.name ?? ''); setDescription(project?.description ?? ''); setMembers(project?.members.join(', ') ?? ''); setError('') }, [project])
  const save = (event: FormEvent) => {
    event.preventDefault()
    if (!name.trim() || !description.trim()) return setError('El nombre y la descripción son obligatorios.')
    const parsedMembers = members.split(',').map(value => value.trim()).filter(Boolean)
    if (!parsedMembers.length) return setError('Agrega al menos un integrante.')
    onSubmit({ name: name.trim(), description: description.trim(), members: parsedMembers })
  }
  return <form onSubmit={save} className="form-stack" noValidate>
    {error && <div className="form-error">{error}</div>}
    <label>Nombre del proyecto <input value={name} onChange={e => setName(e.target.value)} placeholder="Ej. Portal de biblioteca" autoFocus /></label>
    <label>Descripción <textarea value={description} onChange={e => setDescription(e.target.value)} placeholder="Objetivo y alcance general" rows={3} /></label>
    <label>Integrantes <input value={members} onChange={e => setMembers(e.target.value)} placeholder="Nombre 1, Nombre 2" /><small>Separa los nombres con comas.</small></label>
    <div className="form-actions"><button type="button" className="button button-ghost" onClick={onCancel}>Cancelar</button><button className="button button-primary">{project ? 'Guardar cambios' : 'Crear proyecto'}</button></div>
  </form>
}
