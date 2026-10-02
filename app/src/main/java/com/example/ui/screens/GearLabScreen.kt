package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GearMaterial
import com.example.ui.components.MetallicCard
import com.example.ui.renderer.Gear3DRenderer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EnergyCyan
import com.example.ui.theme.MetalGold
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.Screen

@Composable
fun GearLabScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val labGear = uiState.selectedGearForLab
    val labCam = uiState.labCamera

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.HOME) },
                    modifier = Modifier
                        .testTag("gearlab_back_button")
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
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "GEAR LAB 3D",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Precision Kinematics Inspector",
                        fontSize = 11.sp,
                        color = EnergyCyan
                    )
                }
            }

            Text(
                text = "${uiState.labRpm.toInt()} RPM",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EnergyCyan,
                fontFamily = FontFamily.Monospace
            )
        }

        // 3D Inspection Viewport (Interactive orbit via touch)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.rotateLabCamera(
                            dYaw = dragAmount.x * 0.4f,
                            dPitch = dragAmount.y * 0.4f
                        )
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)

                // Background circular coordinate rings
                drawCircle(color = Color(0x15334155), radius = 120f, center = center)
                drawCircle(color = Color(0x15334155), radius = 80f, center = center)
                drawCircle(color = Color(0x15334155), radius = 40f, center = center)

                Gear3DRenderer.drawGear(
                    scope = this,
                    gear = labGear,
                    center = center,
                    camera = labCam,
                    scale = 1.35f
                )
            }

            // Touch Drag Hint in Viewport
            Text(
                text = "SWIPE TO ORBIT 360°",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
            )
        }

        // Controls and Material Selection Scrollable Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Material Selection
            Column {
                Text(
                    text = "METALLIC ALLOY / MATERIAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(GearMaterial.entries) { material ->
                        val isSelected = material == labGear.material
                        Box(
                            modifier = Modifier
                                .testTag("material_${material.name}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) {
                                        Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1)))
                                    } else {
                                        Brush.verticalGradient(listOf(DarkSurfaceElevated, DarkSurface))
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) EnergyCyan else DarkSurfaceBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateLabGearMaterial(material) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = material.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            // Tooth Count Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TEETH COUNT (MODULE PITCH)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${labGear.teethCount} TEETH",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Slider(
                    value = labGear.teethCount.toFloat(),
                    onValueChange = { viewModel.updateLabTeethCount(it.toInt()) },
                    valueRange = 8f..32f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = EnergyCyan,
                        activeTrackColor = EnergyCyan,
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )
            }

            // Dyno RPM Test Bench Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "TEST BENCH DYNO (RPM)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.labRpm.toInt()} RPM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EnergyCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Slider(
                    value = uiState.labRpm,
                    onValueChange = { viewModel.updateLabRpm(it) },
                    valueRange = 0f..240f,
                    colors = SliderDefaults.colors(
                        thumbColor = MetalGold,
                        activeTrackColor = MetalGold,
                        inactiveTrackColor = Color(0xFF1E293B)
                    )
                )
            }

            // Engineering Telemetry Spec Card
            MetallicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "KINEMATIC SPECIFICATIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EnergyCyan,
                        letterSpacing = 1.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Material Alloy:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Text(text = labGear.material.displayName, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Pitch Diameter:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Text(text = "${(labGear.radius * 2).toInt()} mm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Rotational Inertia:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Text(text = "${(labGear.material.massMultiplier * 100).toInt()} kg•cm²", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Friction Coefficient:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                        Text(text = "${labGear.material.frictionMultiplier} μ", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
