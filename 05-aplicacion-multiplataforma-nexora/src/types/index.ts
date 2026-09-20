export type Status = 'pending' | 'progress' | 'review' | 'completed'
export type Priority = 'low' | 'medium' | 'high'
export type View = 'dashboard' | 'projects' | 'board' | 'assistant' | 'settings'

export interface Subtask { id: string; title: string; completed: boolean }

export interface Task {
  id: string
  projectId: string
  title: string
  description: string
  assignee: string
  priority: Priority
  dueDate: string
  status: Status
  tags: string[]
  progress: number
  subtasks: Subtask[]
  createdAt: string
  updatedAt: string
}

export interface Project {
  id: string
  name: string
  description: string
  members: string[]
  createdAt: string
}

export interface Activity { id: string; text: string; date: string; type: 'task' | 'project' | 'system' }

export interface Settings {
  userName: string
  density: 'comfortable' | 'compact'
  animations: boolean
}

export interface AppData {
  projects: Project[]
  tasks: Task[]
  activities: Activity[]
  selectedProjectId: string
  settings: Settings
}

export interface ToastMessage { id: string; message: string; type: 'success' | 'error' | 'info' }
