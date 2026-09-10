export function Progress({ value, label = true }: { value: number; label?: boolean }) {
  const safe = Math.max(0, Math.min(100, value || 0))
  return <div className="progress-wrap">{label && <span>{safe}%</span>}<div className="progress-track"><div style={{ width: `${safe}%` }} /></div></div>
}
