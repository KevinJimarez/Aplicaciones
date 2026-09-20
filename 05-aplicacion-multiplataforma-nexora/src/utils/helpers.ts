import type { Priority, Status, Task } from '../types'

export const uid = () => `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 8)}`
export const today = () => new Date().toISOString().slice(0, 10)
export const addDays = (days: number) => {
  const date = new Date()
  date.setDate(date.getDate() + days)
  return date.toISOString().slice(0, 10)
}
export const formatDate = (value: string) => value ? new Intl.DateTimeFormat('es-MX', { day: 'numeric', month: 'short', year: 'numeric' }).format(new Date(`${value}T12:00:00`)) : 'Sin fecha'
export const daysUntil = (date: string) => Math.ceil((new Date(`${date}T23:59:59`).getTime() - Date.now()) / 86400000)
export const isOverdue = (task: Task) => task.status !== 'completed' && Boolean(task.dueDate) && daysUntil(task.dueDate) < 0
export const projectProgress = (tasks: Task[]) => tasks.length ? Math.round(tasks.reduce((sum, task) => sum + task.progress, 0) / tasks.length) : 0
export const statusLabel: Record<Status, string> = { pending: 'Pendiente', progress: 'En proceso', review: 'En revisión', completed: 'Completado' }
export const priorityLabel: Record<Priority, string> = { low: 'Baja', medium: 'Media', high: 'Alta' }
export const statusOrder: Status[] = ['pending', 'progress', 'review', 'completed']
