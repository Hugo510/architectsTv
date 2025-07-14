package com.example.app_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shared_domain.model.*

@Composable
fun ProjectCard(
    project: Project,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            // Icono profesional del proyecto
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(getStatusColor(project.status).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getStatusIcon(project.status),
                    contentDescription = "Proyecto",
                    tint = getStatusColor(project.status),
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Información del proyecto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Nombre y estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Chip de estado
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = getStatusColor(project.status)
                    ) {
                        Text(
                            text = getStatusDisplayName(project.status),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Descripción
                Text(
                    text = project.description ?: "Sin descripción",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "Última actualización por: ${project.projectManager.name}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                // Barra de progreso
                if (project.progressPercentage > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { project.progress.toFloat() },
                            modifier = Modifier.weight(1f),
                            color = when (project.status) {
                                ProjectStatus.DELIVERY -> Color(0xFF4CAF50)
                                ProjectStatus.CONSTRUCTION -> Color(0xFF2196F3)
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Progreso - ${project.progressPercentage}%",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        text = "Progreso - ${project.progressPercentage}%",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

// Funciones utilitarias compartidas
fun getStatusDisplayName(status: ProjectStatus): String {
    return when (status) {
        ProjectStatus.DESIGN -> "Diseño"
        ProjectStatus.PERMITS_REVIEW -> "Revisión de Permisos"
        ProjectStatus.CONSTRUCTION -> "Construcción"
        ProjectStatus.DELIVERY -> "Entrega"
    }
}

fun getStatusColor(status: ProjectStatus): Color {
    return when (status) {
        ProjectStatus.DESIGN -> Color(0xFF9CA3FF)
        ProjectStatus.PERMITS_REVIEW -> Color(0xFFFBB6CE)
        ProjectStatus.CONSTRUCTION -> Color(0xFF68D391)
        ProjectStatus.DELIVERY -> Color(0xFFA0AEC0)
    }
}

fun getStatusIcon(status: ProjectStatus): ImageVector {
    return when (status) {
        ProjectStatus.DESIGN -> Icons.Default.Architecture
        ProjectStatus.PERMITS_REVIEW -> Icons.Default.Assignment
        ProjectStatus.CONSTRUCTION -> Icons.Default.Construction
        ProjectStatus.DELIVERY -> Icons.Default.DeliveryDining
    }
}
    }
}
