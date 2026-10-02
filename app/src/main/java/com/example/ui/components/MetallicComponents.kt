package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EnergyCyan
import com.example.ui.theme.MetalBrass
import com.example.ui.theme.MetalGold

@Composable
fun MetallicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isPrimary: Boolean = false,
    testTag: String = "metallic_button"
) {
    val gradientBrush = if (isPrimary) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF38BDF8),
                Color(0xFF0284C7),
                Color(0xFF0369A1)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2E3D5B),
                Color(0xFF1E293B),
                Color(0xFF0F172A)
            )
        )
    }

    val borderBrush = Brush.linearGradient(
        colors = if (isPrimary) {
            listOf(Color(0xFFBAE6FD), Color(0xFF0284C7))
        } else {
            listOf(Color(0xFF475569), Color(0xFF1E293B))
        }
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(gradientBrush)
            .border(1.2.dp, borderBrush, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isPrimary) Color(0xFF070B14) else EnergyCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = if (isPrimary) Color(0xFF070B14) else Color(0xFFF8FAFC),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun MetallicCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkSurfaceElevated,
                        DarkSurface
                    )
                )
            )
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun StarRatingRow(
    stars: Int,
    maxStars: Int = 3,
    modifier: Modifier = Modifier,
    starSize: Int = 22
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val filled = i <= stars
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star $i",
                tint = if (filled) MetalGold else Color(0xFF334155),
                modifier = Modifier.size(starSize.dp)
            )
            if (i < maxStars) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun HudBadge(
    label: String,
    value: String,
    icon: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xD90F172A))
            .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Text(text = icon, fontSize = 14.sp)
            }
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    color = Color(0xFFF8FAFC),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
