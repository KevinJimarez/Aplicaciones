import type { AppData, Priority, Status, Task } from '../types'
import { addDays } from '../utils/helpers'

const projectId = 'demo-universidad'
const names = ['Kevin Jimarez', 'Arely Basurto', 'Carlos Ortiz', 'Miguel Juárez']
const task = (id: string, title: string, description: string, assignee: string, priority: Priority, offset: number, status: Status, progress: number, tags: string[], subtasks: string[] = []): Task => ({
  id, projectId, title, description, assignee, priority, dueDate: addDays(offset), status, progress, tags,
  subtasks: subtasks.map((title, index) => ({ id: `${id}-s${index}`, title, completed: index === 0 && progress > 20 })),
  createdAt: new Date().toISOString(), updatedAt: new Date().toISOString(),
})

export const createDemoData = (): AppData => ({
  projects: [{
    id: projectId,
    name: 'Desarrollo de plataforma universitaria',
    description: 'Organización de las actividades necesarias para diseñar, desarrollar y presentar una plataforma web académica.',
    members: names,
    createdAt: new Date().toISOString(),
  }],
  tasks: [
    task('t1', 'Definir alcance del proyecto', 'Documentar objetivos, usuarios y entregables principales.', names[0], 'high', -2, 'completed', 100, ['Planeación'], ['Reunir requisitos', 'Validar alcance']),
    task('t2', 'Diseñar interfaz principal', 'Crear las vistas principales y validar la experiencia de usuario.', names[1], 'high', 2, 'progress', 65, ['Diseño', 'UI'], ['Investigar referencias', 'Crear boceto', 'Definir sistema visual']),
    task('t3', 'Crear componentes de React', 'Construir componentes reutilizables para las vistas del sistema.', names[2], 'high', 5, 'progress', 45, ['Frontend', 'React'], ['Preparar estructura', 'Crear navegación', 'Crear tarjetas']),
    task('t4', 'Implementar almacenamiento local', 'Persistir proyectos, tareas y preferencias de usuario.', names[3], 'medium', 7, 'review', 85, ['Datos'], ['Definir esquema', 'Agregar validación']),
    task('t5', 'Probar funcionalidades', 'Ejecutar pruebas del flujo completo y corregir hallazgos.', names[0], 'medium', 10, 'pending', 10, ['Calidad']),
    task('t6', 'Preparar presentación final', 'Preparar evidencias y demostración para la exposición.', names[1], 'low', 14, 'pending', 0, ['Presentación']),
  ],
  activities: [
    { id: 'a1', text: 'La tarea “Implementar almacenamiento local” pasó a revisión', date: new Date().toISOString(), type: 'task' },
    { id: 'a2', text: 'Se completó “Definir alcance del proyecto”', date: new Date(Date.now() - 3600000 * 5).toISOString(), type: 'task' },
    { id: 'a3', text: 'Proyecto de demostración creado', date: new Date(Date.now() - 86400000).toISOString(), type: 'project' },
  ],
  selectedProjectId: projectId,
  settings: { userName: 'Kevin', density: 'comfortable', animations: true },
})
