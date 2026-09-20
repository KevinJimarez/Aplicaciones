import type { Priority, Task } from '../types'
import { daysUntil, isOverdue, priorityLabel, projectProgress, statusLabel } from '../utils/helpers'

export interface NoraAnswer { text: string; suggestions?: string[]; taskId?: string }

const list = (tasks: Task[]) => tasks.length ? tasks.slice(0, 6).map(task => `• ${task.title} — ${task.assignee || 'sin responsable'}`).join('\n') : 'No encontré tareas en esta categoría.'
const normalize = (text: string) => text.toLocaleLowerCase('es').normalize('NFD').replace(/[\u0300-\u036f]/g, '')

export const suggestSubtasks = (title: string) => {
  const value = normalize(title)
  if (value.includes('diseñ') || value.includes('disen')) return ['Investigar referencias', 'Crear boceto', 'Definir colores y tipografía', 'Diseñar versión final', 'Revisar adaptación móvil']
  if (value.includes('desarroll') || value.includes('program') || value.includes('implement')) return ['Analizar requerimientos', 'Preparar estructura', 'Programar funcionalidad', 'Realizar pruebas', 'Corregir errores']
  if (value.includes('presentacion') || value.includes('exposicion')) return ['Organizar contenido', 'Preparar diapositivas', 'Agregar evidencias', 'Ensayar explicación', 'Revisar versión final']
  return ['Definir el resultado esperado', 'Reunir la información necesaria', 'Ejecutar la actividad principal', 'Revisar el resultado', 'Marcar como terminado']
}

const priorityScore = (task: Task) => (task.priority === 'high' ? 30 : task.priority === 'medium' ? 15 : 5) + Math.max(0, 14 - daysUntil(task.dueDate)) + (100 - task.progress) / 10

export function askNora(question: string, tasks: Task[], projectName = 'el proyecto'): NoraAnswer {
  const q = normalize(question)
  const active = tasks.filter(task => task.status !== 'completed')
  const overdue = active.filter(isOverdue)
  const soon = active.filter(task => daysUntil(task.dueDate) >= 0 && daysUntil(task.dueDate) <= 7)
  if (!question.trim()) return { text: 'Escribe una pregunta para que pueda analizar el tablero.' }
  if (q.includes('divide') || q.includes('subtarea')) {
    const named = tasks.find(task => q.includes(normalize(task.title)))
    const target = named ?? active.sort((a, b) => priorityScore(b) - priorityScore(a))[0]
    if (!target) return { text: 'No hay una tarea disponible para dividir. Crea una primero.' }
    return { text: `Preparé una secuencia práctica para “${target.title}”. Puedes agregarla directamente a la tarea.`, suggestions: suggestSubtasks(target.title), taskId: target.id }
  }
  if (q.includes('pendiente')) return { text: `Hay ${active.length} tarea${active.length === 1 ? '' : 's'} sin completar:\n${list(active)}` }
  if (q.includes('prioridad alta') || q.includes('alta prioridad')) { const high = active.filter(task => task.priority === 'high'); return { text: `Encontré ${high.length} tarea${high.length === 1 ? '' : 's'} de prioridad alta:\n${list(high)}` } }
  if (q.includes('proxima') || q.includes('vencer') || q.includes('fecha')) return { text: `Hay ${soon.length} tarea${soon.length === 1 ? '' : 's'} que vence${soon.length === 1 ? '' : 'n'} en los próximos 7 días y ${overdue.length} atrasada${overdue.length === 1 ? '' : 's'}.\n${list([...overdue, ...soon])}` }
  if (q.includes('avance') || q.includes('progreso')) return { text: `El avance promedio de ${projectName} es ${projectProgress(tasks)}%. ${tasks.filter(t => t.status === 'completed').length} de ${tasks.length} tareas están completadas.` }
  if (q.includes('quien') || q.includes('carga') || q.includes('asignad')) {
    const loads = active.reduce<Record<string, number>>((acc, task) => { const name = task.assignee || 'Sin responsable'; acc[name] = (acc[name] ?? 0) + 1; return acc }, {})
    const ranking = Object.entries(loads).sort((a, b) => b[1] - a[1])
    return { text: ranking.length ? `${ranking[0][0]} tiene la mayor carga activa (${ranking[0][1]} tareas).\n${ranking.map(([name, count]) => `• ${name}: ${count}`).join('\n')}` : 'No hay tareas activas asignadas.' }
  }
  if (q.includes('primero') || q.includes('recomienda') || q.includes('siguiente')) {
    const next = [...active].sort((a, b) => priorityScore(b) - priorityScore(a))[0]
    return { text: next ? `Te recomiendo atender primero “${next.title}”. Tiene prioridad ${priorityLabel[next.priority].toLowerCase()}, vence ${daysUntil(next.dueDate) < 0 ? 'con retraso' : `en ${daysUntil(next.dueDate)} días`} y lleva ${next.progress}% de avance.` : '¡Todo está al día! No hay tareas activas.' }
  }
  if (q.includes('sugiere') && q.includes('prioridad')) {
    const candidate = active[0]
    const suggested: Priority = candidate && daysUntil(candidate.dueDate) <= 3 ? 'high' : overdue.length ? 'high' : 'medium'
    return { text: candidate ? `Para “${candidate.title}” sugiero prioridad ${priorityLabel[suggested].toLowerCase()}, considerando su fecha límite y avance actual.` : 'Crea una tarea para que pueda sugerir su prioridad.' }
  }
  if (q.includes('resumen') || q.includes('proyecto')) {
    const counts = ['pending', 'progress', 'review', 'completed'].map(status => `${statusLabel[status as keyof typeof statusLabel]}: ${tasks.filter(task => task.status === status).length}`).join(' · ')
    return { text: `${projectName} contiene ${tasks.length} tareas y registra ${projectProgress(tasks)}% de avance. ${counts}. Hay ${overdue.length} tareas atrasadas y ${soon.length} próximas a vencer.` }
  }
  return { text: `Puedo analizar las ${tasks.length} tareas del tablero. Prueba preguntando por pendientes, fechas próximas, carga del equipo, avance, prioridades o pídeme un resumen.` }
}
