-- Base de datos académica para EcoRuta.
-- Ejecutar en Supabase > SQL Editor.

create table if not exists public.suscriptores (
  id uuid primary key default gen_random_uuid(),
  nombre text not null check (char_length(nombre) between 2 and 80),
  email text not null check (char_length(email) between 5 and 120),
  interes text not null check (interes in ('bicicleta', 'naturaleza', 'comunidad')),
  recibir_actualizaciones boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create unique index if not exists suscriptores_email_unique
  on public.suscriptores (lower(email));

alter table public.suscriptores enable row level security;

-- La Data API revisa primero permisos SQL y después las políticas RLS.
revoke all on table public.suscriptores from anon, authenticated;
grant insert on table public.suscriptores to anon;
grant select, insert, update, delete on table public.suscriptores to authenticated;
grant all on table public.suscriptores to service_role;

drop policy if exists "public_insert_suscriptores" on public.suscriptores;
drop policy if exists "admin_select_suscriptores" on public.suscriptores;
drop policy if exists "admin_insert_suscriptores" on public.suscriptores;
drop policy if exists "admin_update_suscriptores" on public.suscriptores;
drop policy if exists "admin_delete_suscriptores" on public.suscriptores;

-- Cualquier visitante puede enviar el formulario, pero no consultar la tabla.
create policy "public_insert_suscriptores"
  on public.suscriptores for insert
  to anon
  with check (true);

-- Solo una sesión autenticada puede administrar registros.
create policy "admin_select_suscriptores"
  on public.suscriptores for select
  to authenticated
  using (true);

create policy "admin_insert_suscriptores"
  on public.suscriptores for insert
  to authenticated
  with check (true);

create policy "admin_update_suscriptores"
  on public.suscriptores for update
  to authenticated
  using (true)
  with check (true);

create policy "admin_delete_suscriptores"
  on public.suscriptores for delete
  to authenticated
  using (true);
