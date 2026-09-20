import { ArrowUp, Bot, Check, Lightbulb, Sparkles, UserRound } from 'lucide-react'
import { useEffect, useRef, useState, type FormEvent } from 'react'
import { useNexora } from '../hooks/useNexora'
import { askNora, type NoraAnswer } from '../services/nora'
import { uid } from '../utils/helpers'

interface Message { id: string; role: 'nora' | 'user'; text: string; answer?: NoraAnswer }
const prompts = ['¿Qué tareas tengo pendientes?', '¿Qué debería hacer primero?', 'Genera un resumen del proyecto', '¿Quién tiene más tareas asignadas?']

export function Assistant() {
  const { selectedProject, projectTasks, updateTask, notify } = useNexora()
  const [input, setInput] = useState('')
  const [messages, setMessages] = useState<Message[]>([{ id: 'welcome', role: 'nora', text: 'Hola, soy NORA. Analizo tu tablero localmente para ayudarte a priorizar el trabajo. ¿Qué quieres revisar?' }])
  const endRef = useRef<HTMLDivElement>(null)
  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])
  const submit = (event?: FormEvent, custom?: string) => {
    event?.preventDefault()
    const question = (custom ?? input).trim()
    if (!question) return
    const answer = askNora(question, projectTasks, selectedProject?.name)
    setMessages(current => [...current, { id: uid(), role: 'user', text: question }, { id: uid(), role: 'nora', text: answer.text, answer }])
    setInput('')
  }
  const addSuggestions = (answer: NoraAnswer) => {
    const task = projectTasks.find(item => item.id === answer.taskId)
    if (!task || !answer.suggestions) return notify('La tarea ya no está disponible', 'error')
    const existing = new Set(task.subtasks.map(item => item.title.toLowerCase()))
    const next = answer.suggestions.filter(title => !existing.has(title.toLowerCase())).map(title => ({ id: uid(), title, completed: false }))
    updateTask(task.id, { subtasks: [...task.subtasks, ...next] })
    notify(`${next.length} subtareas agregadas a “${task.title}”`)
  }
  return <div className="assistant-page">
    <aside className="nora-info"><div className="nora-avatar"><Bot size={28} /></div><span className="online"><i />Modo local activo</span><h2>NORA</h2><p>Asistente inteligente de NEXORA</p><div className="info-note"><Lightbulb size={18} /><div><strong>Privacidad primero</strong><span>El análisis ocurre en tu navegador. No enviamos tus tareas a servicios externos.</span></div></div><div className="nora-capabilities"><span>Puede ayudarte con</span><ul><li><Check />Prioridades y vencimientos</li><li><Check />Carga del equipo</li><li><Check />Resumen del avance</li><li><Check />Creación de subtareas</li></ul></div></aside>
    <section className="chat-panel"><header><div><div className="chat-avatar"><Sparkles size={19} /></div><div><strong>NORA – Asistente inteligente</strong><span>{projectTasks.length} tareas disponibles para analizar</span></div></div><span className="local-pill">Sin API</span></header><div className="chat-messages">{messages.map(message => <div className={`message ${message.role}`} key={message.id}><div className="message-avatar">{message.role === 'nora' ? <Bot size={18} /> : <UserRound size={18} />}</div><div className="message-content"><span>{message.role === 'nora' ? 'NORA' : 'Tú'}</span><p>{message.text}</p>{message.answer?.suggestions && <div className="suggestion-box"><strong>Subtareas sugeridas</strong>{message.answer.suggestions.map(item => <div key={item}><Check size={14} />{item}</div>)}<button className="button button-primary" onClick={() => addSuggestions(message.answer!)}>Agregar todas a la tarea</button></div>}</div></div>)}<div ref={endRef} /></div><div className="quick-prompts">{prompts.map(prompt => <button key={prompt} onClick={() => submit(undefined, prompt)}>{prompt}</button>)}</div><form className="chat-input" onSubmit={submit}><textarea value={input} onChange={e => setInput(e.target.value)} onKeyDown={e => { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); submit() } }} placeholder="Pregunta sobre tus tareas..." rows={1} /><button aria-label="Enviar pregunta"><ArrowUp size={19} /></button><small>NORA puede cometer errores. Verifica las recomendaciones importantes.</small></form></section>
  </div>
}
