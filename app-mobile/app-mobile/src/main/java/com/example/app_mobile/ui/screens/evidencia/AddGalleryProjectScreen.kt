package com.example.app_mobile.ui.screens.evidencia

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGalleryProjectScreen(
    viewModel: EvidenciaViewModel, // Recibir ViewModel como parámetro
    onNavigateBack: () -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    var projectDescription by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf("Contemporáneo") }
    var projectLocation by remember { mutableStateOf("") }
    var architectName by remember { mutableStateOf("") }
    var projectArea by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }
    var isVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val hapticFeedback = LocalHapticFeedback.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Focus requesters para navegación entre campos
    val nameFocusRequester = remember { FocusRequester() }
    val descriptionFocusRequester = remember { FocusRequester() }
    val locationFocusRequester = remember { FocusRequester() }
    val architectFocusRequester = remember { FocusRequester() }
    val areaFocusRequester = remember { FocusRequester() }

    val styleOptions = listOf(
        "Contemporáneo", "Minimalista", "Industrial",
        "Moderno", "Clásico", "Rústico", "Colonial"
    )

    // Animación de entrada
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Validación en tiempo real
    val isFormValid by remember {
        derivedStateOf {
            projectName.isNotBlank() &&
                    projectDescription.isNotBlank() &&
                    projectLocation.isNotBlank() &&
                    architectName.isNotBlank() &&
                    projectArea.isNotBlank() &&
                    projectArea.toDoubleOrNull() != null
        }
    }

    // Manejar éxito en la creación
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            if (message.contains("exitosamente", ignoreCase = true)) {
                showSuccessDialog = true
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar Proyecto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateBack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Indicador de progreso del formulario
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${(if (isFormValid) 100 else currentStep * 20)}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    })
                }
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    // Header con animación mejorada
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { -it / 2 },
                                animationSpec = tween(800, delayMillis = 200)
                            ) + scaleIn(
                                initialScale = 0.8f,
                                animationSpec = tween(800, delayMillis = 200)
                            )
                        ) {
                            EnhancedAddProjectHeader()
                        }
                    }

                    // Formulario principal con animaciones escalonadas
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { -it },
                                animationSpec = tween(800, delayMillis = 400)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 400))
                        ) {
                            EnhancedProjectBasicInfoForm(
                                projectName = projectName,
                                onProjectNameChange = {
                                    projectName = it
                                    currentStep = maxOf(currentStep, 1)
                                },
                                projectDescription = projectDescription,
                                onProjectDescriptionChange = {
                                    projectDescription = it
                                    currentStep = maxOf(currentStep, 2)
                                },
                                projectLocation = projectLocation,
                                onProjectLocationChange = {
                                    projectLocation = it
                                    currentStep = maxOf(currentStep, 3)
                                },
                                nameFocusRequester = nameFocusRequester,
                                descriptionFocusRequester = descriptionFocusRequester,
                                locationFocusRequester = locationFocusRequester,
                                architectFocusRequester = architectFocusRequester
                            )
                        }
                    }

                    // Selector de estilo mejorado
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = { it },
                                animationSpec = tween(800, delayMillis = 600)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 600))
                        ) {
                            EnhancedStyleSelectionSection(
                                selectedStyle = selectedStyle,
                                onStyleSelected = {
                                    selectedStyle = it
                                    currentStep = maxOf(currentStep, 4)
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                },
                                styleOptions = styleOptions
                            )
                        }
                    }

                    // Información del arquitecto y área
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(800, delayMillis = 800)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 800))
                        ) {
                            EnhancedProjectDetailsForm(
                                architectName = architectName,
                                onArchitectNameChange = {
                                    architectName = it
                                    currentStep = maxOf(currentStep, 5)
                                },
                                projectArea = projectArea,
                                onProjectAreaChange = {
                                    projectArea = it
                                    currentStep = maxOf(currentStep, 6)
                                },
                                architectFocusRequester = architectFocusRequester,
                                areaFocusRequester = areaFocusRequester
                            )
                        }
                    }

                    // Vista previa animada
                    item {
                        AnimatedVisibility(
                            visible = isVisible && projectName.isNotBlank(),
                            enter = scaleIn(
                                animationSpec = tween(600, delayMillis = 1000)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 1000))
                        ) {
                            EnhancedProjectPreview(
                                name = projectName.ifEmpty { "Nombre del Proyecto" },
                                description = projectDescription.ifEmpty { "Descripción del proyecto..." },
                                style = selectedStyle,
                                location = projectLocation.ifEmpty { "Ubicación" },
                                architect = architectName.ifEmpty { "Nombre del Arquitecto" },
                                area = projectArea.ifEmpty { "0" }
                            )
                        }
                    }

                    // Botones de acción mejorados
                    item {
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(800, delayMillis = 1200)
                            ) + fadeIn(animationSpec = tween(600, delayMillis = 1200))
                        ) {
                            EnhancedActionButtons(
                                onSaveAsDraft = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    // TODO: Guardar como borrador
                                },
                                onCreateProject = {
                                    if (isFormValid) {
                                        isCreating = true
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        keyboardController?.hide()
                                        focusManager.clearFocus()

                                        viewModel.createGalleryProject(
                                            name = projectName,
                                            description = projectDescription,
                                            style = selectedStyle,
                                            location = projectLocation,
                                            architect = architectName,
                                            area = projectArea
                                        )
                                    }
                                },
                                isEnabled = isFormValid,
                                isLoading = isCreating || uiState.isLoading
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }

            // Overlay de carga mejorado
            AnimatedVisibility(
                visible = isCreating || uiState.isLoading,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    EnhancedLoadingCard()
                }
            }
        }

        // Dialog de éxito mejorado
        if (showSuccessDialog) {
            EnhancedSuccessDialog(
                onDismiss = {
                    showSuccessDialog = false
                    isCreating = false
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigateBack()
                }
            )
        }
    }
}

@Composable
private fun EnhancedAddProjectHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.05f)
                        )
                    )
                )
                .padding(28.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono animado
                var iconScale by remember { mutableStateOf(0.8f) }
                val animatedScale by animateFloatAsState(
                    targetValue = iconScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "icon_scale"
                )

                LaunchedEffect(Unit) {
                    delay(500)
                    iconScale = 1f
                }

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(animatedScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddHome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Nuevo Proyecto",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "Agrega un nuevo proyecto completado a tu galería profesional",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp),
                    lineHeight = 24.sp,
                    letterSpacing = 0.1.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedProjectBasicInfoForm(
    projectName: String,
    onProjectNameChange: (String) -> Unit,
    projectDescription: String,
    onProjectDescriptionChange: (String) -> Unit,
    projectLocation: String,
    onProjectLocationChange: (String) -> Unit,
    nameFocusRequester: FocusRequester,
    descriptionFocusRequester: FocusRequester,
    locationFocusRequester: FocusRequester,
    architectFocusRequester: FocusRequester
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Información Básica",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Nombre del proyecto con indicador de progreso
            OutlinedTextField(
                value = projectName,
                onValueChange = onProjectNameChange,
                label = { Text("Nombre del Proyecto *") },
                placeholder = { Text("Ej. Casa Moderna Los Pinos") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocusRequester),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(Icons.Default.Home, contentDescription = null)
                },
                trailingIcon = {
                    if (projectName.isNotBlank()) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { descriptionFocusRequester.requestFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (projectName.isNotBlank())
                        Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Descripción con contador de caracteres
            OutlinedTextField(
                value = projectDescription,
                onValueChange = onProjectDescriptionChange,
                label = { Text("Descripción *") },
                placeholder = { Text("Describe las características principales del proyecto...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(descriptionFocusRequester),
                shape = RoundedCornerShape(16.dp),
                minLines = 3,
                maxLines = 5,
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                supportingText = {
                    Text(
                        text = "${projectDescription.length}/500 caracteres",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { locationFocusRequester.requestFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (projectDescription.isNotBlank())
                        Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ubicación con sugerencias
            OutlinedTextField(
                value = projectLocation,
                onValueChange = onProjectLocationChange,
                label = { Text("Ubicación *") },
                placeholder = { Text("Ej. Durango, México") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(locationFocusRequester),
                shape = RoundedCornerShape(16.dp),
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                trailingIcon = {
                    if (projectLocation.isNotBlank()) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { architectFocusRequester.requestFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (projectLocation.isNotBlank())
                        Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}

@Composable
private fun EnhancedStyleSelectionSection(
    selectedStyle: String,
    onStyleSelected: (String) -> Unit,
    styleOptions: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Estilo Arquitectónico",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(styleOptions) { style ->
                    EnhancedStyleChip(
                        style = style,
                        isSelected = selectedStyle == style,
                        onSelected = { onStyleSelected(style) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedStyleChip(
    style: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isSelected -> 1.05f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chip_scale"
    )

    val backgroundColor = when (style) {
        "Contemporáneo" -> Color(0xFF667EEA)
        "Minimalista" -> Color(0xFF9FACE6)
        "Industrial" -> Color(0xFF74B9FF)
        "Moderno" -> Color(0xFF81ECEC)
        "Clásico" -> Color(0xFFFFBF93)
        "Rústico" -> Color(0xFFD1A3FF)
        "Colonial" -> Color(0xFFFF9F9F)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        onClick = {
            isPressed = true
            onSelected()
        },
        modifier = Modifier
            .scale(scale)
            .width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) backgroundColor else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, backgroundColor.copy(alpha = 0.8f))
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 4.dp
        )
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                delay(100)
                isPressed = false
            }
        }

        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else backgroundColor.copy(alpha = 0.2f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (style) {
                        "Contemporáneo" -> Icons.Default.Home
                        "Minimalista" -> Icons.Default.Business
                        "Industrial" -> Icons.Default.Factory
                        "Moderno" -> Icons.Default.Apartment
                        "Clásico" -> Icons.Default.AccountBalance
                        "Rústico" -> Icons.Default.Cottage
                        "Colonial" -> Icons.Default.AccountBalance // Cambiado de Temple a AccountBalance
                        else -> Icons.Default.Construction
                    },
                    contentDescription = style,
                    tint = if (isSelected) Color.White else backgroundColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = style,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                letterSpacing = 0.2.sp
            )

            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedProjectDetailsForm(
    architectName: String,
    onArchitectNameChange: (String) -> Unit,
    projectArea: String,
    onProjectAreaChange: (String) -> Unit,
    architectFocusRequester: FocusRequester,
    areaFocusRequester: FocusRequester
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Detalles del Proyecto",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Arquitecto
                OutlinedTextField(
                    value = architectName,
                    onValueChange = onArchitectNameChange,
                    label = { Text("Arquitecto *") },
                    placeholder = { Text("Nombre del arquitecto") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (architectName.isNotBlank())
                            Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                    )
                )

                // Área
                OutlinedTextField(
                    value = projectArea,
                    onValueChange = onProjectAreaChange,
                    label = { Text("Área (m²) *") },
                    placeholder = { Text("320") },
                    modifier = Modifier.weight(0.7f),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(Icons.Default.SquareFoot, contentDescription = null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (projectArea.isNotBlank())
                            Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                    )
                )
            }
        }
    }
}

@Composable
private fun EnhancedProjectPreview(
    name: String,
    description: String,
    style: String,
    location: String,
    architect: String,
    area: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Vista Previa",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Mini card preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Header simulado
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when (style) {
                                    "Contemporáneo" -> Color(0xFF667EEA)
                                    "Minimalista" -> Color(0xFF9FACE6)
                                    "Industrial" -> Color(0xFF74B9FF)
                                    "Moderno" -> Color(0xFF81ECEC)
                                    "Clásico" -> Color(0xFFFFBF93)
                                    "Rústico" -> Color(0xFFD1A3FF)
                                    "Colonial" -> Color(0xFFFF9F9F)
                                    else -> Color(0xFFBDBDBD)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (style) {
                                "Contemporáneo" -> Icons.Default.Home
                                "Minimalista" -> Icons.Default.Business
                                "Industrial" -> Icons.Default.Factory
                                "Moderno" -> Icons.Default.Apartment
                                "Clásico" -> Icons.Default.AccountBalance
                                "Rústico" -> Icons.Default.Cottage
                                "Colonial" -> Icons.Default.AccountBalance // Cambiado de Temple a AccountBalance
                                else -> Icons.Default.Construction
                            },
                            contentDescription = style,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Información del proyecto
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )

                    Text(
                        text = description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = location,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "$area m²",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Por $architect",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancedActionButtons(
    onSaveAsDraft: () -> Unit,
    onCreateProject: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Botón principal - Crear proyecto
        Button(
            onClick = onCreateProject,
            enabled = isEnabled && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = if (isLoading) "Creando..." else "Crear Proyecto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Botón secundario - Guardar borrador
        OutlinedButton(
            onClick = onSaveAsDraft,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Guardar como Borrador",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EnhancedLoadingCard() {
    Card(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Creando proyecto...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EnhancedSuccessDialog(
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            initialScale = 0.7f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(animationSpec = tween(400))
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = {
                var iconScale by remember { mutableStateOf(0f) }
                val animatedScale by animateFloatAsState(
                    targetValue = iconScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "success_icon"
                )

                LaunchedEffect(Unit) {
                    delay(200)
                    iconScale = 1f
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(animatedScale)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50).copy(alpha = 0.3f),
                                    Color(0xFF4CAF50).copy(alpha = 0.1f)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "¡Proyecto Creado Exitosamente!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.3.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Tu proyecto se ha agregado exitosamente a la galería. Ahora será visible para todos los usuarios y podrás compartirlo con el mundo.",
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Estadísticas animadas
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(icon = Icons.Default.Visibility, value = "+1", label = "En Galería")
                            StatItem(icon = Icons.Default.Star, value = "5.0", label = "Rating Inicial")
                            StatItem(icon = Icons.Default.Share, value = "∞", label = "Alcance")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Ver en Galería",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 24.dp
        )
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun validateForm(
    name: String,
    description: String,
    location: String,
    architect: String,
    area: String
): Boolean {
    return name.isNotBlank() &&
            description.isNotBlank() &&
            location.isNotBlank() &&
            architect.isNotBlank() &&
            area.isNotBlank() &&
            area.toDoubleOrNull() != null
}
