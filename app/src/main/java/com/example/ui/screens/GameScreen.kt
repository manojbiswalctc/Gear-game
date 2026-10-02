package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Gear
import com.example.model.LevelObjectiveType
import com.example.model.LevelRepository
import com.example.ui.components.HudBadge
import com.example.ui.components.MetallicButton
import com.example.ui.components.MetallicCard
import com.example.ui.components.StarRatingRow
import com.example.ui.renderer.Camera3DState
import com.example.ui.renderer.Gear3DRenderer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EnergyCyan
import com.example.ui.theme.EnergyGreen
import com.example.ui.theme.MachineJam
import com.example.ui.theme.MachineWarning
import com.example.ui.theme.MetalGold
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen
import kotlin.math.hypot

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val level = uiState.currentLevel
    val world = LevelRepository.getWorldForLevel(level.id)

    var showHintDialog by remember { mutableStateOf(false) }
    var selectedGearDetails by remember { mutableStateOf<Gear?>(null) }

    // Pulsing energy animatable for circuit board
    val energyPulse = remember { Animatable(0f) }
    LaunchedEffect(uiState.isPowerOn) {
        if (uiState.isPowerOn) {
            energyPulse.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            energyPulse.snapTo(0f)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        val viewportCenter = Offset(maxWidth.value * 1.5f, maxHeight.value * 0.95f)

        // 3D Machinery Simulation Canvas with multi-touch gestures
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("machinery_canvas")
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        if (zoom != 1.0f) {
                            viewModel.zoomCamera(zoom)
                        }
                        if (pan.x != 0f || pan.y != 0f) {
                            // Two-finger pan or single drag fallback
                            viewModel.panCamera(pan.x * 0.8f, pan.y * 0.8f)
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        // One finger drag tilts and orbits machine camera
                        viewModel.rotateCamera(
                            deltaYaw = dragAmount.x * 0.35f,
                            deltaPitch = dragAmount.y * 0.35f
                        )
                    }
                }
        ) {
            val canvasCenter = Offset(size.width / 2f, size.height / 2f - 40f)

            // Draw futuristic industrial chassis grid background
            val gridSpacing = 48f
            for (gx in 0 until (size.width / gridSpacing).toInt() + 1) {
                drawLine(
                    color = Color(0x18334155),
                    start = Offset(gx * gridSpacing, 0f),
                    end = Offset(gx * gridSpacing, size.height),
                    strokeWidth = 1f
                )
            }
            for (gy in 0 until (size.height / gridSpacing).toInt() + 1) {
                drawLine(
                    color = Color(0x18334155),
                    start = Offset(0f, gy * gridSpacing),
                    end = Offset(size.width, gy * gridSpacing),
                    strokeWidth = 1f
                )
            }

            // Draw mounting peg holes on machine plate
            for (peg in level.pegSlots) {
                val pegProj = uiState.camera.project(peg.x, peg.y, peg.layer, canvasCenter)
                val isHintSlot = uiState.hintLevel >= 2 && level.hints.level2TargetSlot?.id == peg.id

                // Socket mounting plate
                drawCircle(
                    color = if (isHintSlot) Color(0x6600F0FF) else Color(0xFF1E293B),
                    radius = 20f * uiState.camera.zoom,
                    center = pegProj
                )
                drawCircle(
                    color = if (isHintSlot) EnergyCyan else Color(0xFF475569),
                    radius = 20f * uiState.camera.zoom,
                    center = pegProj,
                    style = Stroke(if (isHintSlot) 2.5f else 1.2f)
                )
                // Center spindle hole
                drawCircle(
                    color = Color(0xFF070B14),
                    radius = 6f * uiState.camera.zoom,
                    center = pegProj
                )
            }

            // Draw meshing energy conduit lines between meshed gears
            for (g in uiState.activeGearsOnBoard) {
                if (g.isPowered && g.meshedWith.isNotEmpty()) {
                    val pA = uiState.camera.project(g.x, g.y, g.layer, canvasCenter)
                    for (otherId in g.meshedWith) {
                        val other = uiState.activeGearsOnBoard.find { it.id == otherId }
                        if (other != null) {
                            val pB = uiState.camera.project(other.x, other.y, other.layer, canvasCenter)
                            Gear3DRenderer.drawMeshingEnergyConduit(
                                scope = this,
                                posA = pA,
                                posB = pB,
                                radiusA = g.radius * uiState.camera.zoom,
                                radiusB = other.radius * uiState.camera.zoom
                            )
                        }
                    }
                }
            }

            // Draw all active gears sorted by layer depth (Layer 3 background first, Layer 1 foreground last)
            val sortedGears = uiState.activeGearsOnBoard.sortedByDescending { it.layer }
            for (gear in sortedGears) {
                val projPos = uiState.camera.project(gear.x, gear.y, gear.layer, canvasCenter)
                Gear3DRenderer.drawGear(
                    scope = this,
                    gear = gear,
                    center = projPos,
                    camera = uiState.camera
                )
            }

            // Draw actively dragged gear following touch pointer
            uiState.draggedGear?.let { dGear ->
                Gear3DRenderer.drawGear(
                    scope = this,
                    gear = dGear,
                    center = uiState.dragScreenOffset,
                    camera = uiState.camera,
                    scale = 1.15f
                )
            }
        }

        // ==================== IN-GAME HUD ====================

        // TOP BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back & Level Tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .testTag("back_button")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    HudBadge(
                        label = "SECTOR 0${world.id}",
                        value = "LEVEL ${level.id.toString().padStart(3, '0')}"
                    )
                }

                // Timer & Hint Trigger
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val minutes = uiState.elapsedSeconds / 60
                    val seconds = uiState.elapsedSeconds % 60
                    HudBadge(
                        label = "CHRONO",
                        value = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
                        icon = "⏱"
                    )

                    IconButton(
                        onClick = { showHintDialog = true },
                        modifier = Modifier
                            .testTag("hint_button")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (uiState.hintLevel > 0) EnergyCyan.copy(alpha = 0.25f) else DarkSurfaceElevated)
                            .border(1.dp, if (uiState.hintLevel > 0) EnergyCyan else DarkSurfaceBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Smart Hint",
                            tint = if (uiState.hintLevel > 0) EnergyCyan else MetalGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // OBJECTIVE BANNER
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xD90F172A))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = level.objectiveType.icon, fontSize = 20.sp)
                        Column {
                            Text(
                                text = level.objectiveType.title.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EnergyCyan,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = level.description,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1),
                                maxLines = 1
                            )
                        }
                    }

                    if (uiState.isPowerOn) {
                        Text(
                            text = if (uiState.isLevelCompleted) "ONLINE ⚡" else "ACTIVE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (uiState.isLevelCompleted) EnergyGreen else EnergyCyan,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (uiState.isLevelCompleted) EnergyGreen.copy(0.2f) else EnergyCyan.copy(0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // OVERLOAD JAM NOTIFICATION
        AnimatedVisibility(
            visible = uiState.isSystemOverloaded,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 135.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xEE7F1D1D))
                    .border(1.2.dp, MachineJam, RoundedCornerShape(10.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "⚠ SYSTEM OVERLOAD: ${uiState.overloadMessage ?: "Conflicting rotation vectors!"}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // FLOATING ACTION CONTROLS (Center/Bottom Sides)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 125.dp, start = 16.dp, end = 16.dp)
        ) {
            // Reset Button (Left)
            IconButton(
                onClick = { viewModel.resetLevel() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .testTag("reset_button")
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Level",
                    tint = Color(0xFFCBD5E1),
                    modifier = Modifier.size(22.dp)
                )
            }

            // POWER TOGGLE SWITCH (Center)
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .testTag("power_button")
                    .shadow(12.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            if (uiState.isPowerOn) {
                                listOf(Color(0xFF059669), Color(0xFF10B981))
                            } else {
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            }
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = if (uiState.isPowerOn) EnergyGreen else EnergyCyan,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { viewModel.togglePower() }
                    .padding(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isPowerOn) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = if (uiState.isPowerOn) Color.White else EnergyCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (uiState.isPowerOn) "DISENGAGE" else "IGNITE POWER",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            // Camera Reset Orbit Button (Right)
            IconButton(
                onClick = { viewModel.resetCamera() },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .testTag("camera_reset_button")
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkSurfaceBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = "Reset 3D Angle",
                    tint = EnergyCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // BOTTOM GEAR DOCK INVENTORY
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, DarkBackground.copy(alpha = 0.95f), DarkBackground)
                    )
                )
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEAR INVENTORY (${uiState.inventoryGears.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "DRAG TO MOUNT 👆",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EnergyCyan
                    )
                }

                if (uiState.inventoryGears.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "All gears mounted onto machine chassis.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.inventoryGears) { gear ->
                            GearInventoryCard(
                                gear = gear,
                                isHighlighted = uiState.hintLevel >= 1 && level.inventoryGears.firstOrNull()?.id == gear.id,
                                onDragStart = { startOffset ->
                                    viewModel.startDragging(gear, startOffset)
                                },
                                onDrag = { offset ->
                                    viewModel.updateDragPosition(offset)
                                },
                                onDragEnd = {
                                    // Project drop position to board space
                                    val canvasCenter = Offset(viewportCenter.x / 2f, viewportCenter.y / 2f)
                                    val dropX = (uiState.dragScreenOffset.x - canvasCenter.x) / uiState.camera.zoom - uiState.camera.panX
                                    val dropY = (uiState.dragScreenOffset.y - canvasCenter.y) / (uiState.camera.zoom * uiState.camera.foreshorteningY) - uiState.camera.panY
                                    viewModel.dropGearOnBoard(dropX, dropY, canvasCenter)
                                },
                                onClick = {
                                    selectedGearDetails = gear
                                }
                            )
                        }
                    }
                }
            }
        }

        // VICTORY LEVEL COMPLETION OVERLAY MODAL
        AnimatedVisibility(
            visible = uiState.isLevelCompleted,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xD9050811)),
                contentAlignment = Alignment.Center
            ) {
                MetallicCard(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MECHANISM ONLINE",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EnergyCyan,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Level ${level.id} Synchronized",
                            fontSize = 14.sp,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        StarRatingRow(stars = uiState.starsEarned, starSize = 34)

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "TIME", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "${uiState.elapsedSeconds}s",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "GEAR COINS", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "+${uiState.starsEarned * 25 + 50}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MetalGold
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PARTS", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "+${uiState.starsEarned * 5}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EnergyCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetallicButton(
                                text = "REPLAY",
                                onClick = { viewModel.resetLevel() },
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Refresh
                            )
                            MetallicButton(
                                text = "NEXT LEVEL ▶",
                                onClick = { viewModel.loadLevel(level.id + 1) },
                                modifier = Modifier.weight(1.3f),
                                isPrimary = true
                            )
                        }
                    }
                }
            }
        }

        // SMART HINT DIALOG
        if (showHintDialog) {
            AlertDialog(
                onDismissRequest = { showHintDialog = false },
                containerColor = DarkSurfaceElevated,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💡 SMART HINT SYSTEM", color = MetalGold, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Level ${uiState.hintLevel} / 4 Revealed",
                            fontSize = 12.sp,
                            color = EnergyCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                        when {
                            uiState.hintLevel == 0 -> {
                                Text(
                                    text = "Unlock smart progressive mechanical hints without spoiling the entire puzzle.",
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                            uiState.hintLevel >= 1 -> {
                                Text(text = "• Hint 1: ${level.hints.level1Text}", color = Color.White)
                            }
                        }
                        if (uiState.hintLevel >= 2) {
                            Text(text = "• Hint 2: Placement peg highlighted on machine plate (cyan glow).", color = EnergyCyan)
                        }
                        if (uiState.hintLevel >= 3) {
                            Text(text = "• Hint 3: ${level.hints.level3DirectionText}", color = MetalGold)
                        }
                        if (uiState.hintLevel >= 4) {
                            Text(text = "• Hint 4: Solution layout fully unlocked.", color = EnergyGreen)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.requestNextHint()
                            if (uiState.hintLevel >= 4) showHintDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EnergyCyan, contentColor = Color.Black)
                    ) {
                        Text(if (uiState.hintLevel < 4) "REVEAL NEXT HINT" else "CLOSE")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showHintDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
                    ) {
                        Text("CANCEL")
                    }
                }
            )
        }
    }
}

@Composable
fun GearInventoryCard(
    gear: Gear,
    isHighlighted: Boolean,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(width = 110.dp, height = 96.dp)
            .testTag("gear_card_${gear.id}")
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isHighlighted) 2.dp else 1.dp,
                color = if (isHighlighted) EnergyCyan else DarkSurfaceBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .pointerInput(gear.id) {
                detectDragGestures(
                    onDragStart = { offset -> onDragStart(offset) },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrag(change.position)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            },
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Miniature gear canvas icon preview
            Canvas(modifier = Modifier.size(36.dp)) {
                Gear3DRenderer.drawGear(
                    scope = this,
                    gear = gear,
                    center = Offset(size.width / 2f, size.height / 2f),
                    camera = Camera3DState(pitchDeg = 0f, yawDeg = 0f, zoom = 0.5f),
                    scale = 0.65f
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = gear.type.displayName,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "${gear.teethCount}T • ${gear.material.displayName.take(7)}",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
