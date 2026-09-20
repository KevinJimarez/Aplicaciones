import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { createDemoData } from '../data/demo'
import type { AppData, Project, Settings, Status, Task, ToastMessage } from '../types'
import { uid } from '../utils/helpers'

const STORAGE_KEY = 'nexora_data_v1'

interface NexoraContextValue {
  data: AppData
  selectedProject?: Project
  projectTasks: Task[]
  toasts: ToastMessage[]
  addProject: (project: Omit<Project, 'id' | 'createdAt'>) => void
  updateProject: (id: string, project: Partial<Project>) => void
  deleteProject: (id: string) => void
  selectProject: (id: string) => void
  addTask: (task: Omit<Task, 'id' | 'projectId' | 'createdAt' | 'updatedAt'>) => void
  updateTask: (id: string, task: Partial<Task>) => void
  deleteTask: (id: string) => void
  moveTask: (id: string, status: Status) => void
  updateSettings: (settings: Partial<Settings>) => void
  restoreDemo: () => void
  clearData: () => void
  notify: (message: string, type?: ToastMessage['type']) => void
}

const NexoraContext = createContext<NexoraContextValue | null>(null)

const validData = (value: unknown): value is AppData => {
  if (!value || typeof value !== 'object') return false
  const data = value as Partial<AppData>
  return Array.isArray(data.projects) && Array.isArray(data.tasks) && Array.isArray(data.activities) && typeof data.settings === 'object'
}

const readData = (): AppData => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (!saved) return createDemoData()
    const parsed: unknown = JSON.parse(saved)
    if (!validData(parsed)) throw new Error('Formato inválido')
    const fallback = createDemoData()
    return {
      ...fallback,
      ...parsed,
      settings: { ...fallback.settings, ...parsed.settings },
      selectedProjectId: parsed.projects.some(project => project.id === parsed.selectedProjectId) ? parsed.selectedProjectId : (parsed.projects[0]?.id ?? ''),
    }
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return createDemoData()
  }
}

export function NexoraProvider({ children }: { children: ReactNode }) {
  const [data, setData] = useState<AppData>(readData)
  const [toasts, setToasts] = useState<ToastMessage[]>([])

  useEffect(() => { localStorage.setItem(STORAGE_KEY, JSON.stringify(data)) }, [data])
  useEffect(() => {
    document.documentElement.dataset.density = data.settings.density
    document.documentElement.dataset.animations = String(data.settings.animations)
  }, [data.settings])

  const notify = useCallback((message: string, type: ToastMessage['type'] = 'success') => {
    const id = uid()
    setToasts(current => [...current, { id, message, type }])
    window.setTimeout(() => setToasts(current => current.filter(item => item.id !== id)), 3200)
  }, [])

  const activity = (text: string, type: 'task' | 'project' | 'system' = 'task') => ({ id: uid(), text, date: new Date().toISOString(), type })
  const addProject: NexoraContextValue['addProject'] = project => {
    const next = { ...project, id: uid(), createdAt: new Date().toISOString() }
    setData(current => ({ ...current, projects: [...current.projects, next], selectedProjectId: next.id, activities: [activity(`Se creó el proyecto “${next.name}”`, 'project'), ...current.activities].slice(0, 20) }))
    notify('Proyecto creado correctamente')
  }
  const updateProject: NexoraContextValue['updateProject'] = (id, project) => {
    setData(current => ({ ...current, projects: current.projects.map(item => item.id === id ? { ...item, ...project } : item), activities: [activity('Se actualizaron los datos del proyecto', 'project'), ...current.activities].slice(0, 20) }))
    notify('Proyecto actualizado')
  }
  const deleteProject = (id: string) => {
    setData(current => {
      const projects = current.projects.filter(item => item.id !== id)
      return { ...current, projects, tasks: current.tasks.filter(task => task.projectId !== id), selectedProjectId: projects[0]?.id ?? '', activities: [activity('Se eliminó un proyecto', 'project'), ...current.activities].slice(0, 20) }
    })
    notify('Proyecto eliminado', 'info')
  }
  const selectProject = (id: string) => setData(current => ({ ...current, selectedProjectId: id }))
  const addTask: NexoraContextValue['addTask'] = task => {
    if (!data.selectedProjectId) return notify('Primero crea o selecciona un proyecto', 'error')
    const next: Task = { ...task, id: uid(), projectId: data.selectedProjectId, createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() }
    setData(current => ({ ...current, tasks: [...current.tasks, next], activities: [activity(`Se creó la tarea “${next.title}”`), ...current.activities].slice(0, 20) }))
    notify('Tarea creada correctamente')
  }
  const updateTask: NexoraContextValue['updateTask'] = (id, task) => {
    setData(current => ({ ...current, tasks: current.tasks.map(item => item.id === id ? { ...item, ...task, updatedAt: new Date().toISOString() } : item), activities: [activity('Se actualizó una tarea'), ...current.activities].slice(0, 20) }))
    notify('Tarea actualizada')
  }
  const deleteTask = (id: string) => {
    setData(current => ({ ...current, tasks: current.tasks.filter(item => item.id !== id), activities: [activity('Se eliminó una tarea'), ...current.activities].slice(0, 20) }))
    notify('Tarea eliminada', 'info')
  }
  const moveTask = (id: string, status: Status) => {
    const task = data.tasks.find(item => item.id === id)
    updateTask(id, { status, progress: status === 'completed' ? 100 : task?.progress === 100 ? 75 : task?.progress })
  }
  const updateSettings = (settings: Partial<Settings>) => setData(current => ({ ...current, settings: { ...current.settings, ...settings } }))
  const restoreDemo = () => { setData(createDemoData()); notify('Datos de demostración restaurados') }
  const clearData = () => { setData(current => ({ projects: [], tasks: [], activities: [activity('Se eliminaron los datos locales', 'system')], selectedProjectId: '', settings: current.settings })); notify('Datos locales eliminados', 'info') }

  const selectedProject = data.projects.find(project => project.id === data.selectedProjectId)
  const projectTasks = useMemo(() => data.tasks.filter(task => task.projectId === data.selectedProjectId), [data.tasks, data.selectedProjectId])
  const value = { data, selectedProject, projectTasks, toasts, addProject, updateProject, deleteProject, selectProject, addTask, updateTask, deleteTask, moveTask, updateSettings, restoreDemo, clearData, notify }
  return <NexoraContext.Provider value={value}>{children}</NexoraContext.Provider>
}

export const useNexora = () => {
  const context = useContext(NexoraContext)
  if (!context) throw new Error('useNexora debe usarse dentro de NexoraProvider')
  return context
}
