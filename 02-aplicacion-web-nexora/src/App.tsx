import { useState } from 'react'
import { Layout } from './components/Layout'
import { NexoraProvider } from './hooks/useNexora'
import { Assistant } from './pages/Assistant'
import { Board } from './pages/Board'
import { Dashboard } from './pages/Dashboard'
import { Projects } from './pages/Projects'
import { Settings } from './pages/Settings'
import type { View } from './types'

function NexoraApp() {
  const [view, setView] = useState<View>('dashboard')
  return <Layout view={view} setView={setView}>
    {view === 'dashboard' && <Dashboard navigate={setView} />}
    {view === 'projects' && <Projects navigate={setView} />}
    {view === 'board' && <Board navigate={setView} />}
    {view === 'assistant' && <Assistant />}
    {view === 'settings' && <Settings />}
  </Layout>
}

export default function App() { return <NexoraProvider><NexoraApp /></NexoraProvider> }
