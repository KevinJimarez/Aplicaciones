package com.example.p1_lazycolumn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p1_lazycolumn.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Contenedor()
        }
    }
}

data class Botones(val id: Int, val rotulo: String)

@Composable
fun ListaImagenesHorizontal() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            item {
                Image(
                    painter = painterResource(id = R.drawable.imagen1),
                    contentDescription = "",
                    modifier = Modifier.size(120.dp)
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.imagen2),
                    contentDescription = "",
                    modifier = Modifier.size(120.dp)
                )
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.imagen1),
                    contentDescription = "",
                    modifier = Modifier.size(120.dp)
                )
            }
        }
    }
}

@Composable
fun ListaBotonesHorizontal(modifier: Modifier = Modifier) {
    val listaB = List(size = 10) { Botones(id = it, rotulo = "Botón $it") }
    val botonSeleccionado = remember { mutableStateOf<Botones?>(value = null) }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(all = 15.dp)
    ) {
        item {
            Text(
                text = "Lista de Botones Verticales",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        items(listaB) { button ->
            Button(
                onClick = { botonSeleccionado.value = button },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (botonSeleccionado.value == button)
                        Color.Gray else Color.Blue
                )
            ) {
                Text(text = button.rotulo)
            }
        }

        item {
            botonSeleccionado.value?.let { selected ->
                Text(
                    text = "Vertical seleccionado: ${selected.rotulo}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun Contenedor() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ListaBotonesHorizontal(modifier = Modifier.weight(1f))

        ListaImagenesHorizontal()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Contenedor()
}