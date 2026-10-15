-- Base de datos académica para el CRUD de usuarios de EcoRuta.
-- Ejecutar una sola vez en Supabase > SQL Editor.

create table if not exists public.suscriptores (
  id uuid primary key default gen_random_uuid(),
  nombre text not null check (char_length(nombre) between 2 and 80),
  email text not null,
  interes text not null check (interes in ('bicicleta', 'naturaleza', 'comunidad')),
  recibir_actualizaciones boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create unique index if not exists suscriptores_email_unique
  on public.suscriptores (lower(email));

alter table public.suscriptores enable row level security;

-- Políticas abiertas únicamente para esta demostración académica.
-- No utilizar con datos personales reales ni en una aplicación de producción.
drop policy if exists "demo_select_suscriptores" on public.suscriptores;
drop policy if exists "demo_insert_suscriptores" on public.suscriptores;
drop policy if exists "demo_update_suscriptores" on public.suscriptores;
drop policy if exists "demo_delete_suscriptores" on public.suscriptores;

create policy "demo_select_suscriptores"
  on public.suscriptores for select
  to anon
  using (true);

create policy "demo_insert_suscriptores"
  on public.suscriptores for insert
  to anon
  with check (true);

create policy "demo_update_suscriptores"
  on public.suscriptores for update
  to anon
  using (true)
  with check (true);

create policy "demo_delete_suscriptores"
  on public.suscriptores for delete
  to anon
  using (true);
