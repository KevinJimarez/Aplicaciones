import { FolderPlus, type LucideIcon } from 'lucide-react'

export function EmptyState({ icon: Icon = FolderPlus, title, text, action, onAction }: { icon?: LucideIcon; title: string; text: string; action?: string; onAction?: () => void }) {
  return <div className="empty-state"><div className="empty-icon"><Icon size={25} /></div><h3>{title}</h3><p>{text}</p>{action && <button className="button button-primary" onClick={onAction}>{action}</button>}</div>
}
