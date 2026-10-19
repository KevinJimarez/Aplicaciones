-- EJEMPLOS CRUD PARA EXPLICAR EN CLASE
-- Ejecutar una sentencia a la vez desde Supabase > SQL Editor.

-- CREATE / INSERT: agrega un registro.
insert into public.suscriptores
  (nombre, email, interes, recibir_actualizaciones)
values
  ('Usuario de prueba', 'prueba@ecoruta.mx', 'naturaleza', true)
returning *;

-- READ / SELECT: consulta todos los registros, del más reciente al más antiguo.
select id, nombre, email, interes, recibir_actualizaciones, created_at, updated_at
from public.suscriptores
order by created_at desc;

-- UPDATE: modifica el registro identificado por su correo.
update public.suscriptores
set nombre = 'Usuario actualizado',
    interes = 'bicicleta',
    recibir_actualizaciones = false,
    updated_at = now()
where email = 'prueba@ecoruta.mx'
returning *;

-- DELETE: elimina solamente el registro de demostración.
delete from public.suscriptores
where email = 'prueba@ecoruta.mx'
returning *;
