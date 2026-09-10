package com.example.myapplicationreloj.presentation

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.myapplicationreloj.presentation.theme.MyApplicationRelojTheme
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.text.Normalizer
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class CitaMedica(
    val id: Int,
    val doctor: String,
    val especialidad: String,
    val fecha: String,
    val hora: String
)

data class ContactoSalud(
    val tipo: String,
    val nombre: String,
    val telefono: String
)

data class LecturaSensor(
    val bpm: Int,
    val pasos: Int,
    val fechaHora: String,
    val alerta: Boolean
)

data class CancionSimulada(
    val idRaw: Int,
    val titulo: String,
    val artista: String
)

data class PerfilPaciente(
    val nombre: String,
    val edad: String,
    val pesoKg: String,
    val estaturaCm: String,
    val tipoSangre: String,
    val metaPasos: String,
    val objetivo: String,
    val telefono: String,
    val correo: String
)

data class MensajeWhatsAppSimulado(
    val destinatario: String,
    val telefono: String,
    val mensaje: String,
    val enlace: String
)

private object Rutas {
    const val INICIO = "inicio"
    const val NUTRIWATCH = "nutriwatch"
    const val AGENDAR = "agendar"
    const val MIS_CITAS = "mis_citas"
    const val MONITOREO = "monitoreo"
    const val CONTACTOS = "contactos"
    const val ASISTENTE = "asistente"
    const val PERFIL = "perfil"

    const val ACTIVIDAD = "actividad"
    const val MUSICA = "musica"
    const val CALCULADORA = "calculadora"
    const val LLAMADA = "llamada"
    const val CAMARA = "camara"
    const val YOUTUBE = "youtube"
    const val FACEBOOK = "facebook"
    const val INSTAGRAM = "instagram"
    const val CLASSROOM = "classroom"
    const val PLAYSTORE = "playstore"
    const val NIKE = "nike"
    const val MERCADO_LIBRE = "mercado_libre"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    MyApplicationRelojTheme {
        val formatoFecha = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

        val citas = remember {
            mutableStateListOf(
                CitaMedica(
                    id = 1,
                    doctor = "Ana López",
                    especialidad = "Nutriología",
                    fecha = LocalDate.now().plusDays(1).format(formatoFecha),
                    hora = "10:30 AM"
                )
            )
        }

        val contactos = remember {
            mutableStateListOf(
                ContactoSalud(
                    tipo = "Nutriólogo",
                    nombre = "Dra. Ana López",
                    telefono = "2215169172"
                ),
                ContactoSalud(
                    tipo = "Emergencia",
                    nombre = "Contacto familiar",
                    telefono = "2215169172"
                )
            )
        }

        val lecturas = remember { mutableStateListOf<LecturaSensor>() }

        var perfil by remember {
            mutableStateOf(
                PerfilPaciente(
                    nombre = "Juan Pérez",
                    edad = "31",
                    pesoKg = "78",
                    estaturaCm = "175",
                    tipoSangre = "O+",
                    metaPasos = "8000",
                    objetivo = "Mejorar hábitos alimenticios",
                    telefono = "2210000000",
                    correo = "juan.perez@email.com"
                )
            )
        }

        val alergias = remember {
            mutableStateListOf("Penicilina")
        }

        var pantallaActual by remember { mutableStateOf(Rutas.INICIO) }

        fun navegar(ruta: String) {
            pantallaActual = ruta
        }

        fun regresar() {
            pantallaActual = when (pantallaActual) {
                Rutas.AGENDAR,
                Rutas.MIS_CITAS,
                Rutas.MONITOREO,
                Rutas.CONTACTOS,
                Rutas.ASISTENTE,
                Rutas.PERFIL -> Rutas.NUTRIWATCH

                else -> Rutas.INICIO
            }
        }

        fun actualizarCita(citaActualizada: CitaMedica) {
            val indice = citas.indexOfFirst { it.id == citaActualizada.id }

            if (indice >= 0) {
                citas[indice] = citaActualizada
            }
        }

        fun actualizarContacto(indice: Int, contacto: ContactoSalud) {
            if (indice in contactos.indices) {
                contactos[indice] = contacto
            }
        }

        fun agregarContacto(contacto: ContactoSalud) {
            contactos.add(contacto)
        }

        fun eliminarContacto(indice: Int) {
            if (indice >= 2 && indice in contactos.indices) {
                contactos.removeAt(indice)
            }
        }

        BackHandler(enabled = pantallaActual != Rutas.INICIO) {
            regresar()
        }

        AppScaffold {
            when (pantallaActual) {
                Rutas.INICIO -> ContenedorPrincipalWear(onAbrir = ::navegar)

                Rutas.NUTRIWATCH -> PantallaNutriWatch(
                    citas = citas,
                    lecturas = lecturas,
                    onAbrir = ::navegar,
                    onSalir = ::regresar
                )

                Rutas.AGENDAR -> PantallaAgendarCita(
                    siguienteId = (citas.maxOfOrNull { it.id } ?: 0) + 1,
                    onGuardar = {
                        citas.add(it)
                        pantallaActual = Rutas.NUTRIWATCH
                    },
                    onSalir = ::regresar
                )

                Rutas.MIS_CITAS -> PantallaMisCitas(
                    citas = citas,
                    onActualizar = ::actualizarCita,
                    onEliminar = { id ->
                        citas.removeAll { it.id == id }
                    },
                    onSalir = ::regresar
                )

                Rutas.MONITOREO -> PantallaMonitoreo(
                    lecturas = lecturas,
                    doctor = contactos.first(),
                    onRegistrar = { lecturas.add(0, it) },
                    onSalir = ::regresar
                )

                Rutas.CONTACTOS -> PantallaContactos(
                    contactos = contactos,
                    onActualizar = ::actualizarContacto,
                    onAgregar = ::agregarContacto,
                    onEliminar = ::eliminarContacto,
                    onSalir = ::regresar
                )

                Rutas.ASISTENTE -> PantallaAsistenteIA(
                    citas = citas,
                    lecturas = lecturas,
                    perfil = perfil,
                    doctor = contactos.getOrElse(0) {
                        ContactoSalud("Nutriólogo", "Nutriólogo", "2215169172")
                    },
                    emergencia = contactos.getOrElse(1) {
                        ContactoSalud("Emergencia", "Contacto familiar", "2215169172")
                    },
                    onActualizarCita = ::actualizarCita,
                    onSalir = ::regresar
                )

                Rutas.PERFIL -> PantallaMiPerfil(
                    perfil = perfil,
                    alergias = alergias,
                    onGuardarPerfil = { perfil = it },
                    onAgregarAlergia = { nueva ->
                        if (
                            nueva.isNotBlank() &&
                            alergias.none { it.equals(nueva.trim(), ignoreCase = true) }
                        ) {
                            alergias.add(nueva.trim())
                        }
                    },
                    onEliminarAlergia = { alergia ->
                        alergias.remove(alergia)
                    },
                    onSalir = ::regresar
                )

                Rutas.ACTIVIDAD -> AppDisenosFitnessScreen(onSalir = ::regresar)
                Rutas.MUSICA -> AppMusicaScreen(onSalir = ::regresar)
                Rutas.CALCULADORA -> AppCalculadoraScreen(onSalir = ::regresar)
                Rutas.LLAMADA -> PantallaLlamada(onSalir = ::regresar)
                Rutas.CAMARA -> PantallaCamara(onSalir = ::regresar)

                Rutas.YOUTUBE -> PantallaAppExterna(
                    titulo = "YouTube",
                    icono = "▶",
                    descripcion = "Videos y contenido",
                    uri = "https://www.youtube.com",
                    onSalir = ::regresar
                )

                Rutas.FACEBOOK -> PantallaAppExterna(
                    titulo = "Facebook",
                    icono = "f",
                    descripcion = "Red social",
                    uri = "https://www.facebook.com",
                    onSalir = ::regresar
                )

                Rutas.INSTAGRAM -> PantallaAppExterna(
                    titulo = "Instagram",
                    icono = "◎",
                    descripcion = "Fotos y publicaciones",
                    uri = "https://www.instagram.com",
                    onSalir = ::regresar
                )

                Rutas.CLASSROOM -> PantallaAppExterna(
                    titulo = "Classroom",
                    icono = "C",
                    descripcion = "Clases y actividades",
                    uri = "https://classroom.google.com",
                    onSalir = ::regresar
                )

                Rutas.PLAYSTORE -> PantallaAppExterna(
                    titulo = "Play Store",
                    icono = "▷",
                    descripcion = "Aplicaciones",
                    uri = "market://details?id=com.google.android.wearable.app",
                    onSalir = ::regresar
                )

                Rutas.NIKE -> PantallaAppExterna(
                    titulo = "Nike Run",
                    icono = "✓",
                    descripcion = "Entrenamiento deportivo",
                    uri = "https://www.nike.com",
                    onSalir = ::regresar
                )

                Rutas.MERCADO_LIBRE -> PantallaAppExterna(
                    titulo = "Mercado Libre",
                    icono = "M",
                    descripcion = "Compras en línea",
                    uri = "https://www.mercadolibre.com.mx",
                    onSalir = ::regresar
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContenedorPrincipalWear(onAbrir: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pagina ->
        when (pagina) {
            0 -> CaratulaPrincipalScreen()
            else -> MenuAppsScreen(onAbrir = onAbrir)
        }
    }
}

@Composable
fun CaratulaPrincipalScreen() {
    val zonaMexico = remember { ZoneId.of("America/Mexico_City") }
    var tiempoActual by remember { mutableStateOf(ZonedDateTime.now(zonaMexico)) }

    LaunchedEffect(Unit) {
        while (true) {
            tiempoActual = ZonedDateTime.now(zonaMexico)
            delay(1000L)
        }
    }

    val horas = tiempoActual.format(DateTimeFormatter.ofPattern("HH"))
    val minutos = tiempoActual.format(DateTimeFormatter.ofPattern("mm"))
    val segundos = tiempoActual.format(DateTimeFormatter.ofPattern("ss"))

    val diaSemana = tiempoActual
        .format(DateTimeFormatter.ofPattern("EEEE", Locale("es", "MX")))
        .uppercase(Locale("es", "MX"))

    val fecha = tiempoActual
        .format(DateTimeFormatter.ofPattern("dd MMM", Locale("es", "MX")))
        .uppercase(Locale("es", "MX"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF15212B),
                        Color(0xFF071015),
                        Color.Black
                    )
                )
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centro = center
            val radio = size.minDimension * 0.46f

            drawCircle(
                color = Color(0xFF20323E),
                radius = radio,
                center = centro,
                style = Stroke(width = 1.dp.toPx())
            )

            for (i in 0 until 24) {
                val angulo = Math.toRadians((i * 15.0) - 90.0)
                val largo = if (i % 6 == 0) 9.dp.toPx() else 4.dp.toPx()

                val inicio = Offset(
                    x = centro.x + (radio - largo) * cos(angulo).toFloat(),
                    y = centro.y + (radio - largo) * sin(angulo).toFloat()
                )

                val fin = Offset(
                    x = centro.x + radio * cos(angulo).toFloat(),
                    y = centro.y + radio * sin(angulo).toFloat()
                )

                drawLine(
                    color = if (i % 6 == 0) {
                        Color(0xFF48D9C5)
                    } else {
                        Color.White.copy(alpha = 0.18f)
                    },
                    start = inicio,
                    end = fin,
                    strokeWidth = if (i % 6 == 0) 2.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            drawArc(
                color = Color(0xFF48D9C5),
                startAngle = 135f,
                sweepAngle = 75f,
                useCenter = false,
                topLeft = Offset(13.dp.toPx(), 13.dp.toPx()),
                size = Size(
                    width = size.width - 26.dp.toPx(),
                    height = size.height - 26.dp.toPx()
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = Color(0xFFFF6B5F),
                startAngle = -40f,
                sweepAngle = 55f,
                useCenter = false,
                topLeft = Offset(13.dp.toPx(), 13.dp.toPx()),
                size = Size(
                    width = size.width - 26.dp.toPx(),
                    height = size.height - 26.dp.toPx()
                ),
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "$diaSemana  •  $fecha",
                    color = Color(0xFFB6DAE4),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = horas,
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black
                )

                Column(
                    modifier = Modifier.padding(horizontal = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF48D9C5))
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF48D9C5))
                    )
                }

                Text(
                    text = minutos,
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = "SEG $segundos  •  MÉXICO",
                color = Color(0xFF718594),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.78f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MiniDatoWear("PASOS", "6.4K", Color(0xFF48D9C5))
                MiniDatoWear("BPM", "72", Color(0xFFFF6B5F))
                MiniDatoWear("BATERÍA", "85%", Color(0xFF6BCBFF))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "DESLIZA PARA ABRIR APPS  →",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MiniDatoWear(
    etiqueta: String,
    valor: String,
    colorValor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = etiqueta,
            color = Color.Gray,
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = valor,
            color = colorValor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MenuAppsScreen(onAbrir: (String) -> Unit) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF111820),
                            Color(0xFF070A0D),
                            Color.Black
                        )
                    )
                )
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = 32.dp
                )
            ) {
                item {
                    BotonMenuTransformado(
                        icono = "♥+",
                        titulo = "NutriWatch",
                        ruta = Rutas.NUTRIWATCH,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "🏃",
                        titulo = "Actividad",
                        ruta = Rutas.ACTIVIDAD,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "♫",
                        titulo = "Música",
                        ruta = Rutas.MUSICA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "÷",
                        titulo = "Calculadora",
                        ruta = Rutas.CALCULADORA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "☎",
                        titulo = "Llamada",
                        ruta = Rutas.LLAMADA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "●",
                        titulo = "Cámara",
                        ruta = Rutas.CAMARA,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "▶",
                        titulo = "YouTube",
                        ruta = Rutas.YOUTUBE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "f",
                        titulo = "Facebook",
                        ruta = Rutas.FACEBOOK,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "◎",
                        titulo = "Instagram",
                        ruta = Rutas.INSTAGRAM,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "C",
                        titulo = "Classroom",
                        ruta = Rutas.CLASSROOM,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "▷",
                        titulo = "Play Store",
                        ruta = Rutas.PLAYSTORE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "✓",
                        titulo = "Nike Run",
                        ruta = Rutas.NIKE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "M",
                        titulo = "Mercado Libre",
                        ruta = Rutas.MERCADO_LIBRE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .background(Color(0xFF111820))
                    .padding(top = 14.dp, bottom = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Aplicaciones",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Selecciona una opción",
                    color = Color(0xFF718392),
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun BotonMenuTransformado(
    icono: String,
    titulo: String,
    ruta: String,
    modifier: Modifier,
    transformation: SurfaceTransformation,
    onAbrir: (String) -> Unit
) {
    val colorIcono = when (ruta) {
        Rutas.NUTRIWATCH -> Color(0xFF00BFA5)
        Rutas.ACTIVIDAD -> Color(0xFFD4FF3B)
        Rutas.MUSICA -> Color(0xFFB768FF)
        Rutas.CALCULADORA -> Color(0xFF4C9CFF)
        Rutas.LLAMADA -> Color(0xFF36D675)
        Rutas.CAMARA -> Color(0xFFFF704D)
        Rutas.YOUTUBE -> Color(0xFFFF3B30)
        Rutas.FACEBOOK -> Color(0xFF1877F2)
        Rutas.INSTAGRAM -> Color(0xFFE45AA6)
        Rutas.CLASSROOM -> Color(0xFF37A866)
        Rutas.PLAYSTORE -> Color(0xFF55C7FF)
        Rutas.NIKE -> Color(0xFFF0F0F0)
        Rutas.MERCADO_LIBRE -> Color(0xFFFFD740)

        Rutas.AGENDAR,
        Rutas.MIS_CITAS,
        Rutas.MONITOREO,
        Rutas.CONTACTOS,
        Rutas.ASISTENTE,
        Rutas.PERFIL -> Color(0xFF00BFA5)

        else -> Color(0xFF607D8B)
    }

    val colorTextoIcono = when (ruta) {
        Rutas.ACTIVIDAD,
        Rutas.NIKE,
        Rutas.MERCADO_LIBRE -> Color.Black

        else -> Color.White
    }

    Button(
        onClick = { onAbrir(ruta) },
        modifier = modifier,
        transformation = transformation,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF171D23),
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(colorIcono),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icono,
                    color = colorTextoIcono,
                    fontSize = if (icono.length > 1) 12.sp else 16.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 45.dp, end = 18.dp)
            )

            Text(
                text = "›",
                color = Color(0xFF697986),
                fontSize = 19.sp,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun PantallaNutriWatch(
    citas: List<CitaMedica>,
    lecturas: List<LecturaSensor>,
    onAbrir: (String) -> Unit,
    onSalir: () -> Unit
) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    val proximaCita = citas.firstOrNull()
    val ultimaLectura = lecturas.firstOrNull()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF071412))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                item {
                    TarjetaTransformada(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        color = Color(0xFF10342F)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(13.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Bienvenido, Juan",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (proximaCita == null) {
                                    "No tienes citas próximas"
                                } else {
                                    "${proximaCita.fecha} • ${proximaCita.hora}"
                                },
                                color = Color(0xFF7FE5D2),
                                fontSize = 9.sp
                            )

                            if (proximaCita != null) {
                                Text(
                                    text = "Nutrióloga: ${proximaCita.doctor}",
                                    color = Color.LightGray,
                                    fontSize = 9.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(7.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(13.dp)
                            ) {
                                MiniDatoNutri(
                                    etiqueta = "BPM",
                                    valor = ultimaLectura?.bpm?.toString() ?: "--"
                                )

                                MiniDatoNutri(
                                    etiqueta = "PASOS",
                                    valor = ultimaLectura?.pasos?.toString() ?: "4200"
                                )
                            }
                        }
                    }
                }

                item {
                    BotonMenuTransformado(
                        icono = "♥",
                        titulo = "Monitoreo simulado",
                        ruta = Rutas.MONITOREO,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "＋",
                        titulo = "Agendar cita",
                        ruta = Rutas.AGENDAR,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "📅",
                        titulo = "Mis citas",
                        ruta = Rutas.MIS_CITAS,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "✨",
                        titulo = "Asistente IA",
                        ruta = Rutas.ASISTENTE,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "☎",
                        titulo = "Contactos",
                        ruta = Rutas.CONTACTOS,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    BotonMenuTransformado(
                        icono = "👤",
                        titulo = "Mi perfil",
                        ruta = Rutas.PERFIL,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        onAbrir = onAbrir
                    )
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A1717),
                            contentColor = Color(0xFFFF8A80)
                        )
                    ) {
                        Text("Cerrar NutriWatch")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "♥+  NutriWatch",
                subtitulo = "Seguimiento nutricional"
            )
        }
    }
}

@Composable
fun MiniDatoNutri(
    etiqueta: String,
    valor: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = etiqueta,
            color = Color(0xFF75A69E),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = valor,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PantallaMonitoreo(
    lecturas: List<LecturaSensor>,
    doctor: ContactoSalud,
    onRegistrar: (LecturaSensor) -> Unit,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current
    val sensorManager = remember {
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val sensorCorazon = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    }

    val sensorPasos = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    var modoReal by remember { mutableStateOf(false) }
    var escuchandoSensores by remember { mutableStateOf(false) }
    var bpmReal by remember { mutableIntStateOf(0) }
    var pasosReales by remember { mutableIntStateOf(0) }
    var precisionCorazon by remember { mutableStateOf("Sin lectura") }

    var bpmSimulado by remember { mutableIntStateOf(72) }
    var pasosSimulados by remember { mutableIntStateOf(4280) }
    var estadoSimulado by remember { mutableStateOf("Lectura estable") }

    var whatsappSimulado by remember {
        mutableStateOf<MensajeWhatsAppSimulado?>(null)
    }

    val permisosNecesarios = remember {
        permisosSensoresNecesarios()
    }

    var permisosConcedidos by remember {
        mutableStateOf(
            permisosSensoresConcedidos(
                contexto,
                permisosNecesarios
            )
        )
    }

    val lanzadorPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultados ->
        permisosConcedidos = resultados.values.all { it } ||
                permisosSensoresConcedidos(
                    contexto,
                    permisosNecesarios
                )
    }

    val listener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(evento: SensorEvent) {
                when (evento.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> {
                        val valor = evento.values.firstOrNull()?.toInt() ?: 0

                        if (valor > 0) {
                            bpmReal = valor
                        }
                    }

                    Sensor.TYPE_STEP_COUNTER -> {
                        pasosReales = evento.values
                            .firstOrNull()
                            ?.toInt()
                            ?: pasosReales
                    }
                }
            }

            override fun onAccuracyChanged(
                sensor: Sensor?,
                accuracy: Int
            ) {
                if (sensor?.type == Sensor.TYPE_HEART_RATE) {
                    precisionCorazon = when (accuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH ->
                            "Precisión alta"

                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM ->
                            "Precisión media"

                        SensorManager.SENSOR_STATUS_ACCURACY_LOW ->
                            "Precisión baja"

                        else ->
                            "Esperando contacto con la piel"
                    }
                }
            }
        }
    }

    DisposableEffect(
        modoReal,
        escuchandoSensores,
        permisosConcedidos,
        sensorCorazon,
        sensorPasos
    ) {
        if (
            modoReal &&
            escuchandoSensores &&
            permisosConcedidos
        ) {
            try {
                sensorCorazon?.let {
                    sensorManager.registerListener(
                        listener,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }

                sensorPasos?.let {
                    sensorManager.registerListener(
                        listener,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            } catch (_: SecurityException) {
                permisosConcedidos = false
            }
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val bpmMostrado = if (modoReal) bpmReal else bpmSimulado
    val pasosMostrados = if (modoReal) pasosReales else pasosSimulados
    val alertaActual = bpmMostrado > 0 &&
            (bpmMostrado < 50 || bpmMostrado > 110)

    fun registrarLecturaActual() {
        val lectura = LecturaSensor(
            bpm = bpmMostrado,
            pasos = pasosMostrados,
            fechaHora = fechaHoraActual(),
            alerta = alertaActual
        )

        onRegistrar(lectura)

        Toast.makeText(
            contexto,
            "Lectura guardada",
            Toast.LENGTH_SHORT
        ).show()
    }

    if (whatsappSimulado != null) {
        PantallaWhatsAppSimulado(
            datos = whatsappSimulado!!,
            onVolver = {
                whatsappSimulado = null
            }
        )
        return
    }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF071412))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 13.dp,
                    end = 13.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OpcionPequena(
                            texto = "Simulado",
                            seleccionada = !modoReal
                        ) {
                            escuchandoSensores = false
                            modoReal = false
                        }

                        OpcionPequena(
                            texto = "Sensor real",
                            seleccionada = modoReal
                        ) {
                            modoReal = true
                        }
                    }
                }

                item {
                    TarjetaTransformada(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        color = if (alertaActual) {
                            Color(0xFF4A1717)
                        } else {
                            Color(0xFF12342D)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (modoReal) {
                                    "SENSORES DEL RELOJ"
                                } else {
                                    "SENSORES SIMULADOS"
                                },
                                color = if (modoReal) {
                                    Color(0xFF80CBC4)
                                } else {
                                    Color(0xFFFFD180)
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (bpmMostrado > 0) {
                                    "$bpmMostrado BPM"
                                } else {
                                    "-- BPM"
                                },
                                color = if (alertaActual) {
                                    Color(0xFFFF8A80)
                                } else {
                                    Color.White
                                },
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Black
                            )

                            Text(
                                text = "$pasosMostrados pasos",
                                color = Color.LightGray,
                                fontSize = 10.sp
                            )

                            Text(
                                text = when {
                                    modoReal && !permisosConcedidos ->
                                        "Permisos pendientes"

                                    modoReal &&
                                            sensorCorazon == null &&
                                            sensorPasos == null ->
                                        "El dispositivo no tiene estos sensores"

                                    modoReal && !escuchandoSensores ->
                                        "Sensores detenidos"

                                    modoReal ->
                                        precisionCorazon

                                    else ->
                                        estadoSimulado
                                },
                                color = Color(0xFF8ED6C7),
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (!modoReal) {
                    item {
                        Button(
                            onClick = {
                                bpmSimulado = Random.nextInt(65, 91)
                                pasosSimulados = Random.nextInt(3500, 8001)
                                estadoSimulado = "Lectura estable"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF147D69),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Simular lectura normal")
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                bpmSimulado = Random.nextInt(122, 146)
                                pasosSimulados = Random.nextInt(1000, 6001)
                                estadoSimulado = "Lectura fuera del rango"
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF8A2D2A),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Simular alerta")
                        }
                    }
                } else {
                    if (!permisosConcedidos) {
                        item {
                            Button(
                                onClick = {
                                    lanzadorPermisos.launch(
                                        permisosNecesarios
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                transformation = SurfaceTransformation(
                                    transformationSpec
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF355C7D),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Conceder permisos")
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                if (!permisosConcedidos) {
                                    lanzadorPermisos.launch(
                                        permisosNecesarios
                                    )
                                } else if (
                                    sensorCorazon == null &&
                                    sensorPasos == null
                                ) {
                                    Toast.makeText(
                                        contexto,
                                        "No hay sensores disponibles en este dispositivo",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    escuchandoSensores = !escuchandoSensores
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (escuchandoSensores) {
                                    Color(0xFF8A2D2A)
                                } else {
                                    Color(0xFF147D69)
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                if (escuchandoSensores) {
                                    "Detener sensores"
                                } else {
                                    "Iniciar sensores"
                                }
                            )
                        }
                    }
                }

                if (bpmMostrado > 0) {
                    item {
                        Button(
                            onClick = {
                                registrarLecturaActual()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            )
                        ) {
                            Text("Guardar lectura")
                        }
                    }
                }

                if (alertaActual) {
                    item {
                        Button(
                            onClick = {
                                whatsappSimulado = prepararWhatsAppSimulado(
                                    context = contexto,
                                    destinatario = doctor.nombre,
                                    telefono = doctor.telefono,
                                    mensaje = crearMensajeAlerta(
                                        doctor = doctor.nombre,
                                        bpm = bpmMostrado,
                                        pasos = pasosMostrados
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1FA855),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Simular aviso WhatsApp")
                        }
                    }
                }

                if (lecturas.isNotEmpty()) {
                    item {
                        Text(
                            text = "Historial reciente",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    lecturas.take(3).forEach { lectura ->
                        item {
                            TarjetaTransformada(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                color = Color(0xFF17212B)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(11.dp)
                                ) {
                                    Text(
                                        text = "${lectura.bpm} BPM • ${lectura.pasos} pasos",
                                        color = if (lectura.alerta) {
                                            Color(0xFFFF8A80)
                                        } else {
                                            Color(0xFF80CBC4)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = lectura.fechaHora,
                                        color = Color.Gray,
                                        fontSize = 8.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        )
                    ) {
                        Text("Regresar")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "Monitoreo",
                subtitulo = "Simulado y sensores reales"
            )
        }
    }
}

fun permisosSensoresNecesarios(): Array<String> {
    val permisos = mutableListOf<String>()

    if (Build.VERSION.SDK_INT >= 29) {
        permisos.add(Manifest.permission.ACTIVITY_RECOGNITION)
    }

    if (Build.VERSION.SDK_INT >= 36) {
        permisos.add("android.permission.health.READ_HEART_RATE")
    } else {
        permisos.add(Manifest.permission.BODY_SENSORS)
    }

    return permisos.distinct().toTypedArray()
}

fun permisosSensoresConcedidos(
    context: Context,
    permisos: Array<String>
): Boolean {
    return permisos.all { permiso ->
        context.checkSelfPermission(permiso) ==
                PackageManager.PERMISSION_GRANTED
    }
}

fun fechaHoraActual(): String {
    return ZonedDateTime
        .now(ZoneId.of("America/Mexico_City"))
        .format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy HH:mm:ss"
            )
        )
}

fun crearMensajeAlerta(
    doctor: String,
    bpm: Int,
    pasos: Int,
    paciente: String = "Juan Pérez"
): String {
    val fechaHora = ZonedDateTime
        .now(ZoneId.of("America/Mexico_City"))
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))

    return """
        Alerta de NutriWatch

        Para: $doctor
        Paciente: $paciente
        Frecuencia registrada: $bpm BPM
        Pasos registrados: $pasos
        Fecha y hora: $fechaHora

        La aplicación detectó una lectura fuera del rango configurado.
        Favor de contactar al paciente.
    """.trimIndent()
}

@Composable
fun PantallaContactos(
    contactos: List<ContactoSalud>,
    onActualizar: (Int, ContactoSalud) -> Unit,
    onAgregar: (ContactoSalud) -> Unit,
    onEliminar: (Int) -> Unit,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current

    var whatsappSimulado by remember {
        mutableStateOf<MensajeWhatsAppSimulado?>(null)
    }

    if (whatsappSimulado != null) {
        PantallaWhatsAppSimulado(
            datos = whatsappSimulado!!,
            onVolver = {
                whatsappSimulado = null
            }
        )
        return
    }

    var indiceSeleccionado by remember {
        mutableIntStateOf(0)
    }

    if (contactos.isNotEmpty() && indiceSeleccionado > contactos.lastIndex) {
        indiceSeleccionado = contactos.lastIndex
    }

    val contactoSeleccionado = contactos.getOrElse(indiceSeleccionado) {
        ContactoSalud(
            tipo = "Otro",
            nombre = "Nuevo contacto",
            telefono = ""
        )
    }

    var tipoEditando by remember(
        indiceSeleccionado,
        contactoSeleccionado.tipo
    ) {
        mutableStateOf(contactoSeleccionado.tipo)
    }

    var nombreEditando by remember(
        indiceSeleccionado,
        contactoSeleccionado.nombre
    ) {
        mutableStateOf(contactoSeleccionado.nombre)
    }

    var telefonoEditando by remember(
        indiceSeleccionado,
        contactoSeleccionado.telefono
    ) {
        mutableStateOf(
            contactoSeleccionado.telefono.filter { it.isDigit() }
        )
    }

    val tiposPrimeraFila = remember {
        listOf("Nutriólogo", "Emergencia", "Familiar")
    }

    val tiposSegundaFila = remember {
        listOf("Amigo", "Otro")
    }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF08110F))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 13.dp,
                    end = 13.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                item {
                    Text(
                        text = "Contactos guardados",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                contactos.forEachIndexed { indice, contacto ->
                    item {
                        Button(
                            onClick = {
                                indiceSeleccionado = indice
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (indiceSeleccionado == indice) {
                                    Color(0xFF147D69)
                                } else {
                                    Color(0xFF17231F)
                                },
                                contentColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(31.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (contacto.tipo) {
                                                "Nutriólogo" -> Color(0xFF00A98F)
                                                "Emergencia" -> Color(0xFFC54945)
                                                "Familiar" -> Color(0xFF8D6E63)
                                                "Amigo" -> Color(0xFF456FB0)
                                                else -> Color(0xFF6A5B78)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (contacto.tipo) {
                                            "Nutriólogo" -> "N"
                                            "Emergencia" -> "!"
                                            "Familiar" -> "F"
                                            "Amigo" -> "A"
                                            else -> "C"
                                        },
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    modifier = Modifier.width(112.dp)
                                ) {
                                    Text(
                                        text = contacto.nombre.ifBlank {
                                            "Sin nombre"
                                        },
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Text(
                                        text = "${contacto.tipo} • ${
                                            contacto.telefono.ifBlank {
                                                "Sin número"
                                            }
                                        }",
                                        color = Color.LightGray,
                                        fontSize = 8.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Text(
                                    text = "›",
                                    color = Color.Gray,
                                    fontSize = 17.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            val nuevoIndice = contactos.size

                            onAgregar(
                                ContactoSalud(
                                    tipo = "Otro",
                                    nombre = "Nuevo contacto",
                                    telefono = ""
                                )
                            )

                            indiceSeleccionado = nuevoIndice
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF224C42),
                            contentColor = Color.White
                        )
                    ) {
                        Text("＋ Agregar contacto")
                    }
                }

                item {
                    TarjetaTransformada(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        color = Color(0xFF12342D)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(13.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Editando contacto ${indiceSeleccionado + 1}",
                                color = Color(0xFF83E6D4),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = nombreEditando.ifBlank {
                                    "Sin nombre"
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = tipoEditando,
                                color = Color.LightGray,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Tipo de contacto",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tiposPrimeraFila.forEach { tipo ->
                            BotonTipoContacto(
                                texto = tipo,
                                seleccionado = tipoEditando == tipo
                            ) {
                                tipoEditando = tipo
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tiposSegundaFila.forEach { tipo ->
                            BotonTipoContacto(
                                texto = tipo,
                                seleccionado = tipoEditando == tipo
                            ) {
                                tipoEditando = tipo
                            }
                        }
                    }
                }

                item {
                    CampoWear(
                        valor = nombreEditando,
                        placeholder = "Nombre del contacto",
                        onCambio = {
                            nombreEditando = it
                        }
                    )
                }

                item {
                    TarjetaTransformada(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        color = Color(0xFF151D1B)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Teléfono / WhatsApp",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = telefonoEditando.ifBlank {
                                    "Escribe el número"
                                },
                                color = Color(0xFF7FE5D2),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "México: 10 dígitos",
                                color = Color.Gray,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                item {
                    TecladoTelefono(
                        telefono = telefonoEditando,
                        onTelefonoCambia = {
                            telefonoEditando = it
                        }
                    )
                }

                item {
                    Button(
                        onClick = {
                            val telefonoLimpio = telefonoEditando.filter {
                                it.isDigit()
                            }

                            if (!numeroWhatsAppValido(telefonoLimpio)) {
                                Toast.makeText(
                                    contexto,
                                    "El número debe tener entre 10 y 13 dígitos",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                onActualizar(
                                    indiceSeleccionado,
                                    ContactoSalud(
                                        tipo = tipoEditando,
                                        nombre = nombreEditando.ifBlank {
                                            "Contacto"
                                        }.trim(),
                                        telefono = telefonoLimpio
                                    )
                                )

                                Toast.makeText(
                                    contexto,
                                    "Contacto guardado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF147D69),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar cambios")
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (!numeroWhatsAppValido(telefonoEditando)) {
                                Toast.makeText(
                                    contexto,
                                    "Configura un número válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                whatsappSimulado = prepararWhatsAppSimulado(
                                    context = contexto,
                                    destinatario = nombreEditando.ifBlank {
                                        "Contacto"
                                    },
                                    telefono = telefonoEditando,
                                    mensaje = "Hola ${
                                        nombreEditando.ifBlank {
                                            "contacto"
                                        }
                                    }, este es un mensaje de prueba enviado desde NutriWatch."
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1FA855),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Probar WhatsApp")
                    }
                }

                item {
                    Button(
                        onClick = {
                            abrirAppExterna(
                                contexto,
                                "tel:${
                                    normalizarNumeroTelefono(
                                        telefonoEditando
                                    )
                                }"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        )
                    ) {
                        Text("Llamar contacto")
                    }
                }

                if (indiceSeleccionado >= 2) {
                    item {
                        Button(
                            onClick = {
                                onEliminar(indiceSeleccionado)

                                indiceSeleccionado = if (contactos.size > 1) {
                                    (indiceSeleccionado - 1)
                                        .coerceAtLeast(0)
                                } else {
                                    0
                                }

                                Toast.makeText(
                                    contexto,
                                    "Contacto eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6D2826),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Eliminar contacto")
                        }
                    }
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        )
                    ) {
                        Text("Regresar")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "Contactos",
                subtitulo = "${contactos.size} contactos guardados"
            )
        }
    }
}

@Composable
fun BotonTipoContacto(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(
                when (texto) {
                    "Nutriólogo" -> 66.dp
                    "Emergencia" -> 68.dp
                    else -> 58.dp
                }
            )
            .height(34.dp)
            .clip(RoundedCornerShape(17.dp))
            .background(
                if (seleccionado) {
                    Color(0xFF147D69)
                } else {
                    Color(0xFF202B27)
                }
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontSize = if (texto.length > 8) {
                7.sp
            } else {
                8.sp
            },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun OpcionPequena(
    texto: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(17.dp))
            .background(
                if (seleccionada) {
                    Color(0xFF147D69)
                } else {
                    Color(0xFF26312E)
                }
            )
            .clickable { onClick() }
            .padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontSize = if (texto.length > 9) 7.sp else 8.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TecladoTelefono(
    telefono: String,
    onTelefonoCambia: (String) -> Unit
) {
    val filas = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("Limpiar", "0", "⌫")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        filas.forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                fila.forEach { tecla ->
                    Box(
                        modifier = Modifier
                            .size(
                                width = if (tecla == "Limpiar") 58.dp else 40.dp,
                                height = 38.dp
                            )
                            .padding(2.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                if (tecla == "Limpiar" || tecla == "⌫") {
                                    Color(0xFF3A2422)
                                } else {
                                    Color(0xFF172822)
                                }
                            )
                            .clickable {
                                when (tecla) {
                                    "Limpiar" -> onTelefonoCambia("")
                                    "⌫" -> {
                                        if (telefono.isNotEmpty()) {
                                            onTelefonoCambia(telefono.dropLast(1))
                                        }
                                    }
                                    else -> {
                                        if (telefono.length < 13) {
                                            onTelefonoCambia(telefono + tecla)
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tecla,
                            color = Color.White,
                            fontSize = if (tecla == "Limpiar") 8.sp else 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun numeroWhatsAppValido(numero: String): Boolean {
    val limpio = numero.filter { it.isDigit() }
    return limpio.length in 10..13
}

@Composable
fun PantallaAsistenteIA(
    citas: List<CitaMedica>,
    lecturas: List<LecturaSensor>,
    perfil: PerfilPaciente,
    doctor: ContactoSalud,
    emergencia: ContactoSalud,
    onActualizarCita: (CitaMedica) -> Unit,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current
    val cita = citas.firstOrNull()

    var entrada by remember { mutableStateOf("") }
    var respuestaIA by remember {
        mutableStateOf(
            "Hola ${perfil.nombre}. Puedes escribirme: hola, cuándo es mi cita, pospón mi cita, me siento mal o cuántos pasos llevo."
        )
    }

    var mostrarReprogramacion by remember {
        mutableStateOf(false)
    }

    var whatsappSimulado by remember {
        mutableStateOf<MensajeWhatsAppSimulado?>(null)
    }

    var fechaElegida by remember(cita?.fecha) {
        mutableStateOf(cita?.fecha.orEmpty())
    }

    var horaElegida by remember(cita?.hora) {
        mutableStateOf(cita?.hora.orEmpty())
    }

    fun posponerUnDia() {
        if (cita == null) {
            respuestaIA = "No hay una cita guardada para posponer."
            return
        }

        val nuevaFecha = sumarDiasAFecha(
            fecha = cita.fecha,
            dias = 1
        )

        onActualizarCita(
            cita.copy(fecha = nuevaFecha)
        )

        respuestaIA =
            "Listo. Pospondré tu cita un día. La nueva fecha es $nuevaFecha a las ${cita.hora}."
    }

    fun procesarMensaje(textoOriginal: String) {
        val texto = normalizarTextoIA(textoOriginal)

        when {
            texto.isBlank() -> {
                respuestaIA = "Escribe una pregunta o usa una opción rápida."
            }

            texto.contains("hola") ||
                    texto.contains("buenos dias") ||
                    texto.contains("buenas tardes") -> {
                respuestaIA =
                    "Hola ${perfil.nombre}. Soy el asistente local de NutriWatch. ¿En qué te ayudo?"
            }

            (
                    texto.contains("cuando") ||
                            texto.contains("fecha") ||
                            texto.contains("proxima")
                    ) && texto.contains("cita") -> {
                respuestaIA = obtenerMensajeAsistente(cita)
            }

            texto.contains("pospon") &&
                    texto.contains("cita") -> {
                posponerUnDia()
            }

            (
                    texto.contains("cambiar") ||
                            texto.contains("editar") ||
                            texto.contains("reprogram")
                    ) && texto.contains("cita") -> {
                if (cita == null) {
                    respuestaIA =
                        "No hay una cita guardada. Primero agrega una desde Mis citas."
                } else {
                    respuestaIA =
                        "Selecciona abajo una fecha y hora para reprogramarla."
                    fechaElegida = cita.fecha
                    horaElegida = cita.hora
                    mostrarReprogramacion = true
                }
            }

            texto.contains("mal") ||
                    texto.contains("mareado") ||
                    texto.contains("dolor") -> {
                respuestaIA =
                    "Voy a preparar un aviso para ${doctor.nombre}. Esta app no diagnostica enfermedades."

                whatsappSimulado = prepararWhatsAppSimulado(
                    context = contexto,
                    destinatario = doctor.nombre,
                    telefono = doctor.telefono,
                    mensaje = crearMensajeMalestar(
                        paciente = perfil.nombre,
                        doctor = doctor.nombre
                    )
                )
            }

            texto.contains("pasos") -> {
                val ultima = lecturas.firstOrNull()

                respuestaIA = if (ultima != null) {
                    "Tu última lectura registrada muestra ${ultima.pasos} pasos."
                } else {
                    "Aún no hay pasos guardados. Realiza una lectura desde Monitoreo."
                }
            }

            texto.contains("frecuencia") ||
                    texto.contains("bpm") ||
                    texto.contains("corazon") -> {
                val ultima = lecturas.firstOrNull()

                respuestaIA = if (ultima != null) {
                    "Tu última frecuencia registrada fue de ${ultima.bpm} BPM."
                } else {
                    "Aún no hay una frecuencia registrada."
                }
            }

            texto.contains("emergencia") ||
                    texto.contains("911") -> {
                respuestaIA =
                    "Abriré el marcador con el número 911 para que confirmes la llamada."

                abrirMarcador911(contexto)
            }

            texto.contains("doctor") ||
                    texto.contains("nutriologo") -> {
                respuestaIA =
                    "Tu nutriólogo guardado es ${doctor.nombre}. Su contacto es ${doctor.telefono}."
            }

            texto.contains("ayuda") ||
                    texto.contains("que puedes") -> {
                respuestaIA =
                    "Puedo saludar, consultar o posponer tu cita, mostrar pasos y frecuencia, preparar un aviso por WhatsApp y abrir el marcador 911."
            }

            else -> {
                respuestaIA =
                    "No entendí esa frase. Prueba: hola, cuándo es mi cita, pospón mi cita, me siento mal, pasos, frecuencia o emergencia."
            }
        }
    }

    if (whatsappSimulado != null) {
        PantallaWhatsAppSimulado(
            datos = whatsappSimulado!!,
            onVolver = {
                whatsappSimulado = null
            }
        )
        return
    }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF100B18))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 13.dp,
                    end = 13.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                item {
                    TarjetaTransformada(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        color = Color(0xFF2A183B)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✦",
                                color = Color(0xFFCE93D8),
                                fontSize = 24.sp
                            )

                            Text(
                                text = "Asistente local",
                                color = Color(0xFFCE93D8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = respuestaIA,
                                color = Color.White,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                item {
                    CampoWear(
                        valor = entrada,
                        placeholder = "Escribe: Hola",
                        onCambio = {
                            entrada = it
                        }
                    )
                }

                item {
                    Button(
                        onClick = {
                            procesarMensaje(entrada)
                            entrada = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF71498B),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Enviar al asistente")
                    }
                }

                listOf(
                    "Hola",
                    "¿Cuándo es mi cita?",
                    "Pospón mi cita",
                    "Me siento mal",
                    "¿Cuántos pasos llevo?"
                ).forEach { opcion ->
                    item {
                        Button(
                            onClick = {
                                procesarMensaje(opcion)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25192E),
                                contentColor = Color.White
                            )
                        ) {
                            Text(opcion)
                        }
                    }
                }

                if (mostrarReprogramacion && cita != null) {
                    item {
                        CampoWear(
                            valor = fechaElegida,
                            placeholder = "Fecha dd/MM/yyyy",
                            onCambio = {
                                fechaElegida = it
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = horaElegida,
                            placeholder = "Hora",
                            onCambio = {
                                horaElegida = it
                            }
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                if (!esFechaValida(fechaElegida)) {
                                    Toast.makeText(
                                        contexto,
                                        "Usa el formato dd/MM/yyyy",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    onActualizarCita(
                                        cita.copy(
                                            fecha = fechaElegida.trim(),
                                            hora = horaElegida.ifBlank {
                                                cita.hora
                                            }.trim()
                                        )
                                    )

                                    respuestaIA =
                                        "Listo. Tu cita quedó para el ${fechaElegida.trim()} a las ${horaElegida.ifBlank { cita.hora }}."

                                    mostrarReprogramacion = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF147D69),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Guardar nueva cita")
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            abrirMarcador911(contexto)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8A2D2A),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Emergencia: marcar 911")
                    }
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        )
                    ) {
                        Text("Regresar")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "Asistente IA",
                subtitulo = "Motor local sin API"
            )
        }
    }
}

fun normalizarTextoIA(texto: String): String {
    return Normalizer
        .normalize(
            texto.lowercase(Locale("es", "MX")),
            Normalizer.Form.NFD
        )
        .replace("\\p{Mn}+".toRegex(), "")
        .trim()
}

fun sumarDiasAFecha(
    fecha: String,
    dias: Long
): String {
    return try {
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        LocalDate
            .parse(fecha, formato)
            .plusDays(dias)
            .format(formato)
    } catch (_: Exception) {
        LocalDate
            .now()
            .plusDays(dias)
            .format(
                DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
                )
            )
    }
}

fun crearMensajeMalestar(
    paciente: String,
    doctor: String
): String {
    val fechaHora = ZonedDateTime
        .now(ZoneId.of("America/Mexico_City"))
        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))

    return """
        Aviso de NutriWatch

        Doctor: $doctor
        Paciente: $paciente
        Fecha y hora: $fechaHora

        El paciente seleccionó la opción "Me siento mal" en el asistente de NutriWatch.
        Favor de ponerse en contacto con el paciente.

        Este mensaje fue preparado automáticamente por la aplicación.
    """.trimIndent()
}

fun obtenerMensajeAsistente(cita: CitaMedica?): String {
    if (cita == null) {
        return "No encontré citas. Puedes agendar una nueva consulta desde el menú."
    }

    return try {
        val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaCita = LocalDate.parse(cita.fecha, formato)
        val dias = ChronoUnit.DAYS.between(LocalDate.now(), fechaCita)

        when {
            dias < 0 -> {
                "Tu cita anterior ya pasó. ¿Quieres renovar la fecha?"
            }

            dias == 0L -> {
                "Tienes una cita hoy con ${cita.doctor}. ¿Deseas mantenerla o cambiar la fecha?"
            }

            dias == 1L -> {
                "Tu cita con ${cita.doctor} es mañana a las ${cita.hora}."
            }

            dias <= 7L -> {
                "Tu cita con ${cita.doctor} será en $dias días."
            }

            else -> {
                "Tu próxima cita está programada para el ${cita.fecha}."
            }
        }
    } catch (_: Exception) {
        "Encontré una cita guardada. Revisa la fecha o cámbiala desde este asistente."
    }
}

fun esFechaValida(fecha: String): Boolean {
    return try {
        LocalDate.parse(
            fecha.trim(),
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        true
    } catch (_: Exception) {
        false
    }
}

@Composable
fun PantallaAgendarCita(
    siguienteId: Int,
    onGuardar: (CitaMedica) -> Unit,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current

    var doctor by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("Nutriología") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF071412))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 13.dp,
                    end = 13.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                item {
                    CampoWear(
                        valor = doctor,
                        placeholder = "Nombre del nutriólogo",
                        onCambio = { doctor = it }
                    )
                }

                item {
                    CampoWear(
                        valor = especialidad,
                        placeholder = "Especialidad",
                        onCambio = { especialidad = it }
                    )
                }

                item {
                    CampoWear(
                        valor = fecha,
                        placeholder = "Fecha: dd/MM/yyyy",
                        onCambio = { fecha = it }
                    )
                }

                item {
                    CampoWear(
                        valor = hora,
                        placeholder = "Hora: 10:30 AM",
                        onCambio = { hora = it }
                    )
                }

                item {
                    Button(
                        onClick = {
                            when {
                                doctor.isBlank() -> {
                                    Toast.makeText(
                                        contexto,
                                        "Escribe el nombre del nutriólogo",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                !esFechaValida(fecha) -> {
                                    Toast.makeText(
                                        contexto,
                                        "Usa el formato dd/MM/yyyy",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> {
                                    onGuardar(
                                        CitaMedica(
                                            id = siguienteId,
                                            doctor = doctor.trim(),
                                            especialidad = especialidad.ifBlank {
                                                "Nutriología"
                                            }.trim(),
                                            fecha = fecha.trim(),
                                            hora = hora.ifBlank {
                                                "Por confirmar"
                                            }.trim()
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF147D69),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Guardar cita")
                    }
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(transformationSpec)
                    ) {
                        Text("Cancelar")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "Agendar cita",
                subtitulo = "Consulta nutricional"
            )
        }
    }
}

@Composable
fun CampoWear(
    valor: String,
    placeholder: String,
    onCambio: (String) -> Unit
) {
    BasicTextField(
        value = valor,
        onValueChange = onCambio,
        singleLine = true,
        textStyle = TextStyle(
            color = Color.White,
            fontSize = 11.sp
        ),
        cursorBrush = SolidColor(Color(0xFF55C7FF)),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1A222B))
            .border(
                width = 1.dp,
                color = Color(0xFF344657),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 15.dp, vertical = 15.dp),
        decorationBox = { campo ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (valor.isBlank()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }

                campo()
            }
        }
    )
}

@Composable
fun PantallaMisCitas(
    citas: List<CitaMedica>,
    onActualizar: (CitaMedica) -> Unit,
    onEliminar: (Int) -> Unit,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current

    var citaEditandoId by remember {
        mutableStateOf<Int?>(null)
    }

    var doctorEditando by remember {
        mutableStateOf("")
    }

    var especialidadEditando by remember {
        mutableStateOf("")
    }

    var fechaEditando by remember {
        mutableStateOf("")
    }

    var horaEditando by remember {
        mutableStateOf("")
    }

    fun comenzarEdicion(cita: CitaMedica) {
        citaEditandoId = cita.id
        doctorEditando = cita.doctor
        especialidadEditando = cita.especialidad
        fechaEditando = cita.fecha
        horaEditando = cita.hora
    }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF071412))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 13.dp,
                    end = 13.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                if (citas.isEmpty()) {
                    item {
                        TarjetaTransformada(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            color = Color(0xFF17212B)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(15.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "📅",
                                    fontSize = 24.sp
                                )

                                Text(
                                    text = "No hay citas",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    citas.forEach { cita ->
                        item {
                            TarjetaTransformada(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(this, transformationSpec),
                                color = Color(0xFF17212B)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(13.dp)
                                ) {
                                    Text(
                                        text = "Dra./Dr. ${cita.doctor}",
                                        color = Color(0xFF7FE5D2),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = cita.especialidad,
                                        color = Color.LightGray,
                                        fontSize = 9.sp
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = "📅 ${cita.fecha}",
                                        color = Color.White,
                                        fontSize = 9.sp
                                    )

                                    Text(
                                        text = "⏰ ${cita.hora}",
                                        color = Color.White,
                                        fontSize = 9.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        AccionCita(
                                            texto = "Editar",
                                            color = Color(0xFF355C7D)
                                        ) {
                                            comenzarEdicion(cita)
                                        }

                                        AccionCita(
                                            texto = "+1 día",
                                            color = Color(0xFF147D69)
                                        ) {
                                            val actualizada = cita.copy(
                                                fecha = sumarDiasAFecha(
                                                    cita.fecha,
                                                    1
                                                )
                                            )

                                            onActualizar(actualizada)

                                            Toast.makeText(
                                                contexto,
                                                "Cita pospuesta un día",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }

                                        AccionCita(
                                            texto = "Eliminar",
                                            color = Color(0xFF6D2826)
                                        ) {
                                            onEliminar(cita.id)
                                        }
                                    }
                                }
                            }
                        }

                        if (citaEditandoId == cita.id) {
                            item {
                                CampoWear(
                                    valor = doctorEditando,
                                    placeholder = "Nombre del nutriólogo",
                                    onCambio = {
                                        doctorEditando = it
                                    }
                                )
                            }

                            item {
                                CampoWear(
                                    valor = especialidadEditando,
                                    placeholder = "Especialidad",
                                    onCambio = {
                                        especialidadEditando = it
                                    }
                                )
                            }

                            item {
                                CampoWear(
                                    valor = fechaEditando,
                                    placeholder = "Fecha dd/MM/yyyy",
                                    onCambio = {
                                        fechaEditando = it
                                    }
                                )
                            }

                            item {
                                CampoWear(
                                    valor = horaEditando,
                                    placeholder = "Hora",
                                    onCambio = {
                                        horaEditando = it
                                    }
                                )
                            }

                            item {
                                Button(
                                    onClick = {
                                        if (!esFechaValida(fechaEditando)) {
                                            Toast.makeText(
                                                contexto,
                                                "Usa el formato dd/MM/yyyy",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            onActualizar(
                                                cita.copy(
                                                    doctor = doctorEditando
                                                        .ifBlank {
                                                            cita.doctor
                                                        }
                                                        .trim(),
                                                    especialidad = especialidadEditando
                                                        .ifBlank {
                                                            cita.especialidad
                                                        }
                                                        .trim(),
                                                    fecha = fechaEditando.trim(),
                                                    hora = horaEditando
                                                        .ifBlank {
                                                            cita.hora
                                                        }
                                                        .trim()
                                                )
                                            )

                                            citaEditandoId = null

                                            Toast.makeText(
                                                contexto,
                                                "Cita actualizada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(
                                            this,
                                            transformationSpec
                                        ),
                                    transformation = SurfaceTransformation(
                                        transformationSpec
                                    ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF147D69),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Guardar edición")
                                }
                            }

                            item {
                                Button(
                                    onClick = {
                                        citaEditandoId = null
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .transformedHeight(
                                            this,
                                            transformationSpec
                                        ),
                                    transformation = SurfaceTransformation(
                                        transformationSpec
                                    )
                                ) {
                                    Text("Cancelar edición")
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        )
                    ) {
                        Text("Regresar")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "Mis citas",
                subtitulo = "Editar, posponer o eliminar"
            )
        }
    }
}

@Composable
fun AccionCita(
    texto: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(58.dp)
            .height(31.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(color)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PantallaMiPerfil(
    perfil: PerfilPaciente,
    alergias: List<String>,
    onGuardarPerfil: (PerfilPaciente) -> Unit,
    onAgregarAlergia: (String) -> Unit,
    onEliminarAlergia: (String) -> Unit,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current

    var editando by remember {
        mutableStateOf(false)
    }

    var nombre by remember(perfil.nombre) {
        mutableStateOf(perfil.nombre)
    }

    var edad by remember(perfil.edad) {
        mutableStateOf(perfil.edad)
    }

    var peso by remember(perfil.pesoKg) {
        mutableStateOf(perfil.pesoKg)
    }

    var estatura by remember(perfil.estaturaCm) {
        mutableStateOf(perfil.estaturaCm)
    }

    var tipoSangre by remember(perfil.tipoSangre) {
        mutableStateOf(perfil.tipoSangre)
    }

    var metaPasos by remember(perfil.metaPasos) {
        mutableStateOf(perfil.metaPasos)
    }

    var objetivo by remember(perfil.objetivo) {
        mutableStateOf(perfil.objetivo)
    }

    var telefono by remember(perfil.telefono) {
        mutableStateOf(perfil.telefono)
    }

    var correo by remember(perfil.correo) {
        mutableStateOf(perfil.correo)
    }

    var nuevaAlergia by remember {
        mutableStateOf("")
    }

    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    ScreenScaffold(scrollState = listState) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF071412))
        ) {
            TransformingLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 48.dp),
                state = listState,
                contentPadding = PaddingValues(
                    start = 13.dp,
                    end = 13.dp,
                    top = 6.dp,
                    bottom = 30.dp
                )
            ) {
                item {
                    TarjetaTransformada(
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        color = Color(0xFF12342D)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00A88E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = inicialesPerfil(nombre),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = nombre.ifBlank {
                                    "Paciente"
                                },
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = objetivo.ifBlank {
                                    "Paciente NutriWatch"
                                },
                                color = Color(0xFF8ED6C7),
                                fontSize = 9.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (!editando) {
                    item {
                        TarjetaTransformada(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            color = Color(0xFF17212B)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(13.dp)
                            ) {
                                DatoPerfil("Edad", "$edad años")
                                DatoPerfil("Peso", "$peso kg")
                                DatoPerfil("Estatura", "$estatura cm")
                                DatoPerfil("Meta de pasos", metaPasos)
                                DatoPerfil("Tipo de sangre", tipoSangre)
                                DatoPerfil("Teléfono", telefono)
                                DatoPerfil("Correo", correo)
                                DatoPerfil(
                                    "Alergias",
                                    if (alergias.isEmpty()) {
                                        "Sin alergias registradas"
                                    } else {
                                        alergias.joinToString(", ")
                                    },
                                    Color(0xFFFF8A80)
                                )
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                editando = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF355C7D),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Editar perfil")
                        }
                    }
                } else {
                    item {
                        CampoWear(
                            valor = nombre,
                            placeholder = "Nombre",
                            onCambio = {
                                nombre = it
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = edad,
                            placeholder = "Edad",
                            onCambio = {
                                edad = it.filter { caracter ->
                                    caracter.isDigit()
                                }.take(3)
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = peso,
                            placeholder = "Peso en kg",
                            onCambio = {
                                peso = it.filter { caracter ->
                                    caracter.isDigit() ||
                                            caracter == '.'
                                }.take(6)
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = estatura,
                            placeholder = "Estatura en cm",
                            onCambio = {
                                estatura = it.filter { caracter ->
                                    caracter.isDigit()
                                }.take(3)
                            }
                        )
                    }

                    item {
                        Text(
                            text = "Tipo de sangre",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    listOf(
                        listOf("A+", "A-", "B+", "B-"),
                        listOf("AB+", "AB-", "O+", "O-")
                    ).forEach { fila ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(
                                        this,
                                        transformationSpec
                                    ),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                fila.forEach { opcion ->
                                    OpcionPequena(
                                        texto = opcion,
                                        seleccionada = tipoSangre == opcion
                                    ) {
                                        tipoSangre = opcion
                                    }
                                }
                            }
                        }
                    }

                    item {
                        CampoWear(
                            valor = metaPasos,
                            placeholder = "Meta de pasos",
                            onCambio = {
                                metaPasos = it.filter { caracter ->
                                    caracter.isDigit()
                                }.take(6)
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = objetivo,
                            placeholder = "Objetivo nutricional",
                            onCambio = {
                                objetivo = it
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = telefono,
                            placeholder = "Teléfono",
                            onCambio = {
                                telefono = it.filter { caracter ->
                                    caracter.isDigit()
                                }.take(13)
                            }
                        )
                    }

                    item {
                        CampoWear(
                            valor = correo,
                            placeholder = "Correo",
                            onCambio = {
                                correo = it
                            }
                        )
                    }

                    item {
                        Text(
                            text = "Alergias",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    alergias.forEach { alergia ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .transformedHeight(
                                        this,
                                        transformationSpec
                                    )
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF2A1A1A))
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = alergia,
                                    color = Color(0xFFFFB3AE),
                                    fontSize = 9.sp,
                                    modifier = Modifier.width(120.dp)
                                )

                                Text(
                                    text = "×",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    modifier = Modifier
                                        .clickable {
                                            onEliminarAlergia(alergia)
                                        }
                                        .padding(4.dp)
                                )
                            }
                        }
                    }

                    item {
                        CampoWear(
                            valor = nuevaAlergia,
                            placeholder = "Nueva alergia",
                            onCambio = {
                                nuevaAlergia = it
                            }
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                if (nuevaAlergia.isBlank()) {
                                    Toast.makeText(
                                        contexto,
                                        "Escribe una alergia",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    onAgregarAlergia(nuevaAlergia)
                                    nuevaAlergia = ""
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            )
                        ) {
                            Text("Agregar alergia")
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                onGuardarPerfil(
                                    PerfilPaciente(
                                        nombre = nombre.ifBlank {
                                            "Paciente"
                                        }.trim(),
                                        edad = edad.ifBlank {
                                            perfil.edad
                                        },
                                        pesoKg = peso.ifBlank {
                                            perfil.pesoKg
                                        },
                                        estaturaCm = estatura.ifBlank {
                                            perfil.estaturaCm
                                        },
                                        tipoSangre = tipoSangre,
                                        metaPasos = metaPasos.ifBlank {
                                            perfil.metaPasos
                                        },
                                        objetivo = objetivo.ifBlank {
                                            "Mejorar hábitos alimenticios"
                                        }.trim(),
                                        telefono = telefono.trim(),
                                        correo = correo.trim()
                                    )
                                )

                                editando = false

                                Toast.makeText(
                                    contexto,
                                    "Perfil actualizado",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF147D69),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Guardar perfil")
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                editando = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(
                                transformationSpec
                            )
                        ) {
                            Text("Cancelar edición")
                        }
                    }
                }

                item {
                    Button(
                        onClick = onSalir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .transformedHeight(this, transformationSpec),
                        transformation = SurfaceTransformation(
                            transformationSpec
                        )
                    ) {
                        Text("Regresar")
                    }
                }
            }

            EncabezadoFijo(
                titulo = "Mi perfil",
                subtitulo = if (editando) {
                    "Editando datos"
                } else {
                    "Datos del paciente"
                }
            )
        }
    }
}

fun inicialesPerfil(nombre: String): String {
    val partes = nombre
        .trim()
        .split("\\s+".toRegex())
        .filter { it.isNotBlank() }

    return partes
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifBlank {
            "P"
        }
}

@Composable
fun DatoPerfil(
    etiqueta: String,
    valor: String,
    colorValor: Color = Color.LightGray
) {
    Text(
        text = etiqueta,
        color = Color(0xFF7FE5D2),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = valor,
        color = colorValor,
        fontSize = 9.sp
    )

    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
fun EncabezadoFijo(
    titulo: String,
    subtitulo: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C1D1A))
            .padding(top = 13.dp, bottom = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subtitulo,
            color = Color(0xFF73968F),
            fontSize = 8.sp
        )
    }
}

@Composable
fun TarjetaTransformada(
    modifier: Modifier,
    color: Color,
    contenido: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(color)
    ) {
        contenido()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppDisenosFitnessScreen(onSalir: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 4 })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pagina ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (pagina) {
                    0 -> DisenoPasos()
                    1 -> DisenoSemana()
                    2 -> DisenoYoga()
                    else -> DisenoRelojAnalogo()
                }
            }
        }

        BotonTextoInferior(
            texto = "Regresar",
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onSalir
        )
    }
}

@Composable
fun DisenoPasos() {
    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawArc(
                color = Color(0xFF1E2429),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = 9.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFFD4FF3B),
                startAngle = -90f,
                sweepAngle = 295f,
                useCenter = false,
                style = Stroke(
                    width = 9.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PASOS",
                color = Color(0xFFD4FF3B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "6562",
                color = Color.White,
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "META 8000",
                color = Color.Gray,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun DisenoSemana() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Actividad semanal",
            color = Color(0xFFD4FF3B),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("🏃", "🧘", "🚴").forEach { icono ->
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD4FF3B)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icono,
                        fontSize = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "1 entrenamiento esta semana",
            color = Color.LightGray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun DisenoYoga() {
    var iniciado by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (iniciado) "Sesión activa" else "Power Yoga",
            color = Color(0xFFD4FF3B),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(45.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    if (iniciado) {
                        Color(0xFFFF5B3D)
                    } else {
                        Color(0xFFD4FF3B)
                    }
                )
                .clickable {
                    iniciado = !iniciado
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (iniciado) "Detener" else "Iniciar",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Última sesión: 45 min",
            color = Color.Gray,
            fontSize = 10.sp
        )
    }
}

@Composable
fun DisenoRelojAnalogo() {
    val zonaMexico = remember { ZoneId.of("America/Mexico_City") }
    var ahora by remember { mutableStateOf(ZonedDateTime.now(zonaMexico)) }

    LaunchedEffect(Unit) {
        while (true) {
            ahora = ZonedDateTime.now(zonaMexico)
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier.size(165.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centro = center
            val radio = size.minDimension / 2f

            for (i in 0 until 12) {
                val angulo = Math.toRadians((i * 30.0) - 90.0)

                val inicio = Offset(
                    x = centro.x + (radio - 12.dp.toPx()) * cos(angulo).toFloat(),
                    y = centro.y + (radio - 12.dp.toPx()) * sin(angulo).toFloat()
                )

                val fin = Offset(
                    x = centro.x + (radio - 4.dp.toPx()) * cos(angulo).toFloat(),
                    y = centro.y + (radio - 4.dp.toPx()) * sin(angulo).toFloat()
                )

                drawLine(
                    color = Color.White.copy(alpha = 0.55f),
                    start = inicio,
                    end = fin,
                    strokeWidth = 2f
                )
            }

            val minuto = ahora.minute
            val hora = ahora.hour % 12

            val anguloMinuto = Math.toRadians((minuto * 6.0) - 90.0)
            val anguloHora = Math.toRadians(
                ((hora * 30.0) + minuto * 0.5) - 90.0
            )

            drawLine(
                color = Color.White,
                start = centro,
                end = Offset(
                    x = centro.x + radio * 0.68f * cos(anguloMinuto).toFloat(),
                    y = centro.y + radio * 0.68f * sin(anguloMinuto).toFloat()
                ),
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawLine(
                color = Color(0xFFD4FF3B),
                start = centro,
                end = Offset(
                    x = centro.x + radio * 0.45f * cos(anguloHora).toFloat(),
                    y = centro.y + radio * 0.45f * sin(anguloHora).toFloat()
                ),
                strokeWidth = 7.dp.toPx(),
                cap = StrokeCap.Round
            )

            drawCircle(
                color = Color(0xFFD4FF3B),
                radius = 4.dp.toPx(),
                center = centro
            )
        }
    }
}

@Composable
fun AppCalculadoraScreen(onSalir: () -> Unit) {
    var expresion by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 25.dp, vertical = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "‹",
                    color = Color(0xFF00E676),
                    fontSize = 24.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSalir() }
                        .padding(horizontal = 7.dp)
                )

                Text(
                    text = expresion.ifEmpty { "0" },
                    color = Color.White,
                    fontSize = if (expresion.length > 10) 13.sp else 18.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.width(110.dp)
                )

                Text(
                    text = "⌫",
                    color = Color(0xFF00E676),
                    fontSize = 15.sp,
                    modifier = Modifier
                        .clickable {
                            if (expresion.isNotEmpty()) {
                                expresion = expresion.dropLast(1)
                            }
                        }
                        .padding(5.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val filas = listOf(
                listOf("C", "½", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("±", "0", ".", "=")
            )

            filas.forEach { fila ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    fila.forEach { boton ->
                        val color = when (boton) {
                            "C" -> Color(0xFFFF5B3D)
                            "½", "%", "÷", "×", "−", "+", "=" -> {
                                Color(0xFF20E35A)
                            }

                            else -> Color.White
                        }

                        BotonCalculadora(
                            texto = boton,
                            colorTexto = color
                        ) {
                            expresion = procesarCalculadora(
                                expresionActual = expresion,
                                boton = boton
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BotonCalculadora(
    texto: String,
    colorTexto: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(
                if (texto == "=") {
                    Color(0xFF146B31)
                } else {
                    Color(0xFF151719)
                }
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = colorTexto,
            fontSize = if (texto == "½") 12.sp else 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun procesarCalculadora(
    expresionActual: String,
    boton: String
): String {
    return when (boton) {
        "C" -> ""

        "½" -> {
            val numero = expresionActual.toDoubleOrNull()

            if (numero != null) {
                limpiarResultado(numero / 2.0)
            } else {
                expresionActual
            }
        }

        "%" -> {
            val numero = expresionActual.toDoubleOrNull()

            if (numero != null) {
                limpiarResultado(numero / 100.0)
            } else {
                expresionActual
            }
        }

        "±" -> {
            when {
                expresionActual.isBlank() -> ""
                expresionActual.startsWith("-") -> expresionActual.drop(1)
                expresionActual.toDoubleOrNull() != null -> "-$expresionActual"
                else -> expresionActual
            }
        }

        "=" -> evaluarExpresionMatematica(expresionActual)

        "÷", "×", "−", "+" -> {
            if (
                expresionActual.isNotEmpty() &&
                expresionActual.last().toString() !in listOf("÷", "×", "−", "+")
            ) {
                expresionActual + boton
            } else {
                expresionActual
            }
        }

        "." -> {
            val ultimaParte = expresionActual
                .split("÷", "×", "−", "+")
                .lastOrNull()
                .orEmpty()

            if (!ultimaParte.contains(".")) {
                expresionActual + "."
            } else {
                expresionActual
            }
        }

        else -> {
            if (expresionActual == "Error") {
                boton
            } else {
                expresionActual + boton
            }
        }
    }
}

fun limpiarResultado(valor: Double): String {
    if (valor.isInfinite() || valor.isNaN()) {
        return "Error"
    }

    return BigDecimal(valor)
        .setScale(4, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}

fun evaluarExpresionMatematica(expresion: String): String {
    return try {
        val limpia = expresion
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")

        val indiceOperador = limpia
            .drop(1)
            .indexOfFirst {
                it in charArrayOf('+', '-', '*', '/')
            }
            .let {
                if (it >= 0) it + 1 else -1
            }

        if (
            indiceOperador <= 0 ||
            indiceOperador >= limpia.lastIndex
        ) {
            return expresion
        }

        val operador = limpia[indiceOperador]
        val numero1 = limpia.substring(0, indiceOperador).toDouble()
        val numero2 = limpia.substring(indiceOperador + 1).toDouble()

        val resultado = when (operador) {
            '+' -> numero1 + numero2
            '-' -> numero1 - numero2
            '*' -> numero1 * numero2
            '/' -> {
                if (numero2 != 0.0) {
                    numero1 / numero2
                } else {
                    Double.NaN
                }
            }

            else -> Double.NaN
        }

        limpiarResultado(resultado)
    } catch (_: Exception) {
        "Error"
    }
}

@Composable
fun AppMusicaScreen(onSalir: () -> Unit) {
    val contexto = LocalContext.current

    val canciones = remember {
        listOf(
            CancionSimulada(
                idRaw = contexto.resources.getIdentifier(
                    "prueba1",
                    "raw",
                    contexto.packageName
                ),
                titulo = "Chef Table Radio",
                artista = "Eli Kulp"
            ),
            CancionSimulada(
                idRaw = contexto.resources.getIdentifier(
                    "prueba2",
                    "raw",
                    contexto.packageName
                ),
                titulo = "Inspirational Track",
                artista = "Beat Maker"
            ),
            CancionSimulada(
                idRaw = contexto.resources.getIdentifier(
                    "prueba3",
                    "raw",
                    contexto.packageName
                ),
                titulo = "Workout Groove",
                artista = "Fitness Studio"
            )
        )
    }

    var indiceActual by remember { mutableIntStateOf(0) }
    var reproduciendo by remember { mutableStateOf(false) }
    var favorito by remember { mutableStateOf(false) }
    var progreso by remember { mutableFloatStateOf(0f) }

    val reproductor = remember { MediaPlayer() }

    fun cargarCancion(reproducirAlCargar: Boolean) {
        try {
            reproductor.reset()

            val cancion = canciones[indiceActual]

            if (cancion.idRaw == 0) {
                reproduciendo = false
                progreso = 0f

                Toast.makeText(
                    contexto,
                    "Agrega prueba1.mp3, prueba2.mp3 y prueba3.mp3 en res/raw",
                    Toast.LENGTH_LONG
                ).show()

                return
            }

            val descriptor = contexto.resources.openRawResourceFd(cancion.idRaw)

            reproductor.setDataSource(
                descriptor.fileDescriptor,
                descriptor.startOffset,
                descriptor.length
            )

            descriptor.close()
            reproductor.prepare()

            reproductor.setOnCompletionListener {
                progreso = 0f
                reproduciendo = false
            }

            if (reproducirAlCargar) {
                reproductor.start()
                reproduciendo = true
            }
        } catch (_: Exception) {
            reproduciendo = false
            progreso = 0f
        }
    }

    LaunchedEffect(indiceActual) {
        cargarCancion(reproducirAlCargar = reproduciendo)
    }

    LaunchedEffect(reproduciendo, indiceActual) {
        while (reproduciendo) {
            try {
                if (
                    reproductor.isPlaying &&
                    reproductor.duration > 0
                ) {
                    progreso =
                        reproductor.currentPosition.toFloat() /
                                reproductor.duration.toFloat()
                }
            } catch (_: Exception) {
                progreso = 0f
            }

            delay(400L)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                if (reproductor.isPlaying) {
                    reproductor.stop()
                }
            } catch (_: Exception) {
            }

            reproductor.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF321044),
                        Color(0xFF110517),
                        Color.Black
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MÚSICA",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = canciones[indiceActual].titulo,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = canciones[indiceActual].artista,
                color = Color.LightGray,
                fontSize = 10.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⏮",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            indiceActual =
                                if (indiceActual > 0) {
                                    indiceActual - 1
                                } else {
                                    canciones.lastIndex
                                }
                        }
                        .padding(8.dp)
                )

                Box(
                    modifier = Modifier.size(66.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawArc(
                            color = Color.White.copy(alpha = 0.15f),
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        drawArc(
                            color = Color(0xFFE040FB),
                            startAngle = -90f,
                            sweepAngle = 360f * progreso,
                            useCenter = false,
                            style = Stroke(
                                width = 3.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(53.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable {
                                try {
                                    if (reproduciendo) {
                                        reproductor.pause()
                                        reproduciendo = false
                                    } else {
                                        if (canciones[indiceActual].idRaw == 0) {
                                            Toast.makeText(
                                                contexto,
                                                "Agrega archivos MP3 en app/src/main/res/raw",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            reproductor.start()
                                            reproduciendo = true
                                        }
                                    }
                                } catch (_: Exception) {
                                    cargarCancion(true)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (reproduciendo) "Ⅱ" else "▶",
                            color = Color.White,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "⏭",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            indiceActual =
                                if (indiceActual < canciones.lastIndex) {
                                    indiceActual + 1
                                } else {
                                    0
                                }
                        }
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (favorito) "♥" else "♡",
                    color = if (favorito) {
                        Color(0xFFFF5B7A)
                    } else {
                        Color.White
                    },
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable {
                            favorito = !favorito
                        }
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = "🔊",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clickable {
                            Toast.makeText(
                                contexto,
                                "Volumen controlado por el emulador",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .padding(8.dp)
                )
            }

            BotonTextoInferior(
                texto = "Regresar",
                onClick = onSalir
            )
        }
    }
}

@Composable
fun PantallaLlamada(onSalir: () -> Unit) {
    val contexto = LocalContext.current

    var llamando by remember { mutableStateOf(false) }
    var segundos by remember { mutableIntStateOf(0) }

    LaunchedEffect(llamando) {
        while (llamando) {
            delay(1000L)
            segundos++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF062611))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "📞",
                fontSize = 32.sp
            )

            Text(
                text = if (llamando) {
                    "Llamada activa"
                } else {
                    "Llamada"
                },
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (llamando) {
                    "%02d:%02d".format(
                        segundos / 60,
                        segundos % 60
                    )
                } else {
                    "Contacto médico"
                },
                color = Color.LightGray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            BotonCapsula(
                texto = if (llamando) {
                    "Finalizar"
                } else {
                    "Simular llamada"
                },
                color = if (llamando) {
                    Color(0xFFD32F2F)
                } else {
                    Color(0xFF2E7D32)
                }
            ) {
                llamando = !llamando

                if (!llamando) {
                    segundos = 0
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Abrir marcador",
                color = Color(0xFF7ED9FF),
                fontSize = 10.sp,
                modifier = Modifier
                    .clickable {
                        abrirAppExterna(
                            contexto,
                            "tel:"
                        )
                    }
                    .padding(5.dp)
            )

            BotonTextoInferior(
                texto = "Regresar",
                onClick = onSalir
            )
        }
    }
}

@Composable
fun PantallaCamara(onSalir: () -> Unit) {
    val contexto = LocalContext.current
    var fotoTomada by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MODO FOTO",
                color = Color(0xFFFFEB3B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .height(85.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (fotoTomada) {
                            Color(0xFF1B5E20)
                        } else {
                            Color(0xFF252525)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (fotoTomada) {
                        "✓ Foto simulada"
                    } else {
                        "Visor activo"
                    },
                    color = Color.LightGray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(
                        width = 4.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
                    .clickable {
                        fotoTomada = !fotoTomada
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Text(
                text = "Abrir cámara del sistema",
                color = Color(0xFF7ED9FF),
                fontSize = 9.sp,
                modifier = Modifier
                    .clickable {
                        abrirAppExterna(
                            contexto,
                            "camera:"
                        )
                    }
                    .padding(7.dp)
            )
        }

        BotonTextoInferior(
            texto = "Regresar",
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = onSalir
        )
    }
}

@Composable
fun PantallaAppExterna(
    titulo: String,
    icono: String,
    descripcion: String,
    uri: String,
    onSalir: () -> Unit
) {
    val contexto = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1218))
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF213246)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icono,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = titulo,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = descripcion,
                color = Color.Gray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotonCapsula(
                texto = "Abrir aplicación",
                color = Color(0xFF1565C0)
            ) {
                abrirAppExterna(
                    contexto,
                    uri
                )
            }

            BotonTextoInferior(
                texto = "Regresar",
                onClick = onSalir
            )
        }
    }
}

@Composable
fun BotonCapsula(
    texto: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 17.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BotonTextoInferior(
    texto: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        text = texto,
        color = Color.Gray,
        fontSize = 9.sp,
        modifier = modifier
            .padding(bottom = 4.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 11.dp, vertical = 5.dp)
    )
}

fun prepararWhatsAppSimulado(
    context: Context,
    destinatario: String,
    telefono: String,
    mensaje: String
): MensajeWhatsAppSimulado? {
    val numero = normalizarNumeroWhatsApp(telefono)

    if (!numeroWhatsAppValido(numero)) {
        Toast.makeText(
            context,
            "El número de WhatsApp no es válido",
            Toast.LENGTH_LONG
        ).show()

        return null
    }

    val enlace = "https://wa.me/$numero?text=${Uri.encode(mensaje)}"

    Log.i(
        "NutriWatchWA",
        enlace
    )

    copiarEnlaceAlPortapapeles(
        context = context,
        enlace = enlace
    )

    Toast.makeText(
        context,
        "Enlace copiado. Pégalo en Safari o Chrome de tu Mac.",
        Toast.LENGTH_LONG
    ).show()

    return MensajeWhatsAppSimulado(
        destinatario = destinatario,
        telefono = telefono,
        mensaje = mensaje,
        enlace = enlace
    )
}

@Composable
fun PantallaWhatsAppSimulado(
    datos: MensajeWhatsAppSimulado,
    onVolver: () -> Unit
) {
    val contexto = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF071A13))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF123D30))
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(31.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "W",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.width(125.dp)
                ) {
                    Text(
                        text = datos.destinatario,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = datos.telefono,
                        color = Color(0xFFB5D6CB),
                        fontSize = 8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 4.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .background(Color(0xFFDCF8C6))
                    .padding(12.dp)
            ) {
                Text(
                    text = datos.mensaje,
                    color = Color(0xFF13251E),
                    fontSize = 8.sp,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Enviado  ✓✓",
                        color = Color(0xFF3A7F8F),
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(9.dp))

            Text(
                text = "SIMULACIÓN",
                color = Color(0xFFFFD180),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "El enlace real de WhatsApp quedó copiado.",
                color = Color.LightGray,
                fontSize = 8.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF25D366))
                    .clickable {
                        copiarEnlaceAlPortapapeles(
                            context = contexto,
                            enlace = datos.enlace
                        )

                        Toast.makeText(
                            contexto,
                            "Enlace copiado nuevamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .padding(horizontal = 15.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Copiar enlace otra vez",
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Pégalo con ⌘V en Safari o Chrome.",
                color = Color(0xFF9FC6BA),
                fontSize = 7.sp,
                textAlign = TextAlign.Center
            )

            BotonTextoInferior(
                texto = "Volver",
                onClick = onVolver
            )
        }
    }
}

fun abrirWhatsApp(
    context: Context,
    telefono: String,
    mensaje: String
) {
    prepararWhatsAppSimulado(
        context = context,
        destinatario = "Contacto",
        telefono = telefono,
        mensaje = mensaje
    )
}

fun copiarEnlaceAlPortapapeles(
    context: Context,
    enlace: String
) {
    try {
        val portapapeles = context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

        portapapeles.setPrimaryClip(
            ClipData.newPlainText(
                "Alerta NutriWatch",
                enlace
            )
        )
    } catch (_: Exception) {
        // La aplicación continúa aunque el emulador no permita copiar.
    }
}

fun normalizarNumeroWhatsApp(numero: String): String {
    val limpio = numero.filter { it.isDigit() }

    return when {
        limpio.startsWith("52") -> limpio
        limpio.length == 10 -> "52$limpio"
        else -> limpio
    }
}

fun normalizarNumeroTelefono(numero: String): String {
    return numero.filter { it.isDigit() || it == '+' }
}

fun abrirMarcador911(context: Context) {
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:911")
            )
        )
    } catch (_: Exception) {
        Toast.makeText(
            context,
            "Simulación de llamada: marcando 911",
            Toast.LENGTH_LONG
        ).show()
    }
}

fun abrirAppExterna(
    context: Context,
    uriString: String
) {
    try {
        val intent = when {
            uriString.startsWith("tel:") -> {
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse(uriString)
                )
            }

            uriString.startsWith("camera:") -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            }

            else -> {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(uriString)
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
        }

        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(
            context,
            "El emulador no tiene una aplicación compatible",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun DefaultPreview() {
    WearApp()
}
