import { Component, type ErrorInfo, type ReactNode } from 'react'

export class ErrorBoundary extends Component<{ children: ReactNode }, { failed: boolean }> {
  state = { failed: false }
  static getDerivedStateFromError() { return { failed: true } }
  componentDidCatch(error: Error, info: ErrorInfo) { console.error('NEXORA pudo recuperarse de un error:', error, info) }
  render() {
    if (this.state.failed) return <main className="fatal"><div><strong>NEXORA</strong><h1>Algo no salió como esperábamos</h1><p>Tus datos siguen guardados. Recarga la página para continuar.</p><button onClick={() => location.reload()}>Recargar aplicación</button></div></main>
    return this.props.children
  }
}
