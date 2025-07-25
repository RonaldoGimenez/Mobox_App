package com.example.mobox_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mobox_app.ui.theme.Skeleton

@Composable
fun HomeTopBar(
    userName: String,
    userInitials: String,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar con iniciales
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitials,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight =
                        FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
            // Saludo
            Column {
                Text(
                    text = "Bienvenido",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Skeleton
                )
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight =
                        FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        // Botón de Cerrar Sesión
        IconButton(onClick = onLogoutClick) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}