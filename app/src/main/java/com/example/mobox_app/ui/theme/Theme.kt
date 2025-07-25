package com.example.mobox_app.ui.theme // o el nombre de tu paquete

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.example.mobox_app.ui.theme.*

// 1. Se define la paleta de colores para el tema oscuro.
//    Usa las variables que creamos en el archivo Color.kt.
private val DarkColorScheme = darkColorScheme(
    primary = GradientEnd,        // Color de respaldo para el rol primario, ya que el principal es un gradiente.
    background = AppBackground,   // Fondo principal de la app (#292931)
    surface = InputBackground,    // Fondo para componentes como TextFields (#494950)
    onPrimary = TextAndIconWhite, // Texto sobre el color primario
    onBackground = TextAndIconWhite, // Texto principal sobre el fondo
    onSurface = TextAndIconWhite,    // Texto sobre las superficies (ej. dentro de un TextField)
    error = ErrorText             // Color para los textos de error (#D92D20)
)

// 2. Se define el Brush reutilizable para el gradiente principal.
fun primaryGradientBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(GradientStart, GradientEnd)
    )
}

// 3. Se crea el tema principal de la aplicación.
@Composable
fun MoboxTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography, // Tu tipografía desde Type.kt
        content = content
    )
}