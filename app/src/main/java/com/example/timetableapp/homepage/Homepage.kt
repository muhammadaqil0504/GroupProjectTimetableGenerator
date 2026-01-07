package com.example.timetableapp.homepage // FIXED: Lowercase package name

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Import the ScallopedHeader you moved to the top level in MainActivity
import com.example.timetableapp.ScallopedHeader

@Composable
fun HomepageInterface(
    onGenerateNavClick: () -> Unit,
    onEditNavClick: () -> Unit,
    onSubjectNavClick: () -> Unit,
    onTimetableClick: () -> Unit
) {
    val chalkboardGreen = Color(0xFF4B6E63)
    val cardColor = Color(0xFFF5E6D3)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(chalkboardGreen)
    ) {
        ScallopedHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(130.dp))

            Text(
                text = "HI STUDENT !",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "READY TO PLAN YOUR WEEK ?",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(40.dp))

            // 1. Uses built-in DateRange icon
            MenuButton(
                text = "Generate Timetable",
                icon = Icons.Default.DateRange,
                bgColor = cardColor,
                onClick = onGenerateNavClick
            )

            // 2. Uses built-in Edit icon
            MenuButton(
                text = "Edit Timetable",
                icon = Icons.Default.Edit,
                bgColor = cardColor,
                onClick = onEditNavClick
            )

            // 3. Uses built-in Star/Bookmark icon
            MenuButton(
                text = "Subject",
                icon = Icons.Default.Star,
                bgColor = cardColor,
                onClick = onSubjectNavClick
            )

            // 4. Uses built-in Notifications/Bell icon
            MenuButton(
                text = "TIMETABLE",
                icon = Icons.Outlined.Notifications,
                bgColor = cardColor,
                onClick = onTimetableClick
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    bgColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(72.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(26.dp)
            )

            Spacer(Modifier.width(20.dp))

            Text(
                text = text,
                color = Color.Black,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}