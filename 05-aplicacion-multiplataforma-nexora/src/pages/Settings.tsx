import { DatabaseBackup, Eraser, MonitorCog, Save, Sparkles, UserRound } from 'lucide-react'
import { useEffect, useState, type FormEvent } from 'react'
import { useNexora } from '../hooks/useNexora'

export function Settings() {
  const { data, updateSettings, restoreDemo, clearData, notify } = useNexora()
  const [name, setName] = useState(data.settings.userName)
  useEffect(() => setName(data.settings.userName), [data.settings.userName])
  const save = (event: FormEvent) => { event.preventDefault(); if (!name.trim()) return notify('El nombre no puede quedar vacío', 'error'); updateSettings({ userName: name.trim() }); notify('Preferencias guardadas') }
  const restore = () => { if (confirm('¿Restaurar los datos de demostración? Se reemplazarán los proyectos y tareas actuales.')) restoreDemo() }
  const clear = () => { if (confirm('¿Eliminar todos los proyectos y tareas locales? Esta acción no se puede deshacer.')) clearData() }
  return <div className="settings-page page-stack"><section className="page-heading"><div><span className="eyebrow">Preferencias</span><h2>Configuración</h2><p>Personaliza tu espacio y administra los datos guardados.</p></div></section>
    <form className="settings-grid" onSubmit={save}><section className="settings-card"><header><div className="settings-icon"><UserRound /></div><div><h3>Perfil personal</h3><p>Este nombre se muestra en tu espacio.</p></div></header><label>Nombre del usuario <input value={name} onChange={e => setName(e.target.value)} maxLength={40} /></label></section>
      <section className="settings-card"><header><div className="settings-icon"><MonitorCog /></div><div><h3>Apariencia</h3><p>Ajusta la densidad de la interfaz.</p></div></header><div className="segmented"><button type="button" className={data.settings.density === 'comfortable' ? 'active' : ''} onClick={() => updateSettings({ density: 'comfortable' })}>Cómoda</button><button type="button" className={data.settings.density === 'compact' ? 'active' : ''} onClick={() => updateSettings({ density: 'compact' })}>Compacta</button></div><label className="switch-row"><div><strong>Animaciones sutiles</strong><span>Transiciones y movimientos de interfaz.</span></div><input type="checkbox" checked={data.settings.animations} onChange={e => updateSettings({ animations: e.target.checked })} /><i /></label></section>
      <section className="settings-card full-settings"><header><div className="settings-icon"><DatabaseBackup /></div><div><h3>Datos locales</h3><p>NEXORA guarda la información únicamente en este navegador.</p></div></header><div className="data-actions"><div><Sparkles /><span><strong>Datos de demostración</strong><small>Restaura el proyecto universitario inicial.</small></span><button type="button" className="button button-soft" onClick={restore}>Restaurar</button></div><div><Eraser /><span><strong>Eliminar datos locales</strong><small>Borra todos los proyectos y tareas.</small></span><button type="button" className="button button-danger" onClick={clear}>Eliminar todo</button></div></div></section><div className="settings-save"><button className="button button-primary"><Save size={17} />Guardar preferencias</button></div></form>
  </div>
}
